package de.fencing_game.paul.examples.accessible;

import java.lang.reflect.Field;

public interface Accessor {


    public void printObject(DataClass value);


    public Field getField();

}