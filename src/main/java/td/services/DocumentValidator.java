package td.services;

import td.domain.Section;
import td.domain.Term;
import td.domain.WordDocument;

import java.nio.file.Path;
import java.util.*;

public class DocumentValidator {
    private XmlRulesGetter xmlRules = new XmlRulesGetter();

    public List<String> validate(WordDocument document, Path schema, Boolean isValidateAbbreviation) {
        try {
            Map<String, Map<String, String>> documentRules = xmlRules.getDocumentRules(schema);
            List<String> sectionKindList = xmlRules.getSectionKind(schema);

            List<String> errors = new ArrayList<>();
            for (int i = 0; i < document.getSections().size(); i++) {
                if (documentRules.containsKey(sectionKindList.get(i))) {
                    for (Map.Entry<String, Map<String, String>> entry : documentRules.entrySet()) {
                        if (entry.getKey().equals(sectionKindList.get(i))) {
                            Map<String, String> rules = entry.getValue();
                            for (Map.Entry<String, String> stringEntry : rules.entrySet()) {

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

                                //проверка аббревиатур
                                if (stringEntry.getKey().equals("valid")) {
//                                    if (Boolean.parseBoolean(stringEntry.getValue())) {
                                    if (isValidateAbbreviation) {
                                        List<Section> sections = new ArrayList<>();
                                        for (int j = 0; j < i; j++) {
                                            sections.add(document.getSections().get(j));
                                        }
                                        List<Term> termList = AbbreviationValidator.getTermList(document.getSections().get(i));
                                        Set<String> abbreviationInText = AbbreviationValidator.getAbbrInText(sections);
                                        for (Iterator<String> iterator = abbreviationInText.iterator(); iterator.hasNext(); ) {
                                            for (int j = 0; j < termList.size(); j++) {
                                                String str = iterator.next();
                                                if ((str.equals(termList.get(j).getTerm()))) {
                                                    System.out.println("Найдено совпадение: " + termList.get(j).getTerm());
                                                    iterator.remove();
                                                    break;
                                                }
                                            }
                                        }
                                        if (!abbreviationInText.isEmpty()) {
                                            errors.add("В тексте найдены аббревиатуры, которые не указаны в списке терминов: " +
                                                    abbreviationInText);
                                        }
//                                        System.out.println("Осталось: ");
//                                        for (String abbr : abbreviationInText) {
//                                            System.out.println(abbr);
//                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            return errors;
        } catch (
                Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
