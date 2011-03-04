package de.fencing_game.paul.examples;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import javax.swing.*;

/**
 * An adapter class for calling an (almost) arbitrary
 * method as an ActionListener.
 *<p>
 * In fact, you can use any public method which has either no parameter
 * or any parameter of type {@link ActionEvent} or some superclass
 * of it (then it gets passed the ActionEvent there).
 *</p>
 * <p>
 * Additionally, the adapter needs an object of a class containing
 * this method as the receiver.
 * </p>
 *<p>
 * Inspired by the question <a href="http://stackoverflow.com/questions/5182558/java-vs-c-addactionlistener-vs-event-subscription">Java vs C# - AddActionListener vs event subscription</a> on Stackoverflow.
 *</p>
 * @author Paŭlo Ebermann
 */
public class MethodAdapter implements ActionListener {

    /** the receiver object.  */
    private Object target;
    /** the method to be invoked. */
    private Method method;
    /** true if the method accepts the ActionEvent argument,
        false if it is a no-arg method. */
    private boolean takesArgument;

    /**
     * creates a new MethodAdapter.
     * @param o the receiver object.
     * @param mName the name of a method on the receiver.
     *     If there are multiple same-named methods in the class
     *     of the receiver, we take the first one of these: <ul>
     *  <li> a public method taking an ActionEvent as parameter</li>
     *  <li> a public method taking an AWTEvent as parameter</li>
     *  <li> a public method taking an EventObject as parameter</li>
     *  <li> a public method taking an Object as parameter</li>
     *  <li> a public method taking no parameter.</li>
     * </ul>
     * @throws IllegalArgumentException if there is no such method.
     */
    public MethodAdapter(Object o, String mName) {
        this(o, findMethod(o, mName));
    }

    /**
     * creates a new MethodAdapter.
     * @param o the receiver object.
     * @param m the method to be invoked.
     *   This method has to be declared in the class of the receiver object
     *   or some supertype, has to take no or one argument, and if one, then
     *   of some supertype of {@link ActionEvent}.
     * @throws IllegalArgumentException if the method does not fit the
     *   receiver, if the method takes too much arguments or arguments of
     *   wrong types.
     */
    public MethodAdapter(Object o, Method m) {
        Class<?>[] params = m.getParameterTypes();
        if(!m.getDeclaringClass().isInstance(o)) {
            throw new IllegalArgumentException("wrong target object");
        }
        if(params.length > 1) {
            throw new IllegalArgumentException("too many arguments");
        }
        if(params.length == 1 &&
           ! params[0].isAssignableFrom(ActionEvent.class)) {
            throw new IllegalArgumentException("method not compatible: " + m);
        }
        this.target = o;
        this.method = m;
        this.takesArgument = params.length > 0;
    }

    private static Method findMethod(Object o, String mName) {
        Class<?> c = o.getClass();
        Class<?> eventClass = ActionEvent.class;
        while(eventClass != null) {
            try {
                return c.getMethod(mName, ActionEvent.class);
            }
            catch(NoSuchMethodException ex) {}
            eventClass = eventClass.getSuperclass();
        }
        try {
            // try a no-argument method
            return c.getMethod(mName);
        }
        catch(NoSuchMethodException ex) {
            throw new IllegalArgumentException("No fitting method named '" +
                                               mName +"' on this object " + o +
                                               " of class " + c.getName());
        }
    }

    /**
     * the implementation of the actionPerformed method.
     * We delegate to our target object and method.
     * Any return value of
     * the method is silently ignored.
     * @throws RuntimeException if any exception is thrown by the invoked
     *   method (or during the invoke process), it is wrapped in a
     *   RuntimeException and rethrown. 
     */
    public void actionPerformed(ActionEvent event) {
        try {
            if(takesArgument) {
                method.invoke(target, event);
            }
            else {
                method.invoke(target);
            }
        }
        catch(Exception e) {
            if(e instanceof InvocationTargetException) {
                Throwable t = e.getCause();
                if(t instanceof Error)
                    throw (Error)t;
                e = (Exception)t;
            }
            if(e instanceof RuntimeException)
                throw (RuntimeException) e;
            throw new RuntimeException(e);
        }
    }

    /**
     * main method for testing purposes.
     */
    public static void main(String[] params) {
        JFrame f = new JFrame("Test");
        JButton b = new JButton("close");
        b.addActionListener(new MethodAdapter(f, "dispose"));
        f.getContentPane().add(b);
        f.setVisible(true);
    }

}