package de.ostfalia.retro24emulatorfx;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
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
    private KeyFrame kf;

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


        kf = new KeyFrame(
                Duration.seconds(0.01),  // 100 Hz => 0.01 Sekunden = 10 Millisekunden
                this::handleAction
        );


        gameLoop.getKeyFrames().add(kf);

        debugWindow = new DebugWindow(this, cpu, memory, gameLoop);

        mainWindow = new MainWindow(mainStage, cpu, memory, ppu, loader, gameLoop, debugWindow);
    }

    private void handleAction(ActionEvent event) {

        mainWindow.setIoRefreshable(true);

        if (!cpu.getHlt()){
            cpu.tick();

        }

        if (ppu.isDrawFlag()){
            ppu.render();
            // Setzt DrawFlag auf False;
            memory.write(0x000A, 0x00);
        }

       System.out.println( memory.read(0x0020));

        mainWindow.updateDebugInfo();
        //memory.write(0x0020, 0x00);
    }

    public void changeClockSpeed(double speed){
        kf = new KeyFrame(
                Duration.seconds(speed),  // 100 Hz => 0.01 Sekunden = 10 Millisekunden
                this::handleAction
        );
        gameLoop.getKeyFrames().set(0, kf);
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
