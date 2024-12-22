package de.ostfalia.retro24emulatorfx;

import java.util.Arrays;

public class Memory {
    private static final int SIZE_IO = 0x0100; // 256 Bytes für I/O-Page (0x0000 - 0x00FF)
    private static final int SIZE_RAM = 0xDC00; // 56320 Bytes für RAM (0x0100 - 0xDFFF)
    private static final int SIZE_VIDEO = 0x2000; // 8192 Bytes für Video (0xE000 - 0xFFFF)

    private static final int SCREEN_WIDTH = 64;
    private static final int SCREEN_HEIGHT = 64;
    private static final int PIXEL_SIZE = 10; // Größe eines Pixels in der Anzeige

    //private static Memory instance;
    private int[] io;
    private int[] program;
    private int[] video;

    /*
        Speicher-Layout:
        - I/O-Page bei $0000-$00FF
        - Programmspeicher bei $0100-$DFFF
        - Grafikspeicher bei $E000-$FFFF
     */

    public Memory() {
        io = new int[SIZE_IO];
        program = new int[SIZE_RAM];
        video = new int[SIZE_VIDEO];

        // Setzt den RAM-Bereich von $0100-$DFFF auf 0xFF
        Arrays.fill(program, 0xFF);
        setTitle();
    }

    private void writeChars(int offset_Zeile, int offset_Spalte, int[][] chars){
        for (int x = 0; x < chars.length; x++) {
            for (int y = 0; y < chars[x].length; y++) {
                int index = (y + offset_Zeile) * 8 + (x + offset_Spalte);
                video[index] = chars[x][y];
            }
        }
    }

    public int read(int address) {
        if (address >= 0x0000 && address <= 0x00FF) {
            return io[address]; // I/O-Bereich
        } else if (address >= 0x0100 && address <= 0xDFFF) {
            return program[address - 0x0100]; // RAM-Bereich
        } else if (address >= 0xE000 && address <= 0xFFFF) {
            return video[address - 0xE000]; // Video-Bereich
        } else {
            throw new IllegalArgumentException("Ungültige Adresse: " + Integer.toHexString(address));
        }
    }

    public void write(int address, int value) {
        if (address >= 0x0000 && address <= 0x00FF) {
            io[address] = value; // I/O-Bereich
        } else if (address >= 0x0100 && address <= 0xDFFF) {
            program[address - 0x0100] = value; // RAM-Bereich
        } else if (address >= 0xE000 && address <= 0xFFFF) {
            video[address - 0xE000] = value; // Video-Bereich
        } else {
            throw new IllegalArgumentException("Ungültige Adresse: " + Integer.toHexString(address));
        }
    }

    public void setTitle(){
        // Setzt Titelbildschirm
        int[][] retro_zeichen = {
                {0b01111100, 0b01000010, 0b01000010, 0b01111100, 0b01001000, 0b01000100, 0b01000010}, // R
                {0b01111110, 0b01000000, 0b01000000, 0b01111100, 0b01000000, 0b01000000, 0b01111110}, // E
                {0b01111110, 0b00010000, 0b00010000, 0b00010000, 0b00010000, 0b00010000, 0b00010000}, // T
                {0b01111100, 0b01000010, 0b01000010, 0b01111100, 0b01001000, 0b01000100, 0b01000010}, // R
                {0b00111100, 0b01000010, 0b01000010, 0b01000010, 0b01000010, 0b01000010, 0b00111100}, // O
        };

        writeChars(21,1, retro_zeichen);

        int[][] num_zeichen = {
                {0b00111100, 0b01000010, 0b00000100, 0b00001000, 0b00010000, 0b00100000, 0b01111110}, // 2
                {0b00001000, 0b00011000, 0b00101000, 0b01001000, 0b01111110, 0b00001000, 0b00001000}  // 4
        };
        writeChars(31,5, num_zeichen);
    }

    public void clearVideo() {
        Arrays.fill(video, 0x00);
    }

    public void clearProgram() {
        Arrays.fill(program, 0xFF);
    }

    public void clearAll() {
        Arrays.fill(video, 0x00);
        Arrays.fill(program, 0xFF);
        Arrays.fill(io, 0x00);
    }


}