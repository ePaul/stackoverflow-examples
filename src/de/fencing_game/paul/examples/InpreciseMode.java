package de.fencing_game.paul.examples;

import java.util.Arrays;

/**
 * searching of dense points in a distribution.
 *
 * Inspired by http://stackoverflow.com/questions/5329628/finding-a-mode-with-decreasing-precision.
 */
public class InpreciseMode {

    /** our input data, should be sorted ascending. */
    private double[] data;

    public InpreciseMode(double ... data) {
        this.data = data;
    }
    

    /**
     * searchs the smallest neighbourhood (by diameter) which
     * contains at least minSize elements.
     *
     * @return an array of two arrays:
     *     {   { the middle point of the neighborhood,
     *           the diameter of the neighborhood  },
     *        all the elements of the neigborhood }
     *
     * TODO: better return an object of a class encapsuling these.
     */
    public double[][] findSmallNeighbourhood(int minSize) {
        int currentLeft = -1;
        int currentRight = -1;
        double currentMinDiameter = Double.POSITIVE_INFINITY;

        for(int i = 0; i + minSize-1 < data.length; i++) {
            double diameter = data[i+minSize-1] - data[i];
            if(diameter < currentMinDiameter) {
                currentMinDiameter = diameter;
                currentLeft = i;
                currentRight = i + minSize-1;
            }
        }
        return
            new double[][] {
            { 
                (data[currentRight] + data[currentLeft])/2.0,
                currentMinDiameter
            },
            Arrays.copyOfRange(data, currentLeft, currentRight+1)
        };
    }

    public void printSmallNeighbourhoods() {
        for(int frequency = 2; frequency <= data.length; frequency++) {
            double[][] found = findSmallNeighbourhood(frequency);
            
            System.out.printf("There are %d elements in %f radius "+
                              "around %f:%n     %s.%n",
                              frequency, found[0][1]/2, found[0][0],
                              Arrays.toString(found[1]));
        }
    }

    
    public static void main(String[] params) {
        InpreciseMode m =
            new InpreciseMode(1.12, 1.13, 1.15, 2.0, 3.4, 3.44, 4.1,
                              4.2, 4.3, 4.4);
        m.printSmallNeighbourhoods();
    }

}
