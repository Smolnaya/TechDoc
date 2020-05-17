package td.services;

import methodOfSummarizationAndElementsOfText.MethodsOfSummarizationAndElementsOfText;
import ru.library.text.word.Word;
import ru.textanalysis.tawt.jmorfsdk.JMorfSdk;
import ru.textanalysis.tawt.jmorfsdk.loader.JMorfSdkFactory;
import td.domain.Section;
import td.domain.Term;
import td.domain.WordDocument;

import java.nio.file.Path;
import java.util.*;

import static java.lang.Math.round;

public class RulesValidator {
    private List<Section> generalRulesSections;
    private List<String> sectionKindList;
    private List<String> documentKeyWords;
    private List<String> xsdKeyWords;
    private HashSet<String> commonWords;
    private WordDocument document;
    private static JMorfSdk jMorfSdk = JMorfSdkFactory.loadFullLibrary();

    public RulesValidator(WordDocument document, Path schema,
                          Map<String, String> userGeneralRules) {
        XmlRulesGetter xmlRules = new XmlRulesGetter();
        this.generalRulesSections = new ArrayList<>();
        this.document = document;
        this.sectionKindList = xmlRules.getSectionKind(schema);
        this.documentKeyWords = new ArrayList<>();
        this.xsdKeyWords = getUserKeyWords(userGeneralRules);
        this.commonWords = new HashSet<>();
    }

    public List<String> validateRules(Map<String, Map<String, String>> userSectionRules,
                                      Map<String, String> userGeneralRules) {
        try {
            List<String> errors = new ArrayList<>();
            for (int i = 0; i < document.getSections().size(); i++) {
                if (userSectionRules.containsKey(sectionKindList.get(i))) {
                    for (Map.Entry<String, Map<String, String>> entry : userSectionRules.entrySet()) {
                        if (entry.getKey().equals(sectionKindList.get(i))) {
                            Map<String, String> rules = entry.getValue();
                            for (Map.Entry<String, String> stringEntry : rules.entrySet()) {

                                //проверка вхождений заданных слов
                                if (stringEntry.getKey().equals("wordsInclusion")) {
                                    String content = getSectionContent(document.getSections().get(i));
                                    String words = stringEntry.getValue();
                                    String title = document.getSections().get(i).getTitle();
                                    errors.addAll(checkWordInclusion(content, words, title));
                                }

                                //поиск разделов для применения общих правил
                                if (stringEntry.getKey().equals("generalRules")) {
                                    if (stringEntry.getValue().equals("true")) {
                                        generalRulesSections.add(document.getSections().get(i));
                                    }
                                }

                                //точное совпадение заголовка
                                if (stringEntry.getKey().equals("title")) {
                                    if (!document.getSections().get(i).getTitle().trim().equals(stringEntry.getValue())) {
                                        errors.add(String.format("\nНеверный заголовок: '%s', ожидалось: '%s'.",
                                                document.getSections().get(i).getTitle().trim(), stringEntry.getValue()));
                                    }
                                }

                                //неточное совпадение заголовка
                                if (stringEntry.getKey().equals("titleContains")) {
                                    String title = document.getSections().get(i).getTitle().trim();
                                    String value = stringEntry.getValue().trim();
                                    String report = validateTitleContains(title, value);
                                    if(!report.isEmpty()) {
                                        errors.add(report);
                                    }
                                }

                                //неточное совпадание заголовка последнего раздела 2 уровня
                                if (stringEntry.getKey().equals("lastSubsectionTitle")) {
                                    if (!document.getSections().get(i).getSubheadersList().isEmpty()) {
                                        int lastSection = document.getSections().get(i).getSubheadersList().size() - 1;
                                        String title = document.getSections().get(i).getSubheadersList().get(lastSection).getTitle().trim();
                                        String value = stringEntry.getValue().trim();
                                        String sectionTitle = document.getSections().get(i).getTitle();
                                        String report = validateTitleContains(title, value);
                                        if(!report.isEmpty()) {
                                            errors.add(String.format("\nНеверный последний заголовок раздела: '%s'. %s",
                                                    sectionTitle, report));
                                        }
                                    }
                                }

                                //точное количество подразделов
                                if (stringEntry.getKey().equals("subsectionQuantity")) {
                                    if (document.getSections().get(i).getSubheadersList().size() !=
                                            Integer.parseInt(stringEntry.getValue())) {
                                        errors.add("\nНеверное количество подзаголовков раздела '" + document.getSections().get(i).getTitle() +
                                                "': '" + document.getSections().get(i).getSubheadersList().size() +
                                                "' ожидалось: '" + stringEntry.getValue() + "'.");
                                    }
                                }

                                //минимальное количество подразделов
                                if (stringEntry.getKey().equals("minSubsectionQuantity")) {
                                    if (document.getSections().get(i).getSubheadersList().size() <
                                            Integer.parseInt(stringEntry.getValue())) {
                                        errors.add("\nКоличество подразделов раздела '" + document.getSections().get(i).getTitle() +
                                                "' меньше: '" + document.getSections().get(i).getSubheadersList().size() +
                                                "' ожидалось как минимум " + stringEntry.getValue() + ".");
                                    }
                                }

                                //максимальное количество подразделов
                                if (stringEntry.getKey().equals("maxSubsectionQuantity")) {
                                    if (document.getSections().get(i).getSubheadersList().size() >
                                            Integer.parseInt(stringEntry.getValue())) {
                                        errors.add("\nКоличество подразделов раздела '" + document.getSections().get(i).getTitle() +
                                                "' больше: '" + document.getSections().get(i).getSubheadersList().size() +
                                                "' ожидалось как максимум " + stringEntry.getValue() + ".");
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (!userGeneralRules.isEmpty()) {
                errors.addAll(validateGeneralRules(userGeneralRules));
            }
            return errors;
        } catch (
                Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<String> getUserKeyWords(Map<String, String> userGeneralRules) {
        List<String> kw = new ArrayList<>();
        for (Map.Entry<String, String> entry : userGeneralRules.entrySet()) {
            if (entry.getKey().equals("keyWords")) {
                String words[] = entry.getValue().split(" ");
                kw.addAll(Arrays.asList(words));
            }
        }
        return kw;
    }

    public List<String> validateGeneralRules(Map<String, String> userGeneralRules) {
        List<String> errors = new ArrayList<>();
        boolean isGetKw = false;

        for (Map.Entry<String, String> entry : userGeneralRules.entrySet()) {

            //поиск ключевых слов в документе
            if (entry.getKey().equals("getKeyWords")) {
                if (Boolean.parseBoolean(entry.getValue())) {
                    isGetKw = Boolean.parseBoolean(entry.getValue());
                }
            }

            //проверка ключевых слов
            if (entry.getKey().equals("validateKeyWords")) {
                if (Boolean.parseBoolean(entry.getValue())) {
                    getDocumentKeyWords();

                    //поиск общий слов
                    for (String xsdWord : xsdKeyWords) {
                        for (String documentWord : documentKeyWords) {
                            if (xsdWord.equals(documentWord)) {
                                commonWords.add(xsdWord);
                            }
                        }
                    }

                    if (!commonWords.isEmpty()) {
                        float commonWordsPercentage = (float) 100 * commonWords.size() / xsdKeyWords.size();
                        errors.add(String.format("\nСовпадений по ключевым словам: %.2f%%.", commonWordsPercentage));
                        if (isGetKw) {
                            errors.add("Общие ключевые слова: ");
                            errors.addAll(commonWords);
                        }
                    } else {
                        errors.add("\nВ разделах не найдено заданных ключевых слов.");
                    }
                }
            }

            //проверка аббревиатур
            if (entry.getKey().equals("validateAbbreviation")) {
                if (Boolean.parseBoolean(entry.getValue())) {
                    errors.add("\nПроверка аббревиатур");
                    errors.addAll(validateAbbreviation(document));
                }
            }
        }

        return errors;
    }

    private void getDocumentKeyWords() {
        for (int i = 0; i < generalRulesSections.size(); i++) {
            String content = getSectionContent(generalRulesSections.get(i));
            if (!content.trim().isEmpty()) {
                List<Word> keyWords = getKeyWords(content);
                for (int j = 0; j < keyWords.size(); j++) {
                    documentKeyWords.add(keyWords.get(j).getWord());
                }
            }
        }
    }

    private List<String> validateAbbreviation(WordDocument document) {
        List<String> errors = new ArrayList<>();
        for (int j = 0; j < sectionKindList.size(); j++) {
            if (sectionKindList.get(j).equals("listOfTermsAndAbbreviations")) {
                List<Term> termList = AbbreviationValidator.getTermList(document.getSections().get(j));
                if (!termList.isEmpty()) {
                    Set<String> abbreviationInText = AbbreviationValidator.getAbbrInText(generalRulesSections);
                    Iterator<String> iterator = abbreviationInText.iterator();
                    List<String> foundAbbr = new ArrayList<>();
                    foundAbbr.add("\nНайденные совпадения: ");
                    while (iterator.hasNext()) {
                        for (int k = 0; k < termList.size(); k++) {
                            String str = iterator.next();
                            if ((str.equals(termList.get(k).getTerm()))) {
                                foundAbbr.add(termList.get(k).getTerm());
                                iterator.remove();
                                break;
                            }
                        }
                    }
                    if (foundAbbr.size() > 1) {
                        errors.addAll(foundAbbr);
                    }
                    if (!abbreviationInText.isEmpty()) {
                        errors.add("\nВ тексте найдены аббревиатуры, которые не указаны в списке аббревиатур: " +
                                abbreviationInText);
                    }
                } else errors.add("Список аббревиатур не найден.");
            }
        }
        return errors;
    }

    private List<Word> getKeyWords(String content) {
        MethodsOfSummarizationAndElementsOfText ms = new MethodsOfSummarizationAndElementsOfText();
        List<Word> keyWords = ms.getKeyWords(content);
        return keyWords;
    }

    private String getSectionContent(Section section) {
        StringBuilder content = new StringBuilder();
        for (int i = 0; i < section.getContent().size(); i++) {
            content.append(section.getContent().get(i));
        }
        if (!section.getSubheadersList().isEmpty()) {
            for (int i = 0; i < section.getSubheadersList().size(); i++) {
                content.append(getSectionContent(section.getSubheadersList().get(i)));
            }
        }
        return content.toString();
    }

    private List<String> checkWordInclusion(String content, String value, String title) {
        String[] contentWordRaw = content.split(" ");
        Set<String> contentWordSet = new HashSet<>();
        for (String string : contentWordRaw) {
            contentWordSet.add(string.replaceAll("[^A-Za-zА-Яа-я]", ""));
        }
        Set<String> initialContentWord = new HashSet<>();
        for (String string : contentWordSet) {
            List<String> wordForms = jMorfSdk.getStringInitialForm(string);
            if (!wordForms.isEmpty()) {
                initialContentWord.add(wordForms.get(0));
            }
        }

        String[] words = value.split(" ");
        Set<String> initialUserWords = new HashSet<>();
        for (String string : words) {
            List<String> wordForms = jMorfSdk.getStringInitialForm(string);
            if (!wordForms.isEmpty()) {
                initialUserWords.add(wordForms.get(0));
            }
        }

        List<String> foundWords = new ArrayList<>();
        for (String userWord : initialUserWords) {
            for (String contentWord : initialContentWord) {
                if (userWord.equals(contentWord)) {
                    foundWords.add(userWord);
                }
            }
        }

        List<String> report = new ArrayList<>();
        if (!foundWords.isEmpty()) {
            float percent = (float) 100 * foundWords.size() / initialUserWords.size();
            report.add(String.format("\nВ разделе %s найдено %.2f%% заданных слов:", title, percent));
            report.addAll(foundWords);
        } else {
            report.add(String.format("\nВ разделе %s заданные слова не найдены.", title));
        }
        return  report;
    }

    private String validateTitleContains(String title, String value) {
        String valueInitialForm = jMorfSdk.getStringInitialForm(value.toLowerCase()).get(0);
        boolean hasUppercase = value.equals(value.toUpperCase());
        String report = "";
        if (hasUppercase) {
            if (!title.contains(valueInitialForm.toUpperCase())) {
                report = String.format("\nОжидалось, что в заголовке '%s' встретится: '%s'.", title, valueInitialForm.toUpperCase());
            }
            return report;
        } else {
            String titleLowerCase = title.toLowerCase();
            if (!titleLowerCase.contains(valueInitialForm)) {
                report = String.format("\nОжидалось, что в заголовке '%s' встретится: '%s'.", title, valueInitialForm);
            }
            return report;
        }
    }
}
