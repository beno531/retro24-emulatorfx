package de.ostfalia.retro24emulatorfx;

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.*;

public class Emulator extends Application {
    Timeline gameLoop;
    private Stage mainStage;
    private Memory memory;
    private CPU cpu;
    private PPU ppu;
    private Loader loader;

    private void initialize() {
        mainStage.setTitle("Retro-24 Emulator");

        memory = new Memory();

        loader = new Loader();
        loader.connectMemory(memory);

        ppu = new PPU();
        ppu.connectMemory(memory);

        cpu = new CPU();
        cpu.connectMemory(memory);

        // Initialize menu that contains buttons for exiting and switching applications to run.
        MenuBar menuBar = new MenuBar();
        Menu menuFile = new Menu("Program");

        MenuItem loadRomItem = new MenuItem("Load Bin");
        loadRomItem.setOnAction(e -> {
            // Open file choose to let the user select a ROM.
            FileChooser f = new FileChooser();
            f.setTitle("Open Bin File");
            File file = f.showOpenDialog(mainStage);

            if (file != null) {
                loadProgram(file.getPath());
            }
        });

        MenuItem resetItem = new MenuItem("Reset");
        resetItem.setOnAction(e -> {
            hardReset();
        });

        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(e -> {
            System.exit(0);
        });

        menuFile.getItems().add(loadRomItem);
        menuFile.getItems().add(resetItem);
        menuFile.getItems().add(exitItem);

        menuBar.getMenus().add(menuFile);

        // Place all elements into the main window.
        VBox root = new VBox();
        root.getChildren().add(menuBar);
        root.getChildren().add(ppu);

        Scene mainScene = new Scene(root);

        // Set up the main window for show.
        mainStage.setScene(mainScene);
        mainStage.setResizable(false);

        gameLoop = new Timeline();
        gameLoop.setCycleCount(Timeline.INDEFINITE);

        // Construct the keyframe telling the application what to happen inside the game loop.
        KeyFrame kf = new KeyFrame(
                Duration.seconds(0.01),  // 100 Hz => 0.01 Sekunden = 10 Millisekunden
                actionEvent -> {

                    if (!cpu.getHlt()){
                        cpu.tick();
                    }

                    if (ppu.isDrawFlag()){
                        ppu.render();
                    }
                }
        );

        gameLoop.getKeyFrames().add(kf);

        loader.loadStartProgramm();

        gameLoop.play();

        mainStage.show();
    }

    public void loadProgram(int[] program) {
        loader.writeProgram(program);
    }

    private void loadProgram(String program) {
        gameLoop.stop();
        softReset();
        loader.writeProgram(program);
        gameLoop.play();
    }

    private void softReset(){
        memory.clearProgram();
        memory.clearVideo();
        cpu.reset();
    }

    private void hardReset(){
        memory.clearAll();
        cpu.reset();
        loader.loadStartProgramm();
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        mainStage = primaryStage;
        initialize();
    }
}
