package td.tasks;

import org.xml.sax.SAXException;
import td.domain.WordDocument;
import td.services.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Validator {
    private static final String XML_FILE = "libs/xml/generatedXml.xml";
    private final Logger log = Logger.getLogger(getClass().getName());
    private final WordDocumentCreator wordDocumentCreator = new WordDocumentCreator();
    private final XmlFileCreator xmlCreator = new XmlFileCreator();

    private final List<String> report = new ArrayList<>();

    public List<String> validate(String schema, Path docPath, Map<String, String> userGeneralRules,
                                 Map<String, Map<String, String>> userSectionRules) {

        WordDocument document = validateDocModel(docPath);
        if (!document.getSections().isEmpty()) {
            if (validateXml(schema)) {
                if (validateRules(Paths.get(schema), document, userGeneralRules, userSectionRules)) {
                    log.log(Level.INFO, "Validation - true!");
                }
            }
        }
        return report;
    }

    private WordDocument validateDocModel(Path docPath) {
        try {
            WordDocument document = wordDocumentCreator.createNewDocument(docPath);
            xmlCreator.createNewXmlTree(document);
            if (document.getSections().isEmpty()) {
                report.add("Заголовки не найдены. Примените специальные стили для заголовков.");
            } else {
                log.log(Level.INFO, "First validation - true");
                report.add("Структура документа верна.");
            }
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
            report.add("\nДокумент соотвествует шаблону.");
            return valid;
        } catch (SAXException | IOException e) {
            log.log(Level.WARNING, "Wrong structure.\n" + e.getMessage());
            report.add("\nДокумент не соотвествует шаблону." + e.getMessage());
            return false;
        }
    }

    public Boolean validateRules(Path schema, WordDocument document, Map<String, String> userGeneralRules,
                                 Map<String, Map<String, String>> userSectionRules) {
        RulesValidator rulesValidator = new RulesValidator(document, schema, userGeneralRules);
        List<String> errors = rulesValidator.validateRules(userSectionRules, userGeneralRules);
        if (errors.isEmpty()) {
            log.log(Level.INFO, "Third validation - true");
            report.add("\nДокумент соответствует требованиям.");
            return true;
        } else {
            log.log(Level.WARNING, "Third validation - false");
            for (String error : errors) {
                log.log(Level.WARNING, error);
                report.add(error);
            }
            return false;
        }
    }
}
