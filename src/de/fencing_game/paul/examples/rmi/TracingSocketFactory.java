package de.fencing_game.paul.examples.rmi;

import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;
import java.io.*;

/**
 * An RMI socket factory used for tracing purposes.
 * Its output streams print a note for every write and flush.
 *
 * This was mainly used to see whether there are enough {@code flush}s.
 */
public class TracingSocketFactory
    extends WrappingSocketFactory
{

    private static final long serialVersionUID = 1;

    // ------------- implementation ------------

    /**
     * Wraps the output stream in a debugging variant which also
     * prints information about every method before forwarding it
     * to the relevant method of the destination stream.
     *
     * The input stream does nothing special.
     */
    protected StreamPair wrap(InputStream in, OutputStream out, boolean server)
    {
        InputStream wrappedIn = in;
        OutputStream wrappedOut = new FilterOutputStream(out) {
                public void write(int b) throws IOException {
                    System.err.println("write(.)");
                    super.write(b);
                }
                public void write(byte[] b, int off, int len)
                    throws IOException {
                    System.err.println("write(" + len + ")");
                    super.out.write(b, off, len);
                }
                public void flush() throws IOException {
                    System.err.println("flush()");
                    super.flush();
                }
            };
        return new StreamPair(wrappedIn, wrappedOut);
    }

    /**
     * main-method for testing and example purposes.
     */
    public static void main(String[] egal)
        throws Exception
    {
        TracingSocketFactory fac = new TracingSocketFactory();
        Remote server =
            UnicastRemoteObject.exportObject(new EchoServerImpl(),
                                             0, fac, fac);
        System.err.println("server: " + server);

        Registry registry =
            LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
        
        registry.bind("echo", server);

        Thread.sleep(3*60*1000);
    }
}
