package td.services;

import methodOfSummarizationAndElementsOfText.MethodsOfSummarizationAndElementsOfText;
import ru.library.text.word.Word;
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

    public RulesValidator(WordDocument document, Path schema) {
        XmlRulesGetter xmlRules = new XmlRulesGetter();
        this.generalRulesSections = new ArrayList<>();
        this.document = document;
        this.sectionKindList = xmlRules.getSectionKind(schema);
        this.documentKeyWords = new ArrayList<>();
        this.xsdKeyWords = new ArrayList<>();
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

                                //поиск разделов для применения общих правил
                                if (stringEntry.getKey().equals("generalRules")) {
                                    if (stringEntry.getValue().equals("true")) {
                                        generalRulesSections.add(document.getSections().get(i));
                                    }
                                }

                                //точное совпадение заголовка
                                if (stringEntry.getKey().equals("title")) {
                                    if (!document.getSections().get(i).getTitle().trim().equals(stringEntry.getValue())) {
                                        errors.add("Неверный заголовок: '" + document.getSections().get(i).getTitle().trim()
                                                + "' ожидалось: '" + stringEntry.getValue() + "'.");
                                    }
                                }

                                //неточное совпадение заголовка
                                if (stringEntry.getKey().equals("titleContains")) {
                                    if (!document.getSections().get(i).getTitle().trim().contains(stringEntry.getValue())) {
                                        errors.add("Неверный заголовок: '" + document.getSections().get(i).getTitle().trim()
                                                + "' ожидалось, что в заголовке встретится: '" + stringEntry.getValue() + "'.");
                                    }
                                }

                                //неточное совпадание заголовка последнего раздела 2 уровня
                                if (stringEntry.getKey().equals("lastSubsectionTitle")) {
                                    if (!document.getSections().get(i).getSubheadersList().isEmpty()) {
                                        int lastSection = document.getSections().get(i).getSubheadersList().size() - 1;
                                        String title = document.getSections().get(i).getSubheadersList().get(lastSection).getTitle().trim();
                                        if (!title.contains(stringEntry.getValue())) {
                                            errors.add("Неверный послений заголовок раздела: '" + title
                                                    + "' ожидалось, что в заголовке встретится: '" + stringEntry.getValue() + "'.");
                                        }
                                    }
                                }

                                //точное количество подразделов
                                if (stringEntry.getKey().equals("subsectionQuantity")) {
                                    if (document.getSections().get(i).getSubheadersList().size() !=
                                            Integer.parseInt(stringEntry.getValue())) {
                                        errors.add("Неверное количество подзаголовков раздела '" + document.getSections().get(i).getTitle() +
                                                "': '" + document.getSections().get(i).getSubheadersList().size() +
                                                "' ожидалось: '" + stringEntry.getValue() + "'.");
                                    }
                                }

                                //минимальное количество подразделов
                                if (stringEntry.getKey().equals("minSubsectionQuantity")) {
                                    if (document.getSections().get(i).getSubheadersList().size() <
                                            Integer.parseInt(stringEntry.getValue())) {
                                        errors.add("Количество подразделов раздела '" + document.getSections().get(i).getTitle() +
                                                "' меньше: '" + document.getSections().get(i).getSubheadersList().size() +
                                                "' ожидалось как минимум " + stringEntry.getValue() + ".");
                                    }
                                }

                                //максимальное количество подразделов
                                if (stringEntry.getKey().equals("maxSubsectionQuantity")) {
                                    if (document.getSections().get(i).getSubheadersList().size() >
                                            Integer.parseInt(stringEntry.getValue())) {
                                        errors.add("Количество подразделов раздела '" + document.getSections().get(i).getTitle() +
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

    public List<String> validateGeneralRules(Map<String, String> userGeneralRules) {
        List<String> errors = new ArrayList<>();
        boolean isGetKw = false;

        for (Map.Entry<String, String> entry : userGeneralRules.entrySet()) {

            //проверка аббревиатур
            if (entry.getKey().equals("validateAbbreviation")) {
                if (Boolean.parseBoolean(entry.getValue())) {
                    errors.add(validateAbbreviation(document));
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

                    float value = (float) 100 * commonWords.size() / xsdKeyWords.size();
                    int commonWordsPercentage = round(value);
                    errors.add("Совпадений по ключевым словам: " + commonWordsPercentage + "%");
                    if (isGetKw) {
                        errors.add("Общие ключевые слова: ");
                        errors.addAll(commonWords);
                    }
                }
            }

            //поиск ключевых слов в шаблоне
            if (entry.getKey().equals("keyWords")) {
                String words[] = entry.getValue().split(" ");
                xsdKeyWords.addAll(Arrays.asList(words));
            }

            //поиск ключевых слов в документе
            if (entry.getKey().equals("getKeyWords")) {
                if (Boolean.parseBoolean(entry.getValue())) {
                    isGetKw = Boolean.parseBoolean(entry.getValue());
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

    private String validateAbbreviation(WordDocument document) {
        String errors = "";
        for (int j = 0; j < sectionKindList.size(); j++) {
            if (sectionKindList.get(j).equals("listOfTermsAndAbbreviations")) {
                List<Term> termList = AbbreviationValidator.getTermList(document.getSections().get(j));
                Set<String> abbreviationInText = AbbreviationValidator.getAbbrInText(generalRulesSections);
                Iterator<String> iterator = abbreviationInText.iterator();
                while (iterator.hasNext()) {
                    for (int k = 0; k < termList.size(); k++) {
                        String str = iterator.next();
                        if ((str.equals(termList.get(k).getTerm()))) {
                            System.out.println("Найдено совпадение: " + termList.get(k).getTerm());
                            iterator.remove();
                            break;
                        }
                    }
                }
                if (!abbreviationInText.isEmpty()) {
                    errors = "В тексте найдены аббревиатуры, которые не указаны в списке терминов: " +
                            abbreviationInText;
                }
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
}
