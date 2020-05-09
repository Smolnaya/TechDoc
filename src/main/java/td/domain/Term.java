package td.domain;

public class Term {
    private String term;
    private String value;

    public Term(String term, String value) {
        this.term = term;
        this.value = value;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
