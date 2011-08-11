package de.fencing_game.paul.examples;

import java.net.*;
import java.io.*;


/**
 * Inspired by http://stackoverflow.com/questions/6599797/tcp-round-trip-time-calculation-using-java (I suppose).
 */
public class SocketTimer {

    static final int RECEIVER_PORT = 15678;


    public void timeClient(String address) throws IOException {
        int count = 10000;
        Socket sendingSocket = new Socket(address, RECEIVER_PORT);
        sendingSocket.setTcpNoDelay(true);
        OutputStream outputStream = sendingSocket.getOutputStream();
        InputStream inputStream = sendingSocket.getInputStream();
        byte[] msg =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
            .getBytes("US-ASCII");
        long[] times = new long[count];
        System.out.println("timing ...");
        for (int i = 0; i < count; i++) {
            long start = System.nanoTime();
            outputStream.write(msg,0,64);
            outputStream.flush();

            inputStream.read(msg,0,64);
            long end = System.nanoTime(); 
            times[i] = end - start;
        }

        System.out.println("times[0000]: " + times[0]);
        System.out.println("times[3000]: " + times[3000]);
        System.out.println("times[5000]: " + times[5000]);
        System.out.println("times[9000]: " + times[9000]);

        sendingSocket.close();
    }


    public void timeServer() throws IOException {
        ServerSocket ss = new ServerSocket(RECEIVER_PORT);
        Socket receivingSocket = ss.accept();
        receivingSocket.setTcpNoDelay(true);
        byte[] data = new byte[64];
        OutputStream outputStream = receivingSocket.getOutputStream();
        InputStream inputStream = receivingSocket.getInputStream();
        

        while(true) {
            int len = inputStream.read(data, 0, 64);
            assert(len == 64);
            outputStream.write(data, 0, 64);
            outputStream.flush();
        }
    }


    public static void main(String[] address)
        throws IOException 
    {
        SocketTimer st = new SocketTimer();
        if(address.length > 0) {
            st.timeClient(address[0]);
        }
        else {
            st.timeServer();
        }
    }


}