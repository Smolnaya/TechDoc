package td.services;

import com.spire.doc.Document;
import com.spire.doc.FileFormat;
import com.spire.doc.Section;
import com.spire.doc.documents.Paragraph;
import com.spire.doc.documents.ParagraphStyle;
import database.DbSql;
import database.models.ParagraphRule;
import database.models.TextRangeRule;

import java.awt.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DocFileCreator {
    private final Logger log = Logger.getLogger(getClass().getName());
    private final DbSql dbSql = new DbSql();
    private final String xsdPathString;
    private final String filePath;

    public DocFileCreator(String xsd, String toPath) {
        this.xsdPathString = xsd;
        this.filePath = toPath;
    }

    public void createDocFile() {
        XmlRulesGetter xmlRules = new XmlRulesGetter();
        Path xsdPath = Paths.get(xsdPathString);
        List<String> sectionKindList = xmlRules.getSectionKind(xsdPath);
        Set<String> set = new LinkedHashSet<>(sectionKindList);
        Map<String, Map<String, String>> sectionRules = xmlRules.getDocumentRules(xsdPath);
        List<String> headers = new ArrayList<>();

        // Перебор разделов
        for (String str : set) {
            // Поиск заголовка
            for (Map.Entry<String, Map<String, String>> section : sectionRules.entrySet()) {
                if (str.equals(section.getKey())) {
                    for (Map.Entry<String, String> rule : section.getValue().entrySet()) {
                        if (rule.getKey().equals("title") || rule.getKey().equals("titleContains")) {
                            headers.add(rule.getValue());
                        }
                    }
                }
            }
        }

        int tempId = dbSql.getTempID(xsdPathString);
        int headerStyleID = dbSql.getStyleID(tempId, 1);
        int textStyleID = dbSql.getStyleID(tempId, 0);
        ParagraphRule paragraphRuleHeader = dbSql.getParaRule(headerStyleID);
        ParagraphRule paragraphRuleText = dbSql.getParaRule(textStyleID);
        TextRangeRule textRangeRuleHeader = dbSql.getTRRule(headerStyleID);
        TextRangeRule textRangeRuleText = dbSql.getTRRule(textStyleID);

        try {
            writeFile(headers, paragraphRuleHeader, textRangeRuleHeader,
                    paragraphRuleText, textRangeRuleText, filePath);
        } catch (Exception exception) {
            log.log(Level.WARNING, exception.getMessage());
        }
    }


    private void writeFile(List<String> headers, ParagraphRule paragraphRuleHeader, TextRangeRule textRangeRuleHeader,
                            ParagraphRule paragraphRuleText, TextRangeRule textRangeRuleText, String fileName) {
        Document document = new Document();
        createStyles(document, textRangeRuleHeader, textRangeRuleText);

        for (String header : headers) {
            Section section = document.addSection();

            // Текст заголовка
            Paragraph para1 = section.addParagraph();
            para1.appendText(header);
            // Настройки абзаца
            para1.getFormat().setFirstLineIndent(paragraphRuleHeader.getParagraphIndent());
            para1.getFormat().setLineSpacing(paragraphRuleHeader.getLineSpace());
            para1.getFormat().setHorizontalAlignment(paragraphRuleHeader.getHA());
            // Настройка текста
            para1.applyStyle("Heading 1 TD");

            // Текст раздела
            Paragraph para2 = section.addParagraph();
            para2.appendText("Текст раздела");
            // Настройки абзаца
            para2.getFormat().setFirstLineIndent(paragraphRuleText.getParagraphIndent());
            para2.getFormat().setLineSpacing(paragraphRuleText.getLineSpace());
            para2.getFormat().setHorizontalAlignment(paragraphRuleText.getHA());
            para2.getFormat().setAfterAutoSpacing(false);
            para2.getFormat().setBeforeAutoSpacing(false);
            // Настройка текста
            para2.applyStyle("Main Text TD");

            // Сохранение файла
            document.saveToFile(fileName, FileFormat.Docx);
        }
    }

    private void createStyles(Document document, TextRangeRule textRangeRuleHeader, TextRangeRule textRangeRuleText) {
        ParagraphStyle style1 = new ParagraphStyle(document);
        style1.setName("Heading 1 TD");
        style1.getCharacterFormat().setBold(textRangeRuleHeader.getTextBold());
        style1.getCharacterFormat().setFontName(textRangeRuleHeader.getFontName());
        style1.getCharacterFormat().setFontSize(textRangeRuleHeader.getFontSize());
        style1.getCharacterFormat().setTextColor(Color.decode(textRangeRuleHeader.getTextColor()));
        style1.getCharacterFormat().setItalic(textRangeRuleHeader.getTextItalic());
        document.getStyles().add(style1);

        ParagraphStyle style2 = new ParagraphStyle(document);
        style2.setName("Main Text TD");
        style2.getCharacterFormat().setBold(textRangeRuleText.getTextBold());
        style2.getCharacterFormat().setFontName(textRangeRuleText.getFontName());
        style2.getCharacterFormat().setFontSize(textRangeRuleText.getFontSize());
        style2.getCharacterFormat().setTextColor(Color.decode(textRangeRuleText.getTextColor()));
        style2.getCharacterFormat().setItalic(textRangeRuleText.getTextItalic());
        document.getStyles().add(style2);
    }
}
