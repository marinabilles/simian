package de.crispda.sola.multitester.runner;

import com.google.common.io.Resources;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class ExperimentRunner extends Application {

    public static void main(String[] args) {
        System.setProperty("prism.lcdtext", "false");
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(Resources.getResource("runner.fxml"));
        primaryStage.setTitle("Experiment Runner");
        primaryStage.setMinWidth(200);
        primaryStage.setMinHeight(200);
        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(Resources.getResource("runner.css").toString());
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(windowEvent -> {
            synchronized (lock) {
                controller.shutDown();
            }
        });
        primaryStage.show();
    }

    private static RunnerController controller;
    private static final Object lock = new Object();

    public static void setController(RunnerController c) {
        synchronized (lock) {
            controller = c;
        }
    }
}
