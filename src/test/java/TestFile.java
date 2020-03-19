import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.junit.jupiter.api.Test;
import td.templates.HeaderLevel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class TestFile {

    @Test
    public void testfile() {
        Path path = Paths.get("src/test/doc/gqw.docx");
        try {
            XWPFDocument document = new XWPFDocument(Files.newInputStream(path));
            List<XWPFParagraph> paragraphs = document.getParagraphs();

            for (XWPFParagraph paragraph : paragraphs) {
                if (paragraph.getStyleID() != null && HeaderLevel.LEVEL_1.equals(document.getStyles().getStyle(paragraph.getStyleID()).getName())) {
                    System.out.println("Title: " + paragraph.getText());

                    for (int i = 0; i < paragraph.getRuns().size(); i++) {
                        XWPFRun run = paragraph.getRuns().get(i); // == paragraph.getText()
                        String font = run.getFontName(); // NULL
                        Integer size = run.getFontSize(); // -1
                        System.out.println("Run: " + run);
                        System.out.println("Font: " + font);
                        System.out.println("Size: " + size);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
