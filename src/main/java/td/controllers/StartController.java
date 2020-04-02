package td.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class StartController extends Window {

    @FXML
    public void initialize() {
    }

    @FXML
    void clickFileButton(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("ALL files", "*.docx");
        fileChooser.getExtensionFilters().add(extensionFilter);
        File file = fileChooser.showOpenDialog(this);
        if (file != null) {
            System.out.println("Открывается файл");
        }

        try {
            XWPFDocument document = new XWPFDocument(Files.newInputStream(file.toPath()));
            List<XWPFParagraph> paragraphs = document.getParagraphs();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
