package td.tasks;

import javafx.concurrent.Task;
import td.services.FontValidator;
import td.services.TermValidator;

import java.util.Collections;
import java.util.List;

public class TermValidationTask extends Task<List<String>> {
    private static TermValidator termValidator = new TermValidator();
    String filePath;

    public TermValidationTask(String filePath) {
        this.filePath = filePath;
    }

    private String getReport() {
        return termValidator.writeErrorsToDocument(filePath);
    }

    @Override
    protected List<String> call() {
        return Collections.singletonList(getReport());
    }
}