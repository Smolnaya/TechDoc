package td.services;

import com.spire.doc.Document;
import com.spire.doc.FileFormat;
import com.spire.doc.Section;
import com.spire.doc.documents.CommentMark;
import com.spire.doc.documents.CommentMarkType;
import com.spire.doc.documents.Paragraph;
import com.spire.doc.fields.Comment;
import com.spire.doc.fields.TextRange;
import com.spire.doc.formatting.CharacterFormat;
import database.DbSql;
import database.models.ParagraphRule;
import database.models.TextRangeRule;

import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FontValidator {
    private final DbSql dbSql = new DbSql();
    private final Logger log = Logger.getLogger(getClass().getName());

    // получить данные о параметрах абзаца в документе
    private ParagraphRule getParagraphInfo(Paragraph paragraph) {
        ParagraphRule paraInfo = new ParagraphRule();
        paraInfo.setLineSpace(paragraph.getFormat().getLineSpacing());
        paraInfo.setAlignName(paragraph.getFormat().getHorizontalAlignment().toString());
        paraInfo.setParagraphIndent(paragraph.getFormat().getFirstLineIndent());
        return paraInfo;
    }

    // получить данные о параметрах абзаца в документе
    private TextRangeRule getTRInfo(TextRange tr) {
        TextRangeRule trInfo = new TextRangeRule();
        CharacterFormat cf = tr.getCharacterFormat();
        Color color = cf.getTextColor();
        String hex = String.format("0x%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
        trInfo.setTextBold(cf.getBold());
        trInfo.setFontName(cf.getFontName());
        trInfo.setFontSize(cf.getFontSize());
        trInfo.setTextColor(hex);
        trInfo.setTextItalic(cf.getItalic());
//        trInfo.setTextCap   (isAllUpper(tr.getText()));
        return trInfo;
    }

    // проверка капса
    private boolean isAllUpper(String s) {
        for (char c : s.toCharArray()) {
            if (Character.isLetter(c) && Character.isLowerCase(c)) {
                return false;
            }
        }
        return true;
    }

    public String validateFont(String docFilePath, String xsdPath) {
        try {
            Document document = new Document();
            document.loadFromFile(docFilePath);
            int tempId = dbSql.getTempID(xsdPath);

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
                        }
                    }
                    int styleID = dbSql.getStyleID(tempId, level);
                    if (styleID != -1) {
                        ParagraphRule paragraphRule = dbSql.getParaRule(styleID);
                        ParagraphRule prInfo = getParagraphInfo(paragraph);
                        java.util.List<String> paraErrors = paragraphRule.compare(prInfo);
                        if (!paraErrors.isEmpty()) {
                            for (String error : paraErrors) {
                                Comment comment = new Comment(document);
                                comment.getBody().addParagraph().setText(error);
                                comment.getFormat().setAuthor("TechDoc");
                                paragraph.getChildObjects().add(comment);
//                                paragraph.appendComment(error);
                            }
                        }

                        TextRangeRule textRangeRule = dbSql.getTRRule(styleID);
                        for (int k = 0; k < paragraph.getChildObjects().getCount(); k++) {
                            if (paragraph.getChildObjects().get(k) instanceof TextRange) {
                                TextRange tr = (TextRange) paragraph.getChildObjects().get(k);
                                TextRangeRule trInfo = getTRInfo(tr);
                                List<String> trErrors = textRangeRule.compare(trInfo);
                                if (!trErrors.isEmpty()) {
                                    for (String error : trErrors) {
                                        paragraph.appendComment(error);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            document.saveToFile(docFilePath, FileFormat.Docx);
            return "Проверка шрифтов завершена.";
        } catch (Exception e) {
            log.log(Level.WARNING, "", e);
            return "Ошибка при проверке шрифтов.";
        }
    }
}
