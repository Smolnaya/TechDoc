import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
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

public class TempTest {
    @Test
    public void temptest() {
        Path path = Paths.get("src/test/doc/gqw.docx");
        try (XWPFDocument document = new XWPFDocument(Files.newInputStream(path))) {
            List<XWPFParagraph> paragraphs = document.getParagraphs();
//            document.
//            ParagraphAlignment.CENTER
//            for (XWPFParagraph para : paragraphs) {
//
//                for (XWPFRun run : para.getRuns()) {
//                    System.out.println("Current run IsBold : " + run.isBold());
//                    System.out.println("Current run IsItalic : " + run.isItalic());
//                    System.out.println("Current Font Size : " + run.getFontSize());
//                    System.out.println("Current Font Name : " + run.getFontName());
//                }
//            }
//            List<List<XWPFRun>> runs = null;
            for (XWPFParagraph paragraph : paragraphs) {
                if (paragraph.getStyleID() != null && HeaderLevel.LEVEL_1.equals(document.getStyles().getStyle(paragraph.getStyleID()).getName())) {
                    System.out.println("Name: " + paragraph.getText());
//                    runs.add(paragraph.getRuns());
//
                    for (XWPFParagraph xwpfParagraph : paragraphs) {
                        //                        for (int n = 0; n < xwpfParagraph.getRuns().size(); n++) {
//                            XWPFRun run = xwpfParagraph.getRuns().get(n);
////                            String font = run.getFontName();
////                            System.out.println("font: " + font);
////                            Integer size = run.getFontSize();
////                            System.out.println("size: " + size);
//
//                            int fontSize = run.getFontSize();
//                            if (fontSize == -1) {
//                                System.out.println(document.getStyles().getDefaultRunStyle().getFontSize());
//                            } else {
//                                System.out.println(run.getFontSize());
//                            }
//                        }
                    }
                }
//                if (runs != null) {
//                    System.out.println("Runs: " + runs);
//                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
