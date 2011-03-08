package de.fencing_game.paul.examples.gc;



/**
 * A class representing the whole heap of the Java VM.
 *
 * A heap consists of memory cells with indexes from
 * 1 (inclusive) to {@link #size()} (exclusive).
 *
 * These indexes are used as references, reference 0 is used
 * for the null-reference.
 */
public class Heap {

    private MemoryCell[] memoryCells;

    /**
     * creates a Heap of cells with same size.
     */
    public Heap(int size, int cellSize) {
        this.memoryCells = new MemoryCell[size];
        // we intentionally start at 1, since reference 0
        // is the 0-reference, not a valid index.
        for(int i = 1; i < size; i++) {
            memoryCells[i] = new MemoryCell(cellSize, i);
        }
    }


    /**
     * gets the size of the Heap.
     * The maximum index is one less than the
     * result of this method.
     */
    public int getSize() {
        return memoryCells.length;
    }

    /**
     * gets a memory cell.
     */
    public MemoryCell getMemoryCell(int index) {
        return memoryCells[index];
    }


}