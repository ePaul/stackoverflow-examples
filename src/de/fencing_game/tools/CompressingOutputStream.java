package de.fencing_game.tools;

import java.util.zip.*;
import java.io.*;

/**
 * Workaround für kaputten GZipOutputStream, von
 * http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4206909
 * (23-JUN-2002, rsaddey)
 * @see DecompressingInputStream
 */
public class CompressingOutputStream
    extends DeflaterOutputStream {


    public CompressingOutputStream (final OutputStream out)
    {
        super(out,
              // Using Deflater with nowrap == true will ommit headers
              //  and trailers
              new Deflater(Deflater.DEFAULT_COMPRESSION, true));
    }

    private static final byte [] EMPTYBYTEARRAY = new byte[0];
    /**
     * Insure all remaining data will be output.
     */
    public void flush() throws IOException {
        /**
         * Now this is tricky: We force the Deflater to flush
         * its data by switching compression level.
         * As yet, a perplexingly simple workaround for 
         *  http://developer.java.sun.com/developer/bugParade/bugs/4255743.html 
        */
        def.setInput(EMPTYBYTEARRAY, 0, 0);

        def.setLevel(Deflater.NO_COMPRESSION);
        deflate();

        def.setLevel(Deflater.DEFAULT_COMPRESSION);
        deflate();

        out.flush();
    }

    /**
     * Wir schließen auch den (selbst erstellten) Deflater, wenn
     * wir fertig sind.
     */
    public void close()
        throws IOException
    {
        super.close();
        def.end();
    }

} // class
