package de.fencing_game.paul.examples.accessible;

import java.lang.reflect.ReflectPermission;
import java.lang.reflect.Field;
import java.net.URLClassLoader;
import java.net.URL;
import java.io.File;
import java.util.*;

import java.security.*;

public class Main {

    DataClass data = new DataClass(5);
    private List<Field> fields = new ArrayList<Field>();

    Main() {
	try {
	    fields.add(DataClass.class.getDeclaredField("value"));
	}
	catch(Exception ex) {
	    throw new RuntimeException(ex);
	}
    }


    public void testIt(String jar, String className, boolean reuse) {
	System.err.println("Testing " + className + " from " + jar +
			   (reuse ? " with reused field" : "") + ":");
	try {
	    URL[] urls = {
		new File("./jars/" + jar).toURI().toURL(),
	    };
	    ClassLoader cl = new URLClassLoader(urls, Main.class.getClassLoader());
	    Class<?> clazz =
		cl.loadClass("de.fencing_game.paul.examples.accessible." +
			     className);
	    Class<? extends Accessor> accClass =
		clazz.asSubclass(Accessor.class);


	    Accessor acc;

	    if(reuse) {
		acc = accClass.getConstructor(Field.class)
		    .newInstance(fields.get(1));
	    }
	    else {
		acc = accClass.newInstance();
	    }
	    testIt(acc);
	}
	catch(Exception ex) {
	    ex.printStackTrace();
	}
    }

    public void testIt(Accessor acc) {
	try {
	    acc.printObject(data);
	}
	finally {
	    fields.add(acc.getField());
	}
    }

    public void checkFields() {
	for(int i = 0; i < fields.size(); i++) {
	    for(int j = 0; j < fields.size(); j++) {
		if(fields.get(i) == fields.get(j)) {
		    System.err.println("["+i+"] == ["+j+"] : " + fields.get(j)+
				       ", accessible: " +
				       fields.get(j).isAccessible());
		} else if (fields.get(i).equals(fields.get(j))) {
		    System.err.println("["+i+"].equals(["+j+"])");
		} else {
		    System.err.println("["+i+"] =/= ["+j+"]");
		}
	       
	    }
	}
	for(int i = 0; i < fields.size(); i++) {
	    try { 
		System.err.println("["+i+"]: " +fields.get(i).getInt(data));
	    }
	    catch(IllegalAccessException ex) {
		System.err.println("["+i+"]: access forbidden");
	    }
	}
    }


    public static void main(String[] params) {
	Policy.setPolicy(new Policy() {
		public boolean implies(ProtectionDomain domain,
				       Permission permission) {
// 		    System.err.println("checking: " + domain +
// 				       ", " + permission);

		    if(permission instanceof ReflectPermission) {
			CodeSource source = domain.getCodeSource();
			URL url = source.getLocation();
			String path = url.getPath();
			System.err.println("source-path: " + path);
			if(path.contains("disallowed")) {
			    System.err.println("refused!");
			    return false;
			}
		    }
		    return true;
		}
	    });
	System.setSecurityManager(new SecurityManager());
	
	Main m = new Main();
	System.err.println("---------");
	m.testIt("accessor-allowed.jar", "AllowedAccessor", false);
	System.err.println("---------");
	m.testIt("accessor-disallowed.jar", "DisallowedAccessor", false);
	System.err.println("---------");
	m.testIt("accessor-disallowed.jar", "DisallowedAccessor", true);
	System.err.println("---------");
	m.testIt(m.data);
	System.err.println("---------");
	m.checkFields();
    }


}