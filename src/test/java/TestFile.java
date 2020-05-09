
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;
import td.Validator;
import td.domain.Term;
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

    private Path docPath = Paths.get("src/test/doc/fullVKR.docx");
    private static final String XML_FILE = "src/main/java/td/xml/generatedXml.xml";
    private static final String SCHEMA_FILE = "src/main/java/td/xml/VKRBschema.xsd";
    private static final String VKRB_SCHEMA = "src/main/java/td/xml/VKRBschema.xsd";
    private static final String FULL_VKRB_SCHEMA = "src/main/java/td/xml/VKRBfullSchema.xsd";

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
        try {
            validator.validate("ВКРБ", Paths.get("src/test/doc/fullVKR.docx"), true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
