package rs.ac.bg.etf.js150411d;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class TowersOfHanoi extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        GameWindow gameWindow = new GameWindow();
        Group root = new Group(gameWindow);
        Scene scene = new Scene(root, GameWindow.WIDTH,  GameWindow.HEIGHT, true, SceneAntialiasing.BALANCED);
        scene.setFill(Color.LIGHTGRAY);

        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        primaryStage.setResizable(false);
        primaryStage.setTitle("Towers of Hanoi");
        primaryStage.show();
    }

    public static void main(String [] args)
    {
        launch(args);
    }
}
