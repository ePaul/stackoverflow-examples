package de.fencing_game.paul.examples;

import java.rmi.*;

public interface EchoServer extends Remote {

    public String echo(String bla) throws RemoteException;

}