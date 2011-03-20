package de.fencing_game.paul.examples.accessible;

import java.lang.reflect.Field;

/**
 * The data class which we want to access by reflection.
 */
public class DataClass implements Accessor {

    private int value;
    private Field field;

    public DataClass(int val) {
	this.value = val;
	try {
	    this.field = DataClass.class.getDeclaredField("value");
	    System.err.println("field: " + field);
	    System.err.println("field.hash: " + field.hashCode());
	    System.err.println("field.accessible: " + field.isAccessible());
	}
	catch(Exception ex) {
	    throw new RuntimeException(ex);
	}
    }

    public String toString() {
	return "DataClass[" + value + "]";
    }


    public Field getField() {
	return field;
    }

    public void printObject(DataClass data) {
	try {
	    System.out.println("accessed: " + field.getInt(data));
	}
	catch(Exception ex) {
	    ex.printStackTrace();
	}

    }
    

}