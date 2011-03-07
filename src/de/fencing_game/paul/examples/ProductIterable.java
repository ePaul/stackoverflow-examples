package de.fencing_game.paul.examples;

import java.util.*;

/**
 * A iterable over the cartesian product of a iterable of iterables
 * with some common element type.
 *<p>
 * The elements of the product are tuples (lists) of elements, one of
 * each of the original iterables.
 *<p>
 * The iterator iterates the elements in lexicographic order, ordered by
 * the appearance of their components in their respective iterators.
 *<p>
 * Since we are iterating the iterables lazily, the iterators should
 * act the same each time, otherwise you'll get strange results (but it
 * will still be well-defined).
 *</p>
 * 
 * Inspired by the question <a href="http://stackoverflow.com/questions/5220701/how-to-get-a-list-of-all-lists-containing-exactly-one-element-of-each-list-of-a-l/5222370#5222370">How to get a list of all lists containing exactly one element of each list of a list of lists</a> on Stackoverflow (by Dunaril).
 *
 * @author Paŭlo Ebermann
 */
public class ProductIterable<X>
    implements Iterable<List<X>>
{

    private Iterable<? extends Iterable<? extends X>> factors;

    /**
     * creates a new ProductIterable based on a iterable of iterables.
     */
    public ProductIterable(Iterable<? extends Iterable<? extends X>> factors) {
        this.factors = factors;
    }


    /**
     * returns an iterator over the elements of the
     * cartesian product.
     *<p>
     * The lists returned by this iterator are fixed-size, i.e. support
     * no add/remove. Using {@link List#set set} is possible, but does
     *  not effect this ProductIterable.
     *</p>
     *<p>
     * The iterator takes a snapshot of the Iterable given to the
     * constructor in the moment this method is called, the individual
     * iterables' iterators are still called semi-lazily (i.e.
     * maximally one element ahead) when needed.
     *</p>
     */
    public Iterator<List<X>> iterator() {
        return new ProductIterator();
    }

    /**
     * the class implementing our iterator.
     */
    private class ProductIterator
        implements Iterator<List<X>>
    {

        /**
         * an element of our stack, which contains
         * an iterator, the last element returned by
         * this iterator, and the Iterable which created
         * this iterator.
         */
        private class StackElement {
            X item;
            Iterator<? extends X> iterator;
            Iterable<? extends X> factor;
            boolean has;

            StackElement(Iterable<? extends X> fac) {
                this.factor = fac;
                newIterator();
            }

            /**
             * checks whether the {@link #step} call can
             * get a new item.
             * 
             */
            boolean hasNext() {
                return has ||
                    (has = iterator.hasNext());
            }

            /**
             * steps to the next item.
             */
            void step() {
                item = iterator.next();
                has = false;
            }

            /**
             * creates a new iterator.
             */
            void newIterator() {
                iterator = factor.iterator();
                has = false;
            }

            /**
             * for debugging: a string view of this StackElement.
             */
            public String toString() {
                return "SE[ i: " + item + ", f: " + factor + "]";
            }
        }

        /**
         * our stack of iterators to run through
         */
        private Deque<StackElement> stack;
        /**
         * is our next element already produced (= contained in
         * the `item`s of the stack?
         */
        private boolean hasNext;


        /**
         * constructor.
         */
        ProductIterator() {
            stack = new ArrayDeque<StackElement>();
            try {
                fillStack();
                hasNext = true;
            }
            catch(NoSuchElementException ex) {
                hasNext = false;
            }
        }

        /**
         * creates the stack. only called from constructor.
         */
        private void fillStack() {
            for(Iterable<? extends X> fac : factors) {
                StackElement el = new StackElement(fac);
                el.step();
                stack.push(el);
            }
        }

        /**
         * steps the iterator on top of the stack, and maybe the iterators
         * below, too.
         * @return true if more elements are available.
         */
        private boolean stepIterator() {
            if(stack.isEmpty()) 
                return false;
            StackElement top = stack.peek();
            while(!top.hasNext()) {
                stack.pop();
                if (!stepIterator()) {
                    return false;
                }
                top.newIterator();
                stack.push(top);
            }
            top.step();
            return true;
        }

        /**
         * returns true if `next` will return a next element.
         */
        public boolean hasNext() {
            return 
                hasNext || 
                (hasNext = stepIterator());
        }

        /**
         * returns the next element of the cartesian product.
         */
        public List<X> next() {
            if(!hasNext()) {
                throw new NoSuchElementException();
            }
            hasNext = false;
            return makeList();
        }

        /**
         * creates a list from the StackElements in reverse order.
         */
        private List<X> makeList() {
            List<X> list = Utils.createFixedList(stack.size());
            int index = stack.size();
            for(StackElement se : stack) {
                index --;
                list.set(index, se.item);
            }
            return list;
        }

        /**
         * the remove method is not supported,
         * the cartesian product is immutable.
         */
        public void remove() {
            throw new UnsupportedOperationException();
        }

    }  // class ProductIterator


    /**
     * a test method which creates a list of lists and
     * from this the cartesian product.
     */
    public static void main(String[] params)
    {
        List<List<?>> factors =
            Arrays.<List<?>>asList(Arrays.asList(1,2),
                                   Arrays.asList(10,20,30, 20),
                                   Arrays.asList(100));
        System.out.println("factors: " + factors);
        Iterable<List<Object>> product =
            new ProductIterable<Object>(factors);

        System.out.println("product: " + Utils.asList(product));
    }

}
