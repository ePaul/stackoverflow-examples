package de.fencing_game.paul.examples.rmi;

import javax.rmi.ssl.*;
import java.rmi.server.*;
import java.rmi.registry.*;
import java.rmi.Remote;

import javax.net.ssl.*;

public class CompressedSSLForRMI {

    /**
     * main-method for testing and example purposes.
     *
     * For this to work, you need to set these System properties
     * on the server side:
     *<ul>
     *  <li>javax.net.ssl.keyStore  - the location of the keystore</li>
     *  <li>javax.net.ssl.keyStorePassword - the password for the keystore.</li>
     *</ul>
     *
     * The client needs these properties:
     * <ul>
     *   <li>javax.net.ssl.trustStore</li>
     *   <li>javax.net.ssl.trustStorePassword</li>
     * </ul>
     *
     */
    public static void main(String[] ignored)
        throws Exception
    {

	CompressedRMISocketFactory fac =
	    new CompressedRMISocketFactory(new SslRMIClientSocketFactory(),
					   new SslRMIServerSocketFactory());

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