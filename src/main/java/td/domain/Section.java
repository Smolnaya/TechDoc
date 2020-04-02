package td.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Section {
    private int level;
    private String fontStyleName;
    private int fontSize;
    private Boolean fontBold;
    private Boolean fontItalic;
    private String alignment;
    private Boolean upperCase;
    private int indent;
    private String title;
    private List<Section> subheadersList;
    private Set<Section> subheadersSet;
    private Section parentHeader;
    private List<String> content;

    public Section() {
        this.subheadersList = new ArrayList<>();
        this.subheadersSet = new HashSet<>();
        this.content = new ArrayList<>();
    }

    public Section(Section parentHeader) {
        this.parentHeader = parentHeader;
        this.subheadersList = new ArrayList<>();
        this.subheadersSet = new HashSet<>();
    }

    public List<String> getContent() {
        return content;
    }

    public void setContent(List<String> content) {
        this.content = content;
    }

    public Section getParentHeader() {
        return parentHeader;
    }

    public void setParentHeader(Section parentHeader) {
        this.parentHeader = parentHeader;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getFontStyleName() {
        return fontStyleName;
    }

    public void setFontStyleName(String fontStyleName) {
        this.fontStyleName = fontStyleName;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public Boolean getFontBold() {
        return fontBold;
    }

    public void setFontBold(Boolean fontBold) {
        this.fontBold = fontBold;
    }

    public Boolean getFontItalic() {
        return fontItalic;
    }

    public void setFontItalic(Boolean fontItalic) {
        this.fontItalic = fontItalic;
    }

    public String getAlignment() {
        return alignment;
    }

    public void setAlignment(String alignment) {
        this.alignment = alignment;
    }

    public Boolean getUpperCase() {
        return upperCase;
    }

    public void setUpperCase(Boolean upperCase) {
        this.upperCase = upperCase;
    }

    public int getIndent() {
        return indent;
    }

    public void setIndent(int indent) {
        this.indent = indent;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Section> getSubheadersList() {
        return subheadersList;
    }

    public void setSubheadersList(List<Section> subheadersList) {
        this.subheadersList = subheadersList;
    }

    public Set<Section> getSubheadersSet() {
        return subheadersSet;
    }

    public void setSubheadersSet(Set<Section> subheadersSet) {
        this.subheadersSet = subheadersSet;
    }

    public String printTitle() {
        String str = "";
        for (int i = 1; i < level; i++) {
            str += "    ";
        }
        return str + title;
    }
}
