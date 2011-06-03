package de.fencing_game.tools;


import java.util.zip.*;
import java.io.*;

/**
 * Workaround für kaputten GZipOutputStream, von
 * http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4206909
 * (23-JUN-2002, rsaddey)
 * @see CompressingOutputStream
 */
public class DecompressingInputStream extends 
                                          InflaterInputStream {

    public DecompressingInputStream (final InputStream in) {
        // Using Inflater with nowrap == true will ommit headers and trailers
        super(in, new Inflater(true));
    }

    /**
     * available() should return the number of bytes that can be read without
     * running into blocking wait. Accomplishing this feast would eventually
     * require to pre-inflate a huge chunk of data, so we rather opt for a
     * more relaxed contract (java.util.zip.InflaterInputStream does not 
     * fit the bill). 
     * This code has been tested to work with BufferedReader.readLine();
     */
    public int available() throws IOException {
        if (!inf.finished() && !inf.needsInput()) {
            return 1;
        } else {
            return in.available();
        }
    }

    /**
     * Wir schließen auch den (selbst erstellten) Inflater, wenn
     * wir fertig sind.
     */
    public void close()
        throws IOException
    {
        super.close();
        inf.end();
    }


} //class