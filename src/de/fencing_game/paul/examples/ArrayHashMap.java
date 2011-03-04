package de.fencing_game.paul.examples;

import java.util.*;

/**
 * A combination of ArrayList and HashMap which allows O(1) for read and
 * modifiying access by index and by key.
 * <p>
 *   Removal (either by key or by index) is O(n), though,
 *   as is indexed addition of a new Entry somewhere else than the end.
 *   (Adding at the end is in amortized O(1).)
 * </p>
 * <p>
 *   (The O(1) complexity for key based operations is under the condition
 *    "if the hashCode() method of the keys has a suitable distribution and
 *     takes constant time", as for any hash-based data structure.)
 * </p>
 * <p>
 *  This map allows null keys and values, but clients should think about
 *  avoiding using these, since some methods return null to show
 *  "no such mapping".
 * </p>
 * <p>
 *   This class is not thread-safe (like ArrayList and HashMap themselves).
 * </p>
 * <p>
 *  This class is inspired by the question
 *    <a href="http://stackoverflow.com/questions/5192706/java-is-there-a-container-which-effectively-combines-hashmap-and-arraylist">Is there a container which effectively combines HashMap and ArrayList?</a> on Stackoverflow.
 * </p>
 * @author Paŭlo Ebermann
 */
public class ArrayHashMap<K,V>
    extends AbstractMap<K,V>
    implements IndexedMap<K,V>
{

    /**
     * Our backing map.
     */
    private Map<K, SimpleEntry<K,V>> baseMap;
    /**
     * our backing list.
     */
    private List<SimpleEntry<K,V>> entries;
    
    /**
     * creates a new ArrayHashMap with default parameters.
     * (TODO: add more constructors which allow tuning.)
     */
    public ArrayHashMap() {
        this.baseMap = new HashMap<K,SimpleEntry<K,V>>();
        this.entries = new ArrayList<SimpleEntry<K,V>>();
    }


    /**
     * puts a new key-value mapping, or changes an existing one.
     *
     * If new, the mapping gets an index at the end (i.e. {@link #size()}
     * before it gets increased).
     *
     * This method runs in O(1) time for changing an existing value,
     *  amortized O(1) time for adding a new value.
     *
     * @return the old value, if such, else null.
     */
    public V put(K key, V value) {
        SimpleEntry<K,V> entry = baseMap.get(key);
        if(entry == null) {
            entry = new SimpleEntry<K,V>(key, value);
            baseMap.put(key, entry);
            entries.add(entry);
            return null;
        }
        return entry.setValue(value);
    }

    /**
     * retrieves the value for a key.
     *
     *   This method runs in O(1) time.
     *
     * @return null if there is no such mapping,
     *   else the value for the key.
     */
    public V get(Object key) {
        SimpleEntry<K,V> entry = baseMap.get(key);
        return entry == null ? null : entry.getValue();
    }

    /**
     * returns true if the given key is in the map.
     *
     *   This method runs in O(1) time.
     *
     */
    public boolean containsKey(Object key) {
        return baseMap.containsKey(key);
    }

    /**
     * removes a key from the map.
     *
     *   This method runs in O(n) time, n being the size of this map.
     *
     * @return the old value, if any.
     */
    public V remove(Object key) {
        SimpleEntry<K,V> entry = baseMap.remove(key);
        if(entry == null) {
            return null;
        }
        entries.remove(entry);
        return entry.getValue();
    }


    /**
     * returns a key by index.
     *
     *   This method runs in O(1) time.
     *
     */
    public K getKey(int index) {
        return entries.get(index).getKey();
    }

    /**
     * returns a value by index.
     *
     *   This method runs in O(1) time.
     *
     */
    public V getValue(int index) {
        return entries.get(index).getValue();
    }

    /**
     * Returns a set view of the keys of this map.
     *
     * This set view is ordered by the indexes.
     *
     * It supports removal by key or iterator in O(n) time.
     * Containment check runs in O(1).
     */
    public Set<K> keySet() {
        return new AbstractSet<K>() {
            public void clear() {
                entryList().clear();
            }

            public int size() {
                return entries.size();
            }

            public Iterator<K> iterator() {
                return keyList().iterator();
            }

            public boolean remove(Object key) {
                return keyList().remove(key);
            }

            public boolean contains(Object key) {
                return keyList().contains(key);
            }
        };
    }  // keySet()

    /**
     * Returns a set view of the entries of this map.
     *
     * This set view is ordered by the indexes.
     *
     * It supports removal by entry or iterator in O(n) time.
     *
     * It supports adding new entries at the end, if the key
     * is not already used in this map, in amortized O(1) time.
     *
     * Containment check runs in O(1).
     */
    public Set<Map.Entry<K,V>> entrySet() {
        return new AbstractSet<Map.Entry<K,V>>() {

            public void clear() {
                entryList().clear();
            }

            public int size() {
                return entries.size();
            }
            public Iterator<Map.Entry<K,V>> iterator() {
                return entryList().iterator();
            }
            public boolean add(Map.Entry<K,V> e) {
                return entryList().add(e);
            }

            public boolean contains(Object o) {
                return entryList().contains(o);
            }

            public boolean remove(Object o) {
                return entryList().remove(o);
            }


        };
    }  // entrySet()

    /**
     * Returns a list view of the entries of this map.
     *
     * This list view is ordered by the indexes.
     *
     * It supports removal by entry, iterator or sublist.clear in O(n) time.
     * (n being the length of the total list, not the sublist).
     *
     * It supports adding new entries at the end, if the key
     * is not already used in this map, in amortized O(1) time.
     *
     * Containment check runs in O(1).
     */
    public List<Map.Entry<K,V>> entryList() {
        return new AbstractList<Map.Entry<K,V>>() {
            public void clear() {
                baseMap.clear();
                entries.clear();
            }
            public Map.Entry<K,V> get(int index) {
                return entries.get(index);
            }
            public int size() {
                return entries.size();
            }
            public Map.Entry<K,V> remove(int index) {
                Map.Entry<K,V> e = entries.remove(index);
                baseMap.remove(e.getKey());
                return e;
            }
            public void add(int index, Map.Entry<K,V> newEntry) {
                K key = newEntry.getKey();
                SimpleEntry<K,V> clone = new SimpleEntry<K,V>(newEntry);
                if(baseMap.containsKey(key)) {
                    throw new IllegalArgumentException("duplicate key " +
                                                       key);
                }
                entries.add(index, clone);
                baseMap.put(key, clone);
            }

            public boolean contains(Object o) {
                if(o instanceof Map.Entry) {
                    SimpleEntry<K,V> inMap =
                        baseMap.get(((Map.Entry<?,?>)o).getKey());
                    return inMap != null &&
                        inMap.equals(o);
                }
                return false;
            }

            public boolean remove(Object o) {
                if (!(o instanceof Map.Entry)) {
                    Map.Entry<?,?> e = (Map.Entry<?,?>)o;
                    SimpleEntry<K,V> inMap = baseMap.get(e.getKey());
                    if(inMap != null && inMap.equals(e)) {
                        entries.remove(inMap);
                        baseMap.remove(inMap.getKey());
                        return true;
                    }
                }
                return false;
            }

            protected void removeRange(int fromIndex, int toIndex) {
                List<SimpleEntry<K,V>> subList =
                    entries.subList(fromIndex, toIndex);
                for(SimpleEntry<K,V> entry : subList){
                    baseMap.remove(entry.getKey());
                }
                subList.clear();
            }

        };
    }   // entryList()


    /**
     * Returns a List view of the keys in this map.
     *
     * It allows index read access and key containment check in O(1).
     * Changing a key is not allowed.
     *
     * Removal by key, index, iterator or sublist.clear runs in O(n) time
     * (this removes the corresponding values, too).
     */
    public List<K> keyList() {
        return new AbstractList<K>() {
            public void clear() {
                entryList().clear();
            }
            public K get(int index) {
                return entries.get(index).getKey();
            }
            public int size() {
                return entries.size();
            }
            public K remove(int index) {
                Map.Entry<K,V> e = entries.remove(index);
                baseMap.remove(e.getKey());
                return e.getKey();
            }

            public boolean remove(Object key) {
                SimpleEntry<K,V> entry = baseMap.remove(key);
                if(entry == null) {
                    return false;
                }
                entries.remove(entry);
                return true;
            }

            public boolean contains(Object key) {
                return baseMap.containsKey(key);
            }

            protected void removeRange(int fromIndex, int toIndex) {
                entryList().subList(fromIndex, toIndex).clear();
            }
        };
    }  // keyList()

    /**
     * Returns a List view of the values in this map.
     *
     * It allows get and set by index in O(1) time (set changes the mapping).
     *
     * Removal by value, index, iterator or sublist.clear is possible
     * in O(n) time, this removes the corresponding keys too (only the first
     * key with this value for remove(value)).
     *
     * Containment check needs an iteration, thus O(n) time.
     */
    public List<V> values() {
        return new AbstractList<V>() {
            public int size() {
                return entries.size();
            }
            public void clear() {
                entryList().clear();
            }
            public V get(int index) {
                return entries.get(index).getValue();
            }
            public V set(int index, V newValue) {
                Map.Entry<K,V> e = entries.get(index);
                return e.setValue(newValue);
            }

            public V remove(int index) {
                Map.Entry<K,V> e = entries.remove(index);
                baseMap.remove(e.getKey());
                return e.getValue();
            }
            protected void removeRange(int fromIndex, int toIndex) {
                entryList().subList(fromIndex, toIndex).clear();
            }
        };
    }  // values()


    /**
     * an usage example method.
     */
    public static void main(String[] args) {
        IndexedMap<String,String> imap = new ArrayHashMap<String, String>();
        
        for(int i = 0; i < args.length-1; i+=2) {
            imap.put(args[i], args[i+1]);
        }
        System.out.println(imap.values());
        System.out.println(imap.keyList());
        System.out.println(imap.entryList());
        System.out.println(imap);
        System.out.println(imap.getKey(0));
        System.out.println(imap.getValue(0));

    }


}