package database.models;

public class Style {
    private int styleId;
    private String styleName;

    private String alignment;
    private float lineSpace;
    private float indent;

    private boolean bold;
    private String fontName;
    private float fontSize;
    private String textColor;
    private boolean italic;
    private boolean allCaps;

    @Override
    public String toString() {
        return String.format("style_id = %s, style_name = %s, align_name = %s, line_space = %s, indent = %s, " +
                        "bold = %s, font_name = %s, font_size = %s, text_color = %s, italic = %s, all_caps = %s",
                styleId, styleName, alignment, lineSpace, indent, bold, fontName, fontSize, textColor, italic, allCaps);
    }

    public int getStyleId() {
        return styleId;
    }

    public void setStyleId(int styleId) {
        this.styleId = styleId;
    }

    public String getStyleName() {
        return styleName;
    }

    public void setStyleName(String styleName) {
        this.styleName = styleName;
    }

    public String getAlignment() {
        return alignment;
    }

    public void setAlignment(String alignment) {
        this.alignment = alignment;
    }

    public float getLineSpace() {
        return lineSpace;
    }

    public void setLineSpace(float lineSpace) {
        this.lineSpace = lineSpace;
    }

    public float getIndent() {
        return indent;
    }

    public void setIndent(float indent) {
        this.indent = indent;
    }

    public boolean getBold() {
        return bold;
    }

    public void setBold(boolean bold) {
        this.bold = bold;
    }

    public String getFontName() {
        return fontName;
    }

    public void setFontName(String fontName) {
        this.fontName = fontName;
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

    public boolean getItalic() {
        return italic;
    }

    public void setItalic(boolean italic) {
        this.italic = italic;
    }

    public boolean getAllCaps() {
        return allCaps;
    }

    public void setAllCaps(boolean allCaps) {
        this.allCaps = allCaps;
    }
}

