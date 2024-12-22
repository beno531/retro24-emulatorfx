module com.example.retro24emulatorfx {
    requires javafx.controls;
    requires javafx.fxml;


    opens de.ostfalia.retro24emulatorfx to javafx.fxml;
    exports de.ostfalia.retro24emulatorfx;
}