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
                    XWPFStyles xwpfStyles = style.getStyles();
                    Section section = new Section();                    //раздел
                    List<String> sectionRaragraphs = new ArrayList<>(); //лист абзацев не заголовков
                    if (style != null) {                                //если у абзаца есть название стиля
                        if (style.getName().startsWith("heading")) {    //если у абзаца название стиля начинается с "heading"
                            section.setTitle(paragraphs.get(i).getText()); //название заголовка
                            section.setLevel(Integer.parseInt(paragraphs.get(i).getStyleID())); //уровень заголовка = ID стиля
                            if (section.getLevel() == currentLvl) {     //если уровень заголовка равен текущему
                                section.setParentHeader(currentParentHeader); //у заголовка раздел выше это currentParentHeader
                                currentParentHeader.getSubheadersList().add(section); //этому разделу выше добавить заголовок
                            } else if (section.getLevel() > currentLvl) { //иначе если уровень зоголовка больше текущего(начался подраздел)
                                if (section.getLevel() > currentLvl + 1) { //но не более чем на единицу
                                    throw new Exception("Wrong header level. Cannot continue. Please, correct header levels");
                                }
                                section.setParentHeader(currentHeader); //заголовку присвоить раздел выше текущий
                                currentHeader.getSubheadersList().add(section); //а текущему заголовку добавить раздел
                                currentParentHeader = currentHeader;    //теперь раздел выше это текущий
                                currentLvl++;                           //уровень прибавить
                            } else if (section.getLevel() < currentLvl) { //если уровень заголовка юольше текущего, значит перешли на новы раздел выше уровеня
                                while (currentParentHeader.getLevel() >= section.getLevel()) { //пока раздел уровня выше не станет >= уровню заголовка
                                    currentParentHeader = currentParentHeader.getParentHeader(); //разделу уровня выше присвоить его высший раздел
                                    currentLvl--;                       //текущий уровень уменьшить
                                }
                                section.setParentHeader(currentParentHeader); //у заголвка новый высший раздел
                                currentParentHeader.getSubheadersList().add(section); //этому разделу добавить заголовок
                            }
                            currentHeader = section;                    //текущим становится заголовок,с которым работали
                        } else {
                            sectionRaragraphs.add(paragraphs.get(i).getText());
                        }
                        section.setContent(sectionRaragraphs);
                        gqw.getSections().add(section);                //у документа новая секция
                    }
                }
            }
            for (int i = 0; i < gqw.getSections().size(); i++) {
                System.out.println(gqw.getSections().get(i).printTitle());
                for (int j = 0; j < gqw.getSections().get(i).getContent().size(); j++) {
                    System.out.println(gqw.getSections().get(i).getContent().get(j));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
