package de.fencing_game.paul.examples;

import java.rmi.registry.*;

public class EchoClient {

   public static void main(String[] egal)
        throws Exception
    {
        //        TracingSocketFactory fac = new TracingSocketFactory();

        Registry registry =
            LocateRegistry.getRegistry("localhost",
                                       Registry.REGISTRY_PORT);
        
        EchoServer es = (EchoServer)registry.lookup("echo");
        System.err.println("es: " + es);
        System.out.println(es.echo("hallo"));

    }

}