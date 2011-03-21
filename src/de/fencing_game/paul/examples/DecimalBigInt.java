package de.fencing_game.paul.examples;

import java.util.Arrays;
import java.util.Formatter;

/**
 * A big-number class for natural numbers (i.e. nonnegative),
 * which uses a decimal-based format (instead of a binary based
 * one like the {@link java.math.BigInteger}).
 * <p>
 * The numbers are stored in a positional notation with base 1000000000 (10⁹),
 * which supports easier conversion from/to decimal.
 * </p><p>
 * Until now we only have addition and multiplication, no subtraction
 * or division.
 * </p><p>
 * See my answer <a href="http://stackoverflow.com/questions/5318068/very-large-numbers-in-java-without-using-java-math-biginteger/5318896#5318896">Creating a simple Big number class in Java</a>
 * on Stackoverflow for details on how it is done.
 * </p>
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


    public final static DecimalBigInt ZERO = new DecimalBigInt();
    public final static DecimalBigInt ONE = new DecimalBigInt(1);

    /**
     * creates a DecimalBigInt based on an array of digits.
     * @param digits a list of digits, each between 0 (inclusive)
     *    and {@link BASE} (exclusive).
     * @throws IllegalArgumentException if any digit is out of range.
     */
    public DecimalBigInt(int... digits) {
        // we count how much leading zeroes there are.
        int zeroCount = 0;
        // zero == until now no nonzero digit seen.
        boolean zero = true;
        for(int digit : digits) {
            if(digit < 0 ||  BASE <= digit) {
                throw new IllegalArgumentException("digit " + digit +
                                                   " out of range!");
            }
            if(zero) {
                if (digit != 0) {
                    zero = false;
                }
                else {
                    zeroCount ++;
                }
            }
        }
        // cut off leading zeros by copying only the rest.
        // (We always do the copying, since we want to be independent
        //  from the input array.)
        this.digits = Arrays.copyOfRange(digits, zeroCount, digits.length);
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
     * converts a nonnegative {@code long} value to a DecimalBigInt number.
     * @throws IllegalArgumentException if number is negative.
     * @return the DecimalBigInt representation of the same natural number.
     */
    public static DecimalBigInt valueOf(long number) {
        if(number < 0) {
            throw new IllegalArgumentException("negative number: " +number);
        }
        if(number == 0L)
            return ZERO;
        if(number == 1L)
            return ONE;

        // BASE^2 = 10^18 < 2^63 < 10^19 < 10^27 = BASE^3
        // => long can have maximally 3 of our digits
        //    (and these are really needed)
        int[] digits = new int[3];
        // start with the last digit
        int index = 2;
        while(number > 0) {
            digits[index] = (int)(number % BASE);
            number = number / BASE;
            index-- ;
        }
        return new DecimalBigInt(digits);
    }

    /**
     * creates a DecimalBigInt from a string-representation
     * in some arbitrary radix.
     *
     * Note: this is not the most efficient implementation, since we
     * use the Horner scheme on DecimalBigInt values, instead working
     * directly on {@code int[]} and convert to a DecimalBigInt only
     * in the end.
     *
     * @param text the big-endian string representation of the number, using
     *  the decimal digits '0' ... '9' and additionally the latin
     *  letters 'A' ... 'Z' (or 'a' ... 'z'). (Only letters below
     *  the given radix are allowed, of course.)
     * @param radix the radix used in the representation, between
     *   {@link Character.MIN_RADIX} (2, inclusive) and
     *   {@link Character.MAX_RADIX} (36, inclusive).
     */
    public static DecimalBigInt valueOf(String text, int radix) {
        if(radix < Character.MIN_RADIX || Character.MAX_RADIX < radix) {
            throw new IllegalArgumentException("radix out of range: " + radix);
        }
        DecimalBigInt bigRadix = new DecimalBigInt(radix);
        DecimalBigInt value = ZERO;
        for(char digit : text.toCharArray()) {
            int iDigit = Character.digit(digit, radix);
            if(iDigit < 0) {
                throw new NumberFormatException("digit " + digit +
                                                " is not a valid base-"+radix+
                                                "-digit.");
            }
            DecimalBigInt bigDigit = new DecimalBigInt(iDigit);
            value = value.times(bigRadix).plus(bigDigit);
        }
        return value;
    }

    /**
     * creates a DecimalBigInt from a representation
     * in some arbitrary radix.
     *
     * @param digits the individual digits, each between 0 (inclusive)
     *   and radix, exclusive.
     * @param radix the radix of the representation, an arbitrary value >= 2.
     *    (Base-1 representations are not allowed.)
     */
    public static DecimalBigInt valueOf(int[] digits, int radix) {
        if(radix < 2) {
            throw new IllegalArgumentException("illegal radix: " + radix);
        }
        DecimalBigInt bigRadix = valueOf(radix);
        DecimalBigInt value = ZERO;
        for(int digit : digits) {
            if(digit < 0 || radix <= digit) {
                throw new IllegalArgumentException("digit " + digit +
                                                   " out of range");
            }
            DecimalBigInt bigDigit = DecimalBigInt.valueOf(digit);
            value = value.times(bigRadix).plus(bigDigit);
        }
        return value;
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
     *
     * Each 1000 factors we print the current digit count
     * (in our internal radix) to the standard error stream.
     * @param n should be < BASE (but you supposedly don't want to
     *  use bigger number anyway, as it takes years).
     */
    public static DecimalBigInt faculty(int n) {
        DecimalBigInt fac = DecimalBigInt.ONE;
        for(int i = 2; i <= n; i++) {
            fac = fac.times(new DecimalBigInt(i));
            if(i % 1000 == 0) {
                System.err.println("log_BASE(fac("+i+")) = " +
                                   fac.digits.length);
            }
        }
        return fac;
    }


    /**
     * a test method, which demonstrates the usage.
     */
    public static void main(String[] params) {
        // test of constructor + toString
        DecimalBigInt d = new DecimalBigInt(0,0, 7, 5, 2, 12345);
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

        // test of valueOf
        DecimalBigInt d3 = DecimalBigInt.valueOf("12345678901234567890", 10);
        System.out.println("d3: " + d3); // should be the same as d2
        DecimalBigInt d4 = DecimalBigInt.valueOf("123456789A0123456789A0", 11);
        System.out.println("d4: " + d4);
        DecimalBigInt d5 = DecimalBigInt.valueOf("123456012345601234560", 7);
        System.out.println("d5: " + d5);

        DecimalBigInt d6 = DecimalBigInt.valueOf(Long.MAX_VALUE);
        System.out.println("d6: " + d6);

        DecimalBigInt d7 = DecimalBigInt.valueOf(new int[]{3, 5, 7}, 100);
        System.out.println("d7: " + d7);


        // test of compareTo
        System.out.println("d2 <=> d: " + d2.compareTo(d));
        System.out.println("d2 <=> d3: " + d2.compareTo(d3));
        System.out.println("d <=> d2: " + d.compareTo(d2));
        System.out.println("sum <=> d: " + sum.compareTo(d));
        System.out.println("prod <=> d: " + prod.compareTo(d));
        System.out.println("d <=> prod: " + d.compareTo(prod));

        //// The result should need 3999999 decimal digits. This
        //// is the biggest faculty which Emacs calc can calculate
        //// (in floating point mode):
        //        DecimalBigInt fac = DecimalBigInt.faculty(736275);
        // System.out.println("fac(736275) = " + fac.toDecimalString());
    }


}