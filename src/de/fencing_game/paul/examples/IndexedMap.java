package de.fencing_game.paul.examples;


import java.util.*;

/**
 * A map which additionally to key-based access allows index-based access
 * to keys and values.
 */
public interface IndexedMap<K,V>
    extends Map<K,V>
{

    /**
     * returns a list view of the {@link #entrySet}.
     *
     * This list view supports removal of entries, if the map is mutable.
     *
     * It may also support addition of new entries per the
     *  {@link List#add add} method - but this throws an
     *  {@link IllegalArgumentException} if the key is already used.
     */
    public List<Map.Entry<K,V>> entryList();


    /**
     * returns a list view of the {@link #keySet}.
     * 
     * This list view supports removal of keys (with the corresponding
     * values), but does not support addition of new keys.
     */
    public List<K> keyList();


    /**
     * returns a list view of values contained in this map.
     *
     * This list view supports removal of values (with the corresponding
     * keys), but does not support addition of new values.
     * It may support the {@link List#set set} operation to change the
     * value for a key.
     */
    public List<V> values();


    /**
     * Returns a value of this map by index.
     *
     * This is equivalent to
     *   {@ #values() values()}.{@link List#get get}{@code (index)}.
     */
    public V getValue(int index);

    /**
     * Returns a key of this map by index.
     *
     * This is equivalent to
     *   {@ #keyList keyList()}.{@link List#get get}{@code (index)}.
     */
    public K getKey(int index);

}