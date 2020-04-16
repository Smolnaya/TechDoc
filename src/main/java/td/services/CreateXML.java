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

public class CreateXML {
    public void addNewDocument(WordDocument wordDocument) throws TransformerFactoryConfigurationError, DOMException {
        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document xmlDocument = documentBuilder.parse("src/main/java/td/xml/structure.xml");
            Node root = xmlDocument.getDocumentElement();
            Element file = xmlDocument.createElement("file");
            int currentLevel = 1;
            Element currentElement;
            Element parentElement = xmlDocument.createElement("section");
            for (int i = 0; i < wordDocument.getSections().size(); i++) {
                if (currentLevel == wordDocument.getSections().get(i).getLevel()) {
//                    Element section = xmlDocument.createElement("section");
                    parentElement.setAttribute("level", Integer.toString(wordDocument.getSections().get(i).getLevel()));

                    Element title = xmlDocument.createElement("title");
                    title.setTextContent(wordDocument.getSections().get(i).getTitle());
                    parentElement.appendChild(title);
                    if (wordDocument.getSections().get(i).getContent().size() > 0) {
                        Element content = xmlDocument.createElement("content");
                        String str = "";
                        for (int j = 0; j < wordDocument.getSections().get(i).getContent().size(); j++) {
                            str += wordDocument.getSections().get(i).getContent().get(j) + "\n";
                        }
                        content.setTextContent(str);
                        parentElement.appendChild(content);
                    }

                    currentElement = parentElement;
                    file.appendChild(currentElement);
                } else if (currentLevel < wordDocument.getSections().get(i).getLevel()) {
                    currentLevel++;

                } else if (currentLevel > wordDocument.getSections().get(i).getLevel()) {
//                    while ()
                }
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
            tr.setOutputProperty(OutputKeys.INDENT, "yes");
            tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            tr.transform(source, result);
        } catch (TransformerException | IOException e) {
            e.printStackTrace(System.out);
        }
    }
}
