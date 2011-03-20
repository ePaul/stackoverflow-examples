package de.fencing_game.paul.examples.accessible;

import java.lang.reflect.Field;

/**
 * an accessor which is not allowed to access the field.
 */
public class DisallowedAccessor implements Accessor
{

    private Field field;

    public DisallowedAccessor() {
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

    public DisallowedAccessor(Field f) {
	this.field = f;
    }


    public Field getField() {
	return field;
    }

    public void printObject(DataClass data) {
 	if(!field.isAccessible()) {
 	    field.setAccessible(true);
 	}
	try {
	    System.out.println("accessed: " + field.getInt(data));
	}
	catch(Exception ex) {
	    ex.printStackTrace();
	}
	// forgot to reset to non-accessible
    }

}