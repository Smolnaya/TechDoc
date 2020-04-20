import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class XMLValidator {
    public static final String XML_FILE = "src/main/java/td/xml/newstructure.xml";
    public static final String SCHEMA_FILE = "src/main/java/td/xml/test2.xsd";

    public static void main(String[] args) {
        XMLValidator XMLValidator = new XMLValidator();
        boolean valid = XMLValidator.validate();
        System.out.printf("%s validation = %b.", XML_FILE, valid);
    }

    private boolean validate() {
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        try {
            Schema schema = schemaFactory.newSchema(new File(SCHEMA_FILE));
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(new File(XML_FILE)));
            return true;
        } catch (SAXException | IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}