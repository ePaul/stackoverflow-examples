package de.fencing_game.paul.examples;

import java.nio.*;
import java.util.*;
import java.security.*;

/**
 * Implementations of java.util.Random based on Mersenne Twister and
 * a hash algorithm (in "counter mode").
 */
public class RandomTest {

    public static void main(String[] params)
        throws NoSuchAlgorithmException
    {

        Random r = new MersenneTwister(17);
        System.out.println("testing Mersenne Twister ...");
        testGenerator(r);

        r = new HashRandom("SHA-1", "Test".getBytes());
        System.out.println("testing SHA-1 ...");
        testGenerator(r);

    }

    private static void testGenerator(Random r) {
        List<Integer> l = new ArrayList<Integer>(); 
        for(int i = 0; i < 50; i++) {
            l.add(r.nextInt(100));
        }
        System.out.println(l);
    }


    public static class HashRandom extends Random {
        private byte[] currentData;
        private MessageDigest hash;
        private long counter;
        private ByteBuffer messageBuffer;
        private int nextIndex;

        private static final long serialVersionUID = 1;
        
        public HashRandom(String algName, byte[] seed) 
            throws NoSuchAlgorithmException
        {
            hash = MessageDigest.getInstance(algName);
            messageBuffer = ByteBuffer.allocate(seed.length + 8);
            messageBuffer.mark();
            messageBuffer.putLong(0);
            messageBuffer.put(seed);
            generate();
        }

        private void generate() {
            messageBuffer.reset();
            messageBuffer.putLong(0, counter);
            hash.update(messageBuffer);
            currentData = hash.digest();
            nextIndex = 0;
            counter ++;
            //            System.out.println("currentData: " + Arrays.toString(currentData));
        }

        protected int next(int orgbits) {
            int bits = orgbits;
            //System.err.println(bits);
            int result = 0;
            while(bits > 0) {
                if(nextIndex >= currentData.length) {
                    generate();
                }
                result = result << 8;
                result |= currentData[nextIndex];
                nextIndex++;
                bits = bits - 8;
            }
            return result & ((1<<orgbits) - 1);
        }
    }



    /**
     * Implementation aus http://de.wikipedia.org/wiki/Mersenne-Twister.
     */
    public static class MersenneTwister extends Random {

        private static final long serialVersionUID = 1;


        private int[] y;

        private static final int N = 634;
        private static final int M = 397;
        private static final int HI = 0x80000000;
        private static final int LO = 0x7fffffff;
        private static final int[] A = {0, 0x9908b0df };

        private int nextIndex;

        public MersenneTwister() {
            this((int)(System.currentTimeMillis()/50));
        }


        public MersenneTwister(int seed) {
            y = new int[N];
            y[0] = seed;
            for(int i = 1; i < N; i++) {
                y[i] = (1812433253 * (y[i-1] ^ (y[i-1] >> 30)) + i);
            }
            for(int i = 0; i < 50; i++) {
                generate();
            }
        }

        private void generate() {
            for (int i=0; i<N-M; i++) {
                int h = (y[i] & HI) | (y[i+1] & LO);
                y[i] = y[i+M] ^ (h / 2) ^ A[h & 1];
            }
            for (int i = N-M ; i<N-1; i++) {
                int h = (y[i] & HI) | (y[i+1] & LO);
                y[i] = y[i+(M-N)] ^ (h / 2) ^ A[h & 1];
            }
 
            int h = (y[N-1] & HI) | (y[0] & LO);
            y[N-1] = y[M-1] ^ (h / 2) ^ A[h & 1];

            nextIndex = 0;
        }


        protected int next(int bits) {
            //System.err.println(bits);
            if(nextIndex == N) {
                generate();
            }
            return y[nextIndex++] >>> (32 - bits);
        }

        
    }



}