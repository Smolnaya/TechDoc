package td.domain;

import td.exceptions.LackOfHeadingsException;

import java.util.ArrayList;
import java.util.List;

public class WordDocument {
    private String title;
    private String description;
    private Section rootHeader;
    private List<Section> sections;

    public WordDocument() {
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void printDocumentHeaders() throws LackOfHeadingsException {
        if (getSections().size() > 0) {
            for (int i = 0; i < getSections().size(); i++) {
                System.out.println(getSections().get(i).printTitle());
            }
        } else {
            throw new LackOfHeadingsException("There are no headings in the document.");
        }
    }

    public void printDocumentContent() throws LackOfHeadingsException {
        if (getSections().size() > 0) {
            for (int i = 0; i < getSections().size(); i++) {
                if (getSections().get(i).getContent().size() > 0) {
                    for (int j = 0; j < getSections().get(i).getContent().size(); j++) {
                        System.out.println(getSections().get(i).getContent().get(j));
                    }
                }
            }
        } else {
            throw new LackOfHeadingsException("There are no headings in the document. Unable to retrieve content.");
        }
    }
}
