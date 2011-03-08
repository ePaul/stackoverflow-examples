package de.fencing_game.paul.examples;

import java.util.*;


/**
 * Prints all n-ary numbers of a given length
 * in lexicographic order.
 *
 * This uses a comma-separated list-like presentation,
 * but has the shortest representation :-)
 *
 * http://stackoverflow.com/questions/5238257/practice-for-programming-competition/5238629#5238629
 */
public class NAryNumbersByProductList {

    private static void printNumbers(int length, List<String> digits)
    {
        System.out.println(new ProductList<String>
                           (Collections.nCopies(length, digits)));
    }

    private static void printBinary(int length) {
        printNumbers(length, Arrays.asList("0", "1"));
    }


    /**
     * test method.
     * As parameters, give the length and then the digits.
     */
    public static void main(String[] params) {
        if(params.length == 0) {
            printBinary(5);
            return;
        }
        int len = Integer.parseInt(params[0]);
        if(params.length == 1) {
            printBinary(len);
            return;
        }
        List<String> digits =
            Arrays.asList(params).subList(1, params.length);
        printNumbers(len, digits);
    }




}