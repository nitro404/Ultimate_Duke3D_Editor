package group;

import java.util.*;
import java.io.*;
import javax.swing.*;
import variable.*;
import settings.*;
import console.*;
import plugin.*;

public class GroupPluginManager extends PluginManager {
	
	public static GroupPluginManager instance = null;
	
	public GroupPluginManager() {
		if(instance == null) {
			updateInstance();
		}
		
		addPluginType(GroupPlugin.PLUGIN_TYPE, GroupPlugin.class);
		addPluginType(GroupProcessorPlugin.PLUGIN_TYPE, GroupProcessorPlugin.class);
	}
	
	public void updateInstance() {
		instance = this;
	}
	
	public Vector<GroupPlugin> getGroupPluginsForFileFormat(String fileFormat) {
		if(fileFormat == null) { return null; }
		String formattedFileFormat = fileFormat.trim();
		if(formattedFileFormat.length() == 0) { return null; }
		
		GroupPlugin groupPlugin = null;
		Vector<GroupPlugin> groupPlugins = new Vector<GroupPlugin>();
		for(int i=0;i<m_plugins.size();i++) {
			if(!(m_plugins.elementAt(i) instanceof GroupPlugin)) { continue; }
			
			groupPlugin = (GroupPlugin) m_plugins.elementAt(i);
			
			for(int j=0;j<groupPlugin.numberOfSupportedGroupFileFormats();j++) {
				if(groupPlugin.getSupportedGroupFileFormat(j).equalsIgnoreCase(formattedFileFormat)) {
					groupPlugins.add(groupPlugin);
				}
			}
		}
		return groupPlugins;
	}
	
	public boolean hasGroupPluginForFileFormat(String fileFormat) {
		if(fileFormat == null) { return false; }
		String formattedFileFormat = fileFormat.trim();
		if(formattedFileFormat.length() == 0) { return false; }
		
		GroupPlugin groupPlugin = null;
		for(int i=0;i<m_plugins.size();i++) {
			if(!(m_plugins.elementAt(i) instanceof GroupPlugin)) { continue; }
			
			groupPlugin = (GroupPlugin) m_plugins.elementAt(i);
			
			for(int j=0;j<groupPlugin.numberOfSupportedGroupFileFormats();j++) {
				if(groupPlugin.getSupportedGroupFileFormat(j).equalsIgnoreCase(formattedFileFormat)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public int numberOfGroupPluginsForFileFormat(String fileFormat) {
		if(fileFormat == null) { return 0; }
		String formattedFileFormat = fileFormat.trim();
		if(formattedFileFormat.length() == 0) { return 0; }
		
		int numberOfGroupPlugins = 0;
		
		GroupPlugin groupPlugin = null;
		for(int i=0;i<m_plugins.size();i++) {
			if(!(m_plugins.elementAt(i) instanceof GroupPlugin)) { continue; }
			
			groupPlugin = (GroupPlugin) m_plugins.elementAt(i);
			
			for(int j=0;j<groupPlugin.numberOfSupportedGroupFileFormats();j++) {
				if(groupPlugin.getSupportedGroupFileFormat(j).equalsIgnoreCase(formattedFileFormat)) {
					numberOfGroupPlugins++;
				}
			}
		}
		return numberOfGroupPlugins;
	}
	
	public int indexOfFirstGroupPluginForFileFormat(String fileFormat) {
		if(fileFormat == null) { return -1; }
		String formattedFileFormat = fileFormat.trim();
		if(formattedFileFormat.length() == 0) { return -1; }
		
		GroupPlugin groupPlugin = null;
		for(int i=0;i<m_plugins.size();i++) {
			if(!(m_plugins.elementAt(i) instanceof GroupPlugin)) { continue; }
			
			groupPlugin = (GroupPlugin) m_plugins.elementAt(i);
			
			for(int j=0;j<groupPlugin.numberOfSupportedGroupFileFormats();j++) {
				if(groupPlugin.getSupportedGroupFileFormat(j).equalsIgnoreCase(formattedFileFormat)) {
					return i;
				}
			}
		}
		return -1;
	}
	
	public String getGroupPluginsAsStringExcludingFileFormat(String fileFormat) {
		if(fileFormat == null) { return null; }
		String formattedFileFormat = fileFormat.trim();
		if(formattedFileFormat.length() == 0) { return null; }
		
		Plugin plugin = null;
		GroupPlugin groupPlugin = null;
		String groupPluginsList = new String();
		
		for(int i=0;i<m_plugins.size();i++) {
			plugin = m_plugins.elementAt(i);
			
			if(plugin instanceof GroupPlugin) {
				groupPlugin = (GroupPlugin) plugin;
				
				if(groupPlugin.hasSupportedGroupFileFormat(formattedFileFormat)) {
					if(groupPluginsList.length() > 0) {
						groupPluginsList += ", ";
					}
					
					groupPluginsList += groupPlugin.getName();
				}
			}
		}
		
		return groupPluginsList;
	}
	
	public Vector<GroupPlugin> getGroupPluginsExcludingFileFormat(String fileFormat) {
		if(fileFormat == null) { return null; }
		String formattedFileFormat = fileFormat.trim();
		if(formattedFileFormat.length() == 0) { return null; }
		
		Plugin plugin = null;
		GroupPlugin groupPlugin = null;
		Vector<GroupPlugin> groupPlugins = new Vector<GroupPlugin>();
		
		for(int i=0;i<m_plugins.size();i++) {
			plugin = m_plugins.elementAt(i);
			
			if(plugin instanceof GroupPlugin) {
				groupPlugin = (GroupPlugin) plugin;
				
				if(groupPlugin.hasSupportedGroupFileFormat(formattedFileFormat)) {
					groupPlugins.add(groupPlugin);
				}
			}
		}
		
		return groupPlugins;
	}
	

	public GroupPlugin getPreferredGroupPluginPrompt(String fileFormat) {
		if(fileFormat == null) { return null; }
		
		Vector<GroupPlugin> plugins = getGroupPluginsForFileFormat(fileFormat);
		if(plugins == null || plugins.size() == 0) { return null; }
		
		GroupPlugin plugin = null;
		
		if(hasPreferredPluginForFileFormat(fileFormat, GroupPlugin.class)) {
			String preferredPluginName = getPreferredPluginForFileFormat(fileFormat, GroupPlugin.class);
			plugin = getPlugin(preferredPluginName, GroupPlugin.class);
			
			// if the preferred plugin is not loaded
			if(plugin == null) {
				// get a collection of all unloaded plugins
				VariableCollection unloadedPlugins = getUnloadedPlugins(new File(SettingsManager.instance.pluginDirectoryName));
				
				// get the info for the currently unloaded preferred plugin
				Variable preferredPluginInfo = unloadedPlugins.getVariable(preferredPluginName);
				
				// if the preferred plugin does not exist, run the plugin selection prompt
				if(preferredPluginInfo == null) {
					plugin = null;
				}
				// otherwise if the preferred plugin is currently unloaded, prompt the user to load it
				else {
					int choice = JOptionPane.showConfirmDialog(null, "Preferred plugin is not loaded. Would you like to load it or choose a different plugin?", "Plugin Not Loaded", JOptionPane.WARNING_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION);
					if(choice == JOptionPane.NO_OPTION || choice == JOptionPane.CANCEL_OPTION) { plugin = null; }
					else if(choice == JOptionPane.YES_OPTION) {
						if(!loadPlugin(preferredPluginInfo.getID(), preferredPluginInfo.getValue())) {
							plugin = null;
							
							String message = "Failed to load preferred plugin \"" + preferredPluginInfo.getID() + "\"!";
							
							SystemConsole.instance.writeLine(message);
							
							JOptionPane.showMessageDialog(null, message, "Plugin Loading Failed", JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			}
		}
		
		boolean preferredPluginSelected = plugin != null;
		
		if(plugin == null) {
			plugin = plugins.elementAt(0);
		}
		
		if(plugins.size() > 1 && !preferredPluginSelected) {
			int pluginIndex = -1;
			Object choices[] = plugins.toArray();
			Object value = JOptionPane.showInputDialog(null, "Found multiple plugins supporting this file format.\nChoose a plugin to process this file with:", "Choose Plugin", JOptionPane.QUESTION_MESSAGE, null, choices, choices[0]);
			if(value == null) { return null; }
			for(int i=0;i<choices.length;i++) {
				if(choices[i] == value) {
					pluginIndex = i;
					break;
				}
			}
			if(pluginIndex < 0 || pluginIndex >= plugins.size()) { return null; }
			
			plugin = plugins.elementAt(pluginIndex);
			
			String currentPreferredPluginName = getPreferredPluginForFileFormat(fileFormat, GroupPlugin.class);
			if(currentPreferredPluginName == null || !currentPreferredPluginName.equalsIgnoreCase(plugin.getName())) {
				int choice = JOptionPane.showConfirmDialog(null, "Would you like to set this plugin as your preferred plugin for the " + fileFormat + " file format?", "Set Preferred Plugin", JOptionPane.YES_NO_CANCEL_OPTION);
				if(choice == JOptionPane.YES_OPTION) {
					if(!setPreferredPluginForFileFormat(fileFormat, plugin.getName(), GroupPlugin.class)) {
						SystemConsole.instance.writeLine("Failed to set \"" + plugin.getName() + "\" as preferred plugin for " + fileFormat + " file format.");
					}
				}
			}
		}
		
		return plugin;
	}
	
}
