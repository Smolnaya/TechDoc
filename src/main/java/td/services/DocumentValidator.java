package td.services;

import td.domain.Section;
import td.domain.WordDocument;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DocumentValidator {
    private WordDocumentCreator wordDocumentCreator = new WordDocumentCreator();
    private XmlRulesGetter xmlRules = new XmlRulesGetter();

    public List<String> validate(Path docPath, Path schema) {
        try {
            //Create WordDocument
            WordDocument document = wordDocumentCreator.createNewDocument(docPath);

            //Create xmlRules
            Map<String, Map<String, String>> documentRules = xmlRules.getDocumentRules(schema);
            List<String> sectionKindList = xmlRules.getSectionKind(schema);

            //Check errors
            List<String> errors = new ArrayList<>();
            for (int i = 0; i < document.getSections().size(); i++) {
                if (documentRules.containsKey(sectionKindList.get(i))) {
                    for (Map.Entry<String, Map<String, String>> entry : documentRules.entrySet()) {
                        if (entry.getKey().equals(sectionKindList.get(i))) {
                            Map<String, String> rules = entry.getValue();
                            for (Map.Entry<String, String> stringEntry : rules.entrySet()) {
                                if (stringEntry.getKey().equals("title")) {
                                    if (!document.getSections().get(i).getTitle().trim().equals(stringEntry.getValue())) {
                                        errors.add("wrong title: " + document.getSections().get(i).getTitle()
                                                + "\texpected: " + stringEntry.getValue());
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
