package group;

import java.util.*;
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
	
	public String getLoadedGroupPluginsAsStringExcludingFileFormat(String fileFormat) {
		if(fileFormat == null) { return null; }
		String formattedFileFormat = fileFormat.trim();
		if(formattedFileFormat.length() == 0) { return null; }
		
		Plugin plugin = null;
		GroupPlugin groupPlugin = null;
		String loadedGroupPluginsList = new String();
		
		for(int i=0;i<m_plugins.size();i++) {
			plugin = m_plugins.elementAt(i);
			
			if(plugin.isLoaded() && plugin instanceof GroupPlugin) {
				groupPlugin = (GroupPlugin) plugin;
				
				if(groupPlugin.hasSupportedGroupFileFormat(formattedFileFormat)) {
					if(loadedGroupPluginsList.length() > 0) {
						loadedGroupPluginsList += ", ";
					}
					
					loadedGroupPluginsList += groupPlugin.getName();
				}
			}
		}
		
		return loadedGroupPluginsList;
	}
	
	public Vector<GroupPlugin> getLoadedGroupPluginsExcludingFileFormat(String fileFormat) {
		if(fileFormat == null) { return null; }
		String formattedFileFormat = fileFormat.trim();
		if(formattedFileFormat.length() == 0) { return null; }
		
		Plugin plugin = null;
		GroupPlugin groupPlugin = null;
		Vector<GroupPlugin> loadedGroupPlugins = new Vector<GroupPlugin>();
		
		for(int i=0;i<m_plugins.size();i++) {
			plugin = m_plugins.elementAt(i);
			
			if(plugin.isLoaded() && plugin instanceof GroupPlugin) {
				groupPlugin = (GroupPlugin) plugin;
				
				if(groupPlugin.hasSupportedGroupFileFormat(formattedFileFormat)) {
					loadedGroupPlugins.add(groupPlugin);
				}
			}
		}
		
		return loadedGroupPlugins;
	}
	
}
