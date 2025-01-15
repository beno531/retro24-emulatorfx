package de.ostfalia.retro24emulatorfx;

import javafx.animation.Timeline;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.File;

public class MainWindow {

    private final Stage mainStage;
    private final PPU ppu;
    private final CPU cpu;
    private final Memory memory;
    private final Loader loader;
    private final Timeline gameLoop;
    private final DebugWindow debugWindow;
    private boolean isIoRefreshable = false;

    public MainWindow(Stage stage, CPU cpu, Memory memory, PPU ppu, Loader loader, Timeline gameLoop, DebugWindow debugWindow) {
        this.mainStage = stage;
        this.cpu = cpu;
        this.memory = memory;
        this.ppu = ppu;
        this.loader = loader;
        this.gameLoop = gameLoop;
        this.debugWindow = debugWindow;
        initialize();
    }

    private void initialize() {
        mainStage.setTitle("Retro-24 Emulator");

        MenuBar menuBar = new MenuBar();
        Menu menuFile = new Menu("Program");

        // Menüpunkt "Load Bin"
        MenuItem loadRomItem = new MenuItem("Load Bin");
        loadRomItem.setOnAction(e -> {
            FileChooser f = new FileChooser();
            f.setTitle("Open Bin File");
            File file = f.showOpenDialog(mainStage);

            if (file != null) {
                loadProgram(file.getPath());
            }
        });

        // Menüpunkt "Reset"
        MenuItem resetItem = new MenuItem("Reset");
        resetItem.setOnAction(e -> {
            hardReset();
        });

        // Menüpunkt "Show Debug Window"
        MenuItem debugItem = new MenuItem("Debug Window");
        debugItem.setOnAction(e -> {
            showDebugWindow();
        });

        // Menüpunkt "Exit"
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(e -> {
            System.exit(0);
        });

        menuFile.getItems().add(loadRomItem);
        menuFile.getItems().add(resetItem);
        menuFile.getItems().add(debugItem);
        menuFile.getItems().add(exitItem);

        menuBar.getMenus().add(menuFile);

        VBox root = new VBox();
        root.getChildren().add(menuBar);
        root.getChildren().add(ppu);

        Scene mainScene = new Scene(root);

        mainScene.setOnKeyPressed(event -> {

            if (isIoRefreshable){

                // TODO: Reset Input

                KeyCode keyCode = event.getCode();

                System.out.println("Key pressed: " + keyCode.getName());

                if (keyCode == KeyCode.W || keyCode == KeyCode.UP) {
                    memory.write(0x0020, 0b00000001);
                }else if (keyCode == KeyCode.A || keyCode == KeyCode.LEFT) {
                    memory.write(0x0020, 0b00000100);
                }else if (keyCode == KeyCode.S || keyCode == KeyCode.DOWN) {
                    memory.write(0x0020, 0b00000010);
                }else if (keyCode == KeyCode.D || keyCode == KeyCode.RIGHT) {
                    memory.write(0x0020, 0b00001000);
                }else if (keyCode == KeyCode.SPACE) {
                    memory.write(0x0020, 0b00010000);
                }

                isIoRefreshable = false;
            }
        });


        mainStage.setScene(mainScene);
        mainStage.setResizable(false);

        mainStage.setOnCloseRequest(event -> {
            debugWindow.close();

            for (Stage memorywindow : debugWindow.memoryWindows) {
                memorywindow.close();
            }
            debugWindow.memoryWindows.clear();
        });

        mainStage.show();
    }

    public void setIoRefreshable(boolean ioRefreshable) {
        isIoRefreshable = ioRefreshable;
        memory.write(0x0020, 0x00);
    }

    private void showDebugWindow() {
        debugWindow.show();
    }

    private void loadProgram(String program) {
        gameLoop.stop();
        softReset();
        loader.writeProgram(program);

        if (!debugWindow.isShowing()) {
            gameLoop.play();
        }
    }

    private void softReset() {
        memory.clearAll();
        cpu.reset();
        ppu.render();
    }

    private void hardReset() {
        memory.clearAll();
        cpu.reset();
        memory.setTitle();
        ppu.render();
    }


    public void updateDebugInfo() {
        debugWindow.updateDebugInfo();
    }
}