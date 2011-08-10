package de.fencing_game.paul.examples;

import java.io.PrintStream;
import java.util.Map;
import java.lang.reflect.*;


/**
 * Different implementations of "does this enum type contain a constant
 * with this name?".
 *
 * Inspired by the question <a href="http://stackoverflow.com/q/6850638/600500">Checking if enum type contains constant with given name</a> from
 * BugsPray on Stack Overflow.
 *
 * @author Paŭlo Ebermann (one answer from jjnguy).
 */
public class EnumUtils {


    /**
     * Implementation using {@link Class#getEnumConstants} and iteration
     *  through this array.
     * @author <a href="http://stackoverflow.com/users/2598/jjnguy">jjnguy</a>.
     */
    public static boolean enumTypeContainsA(Class<? extends Enum<?>> clazz,
                                            String val) {
        Object[] arr = clazz.getEnumConstants();
        for (Object e : arr) {
            if (((Enum) e).name().equals(val)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Implementation using {@link Enum#valueOf}, and catching
     * the IllegalArgumentException.
     */
    public static <E extends Enum<E>> boolean enumTypeContainsE(Class<E> e,
                                                                String s) {
        try {
            Enum.valueOf(e, s);
            return true;
        }
        catch(IllegalArgumentException ex) {
            return false;
        }
    }

    /**
     * Implementation using reflection to directly access the
     * map of enum constants.
     */
    public static boolean enumTypeContainsR(Class<? extends Enum<?>> e,
                                            String s) {
        try {
            Method enumDir =
                Class.class.getDeclaredMethod("enumConstantDirectory");
            enumDir.setAccessible(true);
            Map<?,?> dir = (Map<?,?>)enumDir.invoke(e);
            return dir.containsKey(s);
        }
        catch(NoSuchMethodException ex) {
            throw new Error(ex);
        }
        catch(IllegalAccessException ex) {
            throw new Error(ex);
        }
        catch(InvocationTargetException ex) {
            throw new Error(ex.getCause());
        }
    }

    /**
     * simple test method.
     */
    public static void main(String[] ignored) {
        PrintStream out = System.out;

        out.println(System.getProperty("java.version"));

        out.println(EnumUtils.enumTypeContainsA(Thread.State.class, "NEW"));
        out.println(EnumUtils.enumTypeContainsA(Thread.State.class, "OLD"));

        out.println(EnumUtils.enumTypeContainsR(Thread.State.class, "NEW"));
        out.println(EnumUtils.enumTypeContainsR(Thread.State.class, "OLD"));

        out.println(EnumUtils.enumTypeContainsE(Thread.State.class, "NEW"));
        out.println(EnumUtils.enumTypeContainsE(Thread.State.class, "OLD"));
    }

}