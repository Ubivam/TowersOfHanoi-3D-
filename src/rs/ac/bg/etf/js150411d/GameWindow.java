package rs.ac.bg.etf.js150411d;

import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.Accordion;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.util.StringConverter;
import rs.ac.bg.etf.js150411d.animations.DiskFlipper;
import rs.ac.bg.etf.js150411d.animations.DiskMover;
import rs.ac.bg.etf.js150411d.gameflow.DiskMove;
import rs.ac.bg.etf.js150411d.gameflow.GameSolver;
import rs.ac.bg.etf.js150411d.gameflow.RecursiveGameSolver;


public class GameWindow extends BorderPane {

    private enum GameDemoStates {NOT_RUNNING, RUNNING, PAUSED, FINISHED}

    private static final String RESOURCES_PATH = "resources/ui/";

    public static final double WIDTH = 1200;
    public static final double HEIGHT = 800;

    private static final double ROTATE_SPEED = 0.5;
    private static final double MIN_ANGLE_X = -85;
    private static final double MAX_ANGLE_X = 0;
    private static final double DEFAULT_ANGLE_X = -30;
    private static final double MIN_ANGLE_Y = -85;
    private static final double MAX_ANGLE_Y = 85;
    private static final double DEFAULT_ANGLE_Y = -30;
    private static final double DEFAULT_TRANSLATE_Z = -700;

    private static final int MIN_NUM_DISKS = 3;
    private static final int MAX_NUM_DISKS = 10;
    private static final int DEFAULT_NUM_DISKS = MIN_NUM_DISKS;
    private static final double SLIDER_ANIMATION_SPEED_MIN = -1;
    private static final double SLIDER_ANIMATION_SPEED_MAX = 1;
    private static final double SLIDER_ANIMATION_SPEED_DEFAULT = 0;
    private static final int FPS = 60;
    private static final ImageView IMAGE_VIEW_PLAY = new ImageView(new Image(Thread.currentThread().getContextClassLoader().getResource(RESOURCES_PATH + "play.png").toString()));
    private static final ImageView IMAGE_VIEW_PAUSE = new ImageView(new Image(Thread.currentThread().getContextClassLoader().getResource(RESOURCES_PATH + "pause.png").toString()));
    private static final ImageView IMAGE_VIEW_STOP = new ImageView(new Image(Thread.currentThread().getContextClassLoader().getResource(RESOURCES_PATH + "stop.png").toString()));

    private static final double MIN_SPECULAR_POWER = 0;
    private static final double MAX_SPECULAR_POWER = 200;
    private static final double DEFAULT_SPECULAR_POWER = 32;
    private static final int DEFAULT_ROD_INDEX = 1;

    private final Slider numberOfDisksSlider = new Slider(MIN_NUM_DISKS, MAX_NUM_DISKS, DEFAULT_NUM_DISKS);
    private final GridPane numberOfDisksGrid = new GridPane();
    private final ChoiceBox<Shading> materialChoiceBox = new ChoiceBox<Shading>();
    private final Slider specularPowerSlider = new Slider(MIN_SPECULAR_POWER, MAX_SPECULAR_POWER, DEFAULT_SPECULAR_POWER);
    private final ColorPicker specularColorPicker = new ColorPicker(Color.BLACK);
    private final ColorPicker lightColorPicker = new ColorPicker(Color.WHITE);
    private final Slider lightPositionXSlider = new Slider(MIN_ANGLE_X, MAX_ANGLE_X, MIN_ANGLE_Y - DEFAULT_ANGLE_X);
    private final Slider lightPositionYSlider = new Slider(MIN_ANGLE_Y, MAX_ANGLE_Y, DEFAULT_ANGLE_Y);
    private final GridPane settingsGrid = new GridPane();
    private final AnchorPane settingsAnchorPane = new AnchorPane(settingsGrid);
    private final TitledPane settingsTitledPane = new TitledPane("Settings", settingsAnchorPane);

    private final Button startSolvingButton = new Button("Start Game");
    private final Button stopAndResetGameButton = new Button("Stop Game");
    private final Label labelDemo = new Label("Solution Demo");
    private final Slider speedSlider = new Slider(SLIDER_ANIMATION_SPEED_MIN, SLIDER_ANIMATION_SPEED_MAX, SLIDER_ANIMATION_SPEED_DEFAULT);
    private final GridPane speedGrid = new GridPane();
    private final GridPane buttonGrid = new GridPane();
    private final Button playButton = new Button();
    private final Button pauseButton = new Button();
    private final Button stopButton = new Button();
    private final GridPane solveGrid = new GridPane();
    private final AnchorPane solveAnchorPane = new AnchorPane(solveGrid);
    private final TitledPane solveTitledPane = new TitledPane("Solving", solveAnchorPane);

    private final Group cameraPosition = new Group();
    private final PointLight light = new PointLight(Color.WHITE);

    private final Rotate camPositionRotateAroundXAxis = new Rotate(DEFAULT_ANGLE_X, Rotate.X_AXIS);
    private final Rotate camPositionRotateAroundYAxis = new Rotate(DEFAULT_ANGLE_Y, Rotate.Y_AXIS);
    private final Translate camPositionTranslate = new Translate(0, 0, DEFAULT_TRANSLATE_Z);

    private final Translate camTranslate1 = new Translate(0, 0, 0);
    private final Rotate camRotateAroundXAxis = new Rotate(0, Rotate.X_AXIS);
    private final Rotate camRotateAroundYAxis = new Rotate(0, Rotate.Y_AXIS);
    private final Translate camTranslate2 = new Translate(0, 0, 0);

    private final Rotate lightRotateAroundXAxis = new Rotate(DEFAULT_ANGLE_X, Rotate.X_AXIS);
    private final Rotate lightRotateAroundYAxis = new Rotate(DEFAULT_ANGLE_Y, Rotate.Y_AXIS);

    private double startX, startY, endX, endY;
    private double deltaX, deltaY;

    private final HanoiGroup hanoi = new HanoiGroup(DEFAULT_NUM_DISKS, this);
    private final GameSolver gameSolver;
    private DiskMover diskMover;


    private GameDemoStates gameDemoState = GameDemoStates.NOT_RUNNING;

    private int currentRodIndex = DEFAULT_ROD_INDEX;

    public GameWindow() {
        gameSolver = new RecursiveGameSolver(hanoi);

        int currentRow = 0;

        Label labelNumOfDisks = new Label("Number of Disks");
        numberOfDisksGrid.add(labelNumOfDisks, 0, 0);
        GridPane.setHalignment(labelNumOfDisks, HPos.CENTER);

        numberOfDisksGrid.add(numberOfDisksSlider, 0, 1);
        GridPane.setHalignment(numberOfDisksSlider, HPos.CENTER);
        numberOfDisksGrid.setMaxWidth(0.20 * WIDTH);
        numberOfDisksSlider.setMajorTickUnit(1);
        numberOfDisksSlider.setMinorTickCount(0);
        numberOfDisksSlider.setShowTickLabels(true);
        numberOfDisksSlider.setSnapToTicks(true);
        numberOfDisksSlider.valueProperty().addListener((ChangeListener) (arg0, arg1, arg2) -> hanoi.setNumberOFDisks((int) numberOfDisksSlider.getValue()));

        settingsGrid.add(numberOfDisksGrid, currentRow, 0);
        GridPane.setHalignment(numberOfDisksGrid, HPos.CENTER);
        ColumnConstraints columnConstraintsNumberOfDisks = new ColumnConstraints();
        columnConstraintsNumberOfDisks.setPercentWidth(100);
        numberOfDisksGrid.getColumnConstraints().addAll(columnConstraintsNumberOfDisks);

        Label labelCamPosition = new Label("Camera Position");
        settingsGrid.add(labelCamPosition, currentRow++, 1);
        GridPane.setHalignment(labelCamPosition, HPos.CENTER);

        ChoiceBox<String> choiceBoxCamPosition = new ChoiceBox<String>();
        settingsGrid.add(choiceBoxCamPosition, currentRow++, 1);
        GridPane.setHalignment(choiceBoxCamPosition, HPos.CENTER);
        choiceBoxCamPosition.getItems().addAll("Left", "Center", "Right");
        choiceBoxCamPosition.getSelectionModel().select(DEFAULT_ROD_INDEX);
        choiceBoxCamPosition.getSelectionModel().selectedIndexProperty().addListener((ObservableValue<? extends Number> observableValue, Number number, Number number2) -> {
            currentRodIndex = number2.intValue();
            alignCamera(currentRodIndex);
        });
        Label labelMaterial = new Label("Material");
        settingsGrid.add(labelMaterial, currentRow++, 1);
        GridPane.setHalignment(labelMaterial, HPos.CENTER);

        settingsGrid.add(materialChoiceBox, currentRow++, 1);
        GridPane.setHalignment(materialChoiceBox, HPos.CENTER);
        materialChoiceBox.getItems().addAll(Shading.WOOD, Shading.CHARCOAL, Shading.GOLD, Shading.PLASTIC);
        materialChoiceBox.getSelectionModel().selectFirst();
        hanoi.setShading(materialChoiceBox.getSelectionModel().getSelectedItem());
        materialChoiceBox.getSelectionModel().selectedIndexProperty().addListener((ObservableValue<? extends Number> observerValue, Number number, Number number2) -> hanoi.setShading(materialChoiceBox.getItems().get((Integer) number2)));

        GridPane specularPowerGrid = new GridPane();
        Label labelSpecularPower = new Label("Specular Power");
        specularPowerGrid.add(labelSpecularPower,0,0);
        GridPane.setHalignment(labelSpecularPower, HPos.CENTER);

        specularPowerGrid.add(specularPowerSlider, 0 , 1);
        GridPane.setHalignment(specularPowerSlider, HPos.CENTER);
        specularPowerGrid.setMaxWidth(0.20 * WIDTH);
        specularPowerSlider.setMajorTickUnit((MAX_SPECULAR_POWER - MIN_SPECULAR_POWER) / 2);
        specularPowerSlider.setMinorTickCount(0);
        specularPowerSlider.setShowTickMarks(true);
        specularPowerSlider.setShowTickLabels(true);
        specularPowerSlider.setLabelFormatter(new StringConverter<Double>() {
            @Override
            public String toString(Double object) {
                if(object == MIN_SPECULAR_POWER){
                    return "Low";
                }
                else if(object == (MAX_SPECULAR_POWER - MIN_SPECULAR_POWER)/2){
                    return "Medium";
                }else {
                    return "High";
                }
            }
            @Override
            public Double fromString(String string) {
                switch (string){
                    case "Low" :
                        return MIN_SPECULAR_POWER;
                    case "Fast":
                        return MAX_SPECULAR_POWER;
                    default:
                        return (MAX_SPECULAR_POWER - MIN_SPECULAR_POWER) / 2;
                }
            }
        });
        specularPowerSlider.valueProperty().addListener((ChangeListener) (arg0, arg1, arg2) -> hanoi.getShading().setSpecularPower(specularPowerSlider.getValue()));

        settingsGrid.add(specularPowerGrid, 1, 0);
        GridPane.setHalignment(specularPowerGrid, HPos.CENTER);
        ColumnConstraints columnConstraintsSpecularPower = new ColumnConstraints();
        columnConstraintsSpecularPower.setPercentWidth(100);
        specularPowerGrid.getColumnConstraints().addAll(columnConstraintsSpecularPower);


        Label labelSpecularColor = new Label("Specualr Color");
        settingsGrid.add(labelSpecularColor, currentRow++, 0);
        GridPane.setHalignment(labelSpecularColor, HPos.CENTER);

        settingsGrid.add(specularColorPicker, currentRow, 0);
        GridPane.setHalignment(specularColorPicker, HPos.CENTER);
        specularColorPicker.setOnAction((event) -> hanoi.getShading().setSpecularColor(specularColorPicker.getValue()));
        currentRow--;

        Label labelLightColor = new Label("Light Color");
        settingsGrid.add(labelLightColor, currentRow++, 1);
        GridPane.setHalignment(labelLightColor, HPos.CENTER);

        settingsGrid.add(lightColorPicker, currentRow++, 1);
        GridPane.setHalignment(lightColorPicker, HPos.CENTER);
        lightColorPicker.setOnAction((event) -> light.setColor(lightColorPicker.getValue()));

        GridPane lightPositionGrid = new GridPane();
        Label labelLightPosition = new Label("Light Position");
        lightPositionGrid.add(labelLightPosition,0,0);
        GridPane.setHalignment(labelLightPosition, HPos.CENTER);

        lightPositionGrid.add(lightPositionXSlider,0,1);
        GridPane.setHalignment(lightPositionXSlider, HPos.CENTER);

        lightPositionXSlider.setMaxWidth(0.20 * WIDTH);
        lightPositionXSlider.setMajorTickUnit((MAX_ANGLE_X - MIN_ANGLE_Y) / 2);
        lightPositionXSlider.setMinorTickCount(0);
        lightPositionXSlider.setShowTickMarks(true);
        lightPositionXSlider.setShowTickLabels(true);
        lightPositionXSlider.setLabelFormatter(new StringConverter<Double>() {
            @Override
            public String toString(Double object) {
                if(object == MIN_ANGLE_X){
                    return "Front";
                }
                else if(object == MIN_ANGLE_X + (MAX_ANGLE_X- MIN_ANGLE_X) / 2){
                    return "Above";
                } else {
                    return "Back";
                }
            }

            @Override
            public Double fromString(String string) {
                switch (string){
                    case "Front" :
                        return MIN_ANGLE_X;
                    case "Above":
                        return MIN_ANGLE_X + (MAX_ANGLE_X - MIN_ANGLE_X) / 2;
                    case "Back":
                        return MAX_ANGLE_X;
                    default:
                        return (MAX_ANGLE_X - MIN_ANGLE_X) / 2;
                }
            }
        });
        lightPositionXSlider.valueProperty().addListener((ChangeListener) (arg0, arg1, arg2) -> lightRotateAroundXAxis.setAngle(MIN_ANGLE_X - lightPositionXSlider.getValue()));

        lightPositionGrid.add(lightPositionYSlider,0,2);
        GridPane.setHalignment(lightPositionYSlider, HPos.CENTER);
        lightPositionYSlider.setMaxWidth(0.20 * WIDTH);
        lightPositionYSlider.setMajorTickUnit((MAX_ANGLE_Y - MIN_ANGLE_Y) / 2);
        lightPositionYSlider.setMinorTickCount(0);
        lightPositionYSlider.setShowTickMarks(true);
        lightPositionYSlider.setShowTickLabels(true);
        lightPositionYSlider.setLabelFormatter(new StringConverter<Double>() {
            @Override
            public String toString(Double object) {
                if(object == MIN_ANGLE_Y){
                    return "Left";
                }
                else if(object == MIN_ANGLE_Y + (MAX_ANGLE_Y- MIN_ANGLE_Y) / 2){
                    return  "Center";
                } else {
                    return "Right";
                }
            }

            @Override
            public Double fromString(String string) {
                switch (string){
                    case "Left":
                        return MIN_ANGLE_Y;
                    case "Center":
                        return MIN_ANGLE_Y + (MAX_ANGLE_Y- MIN_ANGLE_Y) / 2;
                    case "Right":
                        return MAX_ANGLE_Y;
                    default:
                        return (MAX_ANGLE_Y - MIN_ANGLE_Y) / 2;
                }
            }
        });

        lightPositionYSlider.valueProperty().addListener((ChangeListener) (arg0, arg1, arg2) -> lightRotateAroundYAxis.setAngle(-lightPositionYSlider.getValue()));

        settingsGrid.add(lightPositionGrid,currentRow++,0);
        GridPane.setHalignment(lightPositionGrid, HPos.CENTER);
        ColumnConstraints columnConstraintsLightPosition = new ColumnConstraints();
        columnConstraintsLightPosition.setPercentWidth(100);
        lightPositionGrid.getColumnConstraints().addAll(columnConstraintsLightPosition);

        ColumnConstraints columnConstraintsSettings1 = new ColumnConstraints();
        columnConstraintsSettings1.setPercentWidth(40);
        columnConstraintsSettings1.setFillWidth(true);
        ColumnConstraints columnConstraintsSettings2 = new ColumnConstraints();
        columnConstraintsSettings2.setPercentWidth(60);
        columnConstraintsSettings2.setFillWidth(true);
        settingsGrid.getColumnConstraints().addAll(
                columnConstraintsSettings1,
                columnConstraintsSettings2
        );
        AnchorPane.setLeftAnchor(settingsGrid, 0d);
        AnchorPane.setRightAnchor(settingsGrid, 520d);
        AnchorPane.setTopAnchor(settingsGrid, 0d);
        AnchorPane.setBottomAnchor(settingsGrid, 0d);
        settingsGrid.setVgap(5);
        settingsGrid.setHgap(1);

        settingsTitledPane.setFont(Font.font(14));


        //DEMONSTRATION

        currentRow = 0;

        Label playGameLabel = new Label("Play Game");
        solveGrid.add(playGameLabel,0,currentRow++);
        GridPane.setHalignment(playGameLabel, HPos.CENTER);
        solveGrid.add(startSolvingButton, 0 , currentRow++);
        GridPane.setHalignment(startSolvingButton, HPos.CENTER);

        startSolvingButton.setOnAction(e->{
            if(gameDemoState == GameDemoStates.NOT_RUNNING || gameDemoState == GameDemoStates.FINISHED){
                hanoi.reset();

                labelDemo.setDisable(true);
                stopAndResetGameButton.setDisable(false);
                startSolvingButton.setDisable(true);
                playButton.setDisable(true);
                speedGrid.setDisable(true);
                numberOfDisksGrid.setDisable(true);

                hanoi.setInteractionEnabled(true);
            }
        });

        solveGrid.add(stopAndResetGameButton,0,currentRow++);
        GridPane.setHalignment(stopAndResetGameButton, HPos.CENTER);
        stopAndResetGameButton.setDisable(true);
        stopAndResetGameButton.setOnAction(event -> {
            hanoi.reset();

            labelDemo.setDisable(false);
            stopAndResetGameButton.setDisable(true);
            startSolvingButton.setDisable(false);
            playButton.setDisable(false);
            speedGrid.setDisable(false);
            numberOfDisksGrid.setDisable(false);
            if(hanoi.getSelectedDisk() != null){
                hanoi.getSelectedDisk().setSelected(false);
            }
            hanoi.setSelectedDisk(null);

            hanoi.setInteractionEnabled(false);
        });
        solveGrid.add(labelDemo,currentRow,4);

        Label labelSpeed = new Label("Speed");
        speedGrid.add(labelSpeed,0,0);
        GridPane.setHalignment(labelSpeed, HPos.CENTER);

        speedSlider.setMaxWidth(0.20 * WIDTH);
        speedSlider.setLabelFormatter(new StringConverter<Double>() {
            @Override
            public String toString(Double object) {
                if(object == SLIDER_ANIMATION_SPEED_MIN){
                    return "Slow";
                } else if(object == SLIDER_ANIMATION_SPEED_DEFAULT){
                    return "Normal";
                } else {
                    return "Fast";
                }
            }

            @Override
            public Double fromString(String string) {
                switch (string){
                    case "Slow":
                        return SLIDER_ANIMATION_SPEED_MIN;
                    case "Fast":
                        return SLIDER_ANIMATION_SPEED_MAX;
                    default:
                        return SLIDER_ANIMATION_SPEED_DEFAULT;
                }
            }
        });

        speedSlider.setMinorTickCount(0);
        speedSlider.setMajorTickUnit(1);
        speedSlider.setShowTickMarks(true);
        speedSlider.setShowTickLabels(true);
        speedSlider.valueProperty().addListener((ChangeListener) (arg0, arg1, arg2) -> {
            if(gameDemoState == GameDemoStates.PAUSED || gameDemoState == GameDemoStates.RUNNING){
                var sliderValue = speedSlider.getValue();
                if(sliderValue >= 0){
                    diskMover.setAnimationDuration(DiskMover.DELFAULT_DURATION - sliderValue* 0.90 * DiskMover.DELFAULT_DURATION);
                } else {
                    diskMover.setAnimationDuration(DiskMover.DELFAULT_DURATION - sliderValue * 3 * DiskMover.DELFAULT_DURATION);
                }
            }
        });
        speedGrid.add(speedSlider,0,1);
        GridPane.setHalignment(speedSlider, HPos.CENTER);
        solveGrid.add(speedGrid, 0, currentRow++,2,1);
        GridPane.setHalignment(speedGrid, HPos.CENTER);
        ColumnConstraints columnConstraintsSpeed = new ColumnConstraints();
        columnConstraintsSpeed.setPercentWidth(50);
        speedGrid.getColumnConstraints().add(columnConstraintsSpeed);

        solveGrid.add(playButton,0,currentRow);
        GridPane.setHalignment(playButton, HPos.LEFT);
        playButton.setGraphic(IMAGE_VIEW_PLAY);
        playButton.setOnAction(event -> {
            switch (gameDemoState){
                case FINISHED:
                case NOT_RUNNING:
                    hanoi.reset();
                    gameSolver.solve();

                    diskMover = new DiskFlipper(hanoi, gameSolver.getDiskMoves());
                    var sliderValue = speedSlider.getValue();
                    if(sliderValue >= 0){
                        diskMover.setAnimationDuration(DiskMover.DELFAULT_DURATION - sliderValue * 0.90 * DiskMover.DELFAULT_DURATION);
                    } else {
                        diskMover.setAnimationDuration(DiskMover.DELFAULT_DURATION - sliderValue * 3 * DiskMover.DELFAULT_DURATION);
                    }
                    diskMover.start(()->{
                        stopButton.setDisable(true);
                        pauseButton.setDisable(true);
                        startSolvingButton.setDisable(false);
                        numberOfDisksGrid.setDisable(false);

                        gameDemoState = GameDemoStates.FINISHED;
                    });
                    stopButton.setDisable(true);
                    stopButton.setDisable(false);
                    pauseButton.setDisable(false);
                    startSolvingButton.setDisable(true);
                    numberOfDisksGrid.setDisable(true);

                    gameDemoState = GameDemoStates.RUNNING;
                    break;
                case RUNNING:
                    diskMover.pause();
                    pauseButton.setDisable(true);

                    gameDemoState = GameDemoStates.PAUSED;
                    break;
                case PAUSED:
                    diskMover.resume();

                    gameDemoState = GameDemoStates.RUNNING;

                    break;
            }
        });
        solveGrid.add(pauseButton, 0 ,currentRow);
        GridPane.setHalignment(pauseButton, HPos.CENTER);
        pauseButton.setGraphic(IMAGE_VIEW_PAUSE);
        pauseButton.setDisable(true);
        pauseButton.setOnAction(event -> {
            diskMover.pause();
            pauseButton.setDisable(true);
            stopButton.setDisable(true);
            gameDemoState = GameDemoStates.PAUSED;
        });
        solveGrid.add(stopButton,0,currentRow);
        GridPane.setHalignment(stopButton, HPos.RIGHT);
        stopButton.setGraphic(IMAGE_VIEW_STOP);
        stopButton.setDisable(true);
        stopButton.setOnAction(event -> {
            diskMover.stop();
            hanoi.reset();

            labelDemo.setDisable(false);
            playButton.setDisable(false);
            stopButton.setDisable(true);
            pauseButton.setDisable(true);
            startSolvingButton.setDisable(false);
            numberOfDisksGrid.setDisable(false);

            hanoi.setInteractionEnabled(false);
            gameDemoState = GameDemoStates.NOT_RUNNING;
        });
        ColumnConstraints columnConstraintsDemo = new ColumnConstraints();
        columnConstraintsDemo.setPercentWidth(50);
        columnConstraintsDemo.setFillWidth(true);
        solveGrid.getColumnConstraints().addAll(
                columnConstraintsDemo,
                columnConstraintsDemo
        );

        AnchorPane.setLeftAnchor(solveGrid, 0d);
        AnchorPane.setRightAnchor(solveGrid, -300d);
        AnchorPane.setTopAnchor(solveGrid, 0d);
        AnchorPane.setBottomAnchor(solveGrid, 0d);
        solveGrid.setVgap(10);
        solveGrid.setHgap(10);

        solveTitledPane.setFont(Font.font(14));

        settingsTitledPane.expandedProperty().addListener((Observable observable) ->  settingsTitledPane.setExpanded(true));
        solveTitledPane.expandedProperty().addListener((Observable observable) -> solveTitledPane.setExpanded(true));

        Accordion accordion1 = new Accordion(settingsTitledPane);
        accordion1.setExpandedPane(settingsTitledPane);
        accordion1.setMinWidth(WIDTH);
        accordion1.setPrefWidth(WIDTH);
        accordion1.setMaxWidth(WIDTH);
        accordion1.setMinHeight(0.25 * HEIGHT);
        accordion1.setPrefHeight(0.25 * HEIGHT);
        accordion1.setMaxHeight(0.25 * HEIGHT);
        BorderPane.setAlignment(accordion1, Pos.TOP_LEFT);
        setTop(accordion1);

        Accordion accordion2 = new Accordion(solveTitledPane);
        accordion2.setExpandedPane(solveTitledPane);
        accordion2.setMinWidth(0.25*WIDTH);
        accordion2.setPrefWidth(0.25*WIDTH);
        accordion2.setMaxWidth(0.25*WIDTH);
        accordion2.setMinHeight(0.75 * HEIGHT);
        accordion2.setPrefWidth(0.75 * HEIGHT);
        accordion2.setMaxHeight(0.75 * HEIGHT);
        BorderPane.setAlignment(accordion2, Pos.TOP_LEFT);
        setLeft(accordion2);

        //CENTER

        cameraPosition.getTransforms().addAll(camPositionRotateAroundYAxis,camPositionRotateAroundXAxis);
        cameraPosition.getTransforms().addAll(camPositionTranslate);

        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setFarClip(10000);
        Rotate camRotateAroundZAxis = new Rotate(0, Rotate.Z_AXIS);
        camera.getTransforms().addAll(camTranslate1, camRotateAroundYAxis, camRotateAroundXAxis, camTranslate2, camRotateAroundZAxis);
        alignCamera(currentRodIndex);

        light.getTransforms().addAll(lightRotateAroundXAxis,lightRotateAroundYAxis);
        Translate lightTranslate = new Translate(0,0, DEFAULT_TRANSLATE_Z);
        light.getTransforms().add(lightTranslate);

        Group gameGroup = new Group(hanoi,light,cameraPosition,camera);
        SubScene gameSubScene = new SubScene(gameGroup, WIDTH * 0.75, 0.75 * HEIGHT, true, SceneAntialiasing.BALANCED);
        gameSubScene.setOnScroll(event -> {
            var delta = event.getDeltaY();
            if(camPositionTranslate.getZ() + delta <= -Math.max(HanoiGroup.ROD_HEIGHT, HanoiGroup.PLATFORM_WIDTH/2)){
                camPositionTranslate.setZ(camPositionTranslate.getZ() + delta);
            }
            alignCamera(currentRodIndex);
        });
        gameSubScene.setOnMousePressed(event -> {
            startX = endX = event.getSceneX();
            startY = endY = event.getSceneY();
        });
        gameSubScene.setOnMouseDragged(event -> {
            startX = endX;
            startY = endY;

            endX = event.getSceneX();
            endY = event.getSceneY();

            deltaX = endX - startX;
            deltaY = endY - startY;

            camPositionRotateAroundXAxis.setAngle(camPositionRotateAroundXAxis.getAngle() + deltaY * ROTATE_SPEED);
            camPositionRotateAroundYAxis.setAngle(camPositionRotateAroundYAxis.getAngle() + deltaX * ROTATE_SPEED);

            if(camPositionRotateAroundXAxis.getAngle() > MAX_ANGLE_X) {
                camPositionRotateAroundXAxis.setAngle(MAX_ANGLE_X);
            } else if (camPositionRotateAroundXAxis.getAngle() < MIN_ANGLE_X){
                camPositionRotateAroundXAxis.setAngle(MIN_ANGLE_X);
            }

            if(camPositionRotateAroundYAxis.getAngle() > MAX_ANGLE_Y){
                camPositionRotateAroundYAxis.setAngle(MAX_ANGLE_Y);
            } else if (camPositionRotateAroundYAxis.getAngle() < MIN_ANGLE_Y){
                camPositionRotateAroundYAxis.setAngle(MIN_ANGLE_Y);
            }
            alignCamera(currentRodIndex);
        });
        gameSubScene.setFill(Color.LIGHTGRAY);
        gameSubScene.setCamera(camera);
        BorderPane.setAlignment(gameSubScene, Pos.TOP_CENTER);
        setCenter(gameSubScene);


    }

    public void alignCamera(int currentRodIndex) {
        Bounds camPositionBounds = cameraPosition.getBoundsInParent();

        final double xCamPosition = camPositionBounds.getMinX();
        final double yCamPosition = camPositionBounds.getMinY();
        final double zCamPosition = camPositionBounds.getMinZ();

        camTranslate1.setX((currentRodIndex - 1) * HanoiGroup.ROD_DISTANCE);
        camTranslate1.setY(-HanoiGroup.ROD_HEIGHT / 2);

        final double deltaX = xCamPosition - (currentRodIndex - 1) * HanoiGroup.ROD_DISTANCE;
        final double distanceInXZPlane = Math.sqrt(deltaX * deltaX + zCamPosition * zCamPosition);
        final double deltaY = yCamPosition + HanoiGroup.ROD_HEIGHT/ 2;

        camRotateAroundYAxis.setAngle(Math.toDegrees(Math.atan(deltaX / zCamPosition)) + (zCamPosition > 0 ? Math.signum(xCamPosition) * 180 : 0));
        camRotateAroundXAxis.setAngle(Math.toDegrees(Math.atan(deltaY / distanceInXZPlane)));
        camTranslate2.setZ(-Math.sqrt(distanceInXZPlane * distanceInXZPlane + deltaY * deltaY));
    }

    public Button getButtonGameReset() {
        return stopAndResetGameButton;
    }
}
