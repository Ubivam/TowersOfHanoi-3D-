package rs.ac.bg.etf.js150411d;

import javafx.animation.AnimationTimer;
import javafx.scene.layout.BorderPane;

public class GameWindow extends BorderPane {

    public static final double WIDTH = 1200;
    public static final double HEIGHT = 600;

    private static final String RESOURCES_PREFIX = "resources/";

    HanoiGroup hg = new HanoiGroup(this);
    public GameWindow() {
        this.getChildren().add(hg);
    }

}
