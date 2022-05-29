package td.controllers;

import database.DbSql;
import database.models.Template;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;


public class TemplateController extends Window {
    @FXML
    private Button newTempButton;

    @FXML
    private Button editTempButton;

    @FXML
    private ComboBox<String> templateComboBox;

    private final Logger log = Logger.getLogger(getClass().getName());
    private final ObservableList<String> documentTypeList = FXCollections.observableArrayList();
    private final Map<String, String> schemas = new HashMap<>();
    private final DbSql dbSql = new DbSql();

    @FXML
    public void initialize() {
        log.log(Level.INFO, "Открыто окно шаблонов");
        List<Template> templateList = dbSql.selectTemplates();
        if (templateList != null) {
            for (Template t : templateList) {
                schemas.put(t.getTempName(), t.getTempPath());
            }
        }
        for (Map.Entry<String, String> entry : schemas.entrySet()) {
            documentTypeList.add(entry.getKey());
        }

        templateComboBox.setItems(documentTypeList);
        templateComboBox.getSelectionModel().select(3);
    }

    @FXML
    private void openNewForm() {
        log.log(Level.INFO, "Открывается форма Новый Шаблон");
        openTemplateForm(newTempButton, "");
    }

    @FXML
    private void openEditForm() {
        log.log(Level.INFO, "Открывается форма Редактировать Шаблон");
        String xsdPath = null;
        for (Map.Entry<String, String> entry : schemas.entrySet()) {
            if (entry.getKey().equals(templateComboBox.getValue())) {
                xsdPath = entry.getValue();
            }
        }
        openTemplateForm(editTempButton, xsdPath);
    }

    private void openTemplateForm(Button button, String xsdPath) {
        log.log(Level.INFO, "Открывается форма Шаблоны");
        Stage stage = new Stage();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/templateForm.fxml"));
            Parent root = loader.load();
            root.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/start.css")).toExternalForm());
            stage.setTitle(button.getText());
            stage.getIcons().add(new Image("/images/clip.png"));
            stage.setScene(new Scene(root, 960, 600));
            stage.setResizable(true);
            stage.initModality(Modality.WINDOW_MODAL);
            TemplateEditorController controller = loader.getController();
            controller.initData(xsdPath);
            stage.initOwner(button.getScene().getWindow());
            stage.show();
        } catch (IOException ex) {
            log.log(Level.WARNING, ex.getMessage());
        }
    }
}
