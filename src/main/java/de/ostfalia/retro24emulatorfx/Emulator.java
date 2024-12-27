package de.ostfalia.retro24emulatorfx;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
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

        MenuBar menuBar = new MenuBar();
        Menu menuFile = new Menu("Program");

        MenuItem loadRomItem = new MenuItem("Load Bin");
        loadRomItem.setOnAction(e -> {

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


        VBox root = new VBox();
        root.getChildren().add(menuBar);
        root.getChildren().add(ppu);

        Scene mainScene = new Scene(root);


        mainStage.setScene(mainScene);
        mainStage.setResizable(false);

        gameLoop = new Timeline();
        gameLoop.setCycleCount(Timeline.INDEFINITE);


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

        memory.setTitle();
        ppu.render();

        mainStage.show();
    }

    private void loadProgram(String program) {
        gameLoop.stop();
        softReset();
        loader.writeProgram(program);
        gameLoop.play();
    }

    private void softReset(){
        memory.clearAll();
        cpu.reset();
        ppu.render();
    }

    private void hardReset(){
        memory.clearAll();
        cpu.reset();
        memory.setTitle();
        ppu.render();
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
