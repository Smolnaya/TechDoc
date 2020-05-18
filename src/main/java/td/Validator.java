package td;

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
    private Logger log = Logger.getLogger(getClass().getName());
    private WordDocumentCreator wordDocumentCreator = new WordDocumentCreator();
    private XmlFileCreator xmlCreator = new XmlFileCreator();
    private static final String XML_FILE = "templates/generatedXml.xml";
    private static final String FULL_VKRB_SCHEMA = "templates/VKRB.xsd";
    private static final String GOST34_602_89 = "templates/GOST34_602_89.xsd";
    private static final String RD_50_34_698_90 = "templates/RD_50_34_698_90.xsd";

    private List<String> report = new ArrayList<>();

    public List<String> validate(String docType, Path docPath, Map<String, String> userGeneralRules,
                                 Map<String, Map<String, String>> userSectionRules) {
        String schema = "";
        if (docType.equals("ВКРБ")) {
            schema = FULL_VKRB_SCHEMA;
        } else if (docType.equals("Техническое задание")) {
            schema = GOST34_602_89;
        } else if (docType.equals("Руководство пользователя")) {
            schema = RD_50_34_698_90;
        }
        WordDocument document = validateDocModel(docPath);
        if (!document.getSections().isEmpty()) {
            System.out.println(document.getSections().get(0).getContent());
            for (int i = 0; i < document.getSections().size(); i++) {
            }
            if (validateXml(schema)) {
                if (validateRules(Paths.get(schema), document, userGeneralRules, userSectionRules)) {
                    log.log(Level.INFO, "Validation - true!");
                    return report;
                } else {
                    return report;
                }
            } else {
                return report;
            }
        } else {
            return report;
        }
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
            for (int i = 0; i < errors.size(); i++) {
                log.log(Level.WARNING, errors.get(i));
                report.add(errors.get(i));
            }
            return false;
        }
    }
}
