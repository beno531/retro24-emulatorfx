package de.ostfalia.retro24emulatorfx;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Loader {
    private Memory memory;
    private int[] tempProgram;

    public void connectMemory(Memory memory) {
        this.memory = memory;
    }

    public void writeProgram() {

        if(tempProgram != null){

            int counter = 0X0100;

            for (int val : tempProgram) {
                memory.write(counter, val);
                counter++;
            }
        }
    }

    public void writeProgram(String filePath) {

        File file = new File(filePath);

        try {

            if (!file.exists() || !file.isFile()) {
                throw new IOException("Datei nicht gefunden: " + filePath);
            }

        } catch (IOException e) {
            System.err.println("Fehler beim Laden der Datei: " + e.getMessage());
        }

        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] byteArray = fis.readAllBytes();

            // Konvertiere byte[] zu int[]
            int[] intArray = new int[byteArray.length];
            for (int i = 0; i < byteArray.length; i++) {
                intArray[i] = byteArray[i] & 0xFF;
            }

            tempProgram = intArray;

            writeProgram();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isTempProgramSet() {
        if(tempProgram != null){
            return true;
        }else {
            return false;
        }
    }
}
