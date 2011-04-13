package de.fencing_game.paul.examples;

import java.io.*;
import java.security.*;
import java.security.spec.*;
import javax.crypto.*;
import javax.crypto.spec.*;

public class EncryptDecrypt {


    AlgorithmParameterSpec params;

    public EncryptDecrypt()
        throws Exception
    {
        byte[] ivar = new byte[] {
            0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07,
            0x08, 0x09,0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f
        };
        params = new IvParameterSpec(ivar );
    }
    

    public void encrypt(SecretKey key, File from, File to)
        throws  Exception
    {
        Cipher ourCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        ourCipher.init(Cipher.ENCRYPT_MODE, key, params );
        crypt(ourCipher, from, to);
    }

    public void decrypt(SecretKey key, File from, File to)
        throws Exception
    {
        Cipher ourCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        ourCipher.init(Cipher.DECRYPT_MODE, key, params );
        crypt(ourCipher, from, to);
    }


    private void crypt(Cipher c, File from, File to) 
        throws IOException
    {
        InputStream in = new CipherInputStream(new FileInputStream(from), c);
        OutputStream out = new FileOutputStream(to);
        copyStream(in, out);
    }

    private void copyStream(InputStream in, OutputStream out)
        throws IOException
    {
        byte[] buffer = new byte[4096];
        int count = 0;
        
        while ((count = in.read(buffer)) != -1) {
            out.write(buffer, 0, count);
        }
        out.flush();
        out.close();
        in.close();
    }


    public static void main(String[] params)
        throws Exception
    {
        EncryptDecrypt ed = new EncryptDecrypt();

        SecretKey key;
        if(params.length > 3) {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            kgen.init(128);
            key = kgen.generateKey();
        }
        else {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("AES");
            // AES needs 128 bits = 16 bytes
            KeySpec spec =
                new SecretKeySpec("Test-KeyTest-Key".getBytes("US-ASCII"),
                                  "AES");
            System.out.println(spec);
            
            key = factory.generateSecret(spec);
            System.out.println(key);
        }

        
        ed.encrypt(key, new File(params[0]), new File(params[1]));
        ed.decrypt(key, new File(params[1]), new File(params[2]));
    }

}