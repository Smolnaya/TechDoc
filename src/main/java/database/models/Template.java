package database.models;

public class Template {
    private int tempId;
    private String tempName;
    private String tempPath;
    private String styleName;

    @Override
    public String toString() {
        return String.format("id = %s, name = %s, path = %s, style = %s",
                tempId, tempName, tempPath, styleName);
    }

    public int getTempId() {
        return tempId;
    }

    public void setTempId(int tempId) {
        this.tempId = tempId;
    }

    public String getTempName() {
        return tempName;
    }

    public void setTempName(String tempName) {
        this.tempName = tempName;
    }

    public String getTempPath() {
        return tempPath;
    }

    public void setTempPath(String tempPath) {
        this.tempPath = tempPath;
    }

    public String getStyleName() {
        return styleName;
    }

    public void setStyleName(String styleName) {
        this.styleName = styleName;
    }
}
