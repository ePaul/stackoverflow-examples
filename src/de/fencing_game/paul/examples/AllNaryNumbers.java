package de.fencing_game.paul.examples;

import java.util.Arrays;


/**
 * Prints all n-ary numbers of a given length
 * in lexicographic order.
 *
 * http://stackoverflow.com/questions/5238257/practice-for-programming-competition/5238629#5238629
 */
public class AllNaryNumbers {


    private static void printAll(String prefix, Iterable<String> digits,
                                int length)
    {
        if(length == 0) {
            System.out.println(prefix);
            return;
        }
        for(String digit : digits) {
            printAll(prefix + digit, digits, length-1);
        }
    }

    private static void printNumbers(int length, Iterable<String> digits) {
        printAll("", digits, length);
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
        Iterable<String> digits =
            Arrays.asList(params).subList(1, params.length);
        printNumbers(len, digits);
    }

}