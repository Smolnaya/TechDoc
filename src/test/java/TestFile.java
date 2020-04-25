import org.apache.xmlbeans.SchemaAnnotation;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import td.domain.WordDocument;
import td.services.Service;
import td.services.XmlAnnotationGetter;
import td.services.XmlFileCreator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class TestFile {
    private Path path = Paths.get("src/test/doc/gqw.docx");
    private Service service = new Service();
    private Path xsdPath = Paths.get("src/main/java/td/xml/schema.xsd");

    @Test
    public void checkGetAnnotation() {
        try {
            XmlObject xsdDocument = XmlObject.Factory.parse(xsdPath.toFile());
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = builder.parse(new ByteArrayInputStream(xsdDocument.toString().getBytes()));
            NodeList list = document.getChildNodes();
            Node node = list.item(0);

            if (node.getFirstChild() != null && node.getFirstChild().getNodeType() == Node.TEXT_NODE) {
                System.out.println(node.getFirstChild().getTextContent());
            }
        } catch (XmlException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void checkHeadersText() {
        try {
            WordDocument document = service.checkHeadersLevel(path);
            List<Integer> staticHeaders = service.findStaticHeaders(document);
            for (int i = 0; i < staticHeaders.size(); i++) {
                System.out.println(staticHeaders.get(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void xml() {
        try {
            WordDocument document = service.checkHeadersLevel(path);
            XmlFileCreator createXML = new XmlFileCreator();
            createXML.addNewDocument(document);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void checking() {
        try {
            WordDocument document = service.checkHeadersLevel(path);
            System.out.println("Headers: ");
            document.printDocumentHeaders();
            System.out.println("\nContent: ");
            document.printDocumentContent();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
