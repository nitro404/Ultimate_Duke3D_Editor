package group;

import java.io.*;
import javax.swing.*;
import gui.*;
import utilities.*;
import settings.*;
import console.*;
import version.*;

public class GroupManager extends GroupCollection {
	
	public static GroupManager instance = null;
	public static GroupManagerWindow groupManagerWindow = null;
	public static SettingsManager settings = null;
	public static SystemConsole console = null;
	public static ExtendedClassLoader classLoader = null;
	public static GroupPluginManager pluginManager = null;
	private boolean m_initialized;
	private static int currentGroupNumber = 1;
	public static final String VERSION = "1.0";
	
	public GroupManager() {
		super();
		
		groupManagerWindow = new GroupManagerWindow();
		
		if(instance == null) {
			updateInstance();
		}
		settings = new SettingsManager();
		classLoader = new ExtendedClassLoader();
		console = new SystemConsole();
		pluginManager = new GroupPluginManager();
		
		m_initialized = false;
	}
	
	public void updateInstance() {
		instance = this;
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
		
		if(!pluginManager.initialize(groupManagerWindow.getFrame())) {
			JOptionPane.showMessageDialog(groupManagerWindow.getFrame(), "Failed to initialize plugin manager!", "Initialization Failed", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		
		if(settings.autoLoadPlugins) {
			pluginManager.loadPlugins();
		}
		
		m_initialized = true;
		
		boolean error = false;
		
		console.addTarget(groupManagerWindow);
		
		if(!groupManagerWindow.initialize()) {
			JOptionPane.showMessageDialog(groupManagerWindow.getFrame(), "Failed to initialize Group Manager window!", "Initialization Failed", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		
		if(!error) {
			console.writeLine("Group Manager initialized successfully!");
		}
		
		VersionChecker.checkVersion(false);
		
		return true;
	}
	
	public static int getGroupNumber() {
		return currentGroupNumber++;
	}
	
	public static void incrementGroupNumber() {
		currentGroupNumber++;
	}
	
	public static int currentGroupNumber() {
		return currentGroupNumber;
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
	
	public void close() {
		m_initialized = false;
		
		if(settings.autoSaveSettings) {
			settings.save();
		}
	}
	
}
