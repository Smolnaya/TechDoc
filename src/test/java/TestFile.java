import org.junit.jupiter.api.Test;
import td.domain.WordDocument;
import td.handling.Cheking;
import td.handling.CreateXML;

import java.nio.file.Path;
import java.nio.file.Paths;

public class TestFile {

    @Test
    public void testXML() {
        Cheking cheking = new Cheking();
        Path path = Paths.get("src/test/doc/gqw.docx");
        try {
            WordDocument document = cheking.checkHeadersLevel(path);
            CreateXML createXML = new CreateXML();
            createXML.addNewDocument(document);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCheking() {
        Cheking cheking = new Cheking();
        Path path = Paths.get("src/test/doc/gqw.docx");
        try {
            WordDocument document = cheking.checkHeadersLevel(path);
            System.out.println("Headers: ");
            document.printDocumentHeaders();
            System.out.println("\nContent: ");
            document.printDocumentContent();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
