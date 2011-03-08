package de.fencing_game.paul.examples.gc;


/**
 * The interface to be implemented by the pluggable
 * memory allocation (and garbage collection) algorithm.
 */
public interface Allocator
{

    /**
     * This method is called once at the start of the program.
     * @param Heap the heap from which to take the memory.
     * @param RootList the list of root objects - everything
     *              reachable from here is considered in active
     *              use and may not be collected.
     */
    public void initialize(Heap heap, RootList roots);


    public MemoryCell allocateObject();



}