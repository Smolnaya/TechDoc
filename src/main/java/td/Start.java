package td;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Start extends Application {
    public static void main(String[] args) throws Exception {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        Parent root = (Parent) loader.load(getClass().getResourceAsStream("/fxml/start.fxml"));
        Scene scene = new Scene(root, 960, 600);
        String stylesheet = getClass().getResource("/css/start.css").toExternalForm();
        scene.getStylesheets().add(stylesheet);
        stage.setTitle("TechDoc");
        stage.getIcons().add(new Image("/images/clip.png"));
        stage.setScene(scene);
        stage.show();

    }
}
