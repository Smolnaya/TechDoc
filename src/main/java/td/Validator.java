package td;

import org.xml.sax.SAXException;
import td.domain.WordDocument;
import td.services.DocumentValidator;
import td.services.WordDocumentCreator;
import td.services.XmlFileCreator;
import td.services.XmlValidator;

import java.io.File;
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
    private XmlFileCreator createXML = new XmlFileCreator();
    private static final String XML_FILE = "src/main/java/td/xml/generatedXml.xml";
    private static final String SCHEMA_FILE = "src/main/java/td/xml/schema.xsd";

    private List<String> report = new ArrayList<>();

    public List<String> validate(String docType, Path docPath) {
        String schema = "";
        if (docType.equals("Дипломная работа")) {
            schema = SCHEMA_FILE;
        }
        WordDocument document = firstValidate(docPath);
        if (!document.getSections().isEmpty()) {
            if (secondValidate(schema)) {
                if (thirdValidate(Paths.get(schema), document)) {
                    log.log(Level.INFO, "Validation - true!");
                    return report;
                } else return report;
            } else return report;
        } else return report;
    }

    public WordDocument firstValidate(Path docPath) {
        try {
            WordDocument document = wordDocumentCreator.createNewDocument(docPath);
            createXML.createNewXmlTree(document);
            log.log(Level.INFO, "First validation - true");
            report.add("Модель документа успешно собрана.");
            return document;
        } catch (Exception e) {
            log.log(Level.WARNING, e.getMessage());
            report.add(e.getMessage());
            return new WordDocument();
        }
    }

    public Boolean secondValidate(String schema) {
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

    public Boolean thirdValidate(Path schema, WordDocument document) {
        DocumentValidator documentValidator = new DocumentValidator();
        List<String> errors = documentValidator.validate(document, schema);
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
