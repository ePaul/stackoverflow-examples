package de.fencing_game.paul.examples;

import java.util.*;

/**
 * A skeleton implementation of Map2D.
 *
 * Most methods' implementations delegate to the entrySet,
 * so they should be overwritten to be more efficient if
 * possible.
 *
 * @param <K1> the first key type.
 * @param <K2> the second key type.
 * @param <V> the value type.
 */
public abstract class AbstractMap2D<K1,K2,V>
    implements Map2D<K1,K2,V>
{

    /**
     * returns a collection view of this map's entries.
     * Based on this method most of the other methods are
     * implemented.
     */
    public abstract Set<Map2D.Entry<K1,K2,V>> entrySet();

    /**
     * returns the number of mappings in this map.
     * This implementation returns {@code entrySet().size()}.
     */
    public int size() {
        return entrySet().size();
    }

    /**
     * returns true if the map is empty.
     * This implementation returns {@code entrySet().isEmpty()}.
     */
    public boolean isEmpty() {
        return entrySet().isEmpty();
    }

    
    public boolean containsKeys(Object key1, Object key2) {
        return false;
    }


}