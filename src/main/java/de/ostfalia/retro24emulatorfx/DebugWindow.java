package de.ostfalia.retro24emulatorfx;

import de.ostfalia.util.Event;
import de.ostfalia.util.EventBus;
import de.ostfalia.util.EventType;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;

public class DebugWindow{

    private final CPU cpu;
    private final Memory memory;
    private final Timeline gameLoop;
    private final VBox debugTextBox;
    private Stage debugStage;
    public static final List<Stage> memoryWindows = new ArrayList<>();

    public DebugWindow(CPU cpu, Memory memory, Timeline gameLoop) {
        this.cpu = cpu;
        this.memory = memory;
        this.gameLoop = gameLoop;
        this.debugTextBox = new VBox(5);
    }

    public void show() {

        if (debugStage == null || !debugStage.isShowing()) {

            debugStage = new Stage();
            debugStage.setTitle("Debug Information");


            VBox cpuControlsBox = buildCpuControlsBox();

            VBox memorySnaphotControlBox = buildMemorySnapshotControlBox();


            VBox layout = new VBox(10);
            layout.setPadding(new Insets(10, 10, 10, 10));
            layout.getChildren().addAll(cpuControlsBox, memorySnaphotControlBox, debugTextBox);

            Scene debugScene = new Scene(layout, 400, 400);
            debugStage.setScene(debugScene);
            debugStage.setResizable(false);

            debugStage.setOnCloseRequest(event -> {

                for (Stage memorywindow : memoryWindows) {
                    memorywindow.close();
                }
                memoryWindows.clear();

                if(gameLoop.getStatus() == Timeline.Status.STOPPED || gameLoop.getStatus() == Timeline.Status.PAUSED){
                    gameLoop.play();
                }
            });

            debugStage.show();
            updateDebugInfo();
        } else {
            debugStage.toFront();
        }
    }

    private VBox buildMemorySnapshotControlBox() {

        Button ioMemory = new Button("IO-Memory");
        Button programMemory = new Button("Program-Memory");
        Button videoMemory = new Button("Video-Memory");

        ioMemory.setOnAction(e -> showMemorySnapshotWindow("Snapshot IO-Memory", 0x0000, 0x00FF));
        programMemory.setOnAction(e -> showMemorySnapshotWindow("Snapshot Program-Memory", 0x0100, 0xDFFF));
        videoMemory.setOnAction(e -> showMemorySnapshotWindow("Snapshot Video-Memory", 0xE000, 0xFFFF));

        HBox memoryButtonBox = new HBox(10);
        memoryButtonBox.getChildren().addAll(ioMemory, programMemory, videoMemory);

        VBox memoryDumbBox = new VBox(10);
        memoryDumbBox.getChildren().add(new Text("Memory Snapshots:"));
        memoryDumbBox.getChildren().add(memoryButtonBox);

        return memoryDumbBox;
    }

    private VBox buildCpuControlsBox() {
        Button startButton = new Button("Start");
        Button pauseButton = new Button("Pause");
        Button stepButton = new Button("Step");
        Button handbrakeButton = new Button("Change to 100Hz");

        startButton.setOnAction(e -> startLoop());
        pauseButton.setOnAction(e -> pauseLoop());
        stepButton.setOnAction(e -> stepLoop());
        handbrakeButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (handbrakeButton.getText().equals("Change to 100Hz")) {
                    handbrakeButton.setText("Change to 1kHz");
                } else {
                    handbrakeButton.setText("Change to 100Hz");
                }

                Event changeClockSpeedEvent = new Event(EventType.CLOCK_SPEED_CHANGED);
                EventBus.getInstance().notifyObservers(changeClockSpeedEvent);
            }
        });

        HBox cpuButtonBox = new HBox(10);
        cpuButtonBox.getChildren().addAll(startButton, pauseButton, stepButton, handbrakeButton);

        VBox cpuControlsBox = new VBox(10);
        cpuControlsBox.getChildren().add(new Text("CPU Controls:"));
        cpuControlsBox.getChildren().add(cpuButtonBox);

        return cpuControlsBox;
    }

    // Methode zum Starten des Loops
    private void startLoop() {
        if(gameLoop.getStatus() == Timeline.Status.STOPPED || gameLoop.getStatus() == Timeline.Status.PAUSED){
            gameLoop.play();
        }
    }

    // Methode zum Pausieren des Loops
    public void pauseLoop() {
        if(gameLoop.getStatus() == Timeline.Status.RUNNING || gameLoop.getStatus() == Timeline.Status.PAUSED){
            gameLoop.stop();
        }
    }

    // Methode zum Schrittweisen Ausführen des Loops
    private void stepLoop() {
        KeyFrame keyFrame = gameLoop.getKeyFrames().get(0);
        if (keyFrame != null) {
            keyFrame.getOnFinished().handle(null);
        }
        gameLoop.stop();
    }

    // Methode zum Aktualisieren der Debug-Informationen
    public void updateDebugInfo() {
        debugTextBox.getChildren().clear();

        // CPU-Debug-Informationen
        debugTextBox.getChildren().add(new Text("CPU State:"));
        debugTextBox.getChildren().add(new Text(String.format("Executed Opcode: 0x%02X", cpu.getCurrentOpcode()) + " (" + cpu.getCurrentOpcode() + ")"));
        debugTextBox.getChildren().add(new Text("HLT: " + cpu.getHlt()));
        debugTextBox.getChildren().add(new Text(String.format("IC: 0x%04X", cpu.getIc()) + " (" + cpu.getIc() + ")"));
        debugTextBox.getChildren().add(new Text(String.format("AR: 0x%04X", cpu.getAr()) + " (" + cpu.getAr() + ")"));
        debugTextBox.getChildren().add(new Text(String.format("R0: 0x%02X", cpu.getR0()) + " (" + cpu.getR0() + ")"));
        debugTextBox.getChildren().add(new Text(String.format("R1: 0x%02X", cpu.getR1()) + " (" + cpu.getR1() + ")"));
        debugTextBox.getChildren().add(new Text(String.format("R2: 0x%02X", cpu.getR2()) + " (" + cpu.getR2() + ")"));
        debugTextBox.getChildren().add(new Text(String.format("R3: 0x%02X", cpu.getR3()) + " (" + cpu.getR3() + ")"));
        debugTextBox.getChildren().add(new Text(String.format("Tick-Byte: 0x%02X", cpu.getTickByte()) + " (" + cpu.getTickByte() + ")"));
        debugTextBox.getChildren().add(new Text(String.format("Tock-Byte: 0x%02X", cpu.getTockByte()) + " (" + cpu.getTockByte() + ")"));
    }

    public void close() {
        if (debugStage != null && debugStage.isShowing()) {
            debugStage.close();
        }
    }

    public boolean isShowing() {
        if (debugStage != null) {
            return debugStage.isShowing();
        }

        return false;
    }

    private void showMemorySnapshotWindow(String windowName, int start, int end){
        Stage memorySnapWindow = new Stage();

        memoryWindows.add(memorySnapWindow);

        ListView<String> memoryListView = new ListView<>();

        StringBuilder line = new StringBuilder();
        for (int i = start; i <= end; i++) {
            if ((i - start) % 16 == 0) {
                if (i > start) {
                    memoryListView.getItems().add(line.toString());
                    line = new StringBuilder();  // Zeile zurücksetzen
                }
                // Starten einer neuen Zeile
                line.append(String.format("0x%04X: ", i));
            }
            line.append(String.format("0x%02X ", memory.read(i)));
        }

        memoryListView.getItems().add(line.toString());

        memoryListView.setCellFactory(list -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null && !empty) {
                    setText(item);
                    setFont(Font.font("Monospaced", 14));
                    setStyle("-fx-padding: 5px;");
                } else {
                    setText(null);
                }
            }
        });

        StackPane subRoot = new StackPane();
        subRoot.getChildren().add(memoryListView);
        Scene subScene = new Scene(subRoot, 770, 500);

        memorySnapWindow.setTitle(windowName);
        memorySnapWindow.setResizable(false);
        memorySnapWindow.setScene(subScene);
        memorySnapWindow.show();

        memorySnapWindow.setOnCloseRequest(event -> {
            memoryWindows.remove(memorySnapWindow);
        });
    }
}
