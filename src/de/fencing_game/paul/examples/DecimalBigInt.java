package de.fencing_game.paul.examples;

import java.util.Arrays;
import java.util.Formatter;

/**
 * A big-number class for natural numbers (i.e. nonnegative),
 * which uses a decimal-based format (instead of a binary based
 * one like the {@link java.math.BigInteger}).
 *
 * The numbers are stored in a positional notation with base 1000000000 (10⁹).
 *
 * Until now we only have addition and multiplication, no subtraction
 * or division.
 *
 * See my answer <a href="http://stackoverflow.com/questions/5318068/very-large-numbers-in-java-without-using-java-math-biginteger/5318896#5318896">Creating a simple Big number class in Java</a>
 * on Stackoverflow for details on how it is done.
 * @author Paŭlo Ebermann
 */
public class DecimalBigInt
    implements Comparable<DecimalBigInt>
{

    /**
     * The base (radix) of our positional system.
     */
    public final static int BASE = 1000000000;

    /**
     * The number of decimal digits fitting in one digit
     * of our system.
     */
    private final static int BASE_DECIMAL_DIGITS = 9;


    /**
     * big-endian representation of the digits.
     * Little-endian would have been smarter, but
     * now I don't want to change it everywhere.
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
     * calculates the sum {@code this + that}.
     */
    public DecimalBigInt plus(DecimalBigInt that) {
        int[] result = new int[Math.max(this.digits.length,
                                        that.digits.length)+ 1];

        addDigits(result, result.length-1, this.digits);
        addDigits(result, result.length-1, that.digits);

        // cut off leading zero, if any
        if(result[0] == 0) {
            result = Arrays.copyOfRange(result, 1, result.length);
        }
        return new DecimalBigInt(result);
    }

    /**
     * adds all the digits from the addend array to the result array.
     */
    private void addDigits(int[] result, int resultIndex,
                           int... addend)
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


    /**
     * multiplies two digits and adds the product to the result array
     * at the right digit-position.
     */
    private void multiplyDigit(int[] result, int resultIndex,
                               int firstFactor, int secondFactor) {
        long prod = (long)firstFactor * (long)secondFactor;
        int prodDigit = (int)(prod % BASE);
        int carry = (int)(prod / BASE);
        addDigits(result, resultIndex, carry, prodDigit);
    }


    /**
     * multiplies all digits of two factors and adds them to the result.
     */
    private void multiplyDigits(int[] result, int resultIndex,
                                int[] leftFactor, int[] rightFactor) {
        for(int i = 0; i < leftFactor.length; i++) {
            for(int j = 0; j < rightFactor.length; j++) {

                multiplyDigit(result, resultIndex - (i + j),
                              leftFactor[leftFactor.length-i-1],
                              rightFactor[rightFactor.length-j-1]);
            }
        }
    }

    /**
     * returns the product {@code this × that}.
     */
    public DecimalBigInt times(DecimalBigInt that) {
        int[] result = new int[this.digits.length + that.digits.length];
        multiplyDigits(result, result.length-1, 
                       this.digits, that.digits);

        // cut off leading zero, if any
        if(result[0] == 0) {
            result = Arrays.copyOfRange(result, 1, result.length);
        }
        return new DecimalBigInt(result);
    }

    /**
     * calculates a hashCode for this object.
     */
    public int hashCode() {
        int hash = 0;
        for(int digit : digits) {
            hash = hash * 13 + digit;
        }
        return hash;
    }

    /**
     * compares this object with another object for equality.
     * A DecimalBigInt is equal to another object only if this other
     * object is also a DecimalBigInt and both represent the same
     * natural number.
     */
    public boolean equals(Object o) {
        return o instanceof DecimalBigInt &&
            this.compareTo((DecimalBigInt)o) == 0;
    }


    /**
     * compares this {@link DecimalBigInt} to another one.
     * @return -1 if this < that, 0 if this == that and 1 if this > that.
     */
    public int compareTo(DecimalBigInt that) {
        if(this.digits.length < that.digits.length) {
            return -1;
        }
        if (that.digits.length < this.digits.length) {
            return 1;
        }
        // same length, compare the digits
        for(int i = 0; i < this.digits.length; i++) {
            if(this.digits[i] < that.digits[i]) {
                return -1;
            }
            if(that.digits[i] < this.digits[i]) {
                return 1;
            }
        }
        // same digits
        return 0;
    }


    /**
     * calculates the faculty of an int number.
     * This uses a simple iterative loop.
     */
    public static DecimalBigInt faculty(int n) {
        DecimalBigInt fac = new DecimalBigInt(1);
        for(int i = 2; i <= n; i++) {
            fac = fac.times(new DecimalBigInt(i));
        }
        return fac;
    }


    /**
     * a test method, which demonstrates the usage.
     */
    public static void main(String[] params) {
        // test of constructor + toString
        DecimalBigInt d = new DecimalBigInt(7, 5, 2, 12345);
        System.out.println("d: " + d);

        // test of valueOf
        DecimalBigInt d2 = DecimalBigInt.valueOf("12345678901234567890");
        System.out.println("d2: " +d2);

        // test of toDecimalString
        System.out.println("d: " + d.toDecimalString());
        System.out.println("d2: " + d2.toDecimalString());

        // test of plus
        DecimalBigInt sum = d2.plus(d2).plus(d2); 
        System.out.println("sum: " + sum);

        // test of times:
        DecimalBigInt prod = d2.times(d2);
        System.out.println("prod: " + prod);

        System.out.println("d2 <=> d: " + d2.compareTo(d));
        System.out.println("d <=> d2: " + d.compareTo(d2));
        System.out.println("sum <=> d: " + sum.compareTo(d));
        System.out.println("prod <=> d: " + prod.compareTo(d));
        System.out.println("d <=> prod: " + d.compareTo(prod));

        DecimalBigInt fac = DecimalBigInt.faculty(90);
        System.out.println("fac(90) = " + fac.toDecimalString());
    }


}