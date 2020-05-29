package td.tasks;

import javafx.concurrent.Task;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class ValidationTask extends Task<List<String>> {
    private static Validator validator;
    private String docType;
    private Path docPath;
    private Map<String, String> userGeneralRules;
    private Map<String, Map<String, String>> userSectionRules;


    public ValidationTask(String docType, Path docPath, Map<String, String> userGeneralRules, Map<String, Map<String, String>> userSectionRules) {
        this.docType = docType;
        this.docPath = docPath;
        this.userGeneralRules = userGeneralRules;
        this.userSectionRules = userSectionRules;
        this.validator = new Validator();
    }

    @Override
    protected List<String> call()  {
        return getReport();
    }

    private List<String> getReport() {
        return validator.validate(this.docType, this.docPath, this.userGeneralRules, this.userSectionRules);
    }
}
