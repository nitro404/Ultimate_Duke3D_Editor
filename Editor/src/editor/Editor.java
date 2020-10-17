package editor;

import java.util.*;
import java.io.*;
import java.awt.*;
import javax.swing.*;
import gui.*;
import utilities.*;
import settings.*;
import console.*;
import exception.*;
import version.*;
import plugin.*;
import palette.*;

public class Editor {
	
	public static Editor instance = null;
	public static EditorWindow editorWindow = null;
	public static SettingsManager settings = null;
	public static SystemConsole console = null;
	public static ExtendedClassLoader classLoader = null;
	public static EditorPluginManager pluginManager = null;
	public static Palette palette;
	public static Palette lookup;
	private boolean m_initialized;
	private static int currentItemNumber = 1;
	public static final String VERSION = "1.0.0";
	
	public Editor() {
		editorWindow = new EditorWindow();
		
		instance = this;
		palette = Palette.DEFAULT_PALETTE;
		lookup = Palette.DEFAULT_LOOKUP;
		settings = new SettingsManager();
		classLoader = new ExtendedClassLoader();
		console = new SystemConsole();
		pluginManager = new EditorPluginManager();
		
		m_initialized = false;
	}
	
	public boolean initialize(String[] args) {
		if(m_initialized) { return false; }
		
		if(args != null && args.length > 0 && args[0] != null) {
			String temp = args[0].trim();
			if(temp.length() > 0) {
				settings.settingsFileName = temp;
				
				console.writeLine("Using alternate settings file: " + settings.settingsFileName);
			}
		}
		
		if(settings.load()) {
			console.writeLine("Settings successfully loaded from file: " + settings.settingsFileName);
		}
		else {
			console.writeLine("Failed to load settings from file: " + settings.settingsFileName);
			
			if(settings.settingsFileName != null && !SettingsManager.defaultSettingsFileName.equalsIgnoreCase(settings.settingsFileName)) {
				boolean loaded = false;
				
				while(!loaded) {
					int choice = JOptionPane.showConfirmDialog(null, "Unable to load settings from custom settings file. Use alternate settings file?\nNote that when the program is closed, this settings file will be generated if it does not exist.", "Settings Loading Failed", JOptionPane.YES_NO_CANCEL_OPTION);
					if(choice == JOptionPane.YES_OPTION) {
						String newSettingsFileName = JOptionPane.showInputDialog(null, "Enter a settings file name:", SettingsManager.defaultSettingsFileName);
						if(newSettingsFileName != null) {
							settings.settingsFileName = newSettingsFileName;
							loaded = settings.load();
							
							if(loaded) {
								console.writeLine("Settings successfully loaded from file: " + settings.settingsFileName);
							}
						}
						else {
							break;
						}
					}
					else {
						break;
					}
				}
			}
		}
		
		if(!pluginManager.initialize(editorWindow.getFrame())) {
			JOptionPane.showMessageDialog(editorWindow.getFrame(), "Failed to initialize plugin manager!", "Initialization Failed", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		
		if(settings.autoLoadPlugins) {
			pluginManager.loadPlugins();
		}
		
		updatePalettes(true);
		
		m_initialized = true;
		
		boolean error = false;
		
		console.addTarget(editorWindow);
		
		if(!editorWindow.initialize()) {
			JOptionPane.showMessageDialog(editorWindow.getFrame(), "Failed to initialize Ultimate Duke Nukem 3D Editor window!", "Initialization Failed", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		
		if(!error) {
			console.writeLine("Ultimate Duke Nukem 3D Editor initialized successfully!");
		}
		
		VersionChecker.checkVersion(false);
		
		return true;
	}
	
	public static int getItemNumber() {
		return currentItemNumber++;
	}
	
	public static int currentItemNumber() {
		return currentItemNumber;
	}

	public static Palette loadPalette(File paletteFile) {
		return loadPalette(paletteFile, false);
	}

	public static Palette loadPalette(File paletteFile, boolean showMessages) {
		return loadPalette(paletteFile, false, showMessages);
	}

	public static Palette loadLookup(File lookupFile) {
		return loadLookup(lookupFile, false);
	}

	public static Palette loadLookup(File lookupFile, boolean showMessages) {
		return loadPalette(lookupFile, true, showMessages);
	}
	
	private static Palette loadPalette(File paletteFile, boolean isLookup, boolean showMessages) {
		String type = isLookup ? "lookup" : "palette";
		String extension = Utilities.getFileExtension(paletteFile.getName());
		
		Vector<FilePlugin> plugins = EditorPluginManager.instance.getPluginsForFileFormat(extension);
		if(plugins == null || plugins.isEmpty()) {
			String message = "No plugin found to load default " + type + " file. Perhaps you forgot to load all plugins? Using default " + type + " instead.";
			
			SystemConsole.instance.writeLine(message);
			
			if(showMessages) {
				JOptionPane.showMessageDialog(editorWindow.getFrame(), message, "No Plugin Found", JOptionPane.ERROR_MESSAGE);
			}
			
			return null;
		}
		
		FilePlugin plugin = EditorPluginManager.instance.getPreferredPluginPrompt(extension);
		if(plugin == null) { return null; }
		
		if(!(plugin instanceof PalettePlugin)) {
			String message = "Plugin is not a valid palette plugin, cannot load " + type + " file. Using default " + type + " instead.";
			
			SystemConsole.instance.writeLine(message);

			if(showMessages) {
				JOptionPane.showMessageDialog(editorWindow.getFrame(), message, "Invalid Plugin Type", JOptionPane.ERROR_MESSAGE);
			}
			
			return null;
		}
		
		Palette palette = null;
		try { palette = (Palette) plugin.getNewItemInstance(paletteFile); }
		catch(ItemInstantiationException e) {
			String message = e.getMessage() + " Using default " + type + " instead.";

			SystemConsole.instance.writeLine(message);

			if(showMessages) {
				JOptionPane.showMessageDialog(editorWindow.getFrame(), message, "Plugin Instantiation Failed", JOptionPane.ERROR_MESSAGE);
			}
			
			return null;
		}
		if(palette == null) {
			String message = "Failed to instantiate \"" + plugin.getName() + " (" + plugin.getSupportedFileFormatsAsString() + ")\" plugin when attempting to read " + type + " file: \"" + paletteFile.getName() + "\". Using default " + type + " instead.";
			
			SystemConsole.instance.writeLine(message);

			if(showMessages) {
				JOptionPane.showMessageDialog(editorWindow.getFrame(), message, "Plugin Instantiation Failed", JOptionPane.ERROR_MESSAGE);
			}
			
			return null;
		}
		
		try {
			if(!palette.load()) {
				String message = "Failed to load " + type + " file: \"" + paletteFile.getName() + "\" using plugin: \"" + plugin.getName() + " (" + plugin.getSupportedFileFormatsAsString() + ")\". Using default " + type + " instead.";
				
				SystemConsole.instance.writeLine(message);

				if(showMessages) {
					JOptionPane.showMessageDialog(editorWindow.getFrame(), message, "File Loading Failed", JOptionPane.ERROR_MESSAGE);
				}
				
				return null;
			}
		}
		catch(HeadlessException e) {
			String message = "Exception thrown while loading file: \"" + paletteFile.getName() + "\" using plugin: \"" + plugin.getName() + " (" + plugin.getSupportedFileFormatsAsString() + "): " + e.getMessage() + " Using default " + type + " instead.";
			
			SystemConsole.instance.writeLine(message);

			if(showMessages) {
				JOptionPane.showMessageDialog(editorWindow.getFrame(), message, "Palette Loading Failed", JOptionPane.ERROR_MESSAGE);
			}
			
			return null;
		}
		catch(ItemReadException e) {
			String message = e.getMessage() + " Using default " + type + " instead.";

			SystemConsole.instance.writeLine(message);

			if(showMessages) {
				JOptionPane.showMessageDialog(editorWindow.getFrame(), message, "Palette Loading Failed", JOptionPane.ERROR_MESSAGE);
			}
			
			return null;
		}
		catch(DeserializationException e) {
			String message = e.getMessage() + " Using default " + type + " instead.";

			SystemConsole.instance.writeLine(message);

			if(showMessages) {
				JOptionPane.showMessageDialog(editorWindow.getFrame(), message, "Palette Deserialization Failed", JOptionPane.ERROR_MESSAGE);
			}
			
			return null;
		}
		
		if(isLookup && palette.numberOfPalettes() < 5) {
			String message = "Expected lookup to contain at least 5 sub-palettes, but found " + palette.numberOfPalettes() + ". Using default " + type + " instead.";

			SystemConsole.instance.writeLine(message);

			if(showMessages) {
				JOptionPane.showMessageDialog(editorWindow.getFrame(), message, "Invalid Lookup Palette", JOptionPane.ERROR_MESSAGE);
			}
			
			return null;
		}
		
		return palette;
	}

	public static void updatePalette() {
		updatePalette(false);
	}

	public static void updatePalette(boolean showMessages) {
		if(Utilities.isNonEmptyString(settings.paletteFilePath)) {
			File paletteFile = new File(settings.paletteFilePath);
			
			if(paletteFile.isFile() && paletteFile.exists()) {
				palette = loadPalette(paletteFile, showMessages);
				
				if(palette != null) {
					return;
				}
			}
			else {
				String message = "Palette file is missing or invalid, using default palette instead.\nCheck your settings and ensure that the palette file still exists!";

				if(showMessages) {
					SystemConsole.instance.writeLine(message);
				}
				
				JOptionPane.showMessageDialog(editorWindow.getFrame(), message, "Palette File Missing", JOptionPane.WARNING_MESSAGE);
			}
		}
		
		palette = Palette.DEFAULT_PALETTE;
	}

	public static void updateLookup() {
		updateLookup(false);
	}
	
	public static void updateLookup(boolean showMessages) {
		if(Utilities.isNonEmptyString(settings.lookupFilePath)) {
			File lookupFile = new File(settings.lookupFilePath);
			
			if(lookupFile.isFile() && lookupFile.exists()) {
				lookup = loadLookup(lookupFile, showMessages);
				
				if(lookup != null) {
					return;
				}
			}
			else {
				String message = "Lookup file is missing or invalid, using default lookup instead.\nCheck your settings and ensure that the lookup file still exists!";
				
				if(showMessages) {
					SystemConsole.instance.writeLine(message);
				}
				
				JOptionPane.showMessageDialog(editorWindow.getFrame(), message, "Lookup File Missing", JOptionPane.WARNING_MESSAGE);
			}
		}
		
		lookup = Palette.DEFAULT_LOOKUP;
	}

	public static void updatePalettes() {
		updatePalettes(false);
	}

	public static void updatePalettes(boolean showMessages) {
		updatePalette(showMessages);
		updateLookup(showMessages);
	}
	
	public boolean createLogDirectory() {
		if(settings.logDirectoryName.isEmpty()) { return true; }
		
		File logDirectory = new File(settings.logDirectoryName);
		
		if(!logDirectory.exists()) {
			try {
				return logDirectory.mkdirs();
			}
			catch(SecurityException e) {
				console.writeLine("Failed to create log directory, check read / write permissions.");
				return false;
			}
		}
		
		return true;
	}
	
	public void close() {
		m_initialized = false;
		
		if(settings.autoSaveSettings) {
			settings.save();
		}
	}
	
}
