package de.fencing_game.paul.examples.rome;

import org.xml.sax.InputSource;

import java.nio.charset.Charset;
import java.io.*;
import java.net.*;

import com.sun.syndication.io.*;
import com.sun.syndication.feed.synd.*;


public class RomeTest {

    public static void main(String[] ignored)
        throws IOException, FeedException
    {
        String charset = "UTF-8";
        String url = "http://oglobo.globo.com/rss/plantaopais.xml";


        InputStream is = new URL(url).openConnection().getInputStream();
        InputSource source = new InputSource(is);

        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed = input.build(source);

        System.out.println("description: " + feed.getDescription());
    }


}