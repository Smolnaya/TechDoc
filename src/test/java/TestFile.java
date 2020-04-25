import com.sun.org.apache.xpath.internal.NodeSet;
import org.apache.xmlbeans.SchemaAnnotation;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import td.domain.WordDocument;
import td.services.Service;
import td.services.XmlAnnotationGetter;
import td.services.XmlFileCreator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
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
            NodeList temp = document.getChildNodes().item(0).getChildNodes();
            //Get DOM
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document xml = db.parse(new ByteArrayInputStream(xsdDocument.toString().getBytes()));

            //Get XPath
            XPathFactory xpf = XPathFactory.newInstance();
            XPath xpath = xpf.newXPath();

            String allRules = (String) xpath.evaluate(".//element[@name='document']/annotation/appinfo/text()", xml, XPathConstants.STRING);
            System.out.println(allRules);
            //todo: заполнить map правил Map<String, Map<String, String>>


            NodeList nodes = (NodeList) xpath.evaluate(".//element[@name='document']//sequence/element/@ourRule", xml, XPathConstants.NODESET);
            for (int i = 0; i < nodes.getLength(); i++) {
                System.out.println(i + " " + nodes.item(i).getNodeValue());
            }
            //todo: заполнить лист

        } catch (Exception e) {
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
