package td.templates.headers;

/*
Заголовок 1 уровня
    - шрифт 14 Times New Roman,
    - жирный,
    - всеми заглавными буквами,
    - выравнивание по центру страницы
 */
public class FirstLevelHeader {
    private String level = "heading 1"; //XWPFDocument.getStyles().getStyle(xwpfParagraph.getStyleID()).getName())
    private String fontStyleName = "Times New Roman";
    private int fontSize = 14;
    private Boolean fontBold = true;
    private Boolean fontItalic = false;
    private String alignment = "center"; //ParagraphAlignment.getValue() = int 2
    private Boolean upperCase = true;
    private int indent = 0;
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

    public int getIndent() {
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
        return "FirstLevelHeader{" +
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
