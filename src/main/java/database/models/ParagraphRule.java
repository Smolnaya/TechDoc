package database.models;

import com.spire.doc.documents.HorizontalAlignment;

import java.util.ArrayList;
import java.util.List;

public class ParagraphRule {
    private int paragraphId;
    private int alignId;
    private String alignName;
    private float lineSpace;
    private float paragraphIndent;
    private int styleId;

    public List<String> compare(ParagraphRule pr) {
        List<String> errorList = new ArrayList<>();
        if (!alignName.equals(pr.alignName)) {
            errorList.add("Ошибка в выравнивании.");
        }
        if (lineSpace != pr.lineSpace) {
            errorList.add("Ошибка в межстрочном интервале.");
        }
        if (paragraphIndent != pr.paragraphIndent) {
            errorList.add("Ошибка в отступе.");
        }
        return errorList;
    }

    public String getAlignName() {
        return alignName;
    }

    public HorizontalAlignment getHA() {
        if (alignName.equals("Center")) {
            return HorizontalAlignment.Center;
        } else if (alignName.equals("Left")) {
            return HorizontalAlignment.Left;
        } else if (alignName.equals("Right")) {
            return HorizontalAlignment.Right;
        } else if (alignName.equals("Justify")) {
            return HorizontalAlignment.Justify;
        } else {
            return HorizontalAlignment.Left;
        }
    }

    public void setAlignName(String alignName) {
        this.alignName = alignName;
    }

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
