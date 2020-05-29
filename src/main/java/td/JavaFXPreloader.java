package td;

import javafx.application.Preloader;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class JavaFXPreloader extends Preloader {
    private Stage stage;

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;

        FXMLLoader loader = new FXMLLoader();
        Parent root = (Parent) loader.load(getClass().getResourceAsStream("/fxml/preloader.fxml"));
        Scene scene = new Scene(root, 480, 300);

        String stylesheet = getClass().getResource("/css/start.css").toExternalForm();
        scene.getStylesheets().add(stylesheet);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle("TechDoc");
        stage.getIcons().add(new Image("/images/clip.png"));
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void handleApplicationNotification(Preloader.PreloaderNotification info) {
        Preloader.ProgressNotification ntf = (Preloader.ProgressNotification) info;
        if (ntf.getProgress() == 1.0) {
            stage.hide();
        }
    }
}
