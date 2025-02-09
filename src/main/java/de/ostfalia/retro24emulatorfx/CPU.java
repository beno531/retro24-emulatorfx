package de.ostfalia.retro24emulatorfx;

import java.util.HashMap;
import java.util.Map;
public class CPU {

    private int ar = 0x0000;
    private int ic = 0x0100;
    private int r0 = 0x00;
    private int r1 = 0x00;
    private int r2 = 0x00;
    private int r3 = 0x00;

    private boolean hlt = false;

    private int currentOpcode;

    private Memory memory;
    private Map<Integer, Runnable> opcodeMap = new HashMap<>();

    public CPU() {
        opcodeMap.put(0x00, this::nul); // NUL ($00, 1-Byte-OP): Prozessor tut nichts
        opcodeMap.put(0x01, this::mar); // MAR ($01, 3-Byte-OP): Lädt AR mit den nächsten beiden Bytes.
        opcodeMap.put(0x02, this::sic); // SIC ($02, 1-Byte-OP): Speichert IC an die im AR angegebene Adresse.
        opcodeMap.put(0x03, this::rar); // RAR ($03, 1-Byte-OP): R1/R2 werden ins AR kopiert.
        opcodeMap.put(0x04, this::aar); // AAR ($04, 1-Byte-OP): Addiert R0 aufs AR, bei Überlauf geht Übertrag verloren.
        opcodeMap.put(0x05, this::ir0); // IR0 ($05, 1-Byte-OP): Erhöht den Wert von R0 um 1, allerdings nicht über $FF hinaus.
        opcodeMap.put(0x06, this::a01); // A01 ($06, 1-Byte-OP): Addiert R0 auf R1. Bei Überlauf wird R2 um 1 erhöht. Läuft dabei wiederum R2 über, werden R1 und R2 zu $FF.
        opcodeMap.put(0x07, this::dr0); // DR0 ($07, 1-Byte-OP): Erniedrigt den Wert von R0 um 1, allerdings nicht unter $00.
        opcodeMap.put(0x08, this::s01); // S01 ($08, 1-Byte-OP): Subtrahiert R0 von R1. Falls eine negative Zahl entsteht, enthält R1 dann den Betrag der negativen Zahl. Ferner wird dann R2 um 1 erniedrigt. Tritt dabei ein Unterlauf von R2 auf, werden R1 und R2 zu $00.
        opcodeMap.put(0x09, this::x12); // X12 ($09, 1-Byte-OP): Vertauscht die Inhalte von R1 und R2.
        opcodeMap.put(0x10, this::x01); // X01 ($10, 1-Byte-OP): Vertauscht die Inhalte von R0 und R1.
        opcodeMap.put(0x11, this::jmp); // JMP ($11, 1-Byte-OP): Springt zu der in AR angegebenen Adresse.
        opcodeMap.put(0x12, this::sr0); // SR0 ($12, 1-Byte-OP): Speichert R0 an die in AR angegebene Adresse.
        opcodeMap.put(0x13, this::srw); // SRW ($13, 1-Byte-OP): Speichert R1 an die in AR angegebene Adresse, ferner R2 an die Adresse dahinter.
        opcodeMap.put(0x14, this::lr0); // LR0 ($14, 1-Byte-OP): Lädt R0 aus der in AR angegebenen Adresse.
        opcodeMap.put(0x15, this::lrw); // LRW ($15, 1-Byte-OP): Lädt R1 aus der in AR angegebenen Adresse, ferner R2 aus der Adresse dahinter.
        opcodeMap.put(0x16, this::taw); // TAW ($16, 1-Byte-OP): AR wird nach R1/R2 kopiert.
        opcodeMap.put(0x17, this::mr0); // MR0 ($17, 2-Byte-OP): Das nachfolgende Byte wird nach R0 geschrieben.
        opcodeMap.put(0x18, this::mrw); // MRW ($18, 3-Byte-OP): Die nachfolgenden 2 Bytes werden nach R1 und R2 geschrieben.
        opcodeMap.put(0x19, this::jz0); // JZ0 ($19, 1-Byte-OP): Springt zu der in AR angegebenen Adresse, falls R0=$00 ist.
        opcodeMap.put(0x20, this::jgw); // JGW ($20, 1-Byte-OP): Springt zu der in AR angegebenen Adresse, falls R1 > R2 ist.
        opcodeMap.put(0x21, this::jew); // JEW ($21, 1-Byte-OP): Springt zu der in AR angegebenen Adresse, falls R1=R2 ist.
        opcodeMap.put(0x22, this::or0); // OR0 ($22, 2-Byte-OP): Speichert in R0 das logische ODER aus dem aktuellen Wert von R0 und dem nachfolgenden Byte.
        opcodeMap.put(0x23, this::an0); // AN0 ($23, 2-Byte-OP): Speichert in R0 das logische UND aus dem aktuellen Wert von R0 und dem nachfolgenden Byte.
        opcodeMap.put(0x24, this::je0); // JE0 ($24, 2-Byte-OP): Springt zu der in AR angegebenen Adresse, falls R0 gleich dem nachfolgenden Byte ist.
        opcodeMap.put(0x25, this::c01); // C01 ($25, 1-Byte-OP): Kopiert R0 nach R1.
        opcodeMap.put(0x26, this::c02); // C02 ($26, 1-Byte-OP): Kopiert R0 nach R2.
        opcodeMap.put(0x27, this::irw); // IRW ($27, 1-Byte-OP): Erhöht den Wert von R1 um 1. Bei Überlauf wird R2 um 1 erhöht. Läuft dabei wiederum R2 über, werden R1 und R2 zu $FF.
        opcodeMap.put(0x28, this::drw); // DRW ($28, 1-Byte-OP): Erniedrigt den Wert von R1 um 1. Falls eine negative Zahl entsteht, enthält R1 dann den Betrag der negativen Zahl. Ferner wird dann R2 um 1 erniedrigt. Tritt dabei ein Unterlauf von R2 auf, werden R1 und R2 zu $00.
        opcodeMap.put(0x29, this::x03); // X03 ($29, 1-Byte-OP): Vertauscht die Inhalte von R0 und R3.
        opcodeMap.put(0x2A, this::c03); // C03 ($2A, 1-Byte-OP): Kopiert R0 nach R3.
        opcodeMap.put(0x2B, this::c30); // C30 ($2B, 1-Byte-OP): Kopiert R3 nach R0.
        opcodeMap.put(0x2C, this::pl0); // PL0 ($2C, 1-Byte-OP): Schiebt die Bits in R0 um ein Bit nach „links“ (entspricht Teilen durch 2 ohne Rest)
        opcodeMap.put(0x2D, this::pr0); // PR0 ($2D, 1-Byte-OP): Schiebt die Bits in R0 um ein Bit nach „rechts“ (entspricht Multiplikation mit 2 ohne Übertrag).
        opcodeMap.put(0xFF, this::hlt); // HLT ($FF, 1-Byte-OP): Prozessor hält an
    }

    // Methode simuliert einen Takt der CPU
    public void cycle() {

        currentOpcode = memory.read(ic);

        incrTickByte();
        decrTockByte();

        Runnable instruction = opcodeMap.get(currentOpcode);

        if (instruction != null) {
            instruction.run();
        } else {
            throw new IllegalStateException("Unbekannter Opcode: " + currentOpcode);
        }

    }

    public void connectMemory(Memory memory) {
        this.memory = memory;
    }

    // --- Getter/ Setter ---
    public int getCurrentOpcode() {
        return currentOpcode;
    }

    public int getIc() {
        return ic;
    }

    public void setIc(int ic){
        this.ic = (ic & 0xFFFF);
    }

    public int getAr() {
        return ar;
    }

    public void setAr(int ar) {
        this.ar = (ar & 0xFFFF);
    }

    public int getR0() {
        return r0;
    }

    public void setR0(int r0) {
        this.r0 = (r0 & 0xFF);
    }

    public int getR1() {
        return r1;
    }

    public void setR1(int r1) {
        this.r1 = (r1 & 0xFF);;
    }

    public int getR2() {
        return r2;
    }

    public void setR2(int r2) {
        this.r2 = (r2 & 0xFF);;
    }

    public int getR3() {
        return r3;
    }

    public void setR3(int r3) {
        this.r3 = (r3 & 0xFF);;
    }

    public boolean getHlt() {
        return hlt;
    }

    public void setHlt(boolean hlt) {
        this.hlt = hlt;
    }

    public int getTickByte() {
        return memory.read(0x0010);
    }
    public int getTockByte() {
        return memory.read(0x0011);
    }

    // --- Helper ---

    public void incrTickByte(){
        int tickVal = memory.read(0x0010);
        tickVal = tickVal + 1 & 0xFF;
        memory.write(0x0010, tickVal);
    }
    public void decrTockByte(){
        int tockVal = memory.read(0x0011);
        tockVal = tockVal - 1 & 0xFF;
        memory.write(0x0011, tockVal);
    }

    public void incrIc(int i){
        ic = (ic + i) & 0xFFFF;
    }

    public void reset(){
        ar = 0x0000;
        ic = 0x0100;
        r0 = 0x00;
        r1 = 0x00;
        r2 = 0x00;
        r3 = 0x00;
        hlt = false;
        memory.write(0x0010, 0x00);
        memory.write(0x0011, 0x00);
    }

    // --- Instructions ---

    // Prozessor tut nichts
    private void nul(){
        incrIc(1);
    }

    // Lädt AR mit den nächsten beiden Bytes.
    private void mar(){
        // little endian
        ar = ((memory.read(ic + 2) & 0xFF) << 8) | (memory.read(ic + 1) & 0xFF);
        incrIc(3);
    }

    // Speichert IC an die im AR angegebene Adresse.
    private void sic(){
        int lowByte = ic & 0xFF;
        int highByte = (ic >> 8) & 0xFF;
        memory.write(ar, lowByte);
        memory.write((ar + 1) & 0xFFFF, highByte);
        incrIc(1);
    }

    // R1/R2 werden ins AR kopiert.
    private void rar(){
        ar = ((r2) << 8) | (r1);
        incrIc(1);
    }

    // Addiert (Bitweise) R0 aufs AR, bei Überlauf geht Übertrag verloren.
    private void aar(){
        ar = (ar + r0) & 0xFFFF;
        incrIc(1);
    }

    // Erhöht den Wert von R0 um 1, allerdings nicht über $FF hinaus.
    private void ir0() {
        r0 = Math.min(r0 + 1, 0xFF);
        incrIc(1);
    }

    // Addiert R0 auf R1. Bei Überlauf wird R2 um 1 erhöht. Läuft dabei wiederum R2 über, werden R1 und R2 zu $FF.
    private void a01(){
        int x = (r1 & 0xFF) + (r0 & 0xFF);
        if(x > 0xFF){
            r1 = x & 0xFF;
            int y = r2 + 1;
            if (y > 0xFF) {
                r1 = 0xFF;
                r2 = 0xFF;
            }
            else {
                r2 = y;
            }
        }
        else {
            r1 = x;
        }
        incrIc(1);
    }

    // Erniedrigt den Wert von R0 um 1, allerdings nicht unter $00.
    private void dr0(){
        r0 = Math.max(r0 - 1, 0x00);
        incrIc(1);
    }

    // Subtrahiert R0 von R1. Falls eine negative Zahl entsteht, enthält R1 dann den Betrag der negativen Zahl. Ferner wird dann R2 um 1 erniedrigt. Tritt dabei ein Unterlauf von R2 auf, werden R1 und R2 zu $00.
    private void s01(){
        int x = (r1 & 0xFF) - (r0 & 0xFF);

        if (x < 0) {
            r1 = Math.abs(x);

            int y = r2 - 1;
            if (y < 0) {
                r1 = 0x00;
                r2 = 0x00;
            } else {
                r2 = y;
            }
        } else {
            r1 = x;
        }
        incrIc(1);
    }

    // Vertauscht die Inhalte von R1 und R2.
    private void x12(){
       int temp = r1;
        r1 = r2;
        r2 = temp;
        incrIc(1);
    }

    // Vertauscht die Inhalte von R0 und R1.
    private void x01(){
        int temp = r0;
        r0 = r1;
        r1 = temp;
        incrIc(1);
    }

    // Springt zu der in AR angegebenen Adresse.
    private void jmp(){
        ic = ar;
    }

    // Speichert R0 an die in AR angegebene Adresse.
    private void sr0(){
        memory.write(ar, r0);
        incrIc(1);
    }

    // Speichert R1 an die in AR angegebene Adresse, ferner R2 an die Adresse dahinter.
    private void srw(){
        memory.write(ar, r1);
        memory.write(ar + 1, r2);
        incrIc(1);
    }

    // Lädt R0 aus der in AR angegebenen Adresse.
    private void lr0(){
        r0 = memory.read(ar);
        incrIc(1);
    }

    // Lädt R1 aus der in AR angegebenen Adresse, ferner R2 aus der Adresse dahinter.
    private void lrw(){
        r1 = memory.read(ar);
        r2 = memory.read(ar + 1);
        incrIc(1);
    }

    // AR wird nach R1/R2 kopiert.
    private void taw(){
        int lowByte = ar & 0xFF;
        int highByte = (ar >> 8) & 0xFF;
        r1 = lowByte;
        r2 = highByte;
        incrIc(1);
    }

    // Das nachfolgende Byte wird nach R0 geschrieben.
    private void mr0(){
        r0 = memory.read(ic + 1);
        incrIc(2);
    }

    // Die nachfolgenden 2 Bytes werden nach R1 und R2 geschrieben.
    private void mrw(){
        r1 = memory.read(ic + 1);
        r2 = memory.read(ic + 2);
        incrIc(3);
    }

    // Springt zu der in AR angegebenen Adresse, falls R0=$00 ist.
    private void jz0(){
        if(r0 == 0x00){
            ic = ar;
        }
        else {
            incrIc(1);
        }
    }

    // Springt zu der in AR angegebenen Adresse, falls R1 > R2 ist.
    private void jgw(){
        if(r1 > r2){
            ic = ar;
        }
        else {
            incrIc(1);
        }
    }

    // Springt zu der in AR angegebenen Adresse, falls R1=R2 ist.
    private void jew(){
        if(r1 == r2){
            ic = ar;
        }
        else {
            incrIc(1);
        }
    }

    // Speichert in R0 das logische ODER aus dem aktuellen Wert von R0 und dem nachfolgenden Byte.
    private void or0(){
        int temp = (memory.read(ic + 1)) & 0xFF;
        r0 = (r0 | temp) & 0xFF;
        incrIc(2);
    }

    // Speichert in R0 das logische UND aus dem aktuellen Wert von R0 und dem nachfolgenden Byte.
    private void an0(){
        int temp = (memory.read(ic + 1)) & 0xFF;
        r0 = (r0 & temp) & 0xFF;
        incrIc(2);
    }

    // Springt zu der in AR angegebenen Adresse, falls R0 gleich dem nachfolgenden Byte ist.
    private void je0(){

        if(r0 == memory.read(ic + 1)){
            ic = ar;
        }
        else {
            incrIc(2);
        }
    }

    // Kopiert R0 nach R1.
    private void c01(){
        r1 = r0;
        incrIc(1);
    }

    // Kopiert R0 nach R2.
    private void c02(){
        r2 = r0;
        incrIc(1);
    }

    // Erhöht den Wert von R1 um 1. Bei Überlauf wird R2 um 1 erhöht. Läuft dabei wiederum R2 über, werden R1 und R2 zu $FF.
    private void irw(){
        int x = r1 + 1;
        if(x > 0xFF){
            r1 = 0xFF;
            r2 = Math.min(r2 + 1, 0xFF);
        }
        else {
            r1 = x;
        }
        incrIc(1);
    }

    // Erniedrigt den Wert von R1 um 1. Falls eine negative Zahl entsteht, enthält R1 dann den Betrag der negativen Zahl. Ferner wird dann R2 um 1 erniedrigt. Tritt dabei ein Unterlauf von R2 auf, werden R1 und R2 zu $00.
    private void drw(){
        int x = r1 - 1;

        if (x < 0) {
            r1 = Math.abs(x) & 0xFF;

            int y = r2 - 1;
            if (y < 0) {
                r1 = 0x00;
                r2 = 0x00;
            } else {
                r2 = y;
            }
        } else {
            r1 = x;
        }
        incrIc(1);
    }

    // Vertauscht die Inhalte von R0 und R3.
    private void x03(){
        int temp = r0;
        r0 = r3;
        r3 = temp;
        incrIc(1);
    }

    // Kopiert R0 nach R3.
    private void c03(){
        r3 = r0;
        incrIc(1);
    }

    // Kopiert R3 nach R0.
    private void c30(){
        r0 = r3;
        incrIc(1);
    }

    // Schiebt die Bits in R0 um ein Bit nach „links“
    private void pl0(){
        r0 = (r0 << 1);
        incrIc(1);
    }

    // Schiebt die Bits in R0 um ein Bit nach „rechts“
    private void pr0(){
        r0 = (r0 >> 1);
        incrIc(1);
    }

    // Prozessor hält an
    private void hlt(){
        hlt = true;
        incrIc(1);
    }
}