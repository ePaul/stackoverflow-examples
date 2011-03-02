package de.fencing_game.paul.examples;

import java.net.*;
import java.io.*;


/**
 * Class to test a possible memory leak when using an ObjectOutputStream.
 *
 *<p>
 * This class is adapted from code in <a href="http://stackoverflow.com/questions/5122569/why-is-java-constantly-eating-more-memory">Why is Java constantly eating more memory?</a>, a question by "Apbple" on Stackoverflow.
 * </p>
 * <p><a href="http://stackoverflow.com/questions/5122569/why-is-java-constantly-eating-more-memory/5123240#5123240">My answer there</a> 
 * contains a simpler version of this class. This version here is a
 * unification of all three versions whose heap-size-trace screenshots I
 * posted and commented. It allows selecting the right client behaviour
 * by {@linkplain #main command line parameter}.
 * </p>
 * @author Paŭlo Ebermann (adapted from code from Apbple).
 */
public class SerializationMemoryLeak {

    /**
     * Runs a client, which connects to localhost on port 1234, creates an
     * {@link ObjectOutputStream} and sends lots of Strings there in
     * an endless loop. After each sent String we wait 100 ms and print a ','
     * on System.out.
     * @param differentObjects if true, send a different String each time.
     *    If false, we send the literal string {@code "Hello ..."} each time.
     * @param useReset if true, we call
     *  {@link ObjectOutputStream#reset() out.reset()} after each 1000 sent
     *  objects. If false, we never call this method.
     */
    public static void runClient(boolean differentObjects,
                                  boolean useReset)
        throws IOException, InterruptedException
    {
	Socket socket = null;
	ObjectOutputStream out = null;
        int i = 0;
        while (true) {
            if (socket != null) {
                Object data = 
		    differentObjects ?
		    "Hello " + i + " ..." :
		    "Hello ...";

                out.writeObject(data);
                System.out.print(",");
                Thread.sleep(100);
                if(useReset && i % 1000 == 0) {
                    out.reset();
                }
            } else {
                socket = new Socket("localhost", 1234);
                out = new ObjectOutputStream(socket.getOutputStream());
                System.out.println("connected to server");
            }
            i++;
        }
    }

    /**
     * runs the server. We listen on port 1234 and accept the first
     * connection. Then we simply read and discard data from this stream
     * until the other side closes the connection.
     */
    public static void runServer() throws IOException {
        ServerSocket ss = new ServerSocket(1234);
        Socket s = ss.accept();
        InputStream in = s.getInputStream();
        byte[] buffer = new byte[500];
        while(in.read(buffer) > 0) {
            System.out.print(".");
        }
    }


    /**
     * Entry point for the program.
     * @param args command line arguments.
     *   If none, starts the server.
     *  Otherwise, args[0] can be:
     *   - "same" - starts a client sending the same string again and again.
     *   - "different" - starts a client sending different strings.
     *   - "reset" - starts a client sending different strings, but invoking
     *     stream.reset() every 1000 strings.
     */
    public static void main(String[] args) 
        throws IOException, InterruptedException
    {
        if(args.length < 1) {
            runServer();
        }
        else if (args[0].equals("same")) {
            runClient(false, false);
        }
        else if (args[0].equals("different")) {
            runClient(true, false);
        }
        else if (args[0].equals("reset")) {
            runClient(true, true);
        }
    }

}