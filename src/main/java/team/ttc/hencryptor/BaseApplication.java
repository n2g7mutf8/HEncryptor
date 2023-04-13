package team.ttc.hencryptor;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class BaseApplication extends Application {

    private static BaseApplication instance;

    public static BaseApplication getInstance() {
        return instance;
    }

    private Scene scene;
    private FXMLLoader fxmlLoader;
    private Stage stage;

    @Override
    public void start(Stage stage) throws IOException {
        instance = this;

        this.stage = stage;
        this.fxmlLoader = new FXMLLoader(BaseApplication.class.getResource("main-view.fxml"));
        this.scene = new Scene(fxmlLoader.load(), 350, 620);

        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    public Scene getScene() {
        return scene;
    }

    public FXMLLoader getFxmlLoader() {
        return fxmlLoader;
    }

    public Stage getStage() {
        return stage;
    }
}