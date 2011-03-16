package de.fencing_game.paul.examples;

import java.util.Arrays;
import java.util.Formatter;


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


    /**
     * creates a DecimalBigInt from a decimal representation.
     * @param decimal a string of decimal digits.
     * @throws NumberFormatException if the number is not in
     *     correct decimal format, e.g. if it contains any characters
     *     outside of 0..9.
     */
    public static DecimalBigInt valueOf(String decimal) {
        int decLen = decimal.length();
        int bigLen = (decLen-1) / BASE_DECIMAL_DIGITS + 1;
        // length of first block
        int firstSome = decLen - (bigLen-1) * BASE_DECIMAL_DIGITS;
        int[] digits = new int[bigLen];
        for(int i = 0; i < bigLen ; i++) {
            String block =
                decimal.substring(Math.max(firstSome + (i-1)*BASE_DECIMAL_DIGITS, 0),
                                  firstSome +   i  *BASE_DECIMAL_DIGITS);
            digits[i] = Integer.parseInt(block);
        }
        return new DecimalBigInt(digits);
    }

    /**
     * formats the number as a decimal String.
     */
    public String toDecimalString() {
        StringBuilder b =
            new StringBuilder(BASE_DECIMAL_DIGITS * digits.length);
        Formatter f = new Formatter(b);
        f.format("%d", digits[0]);
        for(int i = 1 ; i < digits.length; i++) {
            f.format("%09d", digits[i]);
        }
        return b.toString();
    }


    /**
     * calculates the sum of this and that.
     */
    public DecimalBigInt plus(DecimalBigInt that) {
        int[] result = new int[Math.max(this.digits.length,
                                        that.digits.length)+ 1];

        addDigits(result, result.length-1, this.digits);
        addDigits(result, result.length-1, that.digits);

        // cut of leading zero, if any
        if(result[0] == 0) {
            result = Arrays.copyOfRange(result, 1, result.length);
        }
        return new DecimalBigInt(result);
    }

    /**
     * adds all the digits from the addend array to the result array.
     */
    private void addDigits(int[] result, int resultIndex,
                           int[] addend)
    {
        int addendIndex = addend.length - 1;
        while(addendIndex >= 0) {
            addDigit(result, resultIndex,
                     addend[addendIndex]);
            addendIndex--;
            resultIndex--;
        }
    }


    /**
     * adds one digit from the addend to the corresponding digit
     * of the result.
     * If there is carry, it is recursively added to the next digit
     * of the result.
     */
    private void addDigit(int[] result, int resultIndex,
                          int addendDigit)
    {
        int sum = result[resultIndex] + addendDigit;
        result[resultIndex] = sum % BASE;
        int carry = sum / BASE;
        if(carry > 0) {
            addDigit(result, resultIndex - 1, carry);
        }
    }


    public static void main(String[] params) {
        // test of constructor + toString
        DecimalBigInt d = new DecimalBigInt(7, 5, 2, 12345);
        System.out.println(d);

        // test of valueOf
        DecimalBigInt d2 = DecimalBigInt.valueOf("12345678901234567890");
        System.out.println(d2);

        // test of toDecimalString
        System.out.println(d.toDecimalString());
        System.out.println(d2.toDecimalString());

        DecimalBigInt sum = d2.plus(d2).plus(d2); 
        System.out.println("sum: " + sum);
    }


}