package td.services;

import java.io.FileOutputStream;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.*;
import org.xml.sax.SAXException;
import td.domain.WordDocument;

public class XmlFileCreator {
    public void createNewXml(WordDocument wordDocument) throws TransformerFactoryConfigurationError, DOMException {
        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document xmlDocument = documentBuilder.parse("src/main/java/td/xml/structure.xml");
            Node root = xmlDocument.getDocumentElement();
            int currentLevel = 1;
            Element parentElement = xmlDocument.getDocumentElement();
            Element currentElement = xmlDocument.createElement("section");
            for (int i = 0; i < wordDocument.getSections().size(); i++) {
                Integer elementLevel = wordDocument.getSections().get(i).getLevel();
                if (currentLevel == elementLevel) {
                    Element section = xmlDocument.createElement("section");
                    section.setAttribute("level", Integer.toString(elementLevel));

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
                        section.appendChild(content);
                    }

                    parentElement.appendChild(section);
                    currentElement = section;
                } else if (currentLevel < elementLevel) {
                    parentElement = currentElement;
                    Element section = xmlDocument.createElement("section");
                    section.setAttribute("level", Integer.toString(elementLevel));

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
                        section.appendChild(content);
                    }

                    parentElement.appendChild(section);
                    currentLevel++;
                    currentElement = section;
                } else if (currentLevel > elementLevel) {
                    while (Integer.parseInt(parentElement.getAttribute("level")) >= elementLevel) {
                        parentElement = (Element) parentElement.getParentNode();
                        currentLevel--;
                    }
                    Element section = xmlDocument.createElement("section");
                    section.setAttribute("level", Integer.toString(elementLevel));

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
                        section.appendChild(content);
                    }

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
