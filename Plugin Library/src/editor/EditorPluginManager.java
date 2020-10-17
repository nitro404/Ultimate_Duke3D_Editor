package editor;

import java.util.*;
import java.io.*;
import javax.swing.*;
import variable.*;
import settings.*;
import console.*;
import plugin.*;
import group.*;
import palette.*;

public class EditorPluginManager extends PluginManager {
	
	public static EditorPluginManager instance = null;
	
	public EditorPluginManager() {
		instance = this;

		addPluginType(GroupPlugin.PLUGIN_TYPE, GroupPlugin.class);
		addPluginType(GroupProcessorPlugin.PLUGIN_TYPE, GroupProcessorPlugin.class);
		addPluginType(PalettePlugin.PLUGIN_TYPE, PalettePlugin.class);
	}
	
	public Vector<FilePlugin> getPluginsForFileFormat(String fileFormat) {
		if(fileFormat == null) { return null; }
		String formattedFileFormat = fileFormat.trim();
		if(formattedFileFormat.isEmpty()) { return null; }
		
		FilePlugin filePlugin = null;
		Vector<FilePlugin> filePlugins = new Vector<FilePlugin>();
		for(int i=0;i<m_plugins.size();i++) {
			if(!(m_plugins.elementAt(i) instanceof FilePlugin)) { continue; }
			
			filePlugin = (FilePlugin) m_plugins.elementAt(i);
			
			for(int j=0;j<filePlugin.numberOfSupportedFileFormats();j++) {
				if(filePlugin.getSupportedFileFormat(j).equalsIgnoreCase(formattedFileFormat)) {
					filePlugins.add(filePlugin);
				}
			}
		}
		return filePlugins;
	}
	
	public boolean hasPluginForFileFormat(String fileFormat) {
		if(fileFormat == null) { return false; }
		String formattedFileFormat = fileFormat.trim();
		if(formattedFileFormat.isEmpty()) { return false; }
		
		FilePlugin filePlugin = null;
		for(int i=0;i<m_plugins.size();i++) {
			if(!(m_plugins.elementAt(i) instanceof FilePlugin)) { continue; }
			
			filePlugin = (FilePlugin) m_plugins.elementAt(i);
			
			for(int j=0;j<filePlugin.numberOfSupportedFileFormats();j++) {
				if(filePlugin.getSupportedFileFormat(j).equalsIgnoreCase(formattedFileFormat)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public int numberOfPluginsForFileFormat(String fileFormat) {
		if(fileFormat == null) { return 0; }
		String formattedFileFormat = fileFormat.trim();
		if(formattedFileFormat.isEmpty()) { return 0; }
		
		int numberOfPlugins = 0;
		
		FilePlugin filePlugin = null;
		for(int i=0;i<m_plugins.size();i++) {
			if(!(m_plugins.elementAt(i) instanceof FilePlugin)) { continue; }
			
			filePlugin = (FilePlugin) m_plugins.elementAt(i);
			
			for(int j=0;j<filePlugin.numberOfSupportedFileFormats();j++) {
				if(filePlugin.getSupportedFileFormat(j).equalsIgnoreCase(formattedFileFormat)) {
					numberOfPlugins++;
				}
			}
		}
		return numberOfPlugins;
	}
	
	public int indexOfFirstPluginForFileFormat(String fileFormat) {
		if(fileFormat == null) { return -1; }
		String formattedFileFormat = fileFormat.trim();
		if(formattedFileFormat.isEmpty()) { return -1; }
		
		FilePlugin filePlugin = null;
		for(int i=0;i<m_plugins.size();i++) {
			if(!(m_plugins.elementAt(i) instanceof FilePlugin)) { continue; }
			
			filePlugin = (FilePlugin) m_plugins.elementAt(i);
			
			for(int j=0;j<filePlugin.numberOfSupportedFileFormats();j++) {
				if(filePlugin.getSupportedFileFormat(j).equalsIgnoreCase(formattedFileFormat)) {
					return i;
				}
			}
		}
		return -1;
	}
	
	public String getPluginsAsStringExcludingFileFormat(String fileFormat) {
		if(fileFormat == null) { return null; }
		String formattedFileFormat = fileFormat.trim();
		if(formattedFileFormat.isEmpty()) { return null; }
		
		Plugin plugin = null;
		FilePlugin filePlugin = null;
		String filePluginsList = new String();
		
		for(int i=0;i<m_plugins.size();i++) {
			plugin = m_plugins.elementAt(i);
			
			if(plugin instanceof FilePlugin) {
				filePlugin = (FilePlugin) plugin;
				
				if(!filePlugin.hasSupportedFileFormat(formattedFileFormat)) {
					if(!filePluginsList.isEmpty()) {
						filePluginsList += ", ";
					}
					
					filePluginsList += filePlugin.getName();
				}
			}
		}
		
		return filePluginsList;
	}
	
	public Vector<FilePlugin> getPluginsExcludingFileFormat(String fileFormat) {
		if(fileFormat == null) { return null; }
		String formattedFileFormat = fileFormat.trim();
		if(formattedFileFormat.isEmpty()) { return null; }
		
		Plugin plugin = null;
		FilePlugin filePlugin = null;
		Vector<FilePlugin> filePlugins = new Vector<FilePlugin>();
		
		for(int i=0;i<m_plugins.size();i++) {
			plugin = m_plugins.elementAt(i);
			
			if(plugin instanceof FilePlugin) {
				filePlugin = (FilePlugin) plugin;
				
				if(!filePlugin.hasSupportedFileFormat(formattedFileFormat)) {
					filePlugins.add(filePlugin);
				}
			}
		}
		
		return filePlugins;
	}
	
	public Vector<String> getSupportedFileFormats() {
		Plugin plugin = null;
		FilePlugin filePlugin = null;
		String fileFormat = null;
		boolean duplicateFileFormat = false;
		Vector<String> fileFormats = new Vector<String>();
		
		for(int i=0;i<m_plugins.size();i++) {
			plugin = m_plugins.elementAt(i);
			
			if(plugin instanceof FilePlugin) {
				filePlugin = (FilePlugin) plugin;
				
				for(int j=0;j<filePlugin.numberOfSupportedFileFormats();j++) {
					fileFormat = filePlugin.getSupportedFileFormat(j).toUpperCase();
					duplicateFileFormat = false;
					
					for(int k=0;k<fileFormats.size();k++) {
						if(fileFormats.elementAt(k).equalsIgnoreCase(fileFormat)) {
							duplicateFileFormat = true;
							break;
						}
					}
					
					if(duplicateFileFormat) { continue; }
					
					fileFormats.add(fileFormat);
				}
			}
		}
		
		return fileFormats;
	}
	
	public Vector<String> getSupportedAndPreferredFileFormats() {
		Vector<String> fileFormats = new Vector<String>();
		
		Vector<String> supportedFileFormats = getSupportedFileFormats();
		
		if(supportedFileFormats != null) {
			for(int i=0;i<supportedFileFormats.size();i++) {
				fileFormats.add(supportedFileFormats.elementAt(i));
			}
		}
		
		Collection<String> preferredFileFormats = getPreferredFileFormats(FilePlugin.class);
		
		boolean duplicateFileFormat = false;
		if(preferredFileFormats != null) {
			for(String preferredFileFormat : preferredFileFormats) {
				duplicateFileFormat = false;
				
				for(int j=0;j<fileFormats.size();j++) {
					if(fileFormats.elementAt(j).equalsIgnoreCase(preferredFileFormat)) {
						duplicateFileFormat = true;
					}
				}
				
				if(duplicateFileFormat) { continue; }
				
				fileFormats.add(preferredFileFormat);
			}
		}
		
		return fileFormats;
	}
	
	public FilePlugin getPreferredPluginPrompt(String fileFormat) {
		if(fileFormat == null) { return null; }
		
		Vector<FilePlugin> plugins = getPluginsForFileFormat(fileFormat);
		if(plugins == null || plugins.isEmpty()) { return null; }
		
		FilePlugin plugin = null;

		if(hasPreferredPluginForFileFormat(fileFormat, FilePlugin.class)) {
			String preferredPluginName = getPreferredPluginForFileFormat(fileFormat, FilePlugin.class);
			plugin = getPlugin(preferredPluginName, FilePlugin.class);
			
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

			String currentPreferredPluginName = getPreferredPluginForFileFormat(fileFormat, FilePlugin.class);
			if(currentPreferredPluginName == null || !currentPreferredPluginName.equalsIgnoreCase(plugin.getName())) {
				int choice = JOptionPane.showConfirmDialog(null, "Would you like to set this plugin as your preferred plugin for the " + fileFormat + " file format?", "Set Preferred Plugin", JOptionPane.YES_NO_CANCEL_OPTION);
				if(choice == JOptionPane.YES_OPTION) {
					if(!setPreferredPluginForFileFormat(fileFormat, plugin.getName(), FilePlugin.class)) {
						SystemConsole.instance.writeLine("Failed to set \"" + plugin.getName() + "\" as preferred plugin for " + fileFormat + " file format.");
					}
				}
			}
		}
		
		return plugin;
	}
	

	public Vector<GroupPlugin> getGroupPluginsForFileFormat(String fileFormat) {
		if(fileFormat == null) { return null; }
		String formattedFileFormat = fileFormat.trim();
		if(formattedFileFormat.isEmpty()) { return null; }
		
		GroupPlugin groupPlugin = null;
		Vector<GroupPlugin> groupPlugins = new Vector<GroupPlugin>();
		for(int i=0;i<m_plugins.size();i++) {
			if(!(m_plugins.elementAt(i) instanceof GroupPlugin)) { continue; }
			
			groupPlugin = (GroupPlugin) m_plugins.elementAt(i);
			
			for(int j=0;j<groupPlugin.numberOfSupportedFileFormats();j++) {
				if(groupPlugin.getSupportedFileFormat(j).equalsIgnoreCase(formattedFileFormat)) {
					groupPlugins.add(groupPlugin);
				}
			}
		}
		return groupPlugins;
	}
	
	public boolean hasGroupPluginForFileFormat(String fileFormat) {
		if(fileFormat == null) { return false; }
		String formattedFileFormat = fileFormat.trim();
		if(formattedFileFormat.isEmpty()) { return false; }
		
		GroupPlugin groupPlugin = null;
		for(int i=0;i<m_plugins.size();i++) {
			if(!(m_plugins.elementAt(i) instanceof GroupPlugin)) { continue; }
			
			groupPlugin = (GroupPlugin) m_plugins.elementAt(i);
			
			for(int j=0;j<groupPlugin.numberOfSupportedFileFormats();j++) {
				if(groupPlugin.getSupportedFileFormat(j).equalsIgnoreCase(formattedFileFormat)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public int numberOfGroupPluginsForFileFormat(String fileFormat) {
		if(fileFormat == null) { return 0; }
		String formattedFileFormat = fileFormat.trim();
		if(formattedFileFormat.isEmpty()) { return 0; }
		
		int numberOfGroupPlugins = 0;
		
		GroupPlugin groupPlugin = null;
		for(int i=0;i<m_plugins.size();i++) {
			if(!(m_plugins.elementAt(i) instanceof GroupPlugin)) { continue; }
			
			groupPlugin = (GroupPlugin) m_plugins.elementAt(i);
			
			for(int j=0;j<groupPlugin.numberOfSupportedFileFormats();j++) {
				if(groupPlugin.getSupportedFileFormat(j).equalsIgnoreCase(formattedFileFormat)) {
					numberOfGroupPlugins++;
				}
			}
		}
		return numberOfGroupPlugins;
	}
	
	public int indexOfFirstGroupPluginForFileFormat(String fileFormat) {
		if(fileFormat == null) { return -1; }
		String formattedFileFormat = fileFormat.trim();
		if(formattedFileFormat.isEmpty()) { return -1; }
		
		GroupPlugin groupPlugin = null;
		for(int i=0;i<m_plugins.size();i++) {
			if(!(m_plugins.elementAt(i) instanceof GroupPlugin)) { continue; }
			
			groupPlugin = (GroupPlugin) m_plugins.elementAt(i);
			
			for(int j=0;j<groupPlugin.numberOfSupportedFileFormats();j++) {
				if(groupPlugin.getSupportedFileFormat(j).equalsIgnoreCase(formattedFileFormat)) {
					return i;
				}
			}
		}
		return -1;
	}
	
	public String getGroupPluginsAsStringExcludingFileFormat(String fileFormat) {
		if(fileFormat == null) { return null; }
		String formattedFileFormat = fileFormat.trim();
		if(formattedFileFormat.isEmpty()) { return null; }
		
		Plugin plugin = null;
		GroupPlugin groupPlugin = null;
		String groupPluginsList = new String();
		
		for(int i=0;i<m_plugins.size();i++) {
			plugin = m_plugins.elementAt(i);
			
			if(plugin instanceof GroupPlugin) {
				groupPlugin = (GroupPlugin) plugin;
				
				if(!groupPlugin.hasSupportedFileFormat(formattedFileFormat)) {
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
		if(formattedFileFormat.isEmpty()) { return null; }
		
		Plugin plugin = null;
		GroupPlugin groupPlugin = null;
		Vector<GroupPlugin> groupPlugins = new Vector<GroupPlugin>();
		
		for(int i=0;i<m_plugins.size();i++) {
			plugin = m_plugins.elementAt(i);
			
			if(plugin instanceof GroupPlugin) {
				groupPlugin = (GroupPlugin) plugin;
				
				if(!groupPlugin.hasSupportedFileFormat(formattedFileFormat)) {
					groupPlugins.add(groupPlugin);
				}
			}
		}
		
		return groupPlugins;
	}
	
	public Vector<String> getSupportedGroupFileFormats() {
		Plugin plugin = null;
		GroupPlugin groupPlugin = null;
		String fileFormat = null;
		boolean duplicateFileFormat = false;
		Vector<String> fileFormats = new Vector<String>();
		
		for(int i=0;i<m_plugins.size();i++) {
			plugin = m_plugins.elementAt(i);
			
			if(plugin instanceof GroupPlugin) {
				groupPlugin = (GroupPlugin) plugin;
				
				for(int j=0;j<groupPlugin.numberOfSupportedFileFormats();j++) {
					fileFormat = groupPlugin.getSupportedFileFormat(j).toUpperCase();
					duplicateFileFormat = false;
					
					for(int k=0;k<fileFormats.size();k++) {
						if(fileFormats.elementAt(k).equalsIgnoreCase(fileFormat)) {
							duplicateFileFormat = true;
							break;
						}
					}
					
					if(duplicateFileFormat) { continue; }
					
					fileFormats.add(fileFormat);
				}
			}
		}
		
		return fileFormats;
	}
	
	public Vector<String> getSupportedAndPreferredGroupFileFormats() {
		Vector<String> fileFormats = new Vector<String>();
		
		Vector<String> supportedFileFormats = getSupportedGroupFileFormats();
		
		if(supportedFileFormats != null) {
			for(int i=0;i<supportedFileFormats.size();i++) {
				fileFormats.add(supportedFileFormats.elementAt(i));
			}
		}
		
		Collection<String> preferredFileFormats = getPreferredFileFormats(GroupPlugin.class);
		
		boolean duplicateFileFormat = false;
		if(preferredFileFormats != null) {
			for(String preferredFileFormat : preferredFileFormats) {
				duplicateFileFormat = false;
				
				for(int j=0;j<fileFormats.size();j++) {
					if(fileFormats.elementAt(j).equalsIgnoreCase(preferredFileFormat)) {
						duplicateFileFormat = true;
					}
				}
				
				if(duplicateFileFormat) { continue; }
				
				fileFormats.add(preferredFileFormat);
			}
		}
		
		return fileFormats;
	}
	
	public Vector<PalettePlugin> getPalettePluginsForFileFormat(String fileFormat) {
		if(fileFormat == null) { return null; }
		String formattedFileFormat = fileFormat.trim();
		if(formattedFileFormat.isEmpty()) { return null; }
		
		PalettePlugin palettePlugin = null;
		Vector<PalettePlugin> palettePlugins = new Vector<PalettePlugin>();
		for(int i=0;i<m_plugins.size();i++) {
			if(!(m_plugins.elementAt(i) instanceof PalettePlugin)) { continue; }
			
			palettePlugin = (PalettePlugin) m_plugins.elementAt(i);
			
			for(int j=0;j<palettePlugin.numberOfSupportedFileFormats();j++) {
				if(palettePlugin.getSupportedFileFormat(j).equalsIgnoreCase(formattedFileFormat)) {
					palettePlugins.add(palettePlugin);
				}
			}
		}
		return palettePlugins;
	}
	
	public boolean hasPalettePluginForFileFormat(String fileFormat) {
		if(fileFormat == null) { return false; }
		String formattedFileFormat = fileFormat.trim();
		if(formattedFileFormat.isEmpty()) { return false; }
		
		PalettePlugin palettePlugin = null;
		for(int i=0;i<m_plugins.size();i++) {
			if(!(m_plugins.elementAt(i) instanceof PalettePlugin)) { continue; }
			
			palettePlugin = (PalettePlugin) m_plugins.elementAt(i);
			
			for(int j=0;j<palettePlugin.numberOfSupportedFileFormats();j++) {
				if(palettePlugin.getSupportedFileFormat(j).equalsIgnoreCase(formattedFileFormat)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public int numberOfPalettePluginsForFileFormat(String fileFormat) {
		if(fileFormat == null) { return 0; }
		String formattedFileFormat = fileFormat.trim();
		if(formattedFileFormat.isEmpty()) { return 0; }
		
		int numberOfPalettePlugins = 0;
		
		PalettePlugin palettePlugin = null;
		for(int i=0;i<m_plugins.size();i++) {
			if(!(m_plugins.elementAt(i) instanceof PalettePlugin)) { continue; }
			
			palettePlugin = (PalettePlugin) m_plugins.elementAt(i);
			
			for(int j=0;j<palettePlugin.numberOfSupportedFileFormats();j++) {
				if(palettePlugin.getSupportedFileFormat(j).equalsIgnoreCase(formattedFileFormat)) {
					numberOfPalettePlugins++;
				}
			}
		}
		return numberOfPalettePlugins;
	}
	
	public int indexOfFirstPalettePluginForFileFormat(String fileFormat) {
		if(fileFormat == null) { return -1; }
		String formattedFileFormat = fileFormat.trim();
		if(formattedFileFormat.isEmpty()) { return -1; }
		
		PalettePlugin palettePlugin = null;
		for(int i=0;i<m_plugins.size();i++) {
			if(!(m_plugins.elementAt(i) instanceof PalettePlugin)) { continue; }
			
			palettePlugin = (PalettePlugin) m_plugins.elementAt(i);
			
			for(int j=0;j<palettePlugin.numberOfSupportedFileFormats();j++) {
				if(palettePlugin.getSupportedFileFormat(j).equalsIgnoreCase(formattedFileFormat)) {
					return i;
				}
			}
		}
		return -1;
	}
	
	public String getPalettePluginsAsStringExcludingFileFormat(String fileFormat) {
		if(fileFormat == null) { return null; }
		String formattedFileFormat = fileFormat.trim();
		if(formattedFileFormat.isEmpty()) { return null; }
		
		Plugin plugin = null;
		PalettePlugin palettePlugin = null;
		String palettePluginsList = new String();
		
		for(int i=0;i<m_plugins.size();i++) {
			plugin = m_plugins.elementAt(i);
			
			if(plugin instanceof PalettePlugin) {
				palettePlugin = (PalettePlugin) plugin;
				
				if(!palettePlugin.hasSupportedFileFormat(formattedFileFormat)) {
					if(palettePluginsList.length() > 0) {
						palettePluginsList += ", ";
					}
					
					palettePluginsList += palettePlugin.getName();
				}
			}
		}
		
		return palettePluginsList;
	}
	
	public Vector<PalettePlugin> getPalettePluginsExcludingFileFormat(String fileFormat) {
		if(fileFormat == null) { return null; }
		String formattedFileFormat = fileFormat.trim();
		if(formattedFileFormat.isEmpty()) { return null; }
		
		Plugin plugin = null;
		PalettePlugin palettePlugin = null;
		Vector<PalettePlugin> palettePlugins = new Vector<PalettePlugin>();
		
		for(int i=0;i<m_plugins.size();i++) {
			plugin = m_plugins.elementAt(i);
			
			if(plugin instanceof PalettePlugin) {
				palettePlugin = (PalettePlugin) plugin;
				
				if(!palettePlugin.hasSupportedFileFormat(formattedFileFormat)) {
					palettePlugins.add(palettePlugin);
				}
			}
		}
		
		return palettePlugins;
	}
	
	public Vector<String> getSupportedPaletteFileFormats() {
		Plugin plugin = null;
		PalettePlugin palettePlugin = null;
		String fileFormat = null;
		boolean duplicateFileFormat = false;
		Vector<String> fileFormats = new Vector<String>();
		
		for(int i=0;i<m_plugins.size();i++) {
			plugin = m_plugins.elementAt(i);
			
			if(plugin instanceof PalettePlugin) {
				palettePlugin = (PalettePlugin) plugin;
				
				for(int j=0;j<palettePlugin.numberOfSupportedFileFormats();j++) {
					fileFormat = palettePlugin.getSupportedFileFormat(j).toUpperCase();
					duplicateFileFormat = false;
					
					for(int k=0;k<fileFormats.size();k++) {
						if(fileFormats.elementAt(k).equalsIgnoreCase(fileFormat)) {
							duplicateFileFormat = true;
							break;
						}
					}
					
					if(duplicateFileFormat) { continue; }
					
					fileFormats.add(fileFormat);
				}
			}
		}
		
		return fileFormats;
	}
	
	public Vector<String> getSupportedAndPreferredPaletteFileFormats() {
		Vector<String> fileFormats = new Vector<String>();
		
		Vector<String> supportedFileFormats = getSupportedPaletteFileFormats();
		
		if(supportedFileFormats != null) {
			for(int i=0;i<supportedFileFormats.size();i++) {
				fileFormats.add(supportedFileFormats.elementAt(i));
			}
		}
		
		Collection<String> preferredFileFormats = getPreferredFileFormats(PalettePlugin.class);
		
		boolean duplicateFileFormat = false;
		if(preferredFileFormats != null) {
			for(String preferredFileFormat : preferredFileFormats) {
				duplicateFileFormat = false;
				
				for(int j=0;j<fileFormats.size();j++) {
					if(fileFormats.elementAt(j).equalsIgnoreCase(preferredFileFormat)) {
						duplicateFileFormat = true;
					}
				}
				
				if(duplicateFileFormat) { continue; }
				
				fileFormats.add(preferredFileFormat);
			}
		}
		
		return fileFormats;
	}
	
}
