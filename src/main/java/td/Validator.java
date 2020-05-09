package td;

import org.xml.sax.SAXException;
import td.domain.WordDocument;
import td.services.DocumentValidator;
import td.services.WordDocumentCreator;
import td.services.XmlFileCreator;
import td.services.XmlValidator;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Validator {
    private Logger log = Logger.getLogger(getClass().getName());
    private WordDocumentCreator wordDocumentCreator = new WordDocumentCreator();
    private XmlFileCreator xmlCreator = new XmlFileCreator();
    private static final String XML_FILE = "src/main/java/td/xml/generatedXml.xml";
    private static final String VKRB_SCHEMA = "src/main/java/td/xml/VKRBschema.xsd";
    private static final String FULL_VKRB_SCHEMA = "src/main/java/td/xml/VKRBfullSchema.xsd";

    private List<String> report = new ArrayList<>();

    public List<String> validate(String docType, Path docPath, Boolean isValidateAbbreviation) {
        String schema = "";
        if (docType.equals("Дипломная работа")) {
            schema = VKRB_SCHEMA;
        } else if (docType.equals("ВКРБ")) {
            schema = FULL_VKRB_SCHEMA;
        }
        WordDocument document = validateDocModel(docPath);
        if (!document.getSections().isEmpty()) {
            if (validateXml(schema)) {
                if (validateRules(Paths.get(schema), document, isValidateAbbreviation)) {
                    log.log(Level.INFO, "Validation - true!");
                    return report;
                } else return report;
            } else return report;
        } else return report;
    }

    private WordDocument validateDocModel(Path docPath) {
        try {
            WordDocument document = wordDocumentCreator.createNewDocument(docPath);
            xmlCreator.createNewXmlTree(document);
            log.log(Level.INFO, "First validation - true");
            report.add("Модель документа успешно собрана.");
            return document;
        } catch (Exception e) {
            log.log(Level.WARNING, e.getMessage());
            report.add(e.getMessage());
            return new WordDocument();
        }
    }

    private Boolean validateXml(String schema) {
        XmlValidator XMLValidator = new XmlValidator();
        try {
            boolean valid = XMLValidator.validate(XML_FILE, schema);
            log.log(Level.INFO, "Second validation - " + valid);
            report.add("Документ соотвествует схеме.");
            return valid;
        } catch (SAXException | IOException e) {
            log.log(Level.WARNING, "Wrong structure.\n" + e.getMessage());
            report.add("Документ не соотвествует схеме." + e.getMessage());
            return false;
        }
    }

    public Boolean validateRules(Path schema, WordDocument document, Boolean isValidateAbbreviation) {
        DocumentValidator documentValidator = new DocumentValidator();
        List<String> errors = documentValidator.validate(document, schema, isValidateAbbreviation);
        if (errors.isEmpty()) {
            log.log(Level.INFO, "Third validation - true");
            report.add("Документ соответствует требованиям.");
            return true;
        } else {
            log.log(Level.WARNING, "Third validation - false");
            for (int i = 0; i < errors.size(); i++) {
                log.log(Level.WARNING, errors.get(i));
                report.add(errors.get(i));
            }
            return false;
        }
    }
}
