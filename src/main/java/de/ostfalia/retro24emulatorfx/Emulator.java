package de.ostfalia.retro24emulatorfx;

import de.ostfalia.util.Event;
import de.ostfalia.util.EventBus;
import de.ostfalia.util.EventObserver;
import de.ostfalia.util.EventType;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Emulator extends Application implements EventObserver {
    private Timeline gameLoop;
    private Stage mainStage;
    private Memory memory;
    private CPU cpu;
    private PPU ppu;
    private Loader loader;
    private MainWindow mainWindow;
    private DebugWindow debugWindow;
    private KeyFrame kf;
    private double clockSpeed = 0.0001;

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
        setClockSpeed(clockSpeed);

        debugWindow = new DebugWindow(cpu, memory, gameLoop);
        mainWindow = new MainWindow(mainStage, cpu, memory, ppu, loader, gameLoop, debugWindow);
    }

    // Hauptroutine des Emulators; Wird mittels Timeline gesteuert
    private void mainLoop(ActionEvent event) {

        mainWindow.setIoRefreshable(true);

        if (!cpu.getHlt()){
            cpu.cycle();
        }

        if (ppu.isDrawFlag()){
            ppu.render();
            resetDrawFlag();
        }

        debugWindow.updateDebugInfo();
    }

    private void resetDrawFlag(){
        memory.write(0x000A, 0x00);
    }

    @Override
    public void handleEvent(Event event) {
        if (event.getType() == EventType.CLOCK_SPEED_CHANGED) {
            changeClockSpeed();
        }
    }

    private void changeClockSpeed(){

        if (clockSpeed == 0.01){
            clockSpeed = 0.0001;
        } else {
            clockSpeed = 0.01;
        }

        setClockSpeed(clockSpeed);
    }

    private void setClockSpeed(double speed){

        var wasRunning = false;

        if(gameLoop.getStatus() == Timeline.Status.RUNNING){
            gameLoop.stop();
            wasRunning = true;
        }

        kf = new KeyFrame(
                Duration.seconds(speed),
                this::mainLoop
        );

        if(gameLoop.getKeyFrames().isEmpty()){
            gameLoop.getKeyFrames().add(kf);
        } else {
            gameLoop.getKeyFrames().set(0, kf);
        }

        if(wasRunning){
            gameLoop.play();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Emulator als Observer registrieren für CLOCK_SPEED_CHANGED Event
        EventBus.getInstance().registerObserver(this);

        mainStage = primaryStage;
        initialize();
    }
}
