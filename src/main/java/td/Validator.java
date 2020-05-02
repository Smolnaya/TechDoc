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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Validator {
    private Logger log = Logger.getLogger(getClass().getName());
    private WordDocumentCreator wordDocumentCreator = new WordDocumentCreator();
    private XmlFileCreator createXML = new XmlFileCreator();
    private static final String XML_FILE = "src/main/java/td/xml/generatedXml.xml";
    private static final String SCHEMA_FILE = "src/main/java/td/xml/schema.xsd";

    public void validate(Path docPath) {
        if (firstValidate(docPath))
            if (secondValidate())
                if(thirdValidate(docPath))
                    log.log(Level.INFO, "Validation - true!!!");
    }

    public Boolean firstValidate(Path path) {
        try {
            WordDocument document = wordDocumentCreator.createNewDocument(path);
            createXML.createNewXml(document);
            log.log(Level.INFO, "First validation - true");
            return true;
        } catch (Exception e) {
            log.log(Level.WARNING, e.getMessage());
            return false;
        }
    }

    public Boolean secondValidate() {
        XmlValidator XMLValidator = new XmlValidator();
        try {
            boolean valid = XMLValidator.validate(XML_FILE, SCHEMA_FILE);
            log.log(Level.INFO, "Second validation - " + valid);
            return valid;
        } catch (SAXException | IOException e) {
            log.log(Level.WARNING, "Wrong structure.\n" + e.getMessage());
            return false;
        }
    }

    public Boolean thirdValidate(Path path) {
        DocumentValidator documentValidator = new DocumentValidator();
        List<String> errors = documentValidator.validate(path, Paths.get(SCHEMA_FILE));
        if (errors.isEmpty()) {
            log.log(Level.INFO, "Third validation - true");
            return true;
        } else {
            log.log(Level.WARNING, "Third validation - false");
            for (int i = 0; i < errors.size(); i++) {
                log.log(Level.WARNING, errors.get(i));
            }
            return false;
        }
    }
}
