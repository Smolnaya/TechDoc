
import org.junit.jupiter.api.Test;
import td.domain.WordDocument;
import td.services.WordDocumentCreator;
import td.services.XmlFileCreator;
import td.services.XmlRulesGetter;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class TestFile {
    private Path path = Paths.get("src/test/doc/gqw.docx");
    private WordDocumentCreator wordDocumentCreator = new WordDocumentCreator();
    private Path xsdPath = Paths.get("src/main/java/td/xml/schema.xsd");

    @Test
    public void checkGetXmlRules() {
        XmlRulesGetter rules = new XmlRulesGetter();

        System.out.println("Document rules: ");
        for (Map.Entry<String, Map<String, String>> entry : rules.getDocumentRules(xsdPath).entrySet()) {
            System.out.println("key =  " + entry.getKey() + "\nvalue = " + entry.getValue());
        }

        System.out.println("Section rules: ");
        List<String> sectionKindList = rules.getSectionKind(xsdPath);
        for (int i = 0; i < sectionKindList.size(); i++) {
            System.out.println(sectionKindList.get(i));
        }
    }

    @Test
    public void xml() {
        try {
            WordDocument document = wordDocumentCreator.createNewDocument(path);
            XmlFileCreator createXML = new XmlFileCreator();
            createXML.createNewXml(document);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
