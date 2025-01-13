package de.ostfalia.retro24emulatorfx;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Emulator extends Application {
    private Timeline gameLoop;
    private Stage mainStage;
    private Memory memory;
    private CPU cpu;
    private PPU ppu;
    private Loader loader;
    private MainWindow mainWindow;
    private DebugWindow debugWindow;

    private void initialize() {
        memory = new Memory();

        loader = new Loader();
        loader.connectMemory(memory);

        ppu = new PPU();
        ppu.connectMemory(memory);

        cpu = new CPU();
        cpu.connectMemory(memory);

        memory.setTitle();
        ppu.render();


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
                        // Setzt DrawFlag auf False;
                        memory.write(0x000A, 0x00);
                    }

                    mainWindow.updateDebugInfo();
                }
        );
        gameLoop.getKeyFrames().add(kf);

        debugWindow = new DebugWindow(cpu, memory, gameLoop);

        mainWindow = new MainWindow(mainStage, cpu, memory, ppu, loader, gameLoop, debugWindow);
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
