package de.fencing_game.paul.examples.rmi;

/**
 * An example RMI server used for testing purposes.
 */
public class EchoServerImpl
    implements EchoServer
{
    public String echo(String param) {
	return param + " " + param;
    }
}
