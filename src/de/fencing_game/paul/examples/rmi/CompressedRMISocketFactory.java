package de.fencing_game.paul.examples.rmi;

import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;
import java.io.*;

import de.fencing_game.tools.*;

/**
 * An RMISocketFactory which enables compressed transmission.
 * We use {@link #CompressingInputStream} and {@link #CompressingOutputStream}
 * for this.
 *
 * As we extend WrappingSocketFactory, this can be used on top of another
 * {@link RMISocketFactory}.
 */
public class CompressedRMISocketFactory
    extends WrappingSocketFactory
{

    private static final long serialVersionUID = 1;

    //------------ Constructors -----------------

    /**
     * Creates a CompressedRMISocketFactory based on a pair of
     * socket factories.
     *
     * @param cFac the base socket factory used for creating client
     *   sockets. This may be {@code null}, then we will use the
     *  {@linkplain RMISocketFactory#getDefault() default socket factory}
     *  of client system where this object is finally used for
     *   creating sockets.
     *   If not null, it should be serializable.
     * @param sFac the base socket factory used for creating server
     *   sockets. This may be {@code null}, then we will use the
     *  {@linkplain RMISocketFactory#getDefault() default RMI Socket factory}.
     *  This will not be serialized to the client.
     */
    public CompressedRMISocketFactory(RMIClientSocketFactory cFac,
                                      RMIServerSocketFactory sFac) {
        super(cFac, sFac);
    }


    /**
     * Creates a CompressedRMISocketFactory based on a socket factory.
     *
     * This constructor is equivalent to
     * {@code CompressedRMISocketFactory(fac, fac)}.
     *
     * @param fac the factory to be used as a base for both client and
     *   server socket. This should be either serializable or {@code null}
     *   (then we will use the
     * {@linkplain RMISocketFactory#getDefault() default socket factory}
     *   as a base).
     */
    public CompressedRMISocketFactory(RMISocketFactory fac) {
        super(fac);
    }

    /**
     * Creates a CompressedRMISocketFactory based on the
     * {@link RMISocketFactory#getSocketFactory global socket factory}.
     *
     * This uses the global socket factory at the time of the constructor
     * call. If this is {@code null}, we will use the
     * {@linkplain RMISocketFactory#getDefault() default socket factory}
     * instead.
     */
    public CompressedRMISocketFactory() {
        super();
    }

    //-------------- Implementation -------------

    /**
     * wraps a pair of streams into compressing/decompressing streams.
     */
    protected StreamPair wrap(InputStream in, OutputStream out,
                              boolean server)
    {
        return new StreamPair(new DecompressingInputStream(in),
                              new CompressingOutputStream(out));
    }

    // -------------- testing/example ----------

    /**
     * main-method for testing and example purposes.
     */
    public static void main(String[] ignored)
        throws Exception
    {
        CompressedRMISocketFactory fac = new CompressedRMISocketFactory();

        Remote server =
            UnicastRemoteObject.exportObject(new EchoServerImpl(),
                                             0, fac, fac);
        System.err.println("server: " + server);

        Registry registry =
            LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
        
        registry.bind("echo", server);

        //        EchoServer es = (EchoServer)registry.lookup("echo");
        //        System.out.println(es.echo("hallo"));

        Thread.sleep(3*60*1000);
    }
}
