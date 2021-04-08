package td.controllers;

import database.DbSql;
import database.models.Template;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.*;
import td.services.Translator;
import td.services.XmlRulesGetter;
import td.tasks.FontValidationTask;
import td.tasks.ValidationTask;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StartController extends Window {

    @FXML
    private Label fileNameLabel;

    @FXML
    private Button fileButton;

    @FXML
    private Button validateButton;

    @FXML
    private Button reportButton;

    @FXML
    private Button button;

    @FXML
    private Button fontButton;

    @FXML
    private Button templateButton;

    @FXML
    private Button styleButton;

    @FXML
    private final Label genRulesLabel = new Label("Правила для документа");

    @FXML
    private VBox container;

    @FXML
    private TextArea reportTextArea;

    @FXML
    private ComboBox<String> documentTypeComboBox;

    @FXML
    ScrollPane rulesScrollPane;

    @FXML
    private Label progressLabel;

    @FXML
    private ProgressIndicator progressIndicator;

    private File docFile;
    private final DbSql dbSql = new DbSql();
    private final ObservableList<String> documentTypeList = FXCollections.observableArrayList();
    private final Map<String, String> schemas = new HashMap<>();
    private Map<String, Map<String, String>> sectionRules = new HashMap<>();
    private Map<String, String> generalRules = new HashMap<>();
    private final XmlRulesGetter rulesGetter = new XmlRulesGetter();
    private final Logger log = Logger.getLogger(getClass().getName());
    private final Translator translator = new Translator();

    private final Font ruleFont = Font.font("Times New Roman", 15);
    private final Font keyFont = Font.font("Times New Roman", FontWeight.BOLD, 14);

    @FXML
    public void initialize() {
        List<Template> templateList = dbSql.selectTemplates();
        if (templateList != null) {
            for (Template t : templateList) {
                schemas.put(t.getTempName(), t.getTempPath());
            }
        }
        for (Map.Entry<String, String> entry : schemas.entrySet()) {
            documentTypeList.add(entry.getKey());
        }

        documentTypeComboBox.setItems(documentTypeList);
        documentTypeComboBox.getSelectionModel().select(0);

        chooseSchema();
        documentTypeComboBox.setOnAction(event -> chooseSchema());
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
    public void chooseSchema() {
        container.getChildren().clear();
        Path xsdPath = null;
        for (Map.Entry<String, String> entry : schemas.entrySet()) {
            if (entry.getKey().equals(documentTypeComboBox.getValue())) {
                xsdPath = Paths.get(entry.getValue());
            }
        }

        Map<String, Map<String, String>> xmlSectionRules =
                translator.translateSectionRulesToRussian(rulesGetter.getDocumentRules(xsdPath));
        Map<String, String> xmlGeneralRules =
                translator.translateGeneralToRussian(rulesGetter.getGeneralRules(xsdPath));

        for (Map.Entry<String, Map<String, String>> entry : xmlSectionRules.entrySet()) {
            Label key = new Label();
            key.setWrapText(true);
            key.setFont(keyFont);
            key.setText(entry.getKey());
            container.getChildren().add(key);
            Map<String, String> valueMap = entry.getValue();
            for (Map.Entry<String, String> stringEntry : valueMap.entrySet()) {
                String rule = stringEntry.getKey();
                String value = stringEntry.getValue();
                if (value.equals("true") || value.equals("false")) {
                    CheckBox checkBox = new CheckBox(rule);
                    checkBox.setFont(ruleFont);
                    checkBox.setWrapText(true);
                    checkBox.setSelected(Boolean.parseBoolean(value));
                    container.getChildren().add(checkBox);
                } else if (value.matches("[\\d]")) {
                    Label label = new Label(rule);
                    label.setFont(ruleFont);
                    label.setWrapText(true);
                    TextField textField = new TextField();
                    textField.textProperty().addListener(new ChangeListener() {
                        @Override
                        public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                            if (!newValue.toString().matches("\\d*")) {
                                textField.setText(newValue.toString().replaceAll("[^\\d]", ""));
                            }
                        }
                    });
                    textField.setText(value);
                    container.getChildren().addAll(label, textField);
                } else {
                    Label label = new Label(rule);
                    label.setFont(ruleFont);
                    label.setWrapText(true);
                    TextField textField = new TextField();
                    textField.setText(value);
                    container.getChildren().addAll(label, textField);
                }
            }
        }

        genRulesLabel.setFont(keyFont);
        container.getChildren().add(genRulesLabel);
        for (Map.Entry<String, String> entry : xmlGeneralRules.entrySet()) {
            String rule = entry.getKey();
            String value = entry.getValue();
            if (value.equals("true") || value.equals("false")) {
                CheckBox checkBox = new CheckBox(rule);
                checkBox.setFont(ruleFont);
                checkBox.setSelected(Boolean.parseBoolean(value));
                container.getChildren().add(checkBox);
            } else if (value.matches("[\\d]")) {
                Label label = new Label(rule);
                label.setFont(ruleFont);
                TextField textField = new TextField();
                textField.textProperty().addListener(new ChangeListener() {
                    @Override
                    public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                        if (!newValue.toString().matches("\\d*")) {
                            textField.setText(newValue.toString().replaceAll("[^\\d]", ""));
                        }
                    }
                });
                textField.setText(value);
                container.getChildren().addAll(label, textField);
            } else {
                Label label = new Label(rule);
                label.setFont(ruleFont);
                TextField textField = new TextField();
                textField.setText(value);
                container.getChildren().addAll(label, textField);
            }
        }
    }

    void getRules() {
        Map<String, Map<String, String>> userSectionRules = new HashMap<>();
        Map<String, String> userGeneralRules = new HashMap<>();
        Map<String, String> currentMap = new HashMap<>();
        String key = "";
        String rule = "";
        String value = "";

        for (int i = 0; i < container.getChildren().size(); i++) {
            Node node = container.getChildren().get(i);
            if (node instanceof Label && ((Label) node).getFont().equals(keyFont)) {
                key = ((Label) node).getText();
            } else if (node instanceof Label && ((Label) node).getFont().equals(ruleFont)) {
                rule = ((Label) node).getText();
            } else if (node instanceof TextField) {
                value = ((TextField) node).getText();
            } else if (node instanceof CheckBox) {
                if (((CheckBox) node).isSelected()) {
                    rule = ((CheckBox) node).getText();
                    value = "true";
                } else {
                    rule = ((CheckBox) node).getText();
                    value = "false";
                }
            }

            if (!rule.isEmpty() && !value.isEmpty()) {
                currentMap.put(rule, value);
                rule = "";
                value = "";
            }

            if (!currentMap.isEmpty()) {
                if (container.getChildren().size() > i + 1) {
                    Node nextNode = container.getChildren().get(i + 1);
                    if (nextNode instanceof Label && ((Label) nextNode).getFont().equals(keyFont)) {
                        userSectionRules.put(key, currentMap);
                        key = "";
                        currentMap = new HashMap<>();
                    }
                } else if (container.getChildren().size() == i + 1) {
                    for (Map.Entry<String, String> entry : currentMap.entrySet()) {
                        userGeneralRules.put(entry.getKey(), entry.getValue());
                    }
                }
            }
        }
        sectionRules = translator.translateSectionRulesToEnglish(userSectionRules);
        generalRules = translator.translateGeneralToEnglish(userGeneralRules);
    }

    @FXML
    void clickValidateButton() {
        if (docFile != null) {
            getRules();
            reportTextArea.clear();
            setBlock(true);

            ValidationTask task = new ValidationTask(documentTypeComboBox.getValue(), docFile.toPath(),
                    generalRules, sectionRules);
            task.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED,
                    new EventHandler<WorkerStateEvent>() {
                        @Override
                        public void handle(WorkerStateEvent t) {
                            List<String> report = task.getValue();
                            for (String s : report) {
                                reportTextArea.appendText(s + "\n");
                            }
                            setBlock(false);
                        }
                    });
            new Thread(task).start();
        }
    }

    private void setBlock(Boolean value) {
        progressLabel.setVisible(value);
        progressIndicator.setVisible(value);
        fileButton.setDisable(value);
        documentTypeComboBox.setDisable(value);
        rulesScrollPane.setDisable(value);
        reportButton.setDisable(value);
        button.setDisable(value);
        templateButton.setDisable(value);
        styleButton.setDisable(value);
        validateButton.setDisable(value);
        fontButton.setDisable(value);

    }

    @FXML
    void getReport() {
        if (!reportTextArea.getText().isEmpty()) {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            File directory = directoryChooser.showDialog(this);
            if (directory != null) {
                try (BufferedWriter bf = new BufferedWriter(new FileWriter(directory + "\\report.txt"))) {
                    bf.write(reportTextArea.getText());
                } catch (IOException ex) {
                    log.log(Level.WARNING, ex.getMessage());
                }
            }
        }
    }

    @FXML
    void clickTemplateButton() {
        log.log(Level.INFO, "Открывается окно Шаблоны");
        Stage stage = new Stage();
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/template.fxml"));
            stage.setTitle("Шаблоны");
            openNewWindow(stage, root, templateButton);
        } catch (IOException ex) {
            log.log(Level.WARNING, ex.getMessage());
        }
    }

    @FXML
    void clickFontButton() {
        if (docFile != null) {
            String xsdPath = null;
            for (Map.Entry<String, String> entry : schemas.entrySet()) {
                if (entry.getKey().equals(documentTypeComboBox.getValue())) {
                    xsdPath = entry.getValue();
                }
            }
            setBlock(true);
            FontValidationTask task = new FontValidationTask(docFile.getAbsolutePath(), xsdPath);

            task.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED,
                    new EventHandler<WorkerStateEvent>() {
                        @Override
                        public void handle(WorkerStateEvent t) {
                            List<String> report = task.getValue();
                            for (String s : report) {
                                reportTextArea.appendText(s + "\n");
                            }
                            setBlock(false);
                        }
                    });
            new Thread(task).start();
        } else
            reportTextArea.appendText("\nНадо выбрать файл");
    }

    @FXML
    void clickStyleButton() {
        log.log(Level.INFO, "Открывается окно Стили");
        String xsdPath = null;
        for (Map.Entry<String, String> entry : schemas.entrySet()) {
            if (entry.getKey().equals(documentTypeComboBox.getValue())) {
                xsdPath = entry.getValue();
            }
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/styleEditor.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Стили");
            StyleEditorController controller = loader.getController();
            controller.initData(xsdPath);
            controller.initialize();
            openNewWindow(stage, root, styleButton);
        } catch (IOException ex) {
            log.log(Level.WARNING, ex.getMessage());
        }
    }

    private void openNewWindow(Stage stage, Parent root, Button button) {
        stage.getIcons().add(new Image("/images/clip.png"));
        stage.setScene(new Scene(root, 960, 600));
        stage.setResizable(false);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(button.getScene().getWindow());
        stage.show();
    }
}