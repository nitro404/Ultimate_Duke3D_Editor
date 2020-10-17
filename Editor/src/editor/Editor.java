package editor;

import java.io.*;
import javax.swing.*;
import gui.*;
import utilities.*;
import settings.*;
import console.*;
import version.*;
import palette.*;

public class Editor implements PaletteChangeListener {
	
	public static Editor instance = null;
	public static EditorWindow editorWindow = null;
	public static SettingsManager settings = null;
	public static SystemConsole console = null;
	public static ExtendedClassLoader classLoader = null;
	public static EditorPluginManager pluginManager = null;
	private boolean m_initialized;
	private static int currentItemNumber = 1;
	public static final String VERSION = "1.0";
	
	public Editor() {
		editorWindow = new EditorWindow();
		
		instance = this;
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
	
	public boolean createLogDirectory() {
		if(settings.logDirectoryName.length() == 0) { return true; }
		
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

	public void notifyPaletteChanged(PalettePanel palettePanel) {
		editorWindow.update();
	}
	
	public void close() {
		m_initialized = false;
		
		if(settings.autoSaveSettings) {
			settings.save();
		}
	}
	
}
