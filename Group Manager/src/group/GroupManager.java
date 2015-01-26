package group;

import java.io.*;
import java.util.*;
import javax.swing.*;
import gui.*;
import utilities.*;
import variable.*;
import settings.*;
import console.*;
import version.*;

public class GroupManager extends GroupCollection implements GroupChangeListener {
	
	public static GroupManager instance;
	public static GroupManagerWindow groupManagerWindow;
	public static SettingsManager settings;
	public static SystemConsole console;
	public static ExtendedClassLoader classLoader;
	public static GroupPluginManager pluginManager;
	private ProgressDialog m_progressDialog;
	private boolean m_initialized;
	private static int currentGroupNumber = 1;
	public static final String VERSION = "1.0";
	
	public GroupManager() {
		super();
		
		groupManagerWindow = new GroupManagerWindow();
		
		instance = this;
		settings = new SettingsManager();
		classLoader = new ExtendedClassLoader();
		console = new SystemConsole();
		pluginManager = new GroupPluginManager();
		
		m_progressDialog = new ProgressDialog(groupManagerWindow.getFrame());
		
		m_initialized = false;
	}
	
	public boolean initialize(String[] args) {
		if(m_initialized) { return false; }
		
		if(args != null && args.length > 0 && args[0] != null) {
			String temp = args[0].trim();
			if(temp.length() > 0) {
				settings.settingsFileName = temp;
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
		
		if(settings.autoLoadPlugins) {
			loadPlugins();
		}
		
		m_initialized = true;
		
		boolean error = false;
		
		console.addTarget(groupManagerWindow);
		
		if(!groupManagerWindow.initialize()) {
			JOptionPane.showMessageDialog(null, "Failed to initialize Group Manager window!", "Group Manager Init Failed", JOptionPane.ERROR_MESSAGE);
			
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
	
	public void displayLoadedPlugins() {
		Vector<GroupPlugin> loadedPlugins = pluginManager.getLoadedPlugins();
		if(loadedPlugins.size() > 0) {
			String listOfLoadedPlugins = new String();
			for(int i=0;i<loadedPlugins.size();i++) {
				listOfLoadedPlugins += (i + 1) + ": " + loadedPlugins.elementAt(i).getName() + (i < loadedPlugins.size() - 1 ? "\n" : "");
			}
			
			JOptionPane.showMessageDialog(groupManagerWindow.getFrame(), "Detected " + loadedPlugins.size() + " loaded plugin" + (loadedPlugins.size() == 1 ? "" : "s") + ":\n" + listOfLoadedPlugins, loadedPlugins.size() + " Plugin" + (loadedPlugins.size() == 1 ? "" : "s") +" Loaded", JOptionPane.INFORMATION_MESSAGE);
		}
		else {
			JOptionPane.showMessageDialog(groupManagerWindow.getFrame(), "No plugins currently loaded.", "No Plugins Loaded", JOptionPane.INFORMATION_MESSAGE);
		}
	}
	
	public boolean loadPluginPrompt() {
		VariableCollection plugins = pluginManager.getUnloadedPlugins(new File(settings.pluginDirectoryName));
		
		if(plugins.numberOfVariables() == 0) {
			JOptionPane.showMessageDialog(groupManagerWindow.getFrame(), "No unloaded plugins found.", "No Unloaded Plugins", JOptionPane.INFORMATION_MESSAGE);
			return false;
		}
		
		String[] choices = new String[plugins.numberOfVariables()];
		for(int i=0;i<plugins.numberOfVariables();i++) {
			choices[i] = plugins.getVariable(i).getID();
		}
		
		String choice = (String) JOptionPane.showInputDialog(groupManagerWindow.getFrame(), "Choose a plugin to load:", "Load Plugin", JOptionPane.QUESTION_MESSAGE, null, choices, choices[0]);
		if(choice == null) { return false; }
		
		Variable plugin = plugins.getVariable(choice);
		if(plugin == null) { return false; }
		
		return loadPlugin(plugin.getID(), plugin.getValue());
	}
	
	public boolean loadPlugin(String pluginName, final String pluginConfigFileName) {
		if(pluginConfigFileName == null) { return false; }
		
		int numberOfLoadedPlugins = pluginManager.numberOfPlugins();
		
		final Task task = new Task(1, m_progressDialog);
		
		Thread pluginLoaderThread = new Thread(new Runnable() {
			public void run() {
				pluginManager.loadPlugin(new File(pluginConfigFileName), task);
			}
		});
		pluginLoaderThread.start();
		
		m_progressDialog.display("Loading", "Loading plugin...", 0, 1);
		
		if(m_progressDialog.userCancelled() || !task.isCompleted()) {
			task.cancel();
			
			pluginLoaderThread.interrupt();
			try { pluginLoaderThread.join(); } catch(InterruptedException e) { }
			
			m_progressDialog.clear();
		}
		
		groupManagerWindow.update();
		
		if(numberOfLoadedPlugins == pluginManager.numberOfPlugins()) {
			if(!m_progressDialog.userCancelled()) {
				console.writeLine("Failed to load plugin" + (pluginName == null ? "" : ": " + pluginName));
				
				JOptionPane.showMessageDialog(groupManagerWindow.getFrame(), "Failed to load plugin " + (pluginName == null ? "" : ": " + pluginName), "Loading Failed", JOptionPane.ERROR_MESSAGE);
			}
			
			return false;
		}
		else {
			console.writeLine("Successfully loaded plugin" + (pluginName == null ? "" : ": " + pluginName));
			
			return true;
		}
	}
	
	public void loadPlugins() {
		int numberOfUnloadedPlugins = pluginManager.numberOfUnloadedPlugins(new File(settings.pluginDirectoryName));
		int numberOfLoadedPlugins = pluginManager.numberOfPlugins();
		
		String[] pluginNames = new String[pluginManager.numberOfPlugins()];
		for(int i=0;i<pluginManager.numberOfPlugins();i++) {
			pluginNames[i] = pluginManager.getPlugin(i).getName();
		}
		
		console.writeLine("Number of unloaded plugins detected: " + numberOfUnloadedPlugins);
		
		if(numberOfUnloadedPlugins > 0) {
			final Task task = new Task(numberOfUnloadedPlugins, m_progressDialog);
			
			Thread pluginLoaderThread = new Thread(new Runnable() {
				public void run() {
					pluginManager.loadPlugins(new File(settings.pluginDirectoryName), task);
				}
			});
			pluginLoaderThread.start();
			
			m_progressDialog.display("Loading", "Loading plugins...", 0, numberOfUnloadedPlugins);
			
			if(m_progressDialog.userCancelled() || !task.isCompleted()) {
				task.cancel();
				
				pluginLoaderThread.interrupt();
				try { pluginLoaderThread.join(); } catch(InterruptedException e) { }
				
				m_progressDialog.clear();
			}
			
			groupManagerWindow.update();
			
			if(pluginManager.numberOfPlugins() == 0 || pluginManager.numberOfPlugins() - numberOfLoadedPlugins == 0) {
				console.writeLine("No plugins were loaded.");
			}
			else {
				int totalPluginsLoaded = (pluginManager.numberOfPlugins() - numberOfLoadedPlugins);
				
				boolean foundPlugin, firstPlugin = true;
				StringBuffer s = new StringBuffer();
				s.append("Successfully loaded " + totalPluginsLoaded + " plugin" + (totalPluginsLoaded == 1 ? "" : "s") + ": ");
				for(int i=0;i<pluginManager.numberOfPlugins();i++) {
					foundPlugin = false;
					for(int j=0;j<pluginNames.length;j++) {
						if(pluginNames[j].equalsIgnoreCase(pluginManager.getPlugin(i).getName())) {
							foundPlugin = true;
						}
					}
					
					if(!foundPlugin) {
						if(!firstPlugin) {
							s.append(", ");
						}
						s.append(pluginManager.getPlugin(i).getName());
						
						firstPlugin = false;
					}
				}
				
				console.writeLine(s.toString());
			}
		}
		else {
			console.writeLine("No plugins to load.");
		}
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
	
	public void notifyGroupChanged(GroupPanel groupPanel) {
		groupManagerWindow.update();
	}
	
	public void close() {
		m_initialized = false;
		
		if(settings.autoSaveSettings) {
			settings.save();
		}
	}
	
}
