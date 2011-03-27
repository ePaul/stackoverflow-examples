package de.fencing_game.paul.examples;

import java.util.*;

/**
 * An unmodifiable set of all subsets of a given size of a given (finite) set.
 *
 * Inspired by http://stackoverflow.com/questions/5428417/idiom-for-getting-unique-pairs-of-collection-elements-in-java
 */
public class FiniteSubSets<X>
    extends AbstractSet<Set<X>> {

    private final Set<X> baseSet;
    private final int subSize;

    /**
     * creates a set of all subsets of a given size of a given set.
     * 
     * @param baseSet the set whose subsets should be in this set.
     * For this to work properly, the baseSet's iterators should iterate
     * every time in the same order, as long as one iteration of this set
     * is in progress.
     *
     * The baseSet does not need to be immutable, but it should not change
     * while an iteration of this set is in progress.
     * @param subSetSize
     */
    public FiniteSubSets(Set<X> baseSet, int subSetSize) {
        this.baseSet = baseSet;
        this.subSize = subSetSize;
    }

    /**
     * calculates the size of this set.
     *
     * This is the binomial coefficient.
     */
    public int size() {
        int baseSize = baseSet.size();
        long size = binomialCoefficient(baseSize, subSize);
        return (int)Math.min(size, Integer.MAX_VALUE);
    }

    public Iterator<Set<X>> iterator() {
        if(subSize == 0) {
            // our IteratorImpl does not work for k == 0.
            return Collections.singleton(Collections.<X>emptySet()).iterator();
        }
        return new IteratorImpl();
    }

    /**
     * checks if some object is in this set.
     *
     * This implementation is optimized compared to 
     * the implementation from AbstractSet.
     */
    public boolean contains(Object o) {
        return o instanceof Set && 
            ((Set)o).size() == subSize &&
            baseSet.containsAll((Set)o);
    }

    
    /**
     * The implementation of our iterator.
     */
    private class IteratorImpl implements Iterator<Set<X>> {

        /**
         * A stack of iterators over the base set.
         * We only ever manipulate the top one, the ones below only
         * after the top one came to its end.
         * The stack should be always full, except in the constructor or
         * inside the {@link #step} method.
         */
        private Deque<Iterator<X>> stack = new ArrayDeque<Iterator<X>>(subSize);
        /**
         * a linked list maintaining the current state of the iteration, i.e.
         * the next subset. It is null when there is no next subset, but
         * otherwise it should have always full length subSize (except when
         * inside step method or constructor).
         */
        private Node current;

        /**
         * constructor to create the stack of iterators and initial
         * node.
         */
        IteratorImpl() {
            try {
                for(int i = 0; i < subSize; i++) {
                    initOneIterator();
                }
            }
            catch(NoSuchElementException ex) {
                current = null;
            }
            //      System.err.println("IteratorImpl() End, current: " + current);
        }

        /**
         * initializes one level of iterator and node.
         * Only called from the constructor.
         */
        private void initOneIterator() {
            Iterator<X> it = baseSet.iterator();
            if(current != null) {
                scrollTo(it, current.element);
            }

            X element = it.next();
            current = new Node(current, element);
            stack.push(it);
        }

        /**
         * gets the next element from the set (i.e. the next
         * subset of the base set).
         */
        public Set<X> next() {
            if(current == null) {
                throw new NoSuchElementException();
            }
            Set<X> result = new SubSetImpl(current);
            step();
            return result;
        }

        /**
         * returns true if there are more elements.
         */
        public boolean hasNext() {
            return current != null;
        }

        /** throws an exception. */
        public void remove() {
            throw new UnsupportedOperationException();
        }


        /**
         * Steps the iterator on the top of the stack to the
         * next element, and store this in {@link #current}.
         *
         * If this iterator is already the end, we recursively
         * step the iterator to the next element.
         *
         * If there are no more subsets at all, we set {@link #current}
         * to null.
         */
        void step() {
            Iterator<X> lastIt = stack.peek();
            current = current.next;
            while(!lastIt.hasNext()) {
                if(current == null) {
                    // no more elements in the first level iterator
                    // ==> no more subsets at all.
                    return;
                }

                // last iterator has no more elements
                // step iterator before and recreate last iterator.
                stack.pop();
                assert current != null;
                step();
                if(current == null) {
                    // after recursive call ==> at end of iteration.
                    return;
                }
                assert current != null;
                
                // new iterator at the top level
                lastIt = baseSet.iterator();
                if(!scrollTo(lastIt, current.element)) {
                    // Element not available anymore => some problem occured
                    current = null;
                    throw new ConcurrentModificationException
                        ("Element " + current.element + " not found!");
                }
                stack.push(lastIt);
            }
            // now we know the top iterator has more elements
            // ==> put the next one in `current`.

            X lastElement = lastIt.next();
            current = new Node(current, lastElement);

        }  // step()

    }

    /**
     * helper method which scrolls an iterator to some element.
     * @return true if the element was found, false if we came
     *    to the end of the iterator without finding the element.
     */
    private static <Y> boolean scrollTo(Iterator<? extends Y> it, Y element) {
        while(it.hasNext()) {
            Y itEl = it.next();
            if(itEl.equals(element)) {
                return true;
            }
        }
        return false;
    }

    /**
     * implementation of our subsets.
     * These sets are really immutable (not only unmodifiable).
     *
     * We implement them with a simple linked list of nodes.
     */
    private class SubSetImpl extends AbstractSet<X>
    {
        private final Node node;

        SubSetImpl(Node n) {
            this.node = n;
        }

        /**
         * the size of this set.
         */
        public int size() {
            return subSize;
        }

        /**
         * an iterator over our linked list.
         */
        public Iterator<X> iterator() {
            return new Iterator<X>() {
                private Node current = SubSetImpl.this.node;
                public X next() {
                    if(current == null) {
                        throw new NoSuchElementException();
                    }
                    X result = current.element;
                    current = current.next;
                    return result;
                }
                public boolean hasNext() {
                    return current != null;
                }
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }
    }

    /**
     * A simple immutable node class, used to implement our iterator and
     * the sets created by them.
     *
     * Two "neighbouring" subsets (i.e. which
     * only differ by the first element) share most of the linked list,
     * differing only in the first node.
     */
    private class Node {
        Node(Node n, X e) {
            this.next = n;
            this.element = e;
        }

        final X element;
        final Node next;
        
        public String toString() {
            return "[" + element + "]==>" + next;
        }
    }

    /**
     * Calculates the binomial coefficient B(n,k), i.e.
     * the number of subsets of size k in a set of size n.
     *
     * The algorithm is taken from the <a href="http://de.wikipedia.org/wiki/Binomialkoeffizient#Algorithmus_zur_effizienten_Berechnung">german wikipedia article</a>.
     */
    private static long binomialCoefficient(int n, int k) {
        if(k < 0 || n < k ) {
            return 0;
        }
        final int n_minus_k = n - k;
        if (k > n/2) {
            return binomialCoefficient(n, n_minus_k);
        }
        long prod = 1;
        for(int i = 1; i <= k; i++) {
            prod = prod * (n_minus_k + i) / i;
        }
        return prod;
    }


    /**
     * Demonstrating test method. We print all subsets (sorted by size) of
     * a set created from the command line parameters, or an example set, if
     * there are no parameters.
     */
    public static void main(String[] params) {
        Set<String> baseSet =
            new HashSet<String>(params.length == 0 ?
                                Arrays.asList("Hello", "World", "this",
                                              "is", "a", "Test"):
                                Arrays.asList(params));
        
        
        System.out.println("baseSet: " + baseSet);

        for(int i = 0; i <= baseSet.size()+1; i++) {
            Set<Set<String>> pSet = new FiniteSubSets<String>(baseSet, i);
            System.out.println("------");
            System.out.println("subsets of size "+i+":");
            int count = 0;
            for(Set<String> ss : pSet) {
                System.out.println("    " +  ss);
                count++;
            }
            System.out.println("in total: " + count + ", " + pSet.size());
        }
    }


}