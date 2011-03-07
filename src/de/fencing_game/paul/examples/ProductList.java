package de.fencing_game.paul.examples;

import java.util.*;

/**
 * The cartesian product of lists, in an (unmodifiable) index-based
 * implementation.
 *
 *<p>
 * The elements of the product are tuples (lists) of elements, one from
 * each of the base list's element lists.
 * These are ordered in lexicographic order, by their appearance in the
 * base lists.
 *</p>
 *<p>
 * This class works lazily, creating the elements of the product only
 * on demand. It needs no additional memory to the base list.
 *</p>
 *<p>
 * This class works even after changes of the base list or its elements -
 * the size of this list changes if any of the factor lists changes size.
 * Such changes should not occur during calls to this method, or
 * you'll get inconsistent results.
 *</p>
 * <p>
 *   The product of the sizes of the component lists should be smaller than
 *   Integer.MAX_INT, otherwise you'll get strange behaviour.
 * </p>
 * 
 *<p>
 * Inspired by the question <a href="http://stackoverflow.com/questions/5220701/how-to-get-a-list-of-all-lists-containing-exactly-one-element-of-each-list-of-a-l/5222370#5222370">How to get a list of all lists containing exactly one element of each list of a list of lists</a> on Stackoverflow (by Dunaril).
 *
 * @author Paŭlo Ebermann
 */
public class ProductList<X>
    extends AbstractList<List<X>>
{

    private List<? extends List<? extends X>> factors;

    /**
     * create a new product list, based on the given list of factors.
     */
    public ProductList(List<? extends List<? extends X>> factors) {
        this.factors = factors;
    }

    /**
     * calculates the total size of this list.
     * This method takes O(# factors) time.
     */
    public int size() {
        int product = 1;
        for(List<?> l : factors) {
            product *= l.size();
        }
        return product;
    }

    /**
     * returns an element of the product list by index.
     *
     * This method calls the get method of each list,
     * so needs needs O(#factors) time if the individual
     * list's get methods are in O(1).
     * The space complexity is O(#factors), since we have to store
     * the result somewhere.
     *
     * @return the element at the given index.
     * The resulting list is of fixed-length and after return independent
     * of this product list. (You may freely modify it like an array.)
     */
    public List<X> get(int index) {
        if(index < 0)
            throw new IndexOutOfBoundsException("index " + index+ " < 0");

        List<X> result = Utils.createFixedList(factors.size());

        // we iteratively lookup the components, using
        // modulo and division to calculate the right
        // indexes.
        for(int i = factors.size() - 1; i >= 0; i--) {
            List<? extends X> subList = factors.get(i);
            int subIndex = index % subList.size();
            result.set(i, subList.get(subIndex));
            index = index / subList.size();
        }
        if(index > 0)
            throw new IndexOutOfBoundsException("too large index");

        return result;
    }

    /**
     * an optimized indexOf() implementation, runs in
     * O(sum n_i) instead of O(prod n_i)
     * (if the individual indexOf() calls take O(n_i) time).
     *
     * Runs in O(1) space.
     */
    public int indexOf(Object o)
    {
        if(!(o instanceof List))
            return -1;
        List<?> list = (List<?>)o;
        if (list.size() != factors.size())
            return -1;
        int index = 0;
        for(int i = 0; i < factors.size(); i++) {
            List<?> subList = factors.get(i);
            Object candidate = list.get(i);
            int subIndex = subList.indexOf(candidate);
            if(subIndex < 0)
                return -1;
            index = index * subList.size() + subIndex;
        }
        return index;
    }

    /**
     * an optimized lastIndexOf() implementation, runs in
     * O(sum n_i) time instead of O(prod n_i) time
     * (if the individual indexOf() calls take O(n_i) time).
     * Runs in O(1) space.
     */
    public int lastIndexOf(Object o)
    {
        if(!(o instanceof List))
            return -1;
        List<?> list = (List<?>)o;
        if (list.size() != factors.size())
            return -1;
        int index = 0;
        for(int i = 0; i < factors.size(); i++) {
            List<?> subList = factors.get(i);
            Object candidate = list.get(i);
            int subIndex = subList.lastIndexOf(candidate);
            if(subIndex < 0)
                return -1;
            index = index * subList.size() + subIndex;
        }
        return index;
    }

    /**
     * an optimized contains check, based on {@link #indexOf}.
     */
    public boolean contains(Object o) {
        return indexOf(o) != -1;
    }


    /**
     * a test method which creates a list of lists and
     * shows the cartesian product of this.
     */
    public static void main(String[] params) {

        @SuppressWarnings("unchecked")
        List<List<Integer>> factors =
            Arrays.asList(Arrays.asList(1,2),
                          Arrays.asList(10,20,30, 20),
                          Arrays.asList(100));
        System.out.println("factors: " + factors);
        List<List<Integer>> product =
            new ProductList<Integer>(factors);
        System.out.println("product: " + product);
        List<Integer> example = Arrays.asList(2,20,100);
        System.out.println("indexOf(" + example +") = " +
                           product.indexOf(example));
        System.out.println("lastIndexOf(" + example +") = " +
                           product.lastIndexOf(example));
    }



}