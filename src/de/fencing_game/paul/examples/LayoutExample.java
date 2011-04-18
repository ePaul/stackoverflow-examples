package de.fencing_game.paul.examples;


import javax.swing.*;
import javax.swing.GroupLayout.*;
import de.fencing_game.gui.basics.LayoutHelper;
import java.awt.Container;


/**
 * Example of using my LayoutHelper.
 *
 * Inspired by the question <a href="http://stackoverflow.com/q/5702857/600500">Please help me understanding BoxLayout alignment issues</a> on StackOverflow.
 */
public class LayoutExample {

    public static void makeGUI() {
        
        JFrame frame = new JFrame("layout example");
        Container pane = frame.getContentPane();

        JButton button = new JButton("Button");
        JProgressBar progressBar = new JProgressBar();
        JLabel label = new JLabel("Label");


        LayoutHelper h = new LayoutHelper(pane);
        
        h.setVerticalGroup
            ( h.sequential( button, progressBar, label));

        h.setHorizontalGroup
            ( ((ParallelGroup)h.parallel())
              .addComponent(button, Alignment.CENTER)
              .addComponent(progressBar)
              .addComponent(label, Alignment.TRAILING));

        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        frame.setSize(200, 200);
        frame.setVisible(true);

    }


    public static void main(String[] params) {
        SwingUtilities.invokeLater(new Runnable() { public void run() {
            makeGUI();
        }});

    }



}