package de.fencing_game.paul.examples;

import java.io.*;
import java.net.*;

/**
 * From http://stackoverflow.com/questions/5370010/java-udp-packet-send-problem/, with a bit of straight-ahead wrapper code.
 */
public class UDPTest {

    private static class Gamer {
        int clientID;
        float x;
        float y;
        InetAddress address;

        public int getClientID() { return clientID; }
        public float getX() { return x; }
        public float getY() { return y; }
        public InetAddress getInetAddress() { return address; }
    }


    private DatagramSocket socket;


    public UDPTest() throws IOException
    {
        this.socket = new DatagramSocket();
    }


    public void sendPackage(Gamer gamerToSend)
        throws IOException
    {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(byteOut);
        
        // Game type
        out.writeInt(1);
        
        // Client
        out.writeInt(gamerToSend.getClientID());
        
        // Position
        out.writeFloat(gamerToSend.getX());
        out.writeFloat(gamerToSend.getY());
        
        DatagramPacket packet = new DatagramPacket
            (
                byteOut.toByteArray(),
                byteOut.size(),
                gamerToSend.getInetAddress(),
                3887
            );
        
        this.socket.send(packet);
        
        byteOut.close();
        out.close();
    }


    public static void main(String[] ignored)
        throws IOException
    {
        Gamer g = new Gamer();
        g.address = InetAddress.getByName("localhost");
        
        UDPTest sender = new UDPTest();
        sender.sendPackage(g);
    }

}