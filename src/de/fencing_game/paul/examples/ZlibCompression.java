package de.fencing_game.paul.examples;

import java.util.zip.*;
import java.io.*;

/**
 * Example program to demonstrate how to use zlib compression with
 * Java.
 * Inspired by http://stackoverflow.com/q/6173920/600500.
 */
public class ZlibCompression {

    /**
     * Compresses a file with zlib compression.
     */
    public static void compressFile(File raw, File compressed)
        throws IOException
    {
        InputStream in = new FileInputStream(raw);
        OutputStream out =
            new DeflaterOutputStream(new FileOutputStream(compressed));
        shovelInToOut(in, out);
        in.close();
        out.close();
    }

    /**
     * Decompresses a zlib compressed file.
     */
    public static void decompressFile(File compressed, File raw)
        throws IOException
    {
        InputStream in =
            new InflaterInputStream(new FileInputStream(compressed));
        OutputStream out = new FileOutputStream(raw);
        shovelInToOut(in, out);
        in.close();
        out.close();
    }

    /**
     * Shovels all data from an input stream to an output stream.
     */
    private static void shovelInToOut(InputStream in, OutputStream out)
        throws IOException
    {
        byte[] buffer = new byte[1000];
        int len;
        while((len = in.read(buffer)) > 0) {
            out.write(buffer, 0, len);
        }
    }


    /**
     * Main method to test it all.
     */
    public static void main(String[] args) throws IOException, DataFormatException {
        File compressed = new File("book1out.dfl");
        compressFile(new File("book1"), compressed);
        decompressFile(compressed, new File("decompressed.txt"));
    }
}