package database.models;

public class TextRange {
    private int textRangeId;
    private int styleId;
    private int fontNameId;
    private boolean textBold;
    private float fontSize;
    private String textColor;
    private boolean textItalic;
    private boolean textCap;

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
