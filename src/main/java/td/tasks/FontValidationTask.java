package td.tasks;

import javafx.concurrent.Task;
import td.services.FontValidator;

import java.util.Collections;
import java.util.List;

public class FontValidationTask extends Task<List<String>> {
    private static FontValidator validator = new FontValidator();
    String filePath;
    String xsdPath;

    public FontValidationTask(String filePath, String xsdPath) {
        this.filePath = filePath;
        this.xsdPath = xsdPath;
    }

    private String getReport() {
        return validator.validateFont(filePath, xsdPath);
    }

    @Override
    protected List<String> call() {
        return Collections.singletonList(getReport());
    }
}