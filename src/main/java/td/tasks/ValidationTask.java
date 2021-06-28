package td.tasks;

import javafx.concurrent.Task;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class ValidationTask extends Task<List<String>> {
    private static Validator validator;
    private final String xsd;
    private final Path docPath;
    private final Map<String, String> userGeneralRules;
    private final Map<String, Map<String, String>> userSectionRules;


    public ValidationTask(String xsdS, Path docPath, Map<String, String> userGeneralRules, Map<String, Map<String, String>> userSectionRules) {
        this.docPath = docPath;
        this.xsd = xsdS;
        this.userGeneralRules = userGeneralRules;
        this.userSectionRules = userSectionRules;
        validator = new Validator();
    }

    @Override
    protected List<String> call()  {
        return getReport();
    }

    private List<String> getReport() {
        return validator.validate(this.xsd, this.docPath, this.userGeneralRules, this.userSectionRules);
    }
}
