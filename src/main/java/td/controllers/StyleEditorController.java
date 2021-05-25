package td.controllers;


import database.DbService;
import database.DbSql;
import database.models.ParagraphRule;
import database.models.TextRangeRule;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Window;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class StyleEditorController extends Window {
    @FXML
    private Label xsdNameLabel;

    @FXML
    private ComboBox<String> styleListCB;

    @FXML
    private ComboBox<String> alignCB;

    @FXML
    private TextField lineSpaceTF;

    @FXML
    private TextField indentTF;

    @FXML
    private CheckBox boldCheck;

    @FXML
    private ComboBox<String> fontNameCB;

    @FXML
    private TextField sizeTF;

    @FXML
    private ColorPicker colorPicker;

    @FXML
    private CheckBox italicCheck;

    @FXML
    private CheckBox capsCheck;

    @FXML
    private Button acceptButton;

    private String xsdPath;
    private int tempID;
    private String tempName;
    private List<Integer> styleIdList = new ArrayList<>();
    private final ObservableList<String> styleList = FXCollections.observableArrayList();
    private final ObservableList<String> alignList = FXCollections.observableArrayList();
    private final ObservableList<String> fontList = FXCollections.observableArrayList();
    private final Logger log = Logger.getLogger(getClass().getName());
    private final DbSql dbSql = new DbSql();

    @FXML
    public void initialize() {
        log.log(Level.INFO, "initialize()");
    }

    public void initData(String path) {
        log.log(Level.INFO, "initData( " + path + ")");
        xsdPath = path;
        tempID = dbSql.getTempID(xsdPath);
        tempName = dbSql.getTempName(xsdPath);
        styleIdList = dbSql.getTempStylesID(tempID);
        styleList.addAll(dbSql.getTempStyles(tempID));
        alignList.addAll(dbSql.getAlignments());
        fontList.addAll(dbSql.getFontNames());

        xsdNameLabel.setText(tempName);
        styleListCB.setItems(styleList);
        alignCB.setItems(alignList);
        fontNameCB.setItems(fontList);
        styleListCB.getSelectionModel().selectFirst();

        fill();
        styleListCB.setOnAction(event -> fill());
    }

    private void fill() {
        log.log(Level.INFO, "fill()");
        int curStyleID = dbSql.getStyleID(styleListCB.getValue());
        ParagraphRule pr = dbSql.getParaRule(curStyleID);
        TextRangeRule tr = dbSql.getTRRule(curStyleID);
        alignCB.getSelectionModel().select(pr.getAlignName());
        indentTF.setText(String.valueOf(pr.getParagraphIndent()));
        lineSpaceTF.setText(String.valueOf(pr.getLineSpace()));
        boldCheck.setSelected(tr.getTextBold());
        fontNameCB.getSelectionModel().select(tr.getFontName());
        sizeTF.setText(String.valueOf(tr.getFontSize()));
        colorPicker.setValue(Color.valueOf(tr.getTextColor()));
        italicCheck.setSelected(tr.getTextItalic());
        capsCheck.setSelected(tr.getTextCap());
    }

    private void setAcceptButton() {

    }
}
