package td.controllers;

import database.DbSql;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Window;

import java.util.logging.Level;
import java.util.logging.Logger;


public class TemplateEditorController extends Window {
    @FXML
    private GridPane gridPane;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private Button newParaButton;

    private final Logger log = Logger.getLogger(getClass().getName());
    private final ObservableList<String> styleList = FXCollections.observableArrayList();
    private final DbSql dbSql = new DbSql();

    @FXML
    public void initialize() {
        log.log(Level.INFO, "Открыто окно форма шаблона");
    }

    protected void initData(String path) {
        styleList.addAll(dbSql.getStyleNames());
        if (path.isEmpty()) {
            createPara();
        }
    }

    @FXML
    private void createPara() {
        GridPane gridPanePara = new GridPane();
        gridPanePara.setPadding(new Insets(10));
        gridPanePara.setVgap(5);
        gridPanePara.setHgap(5);
        gridPanePara.addColumn(1);
        gridPane.addRow(gridPane.getRowCount());
        gridPane.add(gridPanePara, 0, gridPane.getRowCount());

        TextField textField = new TextField();
        textField.setPrefWidth(600);
        gridPanePara.add(textField, 0, 0); // col, row

        Button plusButton = new Button("+");
        plusButton.setOnAction(actionEvent -> createUnderPara(gridPanePara));
        plusButton.setTooltip(new Tooltip("Добавить подраздел"));
        gridPanePara.add(plusButton, 1, 0);

        Button minusButton = new Button("-");
        minusButton.setTooltip(new Tooltip("Удалить раздел"));
        gridPanePara.add(minusButton, 2, 0);

        ComboBox<String> styleListCB = new ComboBox<>();
        styleListCB.getSelectionModel().select(1);
        styleListCB.setItems(styleList);
        styleListCB.setPrefWidth(200);
        gridPanePara.add(styleListCB, 3, 0);
    }

    private void createUnderPara(GridPane gr) {
        TextField textField = new TextField();
        textField.setPrefWidth(600);
        gr.add(textField, 0, gr.getRowCount()); // col, row

        ComboBox<String> styleListCB = new ComboBox<>();
        styleListCB.getSelectionModel().select(1);
        styleListCB.setItems(styleList);
        styleListCB.setPrefWidth(200);
        gr.add(styleListCB, 3, gr.getRowCount() - 1);
    }
}
