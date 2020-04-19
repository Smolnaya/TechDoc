import org.junit.jupiter.api.Test;
import td.domain.WordDocument;
import td.services.Service;
import td.services.CreateXML;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class TestFile {
    private Path path = Paths.get("src/test/doc/gqw.docx");
    private Service service = new Service();

    @Test
    public void checkCreate() {
        List<Integer> list = new ArrayList<Integer>();
        list.add(1);
        list.add(1);
            list.add(2);
                list.add(3);
            list.add(2);
                list.add(3);
            list.add(2);
        list.add(1);
            list.add(2);
                list.add(3);
                    list.add(4);
            list.add(2);
        list.add(1);
            list.add(2);
            list.add(3);
        XmlFileCreator xml = new XmlFileCreator();
        xml.addNewDocument(list);
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
            CreateXML createXML = new CreateXML();
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
