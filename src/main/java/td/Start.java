package td;

import com.sun.javafx.application.LauncherImpl;
import javafx.application.Application;
import javafx.application.Preloader;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import td.services.JMorfSdkManager;

public class Start extends Application {
    public static void main(String[] args) throws Exception {
//        MethodsOfSummarizationAndElementsOfText ms = new MethodsOfSummarizationAndElementsOfText();
//        ms.getKeyWords("слово");
//        JMorfSdk jMorfSdk = JMorfSdkFactory.loadFullLibrary();
//        launch(args);
        LauncherImpl.launchApplication(Start.class, JavaFXPreloader.class, args);
    }

    @Override
    public void init() {
        this.notifyPreloader(new Preloader.ProgressNotification(0.0));
        JMorfSdkManager jMorfSdkManager = new JMorfSdkManager();
        this.notifyPreloader(new Preloader.ProgressNotification(1.0));
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
