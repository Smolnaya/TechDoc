package td.handling;

import java.io.FileOutputStream;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import td.domain.WordDocument;

public class CreateXML {
    public void addNewDocument(WordDocument wordDocument) throws TransformerFactoryConfigurationError, DOMException {
        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document xmlDocument = documentBuilder.parse("src/main/java/td/xml/structure.xml");
            Node root = xmlDocument.getDocumentElement();
            Element file = xmlDocument.createElement("file");

            for (int i = 0; i < wordDocument.getSections().size(); i++) {
                Element section = xmlDocument.createElement("section");

                Element level = xmlDocument.createElement("level");
                level.setTextContent(Integer.toString(wordDocument.getSections().get(i).getLevel()));
                section.appendChild(level);

                Element title = xmlDocument.createElement("title");
                title.setTextContent(wordDocument.getSections().get(i).getTitle());
                section.appendChild(title);

                if (wordDocument.getSections().get(i).getContent().size() > 0) {
                    Element content = xmlDocument.createElement("content");
                    String str = "";
                    for (int j = 0; j < wordDocument.getSections().get(i).getContent().size(); j++) {
                        str += wordDocument.getSections().get(i).getContent().get(j) + "\n";
                    }
                    content.setTextContent(str);
//                    content.setTextContent(wordDocument.getSections().get(i).getContent().toString());
                    section.appendChild(content);
                }
                file.appendChild(section);
            }

            root.appendChild(file);
            writeDocument(xmlDocument);
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            ex.printStackTrace(System.out);
        }
    }

    private static void writeDocument(Document document) throws TransformerFactoryConfigurationError {
        try {
            Transformer tr = TransformerFactory.newInstance().newTransformer();
            DOMSource source = new DOMSource(document);
            FileOutputStream fos = new FileOutputStream("src/main/java/td/xml/newstructure.xml");
            StreamResult result = new StreamResult(fos);
            tr.transform(source, result);
        } catch (TransformerException | IOException e) {
            e.printStackTrace(System.out);
        }
    }
}
