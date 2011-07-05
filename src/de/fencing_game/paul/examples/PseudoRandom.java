package de.fencing_game.paul.examples;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;


/**
 * A pseudo random number generator, which does not
 * produce a series of numbers, but each number determined by
 * some input (and independent of earlier numbers).
 *<p>
 * This is based on the
 * <a href="http://en.wikipedia.org/wiki/Blum_Blum_Shub">Blum Blum Shub
 *  algorithm</a>, combined with the SHA-1 message digest to get the
 *  right index.
 *</p>
 *<p>
 * Inspired by the question
 *  <a href="http://stackoverflow.com/q/6586042/600500">Algorithm
 *   for generating a three dimensional random number space</a> on
 * Stack Overflow, and the answer from woliveirajr.
 */
public class PseudoRandom {

    /**
     * An instance of this class represents a range of
     * integer numbers, both endpoints inclusive.
     */
    public static final class Range {

        public int min;
        public int max;

        public Range(int min, int max) {
            this.min = min;
            this.max = max;
        }

        /**
         * clips a (positive) BigInteger to the range represented
         * by this object.
         * @returns an integer between min and max, inclusive.
         */
        final int clip(BigInteger bigVal) {
            BigInteger modulus =
                BigInteger.valueOf(max + 1L - min);
            return (int)(min + bigVal.mod(modulus).longValue());
        }
    }


    /* M = p * q =
       510458987753305598818664158496165644577818051165198667838943583049282929852810917684801057127 * 1776854827630587786961501611493551956300146782768206322414884019587349631246969724030273647 */
    /**
     * A big number, composed of two large primes.
     */
    private static final BigInteger M =
        new BigInteger("90701151669688414188903413878244126959941449657"+
                       "82009133495922185615411523457607691918744187485"+
                       "10492533485214517262505932675573506751182663319"+
                       "285975046876611245165890299147416689632169");

    /* λ(M) = lcm(p-1, q-1) */
    /**
     * The value of λ(M), where λ is the Carmichael function.
     * This is the lowest common multiple of the predecessors of
     * the two factors of M.
     */
    private static final BigInteger lambdaM =
        new BigInteger("53505758348442070944517069391220634799707248289"+
                       "10045667479610928077057617288038459593720911813"+
                       "73249762745139558184229125081884863164923576762"+
                       "05906844204771187443203120630003929150698");

    /**
     * The number 2 as a BigInteger, for use in the calculations.
     */
    private static final BigInteger TWO = BigInteger.valueOf(2);



    /**
     * the modular square of the seed value.
     */
    private BigInteger s_0;

    /**
     * The MessageDigest used to convert input data
     * to an index for our PRNG.
     */
    private MessageDigest md;



    /**
     * Creates a new PseudoRandom instance, using the given seed.
     */
    public PseudoRandom(BigInteger seed) {
        try {
            this.md = MessageDigest.getInstance("SHA-1");
        }
        catch(NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }
        initializeSeed(seed);
    }

    /**
     * Creates a new PseudoRandom instance, seeded by the given seed.
     */
    public PseudoRandom(byte[] seed) {
        this(new BigInteger(1, seed));
    }

    /**
     * Creates a new PseudoRandom instance,
     * seeded by the current system time.
     */
    public PseudoRandom() {
        this(BigInteger.valueOf(System.currentTimeMillis()));
    }

    /**
     * Transforms the initial seed into some value that is
     * usable by the generator. (This is completely deterministic.)
     */
    private void initializeSeed(BigInteger proposal) {

        // we want our seed be big enough so s^2 > M.
        BigInteger s = proposal;
        while(s.bitLength() <= M.bitLength()/2) {
            s = s.shiftLeft(10);
        }
        // we want gcd(s, M) = 1
        while(!M.gcd(s).equals(BigInteger.ONE)) {
            s = s.add(BigInteger.ONE);
        }
        // we save s_0 = s^2 mod M
        this.s_0 = s.multiply(s).mod(M);
    }
    
    /**
     * calculates {@code x_k = r.clip( s_k )}.
     */
    private int calculate(Range r, BigInteger k) {
        BigInteger exp = TWO.modPow(k, lambdaM);
        BigInteger s_k = s_0.modPow(exp, M);
        return r.clip(s_k);
    }


    /**
     * returns a number given by a range, determined by the given input.
     */
    public int getNumber(Range r, byte[] input) {
        byte[] dig;
        synchronized(md) {
            md.reset();
            md.update(input);
            dig =  md.digest();
        }
        return calculate(r, new BigInteger(1, dig));
    }


    /**
     * returns a number given by a range, determined by the given input.
     */
    public int getNumber(Range r, int... input) {
        byte[] dig;
        synchronized(md) {
            md.reset();
            for(int i : input) {
                md.update(new byte[]{ (byte)(i >> 24), (byte)(i >> 16),
                                      (byte)(i >> 8), (byte)(i >> 0)} );
            }
            dig = md.digest();
        }
        return calculate(r, new BigInteger(1, dig));
    }

    /**
     * Test method.
     */
    public static void main(String[] test) {
        PseudoRandom pr = new PseudoRandom("Hallo Welt".getBytes());
        
        Range r = new Range(10, 30);
        for(int i = 0; i < 10; i++) {
            System.out.println("x("+i+") = " + pr.getNumber(r, i));
        }
        for(int i = 0; i < 5; i++) {
            for(int j = 0; j < 5; j++) {
                System.out.println("x("+i+", "+j+") = " +
                                   pr.getNumber(r, i, j));
            }
        }
        // to show that it really is deterministic:
        for(int i = 0; i < 10; i++) {
            System.out.println("x("+i+") = " + pr.getNumber(r, i));
        }
    }
}
