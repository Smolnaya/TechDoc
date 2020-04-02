package td.domain;

import java.util.ArrayList;
import java.util.List;

public class Document {
    private String title;
    private int index;
    private String description;
    private Section rootHeader;
    private List<Section> sections;

    public Document() {
        this.rootHeader = new Section();
        this.sections = new ArrayList<>();
    }

    public List<Section> getSections() {
        return sections;
    }

    public void setSections(List<Section> sections) {
        this.sections = sections;
    }

    public Section getRootHeader() {
        return rootHeader;
    }

    public void setRootHeader(Section rootHeader) {
        this.rootHeader = rootHeader;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
