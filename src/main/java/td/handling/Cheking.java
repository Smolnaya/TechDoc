package td.handling;

import org.apache.poi.openxml4j.exceptions.NotOfficeXmlFileException;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFStyle;
import org.apache.poi.xwpf.usermodel.XWPFStyles;
import td.domain.WordDocument;
import td.domain.Section;
import td.exceptions.WrongLevelException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Cheking {

    public Cheking() {
    }

    public WordDocument checkHeadersLevel(Path path) throws Exception {
//        Path path = Paths.get("src/test/doc/gqw.docx");
        try {
            XWPFDocument document = new XWPFDocument(Files.newInputStream(path));
            List<XWPFParagraph> paragraphs = document.getParagraphs();
            XWPFStyles styles = document.getStyles();
            WordDocument doc = new WordDocument();
            doc.setTitle(path.getFileName().toString());
            int currentLvl = 1;
            Section currentParentHeader = doc.getRootHeader();
            Section currentHeader = null;
            for (int i = 0; i < paragraphs.size(); i++) {
                if (paragraphs.get(i).getStyleID() != null) {
                    String styleid = paragraphs.get(i).getStyleID();
                    XWPFStyle style = styles.getStyle(styleid);
                    if (style != null) {
                        if (style.getName().startsWith("heading")) {
                            Section heading = new Section();
                            heading.setTitle(paragraphs.get(i).getText());
                            heading.setLevel(Integer.parseInt(paragraphs.get(i).getStyleID()));
                            if (heading.getLevel() == currentLvl) {
                                heading.setParentHeader(currentParentHeader);
                                currentParentHeader.getSubheadersList().add(heading);
                            } else if (heading.getLevel() > currentLvl) {
                                if (heading.getLevel() > currentLvl + 1) {
                                    throw new WrongLevelException("Wrong header level. Cannot continue. Please, correct header levels");
                                }
                                heading.setParentHeader(currentHeader);
                                currentHeader.getSubheadersList().add(heading);
                                currentParentHeader = currentHeader;
                                currentLvl++;
                            } else if (heading.getLevel() < currentLvl) {
                                while (currentParentHeader.getLevel() >= heading.getLevel()) {
                                    currentParentHeader = currentParentHeader.getParentHeader();
                                    currentLvl--;
                                }
                                heading.setParentHeader(currentParentHeader);
                                currentParentHeader.getSubheadersList().add(heading);
                            }
                            doc.getSections().add(heading);
                            currentHeader = heading;
                        } else if (currentHeader != null) {
                            doc.getSections().get(doc.getSections().size() - 1).getContent().add(paragraphs.get(i).getText());
                        }
                    }
                }
            }
            return doc;
        } catch (IOException | NotOfficeXmlFileException e) {
            e.printStackTrace();
            return null;
        }
    }
}
