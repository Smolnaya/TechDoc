
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;
import td.Validator;
import td.domain.WordDocument;
import td.services.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestFile {
    private WordDocumentCreator wordDocumentCreator = new WordDocumentCreator();
    private Validator validator = new Validator();
    private XmlFileCreator createXML = new XmlFileCreator();
    private DocumentValidator documentValidator = new DocumentValidator();
    private Logger log = Logger.getLogger(getClass().getName());

    private Path docPath = Paths.get("src/test/doc/gqw.docx");
    private static final String XML_FILE = "src/main/java/td/xml/generatedXml.xml";
    private static final String SCHEMA_FILE = "src/main/java/td/xml/schema.xsd";

    @Test
    public void tryAbbr() {
        try {
            Boolean errors = AbbreviationValidator.validAbbreviations(docPath.toFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void validate() {
        List<String> report = validator.validate("Дипломная работа", docPath);
        for (String s : report) {
            System.out.println(s);
        }
    }

    //Третья проверка: соответствие созданного WordDocument правилам из схемы
    @Test
    public void thirdValidate() {
        List<String> errors = null;
        try {
            errors = documentValidator.validate(wordDocumentCreator.createNewDocument(docPath), Paths.get(SCHEMA_FILE));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (errors.isEmpty()) {
            System.out.println("Good document");
        } else {
            for (int i = 0; i < errors.size(); i++) {
                System.out.println(errors.get(i));
            }
        }
    }

    //Вторая проверка: соответствие созданной xml при первой проверке на точное совпадение схеме
    @Test
    public void secondValidate() {
        XmlValidator XMLValidator = new XmlValidator();
        try {
            boolean valid = XMLValidator.validate(XML_FILE, SCHEMA_FILE);
            log.log(Level.INFO, "secondValidate = true");
        } catch (SAXException | IOException e) {
            log.log(Level.WARNING, "Wrong structure.\n" + e.getMessage());
        }
    }

    //Первая проверка: дерево документа, если структура правильная - создается xml
    @Test
    public void firstValidate() {
        try {
            WordDocument document = wordDocumentCreator.createNewDocument(docPath);
            createXML.createNewXmlTree(document);
            log.log(Level.INFO, "firstValidate = true");
        } catch (Exception e) {
            log.log(Level.WARNING, e.getMessage());
        }
    }

    @Test
    public void checkGetXmlRules() {
        XmlRulesGetter rules = new XmlRulesGetter();

        System.out.println("Document rules: ");
        for (Map.Entry<String, Map<String, String>> entry : rules.getDocumentRules(Paths.get(SCHEMA_FILE)).entrySet()) {
            System.out.println("key =  " + entry.getKey() + "\nvalue = " + entry.getValue());
        }

        System.out.println("Section kinds: ");
        List<String> sectionKindList = rules.getSectionKind(Paths.get(SCHEMA_FILE));
        for (int i = 0; i < sectionKindList.size(); i++) {
            System.out.println(sectionKindList.get(i));
        }
    }
}
