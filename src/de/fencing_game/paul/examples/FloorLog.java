package de.fencing_game.paul.examples;

/**
 * An algorithm to calculate the floor of the logarithm.
 *
 * From the question <a href="http://stackoverflow.com/questions/5212679/calculate-log-of-a-number-in-java-using-recursion">Calculate log of a number in java using recursion</a> by user647207 on Stackoverflow. 
 *
 * I (Paŭlo Ebermann) removed one superfluous if case and added a test method.
 */
public class FloorLog {

    /**
     * finds the biggest number x so that   base^x <= value.
     * @param base the base of the logarithm, should be > 1.
     * @param value the value to take the logarithm from, should be > 0
     */
    public static int floorLog(int base, int value)
    {
        assert base > 1;
        assert value > 0;
        if (base == value)
            {
                return 1;
            }
        if (value < base) 
            {
                return 0;
            }
        value = value / base;
        return 1 + floorLog(base, value);
    }


    /**
     * test method.
     */
    public static void main(String[] ignored) {
	final int MAX_BASE = 10;
	final int MAX_VALUE = 30;
	System.out.print("log |");
	for(int v = 1; v < MAX_VALUE; v++) {
	    System.out.printf("%3d", v);
	}
	System.out.println();
	System.out.print("----+");
	for(int v = 1; v < MAX_VALUE; v++) {
	    System.out.print("---");
	}
	System.out.println();
        for(int b = 2; b < MAX_BASE; b++) {
	    System.out.printf("%3d |", b);
	    for(int v = 1; v < MAX_VALUE; v++) {
		System.out.printf("%3d", floorLog(b, v));
	    }
	    System.out.println();
        }
    }
}