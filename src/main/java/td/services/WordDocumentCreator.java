package td.services;

import org.apache.poi.openxml4j.exceptions.NotOfficeXmlFileException;
import org.apache.poi.xwpf.usermodel.*;
import td.domain.WordDocument;
import td.domain.Section;
import td.exceptions.LackOfHeadingsException;
import td.exceptions.WrongLevelException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WordDocumentCreator {

    public WordDocument createNewDocument(Path path) throws Exception {
        try {
            XWPFDocument document = new XWPFDocument(Files.newInputStream(path));
            List<XWPFParagraph> paragraphs = document.getParagraphs();
            List<XWPFTable> table = document.getTables();
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
                        if (style.getName().toLowerCase(Locale.ROOT).startsWith("heading")) {
                            Section heading = new Section();
                            heading.setTitle(paragraphs.get(i).getText());
                            int level = 0;
                            Pattern pattern = Pattern.compile("\\d+");
                            Matcher matcher = pattern.matcher(styleid);
                            if (matcher.find()) {
                                level = Integer.parseInt(matcher.group());
                            }
                            heading.setLevel(level);
                            if (heading.getLevel() == currentLvl) {
                                heading.setParentHeader(currentParentHeader);
                                currentParentHeader.getSubheadersList().add(heading);
                            } else if (heading.getLevel() > currentLvl) {
                                if (heading.getLevel() > currentLvl + 1) {
                                    throw new WrongLevelException("Неверный уровень раздела. Для продолжения необходимо исправить уровни.");
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
                            if (currentLvl == 1) {
                                doc.getSections().add(heading);
                            }
                            currentHeader = heading;
                        } else if (currentHeader != null && !paragraphs.get(i).getText().equals("")) {
                            currentHeader.getContent().add((paragraphs.get(i).getText()));
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

    public List<Integer> findStaticHeaders(WordDocument wordDocument) throws LackOfHeadingsException {
        if (wordDocument.getSections().size() > 0) {
            List<Section> sections = wordDocument.getSections();
            List<Integer> resultList = new ArrayList<>();
            for (int i = 0; i < sections.size(); i++) {
                if (sections.get(i).getTitle().equals("ВВЕДЕНИЕ")) {
                    resultList.add(1);
                } else if (sections.get(i).getTitle().equals("ЗАКЛЮЧЕНИЕ")) {
                    resultList.add(2);
                } else if (sections.get(i).getTitle().equals("СПИСОК ИСПОЛЬЗОВАННЫХ ИСТОЧНИКОВ") ||
                        sections.get(i).getTitle().equals("СПИСОК ИСТОЧНИКОВ") ||
                        sections.get(i).getTitle().equals("СПИСОК ЛИТЕРАТУРЫ") ||
                        sections.get(i).getTitle().equals("СПИСОК ИСПОЛЬЗОВАННОЙ ЛИТЕРАТУРЫ") ||
                        sections.get(i).getTitle().equals("СПИСОК ИСПОЛЬЗУЕМОЙ ЛИТЕРАТУРЫ") ||
                        sections.get(i).getTitle().equals("БИБЛИОГРАФИЧЕСКИЙ СПИСОК")) {
                    resultList.add(3);
                } else if (sections.get(i).getTitle().equals("ПРИЛОЖЕНИЯ")) {
                    resultList.add(4);
                }
            }
            return resultList;
        } else {
            throw new LackOfHeadingsException("There are no headings in the document.");
        }
    }
}
