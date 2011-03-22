package de.fencing_game.paul.examples;

import java.util.*;

/**
 * A map with two keys.
 *
 * @see java.util.Map
 * @see http://stackoverflow.com/questions/5354659/two-dimensional-array-structure-question/5355302#5355302
 *
 * @param <K1> the first key type.
 * @param <K2> the second key type.
 * @param <V> the value type.
 */
public interface Map2D<K1,K2,V> {

    /**
     * A pair of keys for our map.
     */
    public interface KeyPair<K1,K2> {
        public K1 getKey1();
        public K2 getKey2();
        /**
         * returns the hashCode of this entry.
         * The hashcode is defines as 
         * {@code  getKey1().hashCode() * 13 + getKey2().hashCode() * 17}
         * If one of these objects is {@code null}, replace 0 for
         * its hash code.
         */
        public int hashCode();

        /**
         * compares this entry to another object. An object {@code that} is
         * equal this entry, if and only if it is also an Map2D.KeyPair, and 
         * {@code
         *  this.getKey1().equals(that.getKey1()) &&
         *  this.getKey2().equals(that.getKey2());
         * }
         */
        public boolean equals(Object that);
    }


    /**
     * an entry in our map, consisting of two keys and a value.
     * @see Map.Entry
     */
    public interface Entry<K1,K2,V> {
        public K1 getKey1();
        public K2 getKey2();
        public V getValue();
        public V setValue(V val);
        /**
         * returns the hashCode of this entry.
         * The hashcode is defines as 
         * {@code  getKey1.hashCode() * 7 + getKey2().hashCode() * 3 +
         *         getValue().hashCode() * 5 }
         * If one of these objects is {@code null}, replace 0 for
         * its hash code.
         */
        public int hashCode();

        /**
         * compares this entry to another object. An object {@code that} is
         * equal this entry, if and only if it is also an Map2D.Entry, and 
         * {@code
         *  this.getKey1().equals(that.getKey1()) &&
         *  this.getKey2().equals(that.getKey2()) &&
         *  this.getValue().equals(that.getValue());
         * }
         */
        public boolean equals(Object that);
    }


    /**
     * the number of mappings in this map.
     */
    public int size();

    /**
     * returns true if this map is empty.
     */
    public boolean isEmpty();


    /**
     * returns true if this map contains a mapping for the specified key pair.
     */
    public boolean containsKeys(Object key1, Object key2);

    /**
     * returns true if this map contains a mapping with this
     * specified key as first key.
     */
    public boolean containsKey1(Object key1);
    /**
     * returns true if this map contains a mapping with this
     * specified key as second key.
     */
    public boolean containsKey2(Object key2);

    /**
     * returns true if this map contains a mapping with the
     * specified value.
     */
    public boolean containsValue(Object value);

    /**
     * retrieves an element from the map.
     * @return null if there is no such pair of keys, else the value.
     */
    public V get(K1 key1, K2 key2);
    
    /**
     * puts a new element in the map.
     * @return the old value, or null, if there was no such mapping.
     */
    public V put(K1 key1, K2 key2, V value);

    /**
     * removes a mapping from this map.
     */
    public V remove(Object key1, Object key2);

    /**
     * puts all mappings from the specified 2d-map into this map.
     */
    public void putAll(Map2D<? extends K1, ? extends K2, ? extends V> map);

    /**
     * clears this map.
     */
    public void clear();

    /**
     * returns a collection view of the values of this map.
     */
    public Collection<V> values();

    /**
     * returns a collection view of this map's entries.
     */
    public Set<Map2D.Entry<K1,K2,V>> entrySet();

    /**
     * returns a map view as a map of key-pairs to values.
     */
    public Map<KeyPair<K1,K2>, V> flattedMap();

    /**
     * returns a map view as a map of maps, by first key first.
     */
    public Map<K1, Map<K2, V>> projection1Map();

    /**
     * returns a map view as a map of maps, by second key first.
     */
    public Map<K2, Map<K1, V>> projection2Map();

    /**
     * compares this map with another object.
     * This Map2D is equal to another object only if it also is a Map2D
     * and these equivalent conditions hold:
     * <ul>
     *   <li>{@code this.entrySet().equals(that.entrySet())}</li>
     *   <li>{@code this.flattedMap().equals(that.flattedMap())}</li>
     *   <li>{@code this.projection1Map().equals(that.projection1Map())}</li>
     *   <li>{@code this.projection2Map().equals(that.projection2Map())}</li>
     * </ul>
     */
    public boolean equals(Object that);

    /**
     * The hashCode is defined as the {@link #entrySet()}{@code .hashCode()}.
     */
    public int hashCode();

}