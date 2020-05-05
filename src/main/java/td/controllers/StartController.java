package td.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import td.Validator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StartController extends Window {

    @FXML
    private Button validateButton;

    @FXML
    private Label fileNameLabel;

    @FXML
    private TextArea reportTextArea;

    @FXML
    private ChoiceBox<String> documentTypeChoiceBox;

    @FXML
    private CheckBox abbreviationCheckBox;

    private ObservableList<String> documentTypeList = FXCollections.observableArrayList("Дипломная работа");
    private File docFile;

    private Logger log = Logger.getLogger(getClass().getName());

    @FXML
    public void initialize() {
        documentTypeChoiceBox.setValue(documentTypeList.get(0));
        documentTypeChoiceBox.setItems(documentTypeList);
    }

    @FXML
    void clickFileButton(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("ALL files", "*.docx");
        fileChooser.getExtensionFilters().add(extensionFilter);
        reportTextArea.clear();
        fileNameLabel.setText("");
        try {
            docFile = fileChooser.showOpenDialog(this);
            fileNameLabel.setText(docFile.getName());
        } catch (NullPointerException e) {
            log.log(Level.WARNING, e.getMessage());
        }
    }

    @FXML
    void clickValidateButton(ActionEvent event) {
        Validator validator = new Validator();
        List<String> report = validator.validate(documentTypeChoiceBox.getValue(),docFile.toPath());
        for (int i = 0; i < report.size(); i++) {
            reportTextArea.appendText(report.get(i) + "\n");
        }
    }
}
