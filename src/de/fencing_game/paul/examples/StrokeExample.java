package de.fencing_game.paul.examples;

import de.fencing_game.gui.basics.TransformedStroke;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

public class StrokeExample extends JPanel {
  

    public void paintComponent(Graphics context) {
        super.paintComponent(context);
        Graphics2D g = (Graphics2D)context.create();

        int height = getHeight();
        int width = getWidth();

        g.scale(width/4.0, height/7.0);

        try {
            g.setStroke(new TransformedStroke(new BasicStroke(2f),
                                              g.getTransform()));
        }
        catch(NoninvertibleTransformException ex) {
            ex.printStackTrace();
        }

        g.setColor(Color.BLACK);
        g.draw(new Rectangle( 1, 2, 2, 4));
    }

    public static void main(String[] params) {
        EventQueue.invokeLater(new Runnable(){public void run() {
            
            StrokeExample example = new StrokeExample();
            
            JFrame f = new JFrame("StrokeExample");
            f.setSize(300, 100);
            f.getContentPane().setLayout(new BorderLayout());
            f.getContentPane().add(example);
            f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            f.setVisible(true);
        }});

    }

}