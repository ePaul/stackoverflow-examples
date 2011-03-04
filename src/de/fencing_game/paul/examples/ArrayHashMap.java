package de.fencing_game.paul.examples;

import java.util.*;

/**
 * A combination of ArrayList and HashMap which allows O(1) for read and
 * modifiying access by index and by key.
 *
 * Removal (either by key or by index) is O(n), though,
 * as is indexed addition of a new Entry somewhere else than the end.
 */
public class ArrayHashMap<K,V>
    extends AbstractMap<K,V>
    implements IndexedMap<K,V>
{
    
    private Map<K, SimpleEntry<K,V>> baseMap;
    private List<SimpleEntry<K,V>> entries;
    

    public ArrayHashMap() {
        this.baseMap = new HashMap<K,SimpleEntry<K,V>>();
        this.entries = new ArrayList<SimpleEntry<K,V>>();
    }

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

    public V get(Object key) {
        SimpleEntry<K,V> entry = baseMap.get(key);
        return entry == null ? null : entry.getValue();
    }

    public boolean containsKey(Object key) {
        return baseMap.containsKey(key);
    }


    public V remove(Object key) {
        SimpleEntry<K,V> entry = baseMap.remove(key);
        if(entry == null) {
            return null;
        }
        entries.remove(entry);
        return entry.getValue();
    }

    public K getKey(int index) {
        return entries.get(index).getKey();
    }

    public V getValue(int index) {
        return entries.get(index).getValue();
    }

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

            public boolean contains(Object o) {
                return baseMap.containsKey(o);
            }
            
        };
    }


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
        };
    }  // entrySet()

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

            protected void removeRange(int fromIndex, int toIndex) {
                for(Map.Entry<K,V> entry : entries.subList(fromIndex, toIndex)){
                    baseMap.remove(entry.getKey());
                }
                entries.subList(fromIndex, toIndex).clear();
            }

        };
    }

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
        };
    }

    public List<V> values() {
        return new AbstractList<V>() {
            public void clear() {
                entryList().clear();
            }
            public V get(int index) {
                return entries.get(index).getValue();
            }
            public int size() {
                return entries.size();
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
        };
    }


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