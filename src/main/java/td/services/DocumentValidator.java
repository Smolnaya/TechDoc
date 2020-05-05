package td.services;

import td.domain.WordDocument;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DocumentValidator {
    private XmlRulesGetter xmlRules = new XmlRulesGetter();

    public List<String> validate(WordDocument document, Path schema) {
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
                                        errors.add("Wrong title: '" + document.getSections().get(i).getTitle().trim()
                                                + "' expected: '" + stringEntry.getValue() + "'.");
                                    }
                                }

                                //неточное совпадение заголовка
                                if (stringEntry.getKey().equals("titleContains")) {
                                    if (!document.getSections().get(i).getTitle().trim().contains(stringEntry.getValue())) {
                                        errors.add("Wrong title: '" + document.getSections().get(i).getTitle().trim()
                                                + "' expected to meet in the title: '" + stringEntry.getValue() + "'.");
                                    }
                                }

                                //неточное совпадание заголовка последнего раздела 2 уровня
                                if (stringEntry.getKey().equals("lastSubsectionTitle")) {
                                    if(!document.getSections().get(i).getSubheadersList().isEmpty()) {
                                        int lastSection = document.getSections().get(i).getSubheadersList().size() - 1;
                                        String title = document.getSections().get(i).getSubheadersList().get(lastSection).getTitle().trim();
                                        if (!title.contains(stringEntry.getValue())) {
                                            errors.add("Wrong last subsection title: '" + title
                                                    + "' expected to meet in the title: '" + stringEntry.getValue() + "'.");
                                        }
                                    }
                                }

                                //точное количество подразделов
                                if (stringEntry.getKey().equals("subsectionQuantity")) {
                                    if(document.getSections().get(i).getSubheadersList().size() !=
                                        Integer.parseInt(stringEntry.getValue())) {
                                        errors.add("Wrong subsections quantity of section '" + document.getSections().get(i).getTitle() +
                                                "': '" + document.getSections().get(i).getSubheadersList().size() +
                                                "' expected: '" + stringEntry.getValue() + "'.");
                                    }
                                }

                                //минимальное количество подразделов
                                if (stringEntry.getKey().equals("minSubsectionQuantity")) {
                                    if(document.getSections().get(i).getSubheadersList().size() <
                                            Integer.parseInt(stringEntry.getValue())) {
                                        errors.add("Less subsections quantity of section '" + document.getSections().get(i).getTitle() +
                                                "': '" + document.getSections().get(i).getSubheadersList().size() +
                                                "' expected at least " + stringEntry.getValue() + ".");
                                    }
                                }

                                //максимальное количество подразделов
                                if (stringEntry.getKey().equals("maxSubsectionQuantity")) {
                                    if(document.getSections().get(i).getSubheadersList().size() >
                                            Integer.parseInt(stringEntry.getValue())) {
                                        errors.add("More subsections quantity of section '" + document.getSections().get(i).getTitle() +
                                                "': '" + document.getSections().get(i).getSubheadersList().size() +
                                                "' expected as high " + stringEntry.getValue() + ".");
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return errors;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
