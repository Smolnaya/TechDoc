import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

public class Create {
    public void addNewDocument(List<Integer> list) throws TransformerFactoryConfigurationError, DOMException {
        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document xmlDocument = documentBuilder.parse("src/main/java/td/xml/structure.xml");
            int currentLevel = 1;
            Element parentElement = xmlDocument.getDocumentElement();
            Element currentElement = xmlDocument.createElement("section");
            for (int i = 0; i < list.size(); i++) {
                if (currentLevel == list.get(i)) {
                    Element section = xmlDocument.createElement("section");
                    section.setAttribute("level", Integer.toString(list.get(i)));
                    parentElement.appendChild(section);
                    currentElement = section;
                } else if (currentLevel < list.get(i)) {
                    parentElement = currentElement;
                    Element section = xmlDocument.createElement("section");
                    section.setAttribute("level", Integer.toString(list.get(i)));
                    parentElement.appendChild(section);
                    currentLevel++;
                }
                else if (currentLevel > list.get(i)) {
                    while (Integer.parseInt(parentElement.getAttribute("level")) >= list.get(i)) {
                        parentElement = (Element) parentElement.getParentNode();
                        currentLevel--;
                    }
                    Element section = xmlDocument.createElement("section");
                    section.setAttribute("level", Integer.toString(list.get(i)));
                    parentElement.appendChild(section);
                    currentElement = section;
                }
            }
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
            tr.setOutputProperty(OutputKeys.INDENT, "yes");
            tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            tr.transform(source, result);
        } catch (TransformerException | IOException e) {
            e.printStackTrace(System.out);
        }
    }
}
