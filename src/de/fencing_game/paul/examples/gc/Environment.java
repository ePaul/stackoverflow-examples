package de.fencing_game.paul.examples.gc;

/**
 * The environment for a user program.
 * This is a simplified view of the Java VM instructions.
 */
public interface Environment {


    /**
     * allocates a new memory cell.
     */
    public int newObject();

    /**
     * gets a member variable of an object.
     */
    public int getReference(int object, int index);

    /**
     * gets some primitive data from an object.
     */
    public int getData(int object, int index);

    /**
     * changes a reference variable in a object.
     */
    public void setReference(int object, int index, int value);

    /**
     * changes some primitive data in a object.
     */
    public void setData(MemoryCell object, int index, int value);

    /**
     * releases a memory cell, when it is not used
     * anymore in a local variable. (It still may be
     * in use through referencing).
     */
    public void release(int object);


    /**
     * calls another method, giving it some arguments.
     *
     * positive arguments must be objects referenced by local variables,
     * negative arguments are interpreted as primitive values.
     */
    public void callMethod(UserMethod m, int ... arguments);


}