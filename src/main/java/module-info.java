module com.example.retro24emulatorfx {
    requires javafx.controls;
    requires javafx.fxml;


    opens de.ostfalia.retro24emulatorfx to javafx.fxml;
    exports de.ostfalia.retro24emulatorfx;
    exports de.ostfalia.util;
    opens de.ostfalia.util to javafx.fxml;
}