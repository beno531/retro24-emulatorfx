package de.ostfalia.retro24emulatorfx;

import java.util.Arrays;

public class Memory {
    public static final int SIZE_IO = 0x0100; // 256 Bytes für I/O-Page (0x0000 - 0x00FF)
    public static final int SIZE_Program = 0xDF00; // 57088 Bytes für Programme (0x0100 - 0xDFFF)
    public static final int SIZE_VIDEO = 0x2000; // 8192 Bytes für Video (0xE000 - 0xFFFF)
    private int[] io;
    private int[] program;
    private int[] video;

    public Memory() {
        io = new int[SIZE_IO];
        program = new int[SIZE_Program];
        video = new int[SIZE_VIDEO];

        Arrays.fill(program, 0xFF);
    }

    public int read(int address) {
        if (address >= 0x0000 && address <= 0x00FF) {
            return io[address]; // I/O-Bereich
        } else if (address >= 0x0100 && address <= 0xDFFF) {
            return program[address - 0x0100]; // Program-Bereich
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
            program[address - 0x0100] = value; // Program-Bereich
        } else if (address >= 0xE000 && address <= 0xFFFF) {
            video[address - 0xE000] = value; // Video-Bereich
        } else {
            throw new IllegalArgumentException("Ungültige Adresse: " + Integer.toHexString(address));
        }
    }

    public void setTitle(){

        int[][] letterR = {
                {1, 1, 1, 1, 0},
                {1, 0, 0, 0, 1},
                {1, 0, 0, 0, 1},
                {1, 1, 1, 1, 0},
                {1, 0, 1, 0, 0},
                {1, 0, 0, 1, 0},
                {1, 0, 0, 0, 1}
        };

        int[][] letterE = {
                {1, 1, 1, 1, 1},
                {1, 0, 0, 0, 0},
                {1, 0, 0, 0, 0},
                {1, 1, 1, 1, 0},
                {1, 0, 0, 0, 0},
                {1, 0, 0, 0, 0},
                {1, 1, 1, 1, 1}
        };

        int[][] letterT = {
                {1, 1, 1, 1, 1},
                {0, 0, 1, 0, 0},
                {0, 0, 1, 0, 0},
                {0, 0, 1, 0, 0},
                {0, 0, 1, 0, 0},
                {0, 0, 1, 0, 0},
                {0, 0, 1, 0, 0}
        };

        int[][] letterO = {
                {0, 1, 1, 1, 0},
                {1, 0, 0, 0, 1},
                {1, 0, 0, 0, 1},
                {1, 0, 0, 0, 1},
                {1, 0, 0, 0, 1},
                {1, 0, 0, 0, 1},
                {0, 1, 1, 1, 0}
        };

        int[][] number2 = {
                {0, 1, 1, 1, 0},
                {1, 0, 0, 0, 1},
                {0, 0, 0, 0, 1},
                {0, 0, 0, 1, 0},
                {0, 0, 1, 0, 0},
                {0, 1, 0, 0, 0},
                {1, 1, 1, 1, 1}
        };

        int[][] number4 = {
                {0, 0, 0, 1, 0},
                {0, 0, 1, 1, 0},
                {0, 1, 0, 1, 0},
                {1, 0, 0, 1, 0},
                {1, 1, 1, 1, 1},
                {0, 0, 0, 1, 0},
                {0, 0, 0, 1, 0}
        };


        // Delete maybe


        int[][] letterP = {
                {1, 1, 1, 1, 0},
                {1, 0, 0, 0, 1},
                {1, 0, 0, 0, 1},
                {1, 1, 1, 1, 0},
                {1, 0, 0, 0, 0},
                {1, 0, 0, 0, 0},
                {1, 0, 0, 0, 0}
        };

        int[][] letterS = {
                {0, 1, 1, 1, 1},
                {1, 0, 0, 0, 0},
                {1, 0, 0, 0, 0},
                {0, 1, 1, 1, 0},
                {0, 0, 0, 0, 1},
                {0, 0, 0, 0, 1},
                {1, 1, 1, 1, 0}
        };
        int[][] letterA = {
                {0, 1, 1, 1, 0},
                {1, 0, 0, 0, 1},
                {1, 0, 0, 0, 1},
                {1, 1, 1, 1, 1},
                {1, 0, 0, 0, 1},
                {1, 0, 0, 0, 1},
                {1, 0, 0, 0, 1}
        };

        int[][] letterC = {
                {0, 1, 1, 1, 0},
                {1, 0, 0, 0, 1},
                {1, 0, 0, 0, 0},
                {1, 0, 0, 0, 0},
                {1, 0, 0, 0, 0},
                {1, 0, 0, 0, 1},
                {0, 1, 1, 1, 0}
        };



        /*
        writeChars(letterP, 14, 25);
        writeChars(letterS, 20, 25);
        writeChars(letterA, 26, 25);
        writeChars(letterC, 32, 25);
         */


        writeChars(letterR, 14, 25);
        writeChars(letterE, 20, 25);
        writeChars(letterT, 26, 25);
        writeChars(letterR, 32, 25);
        writeChars(letterO, 38, 25);
        writeChars(number2, 38, 34);
        writeChars(number4, 44, 34);
    }

    public void writeChars(int[][] letter, int xPos, int yPos) {
        for (int y = 0; y < letter.length; y++) {
            for (int x = 0; x < letter[y].length; x++) {
                if (letter[y][x] == 1) {
                    int index = (y + yPos) * 64 + (x + xPos);
                    video[index] = 1;
                }
            }
        }
    }

    public void clearAll() {
        Arrays.fill(video, 0x00);
        Arrays.fill(program, 0xFF);
        Arrays.fill(io, 0x00);
    }

    public int[] getProgram() {
        return program;
    }
}