package td.controllers;

import database.DbSql;
import database.models.Style;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Window;

import java.util.List;

public class StyleEditorController extends Window {

    @FXML
    private ComboBox<String> stylesComboBox;

    @FXML
    private ComboBox<String> fontNamesComboBox;

    @FXML
    private TextArea sizeTextArea;

    @FXML
    private CheckBox boldCheckBox;

    @FXML
    private CheckBox italicCheckBox;

    @FXML
    private CheckBox capsCheckBox;

    @FXML
    private ColorPicker colorPicker;

    @FXML
    private ComboBox<String> alignmentComboBox;

    @FXML
    private TextArea indentTextArea;

    @FXML
    private TextArea lineSpaceTextArea;

    private final DbSql dbSql = new DbSql();

    @FXML
    public void initialize() {
        ObservableList<String> fontNameList = FXCollections.observableArrayList();
        fontNameList.addAll(dbSql.selectFontNames());
        fontNamesComboBox.setItems(fontNameList);

        ObservableList<String> alignmentList = FXCollections.observableArrayList();
        alignmentList.addAll(dbSql.selectAlignments());
        alignmentComboBox.setItems(alignmentList);

        List<Style> styleList = dbSql.selectStyles();
        ObservableList<String> styleNameList = FXCollections.observableArrayList();
        if (styleList != null) {
            for (Style style : styleList) {
                styleNameList.add(style.getStyleName());
            }
        }
        stylesComboBox.setItems(styleNameList);
        stylesComboBox.getSelectionModel().select(0);

        assert styleList != null;
        chooseStyle(stylesComboBox.getValue(), styleList);
        stylesComboBox.setOnAction(event -> chooseStyle(stylesComboBox.getValue(), styleList));
    }

    @FXML
    public void chooseStyle(String currentStyleName, List<Style> styleList) {
        for (Style style : styleList) {
            if (style.getStyleName().equals(currentStyleName)) {
                fontNamesComboBox.setValue(style.getFontName());
                sizeTextArea.setText(String.valueOf(style.getFontSize()));
                boldCheckBox.setSelected(style.getBold());
                italicCheckBox.setSelected(style.getItalic());
                capsCheckBox.setSelected(style.getAllCaps());
                colorPicker.setValue(Color.web(style.getTextColor()));
                alignmentComboBox.setValue(style.getAlignment());
                indentTextArea.setText(String.valueOf(style.getIndent()));
                lineSpaceTextArea.setText(String.valueOf(style.getLineSpace()));
            }
        }
    }

    @FXML
    public void clickApplyButton() {
        Color color = colorPicker.getValue();
        System.out.println(color);
    }

}
