package td.templates.headers;

/*
Заголовок 2 уровня
    - шрифт 14 Times New Roman,
    - жирный,
    - выравнивание по ширине,
    - отступ абзацный
 */
public class SecondLevelHeader {
    private String level = "heading 2"; //XWPFDocument.getStyles().getStyle(xwpfParagraph.getStyleID()).getName())
    private String fontStyleName = "Times New Roman";
    private int fontSize = 14;
    private Boolean fontBold = true;
    private Boolean fontItalic = false;
    private String alignment = "DISTRIBUTE"; //ParagraphAlignment.getValue() = int 6
    private Boolean upperCase = false;
    private double indent = 1.25;
    private String title = "";

    public String getLevel() {
        return level;
    }

    public String getFontStyleName() {
        return fontStyleName;
    }

    public int getFontSize() {
        return fontSize;
    }

    public Boolean getFontBold() {
        return fontBold;
    }

    public Boolean getFontItalic() {
        return fontItalic;
    }

    public String getAlignment() {
        return alignment;
    }

    public Boolean getUpperCase() {
        return upperCase;
    }

    public double getIndent() {
        return indent;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "SecondLevelHeader{" +
                "level='" + level + '\'' +
                ", fontStyleName='" + fontStyleName + '\'' +
                ", fontSize=" + fontSize +
                ", fontBold=" + fontBold +
                ", fontItalic=" + fontItalic +
                ", alignment='" + alignment + '\'' +
                ", upperCase=" + upperCase +
                ", indent=" + indent +
                ", title='" + title + '\'' +
                '}';
    }
}
