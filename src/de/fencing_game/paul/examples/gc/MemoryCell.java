package de.fencing_game.paul.examples.gc;

import java.util.Arrays;

/**
 * A class representing a cell of memory (i.e. a Java object),
 * which contains some references to other memory cells, and
 * maybe other data too.
 *
 * We represent the references by positive integers (refering to
 * indexes of other memory cells) and 0 for the null reference,
 * primitive data is represented by negative integers.
 * (The meaning of the primitive data is irrelevant to
 * the memory allocation algorithm.)
 */
public class MemoryCell {

    /**
     * The references.
     */
    private final int[] data;

    private final int position;


    public int getPosition() {
        return position;
    }

    /**
     * no public constructor - this is invoked by the Heap.
     */
    MemoryCell(int cellSize, int pos) {
        this.data = new int[cellSize];
        this.position = pos;
    }

    public void clear() {
        Arrays.fill(data, 0, data.length, 0);
    }

    /**
     * returns the number of references in this memory cell.
     */
    public int size() {
        return data.size;
    }

    /**
     * returns the value of the reference with given index.
     */
    public int getReference(int index) {
        return data[index];
    }

    /**
     * checks wether the data at a given index is a reference.
     */
    public boolean isReference(int index) {
        return data[index] >= 0;
    }

    /**
     * changes the value of a reference by index.
     */
    public void setReference(int index, int value) {
        assert value >= 0;
        data[index] = value;
    }

    /**
     * copies the content of this object to another memory cell.
     * The other cell must have the same size as this cell.
     */
    public void copyTo(MemoryCell other) {
        assert other.size() == this.size();
        System.arraycopy(this.data, 0, other.data, 0, this.data.length);
    }

}