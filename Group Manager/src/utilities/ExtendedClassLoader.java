package utilities;

import java.util.regex.*;
import java.io.*;
import console.*;

public class ExtendedClassLoader extends ClassLoader {
	
    public ExtendedClassLoader() {
    	this(ClassLoader.getSystemClassLoader());
    }
    
    public ExtendedClassLoader(ClassLoader parent) {
    	super(parent);
    }
    
    // calls loadClass and links it to the class loader
    public Class<?> loadClass(String className) throws ClassNotFoundException {
    	return loadClass(className, true);
    }
    
    // checks if it can load the class through an alternative method, otherwise it calls findClass and does it by copying the raw data
    public Class<?> loadClass(String className, boolean resolve) throws ClassNotFoundException {
		Class<?> c = null;
	
		// check to see if class is already loaded
		c = findLoadedClass(className);
		if(c != null) {
		    return c;
		}
		
		// check if class is in system class loader
		c = findSystemClass(className);
		if(c != null) {
		    return c;
		}
	
		// otherwise, check if we can find it locally
		c = findClass(className);
		
		return c;
    }
    
    // gets the raw data of the class by searching and retrieving the file locally
    public byte[] serializeClass(String className) {
    	if(className == null) { return null; }
    	
		File f;
		DataInputStream in;
		byte[] data = null;
		
		// append .class to the name of the class (if it is not already present)
		String path = className.trim();
		if(!Pattern.compile(".*\\.class$", Pattern.CASE_INSENSITIVE).matcher(path).matches()) {
			path = path + ".class";
		}
		
		// count the number of periods in the class name (including the .class)
		int numberOfPeriods = 0;
		for(int i=0;i<path.length();i++) {
			if(path.charAt(i) == '.') {
				numberOfPeriods++;
			}
		}
		
		// replace all but the last period in the class name with forward slashes
		String[] temp = path.split("[.]", numberOfPeriods);
		path = "bin";
		for(int i=0;i<temp.length;i++) {
			path += "/" + temp[i];
		}
		
		// attempt to open the compiled class, and store it as a byte array, then return it
		try {
			f = new File(path);
			if(!f.exists()) { return null; }
			in = new DataInputStream(new FileInputStream(f));
		    data = new byte[(int) f.length()];
		    in.readFully(data);
		    in.close();
		}
		catch(Exception e) {
		    return null;
		}
		
		return data;
    }
    // calls deserializeClass on the parameters, and passes in the resolve as true
    public Class<?> deserializeClass(String name, byte[] classData) {
    	return deserializeClass(name, classData, true);
    }
    
    // deserializes a class from a byte array and tries to load the class
    // if loading it fails, the class gets defined, and then resolved
    public Class<?> deserializeClass(String className, byte[] classData, boolean resolve) {
    	if(className == null || classData == null || classData.length == 0) { return null; }
    	
    	Class<?> c;
    	try {
    		// check if the class is already loaded, or available locally (and load it if so)
    		try {
    			c = loadClass(className);
    		}
    		catch(ClassNotFoundException e) {
    			c = null;
    		}
			
			// if not, define the class
			if(c == null) {
				try { c = defineClass(className, classData, 0, classData.length); }
				catch(ClassFormatError e) {
					SystemConsole.instance.writeLine("Class \"" + className + "\" is not properly formatted: " + e.getMessage() + " (Maybe try compressing the jar file?)");
					
					return null;
				}
			}
			
			// if the class should be resolved (loaded), do so
    		if(resolve) {
    			resolveClass(c);
    		}
    	}
    	catch(Exception e) {
    		SystemConsole.instance.writeLine("Unexpected exception thrown while deserializing class \"" + className + "\": " + e.getMessage());
    		
    		return null;
    	}
    	
    	return c;
    }
    
}
