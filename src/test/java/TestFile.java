import org.junit.jupiter.api.Test;
import td.domain.WordDocument;
import td.services.Service;
import td.services.XmlFileCreator;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class TestFile {
    private Path path = Paths.get("src/test/doc/gqw.docx");
    private Service service = new Service();

    @Test
    public void checkCreate() {
        XmlFileCreator xml = new XmlFileCreator();
        WordDocument document = null;
        try {
            document = service.checkHeadersLevel(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        xml.addNewDocument(document);
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
