package td.tasks;

import javafx.concurrent.Task;
import td.services.FontValidator;
import td.services.TermValidator;

import java.util.Collections;
import java.util.List;

public class FontValidationTask extends Task<List<String>> {
    private static FontValidator fontValidator = new FontValidator();
    private static TermValidator termValidator = new TermValidator();
    String filePath;
    String xsdPath;

    public FontValidationTask(String filePath, String xsdPath) {
        this.filePath = filePath;
        this.xsdPath = xsdPath;
    }

    private String getReport() {
//        return termValidator.writeErrorsToDocument(filePath);
        return fontValidator.validateFont(filePath, xsdPath);
    }

    @Override
    protected List<String> call() {
        return Collections.singletonList(getReport());
    }
}