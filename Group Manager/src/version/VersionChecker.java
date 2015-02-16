package version;

import java.io.*;
import java.net.*;
import javax.xml.namespace.*;
import javax.xml.stream.*;
import javax.xml.stream.events.*;
import javax.swing.*;
import utilities.*;
import settings.*;
import console.*;
import group.*;

public class VersionChecker {
	
	public static final String PROGRAM = "program";
	public static final String NAME = "name";
	public static final String VERSION = "version";
	public static final String DATE = "date";
	public static final String LINK = "link";
	
	private VersionChecker() {
		
	}
	
	public static void checkVersion() {
		checkVersion(true);
	}
	
	public static void checkVersion(final boolean verbose) {
		new Thread(
			new Runnable() {
				public void run() {
					checkVersionHelper(verbose);
				}
			}
		).start();
	}
	
	private static boolean checkVersionHelper(boolean verbose) {
		if(SettingsManager.instance.versionFileURL == null) {
			String message = "Version file URL not set, maybe reset your settings?";
			
			SystemConsole.instance.writeLine(message);
			
			if(verbose) { JOptionPane.showMessageDialog(GroupManager.groupManagerWindow.getFrame(), message, "Invalid Version File URL", JOptionPane.ERROR_MESSAGE); }
			
			return false;
		}
		
		URL url;
		try {
			url = new URL(SettingsManager.instance.versionFileURL);
		}
		catch(MalformedURLException e) {
			String message = "Version file URL is invalid or malformed, please check that it is correct or reset your settings:" + e.getMessage();
			
			SystemConsole.instance.writeLine(message);
			
			if(verbose) { JOptionPane.showMessageDialog(GroupManager.groupManagerWindow.getFrame(), message, "Invalid URL", JOptionPane.ERROR_MESSAGE); }
			
			return false;
		}
		
		InputStream in;
		try {
			in = url.openStream();
		}
		catch(IOException e) {
			String message = "Failed to open stream to version file, perhaps the url is wrong or the file is missing?";
			
			SystemConsole.instance.writeLine(message);
			
			if(verbose) { JOptionPane.showMessageDialog(GroupManager.groupManagerWindow.getFrame(), message, "IO Exception", JOptionPane.ERROR_MESSAGE); }
			
			return false;
		}
		
		String name = null;
		String version = null;
		String date = null;
		String link = null;
		XMLEvent event = null;
		StartElement element = null;
		Attribute temp = null;
		
		try {
			XMLInputFactory inputFactory = XMLInputFactory.newInstance();
			XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
			while(eventReader.hasNext()) {
				event = eventReader.nextEvent();
				
				if(event.isStartElement()) {
					element = event.asStartElement();
					
					if(element.getName().getLocalPart().equalsIgnoreCase(PROGRAM)) {
						temp = element.getAttributeByName(new QName(NAME));
						if(temp != null) { name = temp.getValue().trim(); }
						if(!name.equalsIgnoreCase("Duke Nukem 3D Group Manager")) {
							SystemConsole.instance.writeLine("Program name in version file does not match name of program: \"" + name + "\".");
						}
						
						temp = element.getAttributeByName(new QName(VERSION));
						if(temp != null) { version = temp.getValue().trim(); }
						
						temp = element.getAttributeByName(new QName(DATE));
						if(temp != null) { date = temp.getValue().trim(); }
						
						temp = element.getAttributeByName(new QName(LINK));
						if(temp != null) { link = temp.getValue().trim(); }
					}
				}
			}
			
			in.close();
		}
		catch(XMLStreamException e) {
			String message = "XML stream exception thrown while attempting to read version file stream: " + e.getMessage();
			
			SystemConsole.instance.writeLine(message);
			
			if(verbose) { JOptionPane.showMessageDialog(GroupManager.groupManagerWindow.getFrame(), message, "XML Stream Exception", JOptionPane.ERROR_MESSAGE); }
			
			return false;
		}
		catch(IOException e) {
			String message = "Read exception thrown while parsing version file.";
			
			SystemConsole.instance.writeLine(message);
			
			if(verbose) { JOptionPane.showMessageDialog(GroupManager.groupManagerWindow.getFrame(), message, "IO Exception", JOptionPane.ERROR_MESSAGE); }
			
			return false;
		}
		
		try {
			switch(Utilities.compareVersions(GroupManager.VERSION, version)) {
				case -1:
					SystemConsole.instance.writeLine("A new version of Duke Nukem 3D Group Manager is available! Released " + date + ". Download version " + version + " at the following link: \"" + link + "\".");
					
					if(verbose || !SettingsManager.instance.supressUpdates) { JOptionPane.showMessageDialog(GroupManager.groupManagerWindow.getFrame(), "A new version of Duke Nukem 3D Group Manager is available! Released " + date + ".\nDownload version " + version + " at the following link: \"" + link + "\".", "New Version Available", JOptionPane.INFORMATION_MESSAGE); }
					
					break;
				case 0:
					String message = "Duke Nukem 3D Group Manager is up to date with version " + GroupManager.VERSION + ", released " + date + ".";
					
					SystemConsole.instance.writeLine(message);
					
					if(verbose) { JOptionPane.showMessageDialog(GroupManager.groupManagerWindow.getFrame(), message, "Up to Date", JOptionPane.INFORMATION_MESSAGE); }
					
					break;
				case 1:
					SystemConsole.instance.writeLine("Wow, you're from the future? Awesome. Hope you're enjoying your spiffy version " + GroupManager.VERSION + " of the Duke Nukem 3D Group Manager!");
					
					JOptionPane.showMessageDialog(GroupManager.groupManagerWindow.getFrame(), "Wow, you're from the future? Awesome.\nHope you're enjoying your spiffy version " + GroupManager.VERSION + " of the Duke Nukem 3D Group Manager!", "Hello Time Traveller!", JOptionPane.INFORMATION_MESSAGE);
					
					break;
			}
		}
		catch(NumberFormatException e) {
			String message = "Version check failed: Illegal non-numerical value encountered while parsing version.";
			
			SystemConsole.instance.writeLine(message);
			
			if(verbose) { JOptionPane.showMessageDialog(GroupManager.groupManagerWindow.getFrame(), message, "Invalid Version", JOptionPane.ERROR_MESSAGE); }
			
			return false;
		}
		catch(IllegalArgumentException e) { return false; }
		
		return true;
	}
	
}
