import org.apache.poi.xwpf.usermodel.*;
import org.junit.jupiter.api.Test;
import td.domain.Section;
import td.domain.Document;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class TestFile {

    @Test
    public void testfile() throws Exception {
        Path path = Paths.get("src/test/doc/gqw.docx");
        try {
            XWPFDocument document = new XWPFDocument(Files.newInputStream(path));
            List<XWPFParagraph> paragraphs = document.getParagraphs();
            XWPFStyles styles = document.getStyles();
            Document gqw = new Document();
            gqw.setTitle(path.getFileName().toString());
            int currentLvl = 1;                                         //текущий уровень
            Section currentParentHeader = gqw.getRootHeader();          //раздел выше на уровень
            Section currentHeader = null;                               //текущий раздел
            for (int i = 0; i < paragraphs.size(); i++) {               //перебор абзацев документа
                if (paragraphs.get(i).getStyleID() != null) {           //если у абзаца есть стиль
                    String styleid = paragraphs.get(i).getStyleID();    //ID стиля
                    XWPFStyle style = styles.getStyle(styleid);         //название стиля
                    if (style != null) {                                //если у абзаца есть название стиля
                        if (style.getName().startsWith("heading")) {    //если у абзаца название стиля начинается с "heading"
                            Section heading = new Section();            //новый заголовок
                            heading.setTitle(paragraphs.get(i).getText()); //название заголовка
                            heading.setLevel(Integer.parseInt(paragraphs.get(i).getStyleID())); //уровень заголовка = ID стиля
                            if (heading.getLevel() == currentLvl) {     //если уровень заголовка равен текущему
                                heading.setParentHeader(currentParentHeader); //у заголовка раздел выше это currentParentHeader
                                currentParentHeader.getSubheadersList().add(heading); //этому разделу выше добавить заголовок
                            } else if (heading.getLevel() > currentLvl) { //иначе если уровень зоголовка больше текущего(начался подраздел)
                                if (heading.getLevel() > currentLvl + 1) { //но не более чем на единицу
                                    throw new Exception("Wrong header level. Cannot continue. Please, correct header levels");
                                }
                                heading.setParentHeader(currentHeader); //заголовку присвоить раздел выше текущий
                                currentHeader.getSubheadersList().add(heading); //а текущему заголовку добавить раздел
                                currentParentHeader = currentHeader;    //теперь раздел выше это текущий
                                currentLvl++;                           //уровень прибавить
                            } else if (heading.getLevel() < currentLvl) { //если уровень заголовка юольше текущего, значит перешли на новы раздел выше уровеня
                                while (currentParentHeader.getLevel() >= heading.getLevel()) { //пока раздел уровня выше не станет >= уровню заголовка
                                    currentParentHeader = currentParentHeader.getParentHeader(); //разделу уровня выше присвоить его высший раздел
                                    currentLvl--;                       //текущий уровень уменьшить
                                }
                                heading.setParentHeader(currentParentHeader); //у заголвка новый высший раздел
                                currentParentHeader.getSubheadersList().add(heading); //этому разделу добавить заголовок
                            }
                            gqw.getSections().add(heading);              //у документа новая секция
                            currentHeader = heading;                    //текущим становится заголовок,с которым работали
                        } else if (currentHeader != null) {            //если не заголовок, а текст под заголовком, добавить
                            gqw.getSections().get(gqw.getSections().size() - 1).getContent().add(paragraphs.get(i).getText());
                        }
                    }
                }
            }
            for (int i = 0; i < gqw.getSections().size(); i++) {
                System.out.println("\nHeader: " + gqw.getSections().get(i).printTitle());
                if (gqw.getSections().get(i).getContent().size() > 0) {
                    System.out.println("Content of header:");
                    for (int j = 0; j < gqw.getSections().get(i).getContent().size(); j++) {
                        System.out.println(gqw.getSections().get(i).getContent().get(j));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
