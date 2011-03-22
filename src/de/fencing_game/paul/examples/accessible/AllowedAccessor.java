package de.fencing_game.paul.examples.accessible;

import java.lang.reflect.Field;

/**
 * an accessor which is allowed to access the field.
 */
public class AllowedAccessor implements Accessor
{


    private Field field;

    public AllowedAccessor() {
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

    public Field getField() {
	return field;
    }

    public void printObject(DataClass data) {
	if(!field.isAccessible()) {
	    field.setAccessible(true);
	    System.err.println("field.accessible: " + field.isAccessible());
	}
	try {
	    System.out.println("accessed: " + field.getInt(data));
	}
	catch(Exception ex) {
	    ex.printStackTrace();
	}
	// forgot to reset to non-accessible
	System.err.println("field.accessible: " + field.isAccessible());
    }

}