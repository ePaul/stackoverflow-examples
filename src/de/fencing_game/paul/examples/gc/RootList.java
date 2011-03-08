package de.fencing_game.paul.examples.gc;


/**
 * The RootList can be queried for all objects which are in
 * active use (e.g. on the stack of some running thread). It also
 * allows to "stop the world" while querying.
 */
public interface RootList implements Iterable<MemoryCell>
{

    
    public void stopUserThreads();

    public void resumeUserThreads();

}