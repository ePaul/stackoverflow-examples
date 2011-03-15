package de.fencing_game.paul.examples;

import java.util.Arrays;


public class DecimalBigInt {


    public final static int BASE = 1000000000;

    private final static int BASE_DECIMAL_DIGITS = 9;


    /**
     * big-endian representation of the digits.
     */
    private int[] digits;


    /**
     * creates a DecimalBigInt based on an array of digits.
     * @param digits a list of digits, each between 0 (inclusive)
     *    and {@link BASE} (exclusive).
     * @throws IllegalArgumentException if any digit is out of range.
     */
    public DecimalBigInt(int... digits) {
        for(int digit : digits) {
            if(digit < 0 ||  BASE <= digit) {
                throw new IllegalArgumentException("digit " + digit +
                                                   " out of range!");
            }
        }
        this.digits = digits.clone();
    }

  
    /**
     * A simple string view for debugging purposes.
     * (Will be replaced later with a real decimal conversion.)
     */
    public String toString() {
        return "Big" + Arrays.toString(digits);
    }




    public static void main(String[] params) {
        DecimalBigInt d = new DecimalBigInt(7, 5, 2, 12345);
        System.out.println(d);

    }


}