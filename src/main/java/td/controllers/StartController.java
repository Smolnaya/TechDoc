package td.controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import td.Validator;
import td.services.XmlRulesGetter;

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
    private Button validateButton;

    @FXML
    private Button reportButton;

    @FXML
    private Label fileNameLabel;

    @FXML
    private Label genRulesLabel = new Label("Правила для документа");

    @FXML
    private VBox container;

    @FXML
    private TextArea reportTextArea;

    @FXML
    private ChoiceBox<String> documentTypeChoiceBox;

    @FXML
    ScrollPane rulesScrollPane;

    private File docFile;
    private ObservableList<String> documentTypeList = FXCollections.observableArrayList();
    private Map<String, String> schemas = new HashMap<>();
    private Map<String, Map<String, String>> userSectionRules = new HashMap<>();
    private Map<String, String> userGeneralRules = new HashMap<>();
    private XmlRulesGetter rulesGetter = new XmlRulesGetter();
    private Logger log = Logger.getLogger(getClass().getName());

    Font ruleFont = Font.font("Times New Roman", 15);
    Font keyFont = Font.font("Times New Roman", FontWeight.BOLD, 14);

    @FXML
    public void initialize() {
        rulesScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        rulesScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        rulesScrollPane.setContent(container);

        schemas.put("ВКРБ", "templates/VKRBfullSchema.xsd");
        schemas.put("ВКР", "templates/VKRBschema.xsd");

        for (Map.Entry<String, String> entry : schemas.entrySet()) {
            documentTypeList.add(entry.getKey());
        }
        documentTypeChoiceBox.setValue(documentTypeList.get(0));
        documentTypeChoiceBox.setItems(documentTypeList);

        chooseSchema();
        documentTypeChoiceBox.setOnAction(event -> chooseSchema());
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
    void chooseSchema() {
        container.getChildren().clear();
        Path xsdPath = null;
        for (Map.Entry<String, String> entry : schemas.entrySet()) {
            if (entry.getKey().equals(documentTypeChoiceBox.getValue())) {
                xsdPath = Paths.get(entry.getValue());
            }
        }
        Map<String, Map<String, String>> xmlSectionRules = rulesGetter.getDocumentRules(xsdPath);
        Map<String, String> xmlGeneralRules = rulesGetter.getGeneralRules(xsdPath);
        genRulesLabel.setFont(keyFont);

        for (Map.Entry<String, Map<String, String>> entry : xmlSectionRules.entrySet()) {
            Label key = new Label();
            key.setFont(keyFont);
            key.setText(entry.getKey());
            container.getChildren().add(key);
            Map<String, String> valueMap = entry.getValue();
            for (Map.Entry<String, String> stringEntry : valueMap.entrySet()) {
                Label rule = new Label();
                rule.setFont(ruleFont);
                rule.setText(stringEntry.getKey());
                container.getChildren().add(rule);
                String value = stringEntry.getValue();
                if (value.equals("true") || value.equals("false")) {
                    CheckBox checkBox = new CheckBox();
                    checkBox.setSelected(Boolean.parseBoolean(value));
                    container.getChildren().add(checkBox);
                } else if (value.matches("[\\d]")) {
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
                    container.getChildren().add(textField);
                } else {
                    TextField textField = new TextField();
                    textField.setText(value);
                    container.getChildren().add(textField);
                }
            }
        }

        container.getChildren().add(genRulesLabel);
        for (Map.Entry<String, String> entry : xmlGeneralRules.entrySet()) {
            Label rule = new Label();
            rule.setFont(ruleFont);
            rule.setText(entry.getKey());
            container.getChildren().add(rule);
            String value = entry.getValue();
            if (value.equals("true") || value.equals("false")) {
                CheckBox checkBox = new CheckBox();
                checkBox.setSelected(Boolean.parseBoolean(value));
                container.getChildren().add(checkBox);
            } else if (value.matches("[\\d]")) {
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
                container.getChildren().add(textField);
            } else {
                TextField textField = new TextField();
                textField.setText(value);
                container.getChildren().add(textField);
            }
        }
    }

    void getRules() {
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
                    value = "true";
                } else {
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
    }

    @FXML
    void clickValidateButton() {
        if (docFile != null) {
            getRules();
            reportTextArea.clear();
            Validator validator = new Validator();
            List<String> report = validator.validate(documentTypeChoiceBox.getValue(), docFile.toPath(),
                    userGeneralRules, userSectionRules);
            for (int i = 0; i < report.size(); i++) {
                reportTextArea.appendText(report.get(i) + "\n");
            }
        }
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
}