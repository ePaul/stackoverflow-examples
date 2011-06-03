package de.fencing_game.paul.examples;

import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;
import java.io.*;

public class TracingSocketFactory extends WrappingSocketFactory {

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


    private static class EchoServerImpl
        implements EchoServer {

        public String echo(String param) {
            return param + " " + param;
        }
    }

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

        //        EchoServer es = (EchoServer)registry.lookup("echo");
        //        System.out.println(es.echo("hallo"));

        Thread.sleep(3*60*1000);
    }


}