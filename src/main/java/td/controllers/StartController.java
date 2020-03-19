package td.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.apache.poi.ss.formula.functions.T;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import td.templates.HeaderLevel;
import td.templates.headers.FirstLevelHeader;
import td.templates.headers.SecondLevelHeader;
import td.templates.headers.ThirdLevelHeader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
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

            List<FirstLevelHeader> firstLevelHeaders = new ArrayList<FirstLevelHeader>();
            List<SecondLevelHeader> secondLevelHeaders = new ArrayList<>();
            List<ThirdLevelHeader> thirdLevelHeaders = new ArrayList<>();

            for (XWPFParagraph xwpfParagraph : paragraphs) {
                if (xwpfParagraph.getStyleID() != null && HeaderLevel.LEVEL_1.equals(document.getStyles().getStyle(xwpfParagraph.getStyleID()).getName())) {
                    FirstLevelHeader firstLevelHeader = new FirstLevelHeader();
                    firstLevelHeader.setTitle(xwpfParagraph.getText());
                    firstLevelHeaders.add(firstLevelHeader);
                } else if (xwpfParagraph.getStyleID() != null && HeaderLevel.LEVEL_2.equals(document.getStyles().getStyle(xwpfParagraph.getStyleID()).getName())) {
                    SecondLevelHeader secondLevelHeader = new SecondLevelHeader();
                    secondLevelHeader.setTitle(xwpfParagraph.getText());
                    secondLevelHeaders.add(secondLevelHeader);
                } else if (xwpfParagraph.getStyleID() != null && HeaderLevel.LEVEL_3.equals(document.getStyles().getStyle(xwpfParagraph.getStyleID()).getName())) {
                    ThirdLevelHeader thirdLevelHeader = new ThirdLevelHeader();
                    thirdLevelHeader.setTitle(xwpfParagraph.getText());
                    thirdLevelHeaders.add(thirdLevelHeader);
                }
            }

            System.out.println("FirstLevelHeader SIZE: " + firstLevelHeaders.size());
            for (FirstLevelHeader firstLevelHeader : firstLevelHeaders) {
                System.out.println("FirstLevelHeader: " + firstLevelHeader.toString());
            }

            System.out.println("SecondLevelHeader SIZE: " + secondLevelHeaders.size());
            for (SecondLevelHeader secondLevelHeader : secondLevelHeaders) {
                System.out.println("SecondLevelHeader: " + secondLevelHeader.toString());
            }

            System.out.println("ThirdLevelHeader SIZE: " + thirdLevelHeaders.size());
            for (ThirdLevelHeader thirdLevelHeader : thirdLevelHeaders) {
                System.out.println("ThirdLevelHeader: " + thirdLevelHeader.toString());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
