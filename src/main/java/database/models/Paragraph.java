package database.models;

public class Paragraph {
    private int paragraphId;
    private int styleId;
    private int alignId;
    private float lineSpace;
    private float paragraphIndent;

    public int getParagraphId() {
        return paragraphId;
    }

    public void setParagraphId(int paragraphId) {
        this.paragraphId = paragraphId;
    }

    public int getStyleId() {
        return styleId;
    }

    public void setStyleId(int styleId) {
        this.styleId = styleId;
    }

    public int getAlignId() {
        return alignId;
    }

    public void setAlignId(int alignId) {
        this.alignId = alignId;
    }

    public float getLineSpace() {
        return lineSpace;
    }

    public void setLineSpace(float lineSpace) {
        this.lineSpace = lineSpace;
    }

    public float getParagraphIndent() {
        return paragraphIndent;
    }

    public void setParagraphIndent(float paragraphIndent) {
        this.paragraphIndent = paragraphIndent;
    }
}
