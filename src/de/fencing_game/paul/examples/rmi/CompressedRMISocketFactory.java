package de.fencing_game.paul.examples.rmi;

import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;
import java.io.*;

import de.fencing_game.tools.*;


public class CompressedRMISocketFactory extends WrappingSocketFactory {


    protected StreamPair wrap(InputStream in, OutputStream out,
                              boolean server) {
        /*
        return new StreamPair(new DecompressingInputStream(in),
                              new CompressingOutputStream(out));
        */
        return new StreamPair(in, out);
    }


    public static void main(String[] egal)
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