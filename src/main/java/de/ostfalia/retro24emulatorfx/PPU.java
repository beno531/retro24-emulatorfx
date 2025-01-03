package de.ostfalia.retro24emulatorfx;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class PPU extends Canvas{

    private static final int SCREEN_WIDTH = 640;
    private static final int SCREEN_HEIGHT = 640;
    private static final int PIXEL_SIZE = 10;
    private Memory memory;
    GraphicsContext gc;

    public PPU(){
        super(SCREEN_WIDTH, SCREEN_HEIGHT);
        //setFocusTraversable(false);

        gc = this.getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
    }

    public void connectMemory(Memory memory){
        this.memory = memory;
    }

    public void render() {

        for (int y = 0; y < SCREEN_HEIGHT / 10; y++) {
            for (int x = 0; x < SCREEN_WIDTH / 10; x++) {

                // Adresse im Speicher berechnen
                int index = y * SCREEN_WIDTH / 10 + x;

                // Dunkel/Hell aus $E000
                boolean isBright = memory.read(0xE000 + index) != 0;

                // Monochrom/Farbig aus $F000
                boolean isColor = memory.read(0xF000 + index) != 0;

                // Farbe basierend auf den Bits bestimmen
                Color color = determineColor(isBright, isColor);

                // Pixel auf dem Canvas zeichnen
                gc.setFill(color);
                gc.fillRect(x * PIXEL_SIZE, y * PIXEL_SIZE, PIXEL_SIZE, PIXEL_SIZE);
            }
        }
    }

    private Color determineColor(boolean isBright, boolean isColor) {
        if (isBright && isColor) {
            return Color.YELLOW; // Hell und farbig
        } else if (isBright && !isColor) {
            return Color.WHITE; // Hell und monochrom
        } else if (!isBright && isColor) {
            return Color.BLUE; // Dunkel und farbig
        } else {
            return Color.BLACK; // Dunkel und monochrom
        }
    }

    public boolean isDrawFlag(){
        if(memory.read(0x000A) == 0x01){
            return true;
        }
        return false;
    }
}
