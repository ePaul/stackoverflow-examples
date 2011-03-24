package de.fencing_game.paul.examples;

import java.io.*;
import java.security.*;

/**
 * This class provides an outputstream which writes everything
 * to a Signature as well as to an underlying stream.
 *
 * Inspired by http://stackoverflow.com/questions/5412178/is-there-a-signatureoutputstream-or-equivalent-in-java
 */
public class SignatureOutputStream
    extends OutputStream
{

    private OutputStream target;
    private Signature sig;

    /**
     * creates a new SignatureOutputStream which writes to
     * a target OutputStream and updates the Signature object.
     */
    public SignatureOutputStream(OutputStream target, Signature sig) {
        this.target = target;
        this.sig = sig;
    }

    public void write(int b)
        throws IOException
    {
        write(new byte[]{(byte)b});
    }

    public void write(byte[] b)
        throws IOException
    {
        write(b, 0, b.length);
    }

    public void write(byte[] b, int offset, int len)
        throws IOException
    {
        target.write(b, offset, len);
        try {
            sig.update(b, offset, len);
        }
        catch(SignatureException ex) {
            throw new IOException(ex);
        }
    }

    public void flush() 
        throws IOException
    {
        target.flush();
    }

    public void close() 
        throws IOException
    {
        target.close();
    }


    private static byte[] signData(OutputStream target,
                                   PrivateKey key, String[] data)
        throws IOException, GeneralSecurityException
    {
        Signature sig = Signature.getInstance("SHA1withRSA");
        sig.initSign(key);
        DataOutputStream dOut =
            new DataOutputStream(new SignatureOutputStream(target, sig));
        

        for(String s : data) {
            dOut.writeUTF(s);
        }
        byte[] signature = sig.sign();
        return signature;
    }


    private static void verify(PublicKey key, byte[] signature,
                                  byte[] data)
        throws IOException, GeneralSecurityException
    {
        Signature sig = Signature.getInstance("SHA1withRSA");
        sig.initVerify(key);
        
        ByteArrayOutputStream collector =
            new ByteArrayOutputStream(data.length);
        OutputStream checker = new SignatureOutputStream(collector, sig);
        checker.write(data);
        if(sig.verify(signature)) {
            System.err.println("Signature okay");
        }
        else {
            System.err.println("Signature falsed!");
        }
    }


    /**
     * a test method.
     */
    public static void main(String[] params) 
        throws IOException, GeneralSecurityException
    {
        if(params.length < 1) {
            params = new String[] {"Hello", "World!"};
        }

        KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
        KeyPair pair = gen.generateKeyPair();
        

        ByteArrayOutputStream arrayStream = new ByteArrayOutputStream();

        byte[] signature = signData(arrayStream, pair.getPrivate(), params);
        byte[] data = arrayStream.toByteArray();
        
        verify(pair.getPublic(), signature, data);
        
        // change one byte by one
        data[3]++;

        verify(pair.getPublic(), signature, data);

        data = arrayStream.toByteArray();
        
        verify(pair.getPublic(), signature, data);

        // change signature
        signature[4]++;

        verify(pair.getPublic(), signature, data);
        
    }

}