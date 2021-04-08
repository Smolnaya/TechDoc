import com.spire.doc.FileFormat;
import com.spire.doc.fields.Comment;
import com.spire.doc.fields.TextRange;
import com.spire.doc.formatting.CharacterFormat;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.ArrayList;
import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.spire.doc.Document;
import com.spire.doc.Section;
import com.spire.doc.documents.Paragraph;

import database.models.TextRangeRule;
import database.models.ParagraphRule;


public class TestFile {
    String dbPath = "tddb.db";
    Logger log = Logger.getLogger(getClass().getName());
    String docFilePath = "src/test/java/VKR.docx";
    String xmlPath = "libs/xml/generatedXml.xml";
    String xsdPath = "libs/xsd/VKRB.xsd";

    // достать temp_elem_list
    private String getTempElemListValue(String xsd) {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath)) {
            String query = "SELECT temp_elem_list FROM templates WHERE temp_path = '%s'";
            ResultSet rs = conn.createStatement().executeQuery(String.format(query, xsd));
            if (rs.next()) {
                return rs.getString("temp_elem_list");
            } else
                log.log(Level.WARNING, "Не удалось выполнить 'getTempElemList()', пустой ответ.");
                return "";
        } catch (SQLException ex) {
            log.log(Level.WARNING, "Не удалось выполнить 'getTempElemList()'", ex);
            return "";
        }
    }

    // распарсить строку в id элементов
    private List<Integer> getElemIDList(String elemStr) {
        List<Integer> elemIDList = new ArrayList<>();
        String[] arr = elemStr.split(",");
        for (String elem : arr) {
            try {
                elemIDList.add(Integer.parseInt(elem));
            } catch (NumberFormatException nfe) {
                log.log(Level.WARNING, "", nfe);
                return new ArrayList<>();
            }
        }
        return elemIDList;
    }

    // выбрать стиль элемента у которого elem_header = level ---
    int getStyleID(int lvl) {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath)) {
            String query = "SELECT elem_style_id FROM elements WHERE elem_header = %s";
            ResultSet rs = conn.createStatement().executeQuery(String.format(query, lvl));
            if (rs.next()) {
                return rs.getInt("elem_style_id");
            } else {
                log.log(Level.WARNING, "Не удалось выполнить 'getStyleID()', пустой ответ.");
                return -1;
            }
        } catch (SQLException ex) {
            log.log(Level.WARNING, "Не удалось выполнить 'getStyleID()'", ex);
            return -1;
        }
    }

    // выбрать настройки TR по id стиля ---
    TextRangeRule getTRRule(int styleID) {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath)) {
            String query = "SELECT text_ranges.bold, " +
                    "font_names.font_name, " +
                    "text_ranges.font_size, " +
                    "text_ranges.text_color, " +
                    "text_ranges.italic, " +
                    "text_ranges.all_caps " +
                    "FROM text_ranges " +
                    "join font_names on text_ranges.font_name_id = font_names.font_id " +
                    "WHERE text_ranges.text_range_style_id = %s;";
            ResultSet rs = conn.createStatement().executeQuery(String.format(query, styleID));
            TextRangeRule tr = new TextRangeRule();
            if (rs.next()) {
                do {
                    tr.setTextBold  (toBoolean(rs.getInt("bold")));
                    tr.setFontName  (rs.getString   ("font_name"));
                    tr.setFontSize  (rs.getFloat    ("font_size"));
                    tr.setTextColor (rs.getString   ("text_color"));
                    tr.setTextItalic(toBoolean(rs.getInt("italic")));
                    tr.setTextCap   (toBoolean(rs.getInt  ("all_caps")));
                } while (rs.next());
            }
            return tr;
        } catch (SQLException ex) {
                log.log(Level.WARNING, "Не удалось выполнить 'getTRRule()'", ex);
                return new TextRangeRule();
        }
    }

    // выбрать настройки paragraph по id стиля ---
    ParagraphRule getParaRule(int styleID) {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath)) {
            String query = "SELECT alignment.align_name, " +
                    "paragraphs.line_space, " +
                    "paragraphs.indent " +
                    "FROM paragraphs " +
                    "join alignment on paragraphs.alignment_id = alignment.align_id " +
                    "WHERE paragraphs.para_style_id = %s;";
            ResultSet rs = conn.createStatement().executeQuery(String.format(query, styleID));
            ParagraphRule pr = new ParagraphRule();
            if (rs.next()) {
                do {
                    pr.setAlignName      (rs.getString("align_name"));
                    pr.setLineSpace      (rs.getFloat ("line_space"));
                    pr.setParagraphIndent(rs.getFloat ("indent"));
                } while (rs.next());
            }
            return pr;
        } catch (SQLException ex) {
            log.log(Level.WARNING, "Не удалось выполнить 'getParaRule()'", ex);
            return new ParagraphRule();
        }
    }

    // получить данные о параметрах абзаца в документе ---
    ParagraphRule getParagraphInfo(Paragraph paragraph) {
        ParagraphRule paraInfo = new ParagraphRule();
        paraInfo.setLineSpace(paragraph.getFormat().getLineSpacing());
        paraInfo.setAlignName(paragraph.getFormat().getHorizontalAlignment().toString());
        paraInfo.setParagraphIndent(paragraph.getFormat().getFirstLineIndent());
        return paraInfo;
    }

    // получить данные о параметрах абзаца в документе
    TextRangeRule getTRInfo(TextRange tr) {
        TextRangeRule trInfo = new TextRangeRule();
        CharacterFormat cf = tr.getCharacterFormat();
        Color color = cf.getTextColor();
        String hex = String.format("0x%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
        trInfo.setTextBold  (cf.getBold());
        trInfo.setFontName  (cf.getFontName());
        trInfo.setFontSize  (cf.getFontSize());
        trInfo.setTextColor (hex);
        trInfo.setTextItalic(cf.getItalic());
//        trInfo.setTextCap   (isAllUpper(tr.getText()));
        return trInfo;
    }

    boolean isAllUpper(String s) {
        for(char c : s.toCharArray()) {
            if(Character.isLetter(c) && Character.isLowerCase(c)) {
                return false;
            }
        }
        return true;
    }

    boolean toBoolean(int val) {
        return val != 0;
    }


    @Test
    void test() {
        Document document = new Document();
        document.loadFromFile(docFilePath);

        for (int i = 0; i < document.getSections().getCount(); i++) {
            Section section = document.getSections().get(i);
            for (int j = 0; j < section.getParagraphs().getCount(); j++) {
                Paragraph paragraph = section.getParagraphs().get(j);
                String sn = paragraph.getStyleName();
                int level = 0;

                if (sn.startsWith("Heading")) {
                    Pattern pattern = Pattern.compile("\\d+");
                    Matcher matcher = pattern.matcher(sn);
                    if (matcher.find()) {
                        level = Integer.parseInt(matcher.group());
//                        System.out.println("level: " + level);
                    }
                }

                if (level == 0 || level == 1) {
                    int styleID = getStyleID(level);

                    ParagraphRule paragraphRule = getParaRule(styleID);
                    ParagraphRule prInfo = getParagraphInfo(paragraph);
                    List<String> paraErrors = paragraphRule.compare(prInfo);
                    if (!paraErrors.isEmpty()) {
                        for (String error : paraErrors) {
                            paragraph.appendComment(error);
                            System.out.println("--- " + error + ", стиль: " + styleID);
                        }
                    }

                    TextRangeRule textRangeRule = getTRRule(styleID);
                    for (int k = 0; k < paragraph.getChildObjects().getCount(); k++) {
                        if (paragraph.getChildObjects().get(k) instanceof TextRange) {
                            TextRange tr = (TextRange) paragraph.getChildObjects().get(k);
                            TextRangeRule trInfo = getTRInfo(tr);
                            List<String> trErrors = textRangeRule.compare(trInfo);
                            if (!trErrors.isEmpty()) {
                                System.out.println(tr.getText());
                                for (String error : trErrors) {
                                    paragraph.appendComment(error);
                                    System.out.println("--- " + error + ", стиль: " + styleID);
                                }
                            }
                        }
                    }
                }
            }
        }
        document.saveToFile(docFilePath, FileFormat.Docx);
    }
}
