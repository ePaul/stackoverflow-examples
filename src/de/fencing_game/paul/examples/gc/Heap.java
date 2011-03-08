package de.fencing_game.paul.examples.gc;



/**
 * A class representing the whole heap of the Java VM.
 */
public class Heap {

    private MemoryCell[] memoryCells;

    /**
     * creates a Heap of cells with same size.
     */
    public Heap(int size, int cellSize) {
        this.memoryCells = new MemoryCell[size];
        for(int i = 0; i < size; i++) {
            memoryCells[i] = new MemoryCell(cellSize, i);
        }
    }


    public int getSize() {
        return memoryCells.length();
    }

    public MemoryCell getMemoryCell(int index) {
        return memoryCells[i];
    }


}