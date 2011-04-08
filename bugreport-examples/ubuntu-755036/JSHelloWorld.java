import java.applet.*;
import java.awt.*;
import netscape.javascript.*;
import javax.swing.*;

public class JSHelloWorld extends JApplet {
    JTextArea txt = new JTextArea(100,100);

    public void start(){
        JSObject jso = JSObject.getWindow(this);
        try {
            jso.call("updateWebPage", new String[] {"Hihi"});
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public JSHelloWorld() {
        txt.setText("Hello World");
        getContentPane().add(txt);
    }

    public void setText(String s)
    {
        txt.setText(s);
    }

}