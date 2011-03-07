package de.fencing_game.paul.examples;

import java.util.*;

/**
 * A collection of static utility methods for other classes.
 */
public class Utils {


    /**
     * Creates a simple list of given length, initially filled
     * with null references.
     *
     * The returned list only supports {@link List#get get()} and
     * {@link List#set set()}, no structural modifications
     * (adding/removing). (It is not threadsafe, so apply external
     * synchronization if needed.)
     */
    public static <T> List<T> createFixedList(int len) {
        // as the array can only be used through the list, there
        // is no type unsafety here.
        @SuppressWarnings("unchecked")
        List<T> list = (List<T>)Arrays.asList(new Object[len]);
        return list;
    }


    /**
     * converts an Iterable into a list. This method works
     * non-lazily, iterating once over the iterable and adding
     * all its elements into a new ArrayList.
     */
    public static <T> List<T> asList(Iterable<? extends T> iterable) {
        ArrayList<T> result = new ArrayList<T>();
        for(T t : iterable) {
            result.add(t);
        }
        result.trimToSize();
        return result;
    }

}