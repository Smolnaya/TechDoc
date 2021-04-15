package database.models;

import java.util.ArrayList;
import java.util.List;

public class TextRangeRule {
    private int textRangeId;
    private boolean textBold;
    private int fontNameId;
    private String fontName;
    private float fontSize;
    private String textColor;
    private boolean textItalic;
    private boolean textCap;
    private int styleId;

    public List<String> compare(TextRangeRule tr) {
        List<String> errorList = new ArrayList<>();
        if (textBold != tr.getTextBold()) {
            errorList.add("Ошибка с жирностью.");
        }
        if (!fontName.equals(tr.getFontName())) {
            errorList.add("Не тот шрифт.");
        }
        if (fontSize != tr.getFontSize()) {
            errorList.add("Не тот размер. ТЗ: ");
        }
        if (!textColor.equals(tr.getTextColor())) {
            errorList.add("Не тот цвет.");
        }
        if (textItalic != tr.textItalic) {
            errorList.add("Ошибка с наклоном.");
        }
//        if (textCap != tr.textCap) {
//            errorList.add("Ошибка в капсе.");
//        }
        return errorList;
    }

    public String getFontName() {
        return fontName;
    }

    public void setFontName(String fontName) {
        this.fontName = fontName;
    }

    public int getTextRangeId() {
        return textRangeId;
    }

    public void setTextRangeId(int textRangeId) {
        this.textRangeId = textRangeId;
    }

    public int getStyleId() {
        return styleId;
    }

    public void setStyleId(int styleId) {
        this.styleId = styleId;
    }

    public int getFontNameId() {
        return fontNameId;
    }

    public void setFontNameId(int fontNameId) {
        this.fontNameId = fontNameId;
    }

    public boolean getTextBold() {
        return textBold;
    }

    public void setTextBold(boolean textBold) {
        this.textBold = textBold;
    }

    public float getFontSize() {
        return fontSize;
    }

    public void setFontSize(float fontSize) {
        this.fontSize = fontSize;
    }

    public String getTextColor() {
        return textColor;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }

    public boolean getTextItalic() {
        return textItalic;
    }

    public void setTextItalic(boolean textItalic) {
        this.textItalic = textItalic;
    }

    public boolean getTextCap() {
        return textCap;
    }

    public void setTextCap(boolean textCap) {
        this.textCap = textCap;
    }
}
