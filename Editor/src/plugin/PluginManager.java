package plugin;

import java.util.*;
import java.io.*;
import javax.xml.stream.*;
import javax.xml.stream.events.*;
import javax.swing.*;
import exception.*;
import settings.*;
import utilities.*;
import variable.*;
import console.*;
import gui.*;

public class PluginManager {
	
	protected Vector<Plugin> m_plugins;
	protected HashMap<String, PluginType> m_pluginTypes;
	protected HashMap<String, HashMap<String, String>> m_preferredPlugins;
	
	protected ProgressDialog m_progressDialog;
	
	protected JFrame m_targetFrame;
	
	protected boolean m_initialized;
	
	public PluginManager() {
		m_plugins = new Vector<Plugin>();
		m_pluginTypes = new HashMap<String, PluginType>();
		m_preferredPlugins = new HashMap<String, HashMap<String, String>>();
		
		m_progressDialog = null;
		m_targetFrame = null;
		
		m_initialized = false;
	}
	
	public boolean initialize(JFrame targetFrame) {
		if(m_initialized) { return true; }
		
		if(targetFrame == null) { return false; }
		
		m_targetFrame = targetFrame;
		
		m_progressDialog = new ProgressDialog(m_targetFrame);
		
		m_initialized = true;
		
		return true;
	}
	
	public int numberOfPluginTypes() {
		return m_pluginTypes.size();
	}
	
	public boolean hasPluginType(String pluginType) {
		if(pluginType == null) { return false; }
		
		String formattedPluginType = pluginType.trim().toUpperCase();
		if(formattedPluginType.length() == 0) { return false; }
		
		return m_pluginTypes.containsKey(formattedPluginType);
	}

	public boolean hasPluginType(PluginType pluginType) {
		if(!PluginType.isValid(pluginType)) { return false; }
		
		return m_pluginTypes.containsKey(pluginType.getPluginType());
	}
	
	public <T extends Plugin> boolean hasPluginTypeWithClass(Class<T> pluginClass) {
		if(pluginClass == null) { return false; }
		
		for(String pluginTypeName : m_pluginTypes.keySet()) {
			if(m_pluginTypes.get(pluginTypeName).getPluginClass().equals(pluginClass)) {
				return true;
			}
		}
		return false;
	}

	public boolean hasPluginTypeWithClass(String pluginClassName) {
		if(pluginClassName == null) { return false; }
		
		String formattedPluginClassName = pluginClassName.trim();
		if(formattedPluginClassName.length() == 0) { return false; }
		
		for(String pluginTypeName : m_pluginTypes.keySet()) {
			if(m_pluginTypes.get(pluginTypeName).getPluginClass().getName().equals(pluginClassName)) {
				return true;
			}
		}
		return false;
	}
	
	public int indexOfPluginType(String pluginType) {
		if(pluginType == null) { return -1; }
		
		String formattedPluginType = pluginType.trim().toUpperCase();
		if(formattedPluginType.length() == 0) { return -1; }
		
		int i = 0;
		for(String currentPluginType : m_pluginTypes.keySet()) {
			if(pluginType.equalsIgnoreCase(currentPluginType)) {
				return i;
			}
			i++;
		}
		return -1;
	}
	
	public int indexOfPluginType(PluginType pluginType) {
		if(!PluginType.isValid(pluginType)) { return -1; }
		
		int i = 0;
		for(PluginType currentPluginType : m_pluginTypes.values()) {
			if(pluginType.equals(currentPluginType)) {
				return i;
			}
			i++;
		}
		return -1;
	}
	
	public <T extends Plugin> int indexOfPluginTypeWithClass(Class<T> pluginClass) {
		if(pluginClass == null) { return -1; }
		
		int i = 0;
		for(String pluginTypeName : m_pluginTypes.keySet()) {
			if(m_pluginTypes.get(pluginTypeName).getPluginClass().equals(pluginClass)) {
				return i;
			}
			i++;
		}
		return -1;
	}
	
	public int indexOfPluginTypeWithClass(String pluginClassName) {
		if(pluginClassName == null) { return -1; }

		String formattedPluginClassName = pluginClassName.trim();
		if(formattedPluginClassName.length() == 0) { return -1; }
		
		int i = 0;
		for(String pluginTypeName : m_pluginTypes.keySet()) {
			if(m_pluginTypes.get(pluginTypeName).getPluginClass().getName().equals(formattedPluginClassName)) {
				return i;
			}
			i++;
		}
		return -1;
	}
	
	public PluginType getPluginType(int index) {
		if(index < 0 || index >= m_pluginTypes.keySet().size()) { return null; }
		
		int i = 0;
		for(String currentPluginType : m_pluginTypes.keySet()) {
			if(i == index) {
				return m_pluginTypes.get(currentPluginType);
			}
			i++;
		}
		return null;
	}
	
	public PluginType getPluginType(String pluginType) {
		if(pluginType == null) { return null; }
		
		String formattedPluginType = pluginType.trim().toUpperCase();
		if(formattedPluginType.length() == 0) { return null; }
		
		return m_pluginTypes.get(formattedPluginType);
	}
	
	public <T extends Plugin> PluginType getPluginTypeWithClass(Class<T> pluginClass) {
		if(pluginClass == null) { return null; }
		
		PluginType pluginType = null;
		for(String pluginTypeName : m_pluginTypes.keySet()) {
			pluginType = m_pluginTypes.get(pluginTypeName);
			
			if(pluginType.getPluginClass().equals(pluginClass)) {
				return pluginType;
			}
		}
		return null;
	}

	public PluginType getPluginTypeWithClass(String pluginClassName) {
		if(pluginClassName == null) { return null; }

		String formattedPluginClassName = pluginClassName.trim();
		if(formattedPluginClassName.length() == 0) { return null; }
		
		PluginType pluginType = null;
		for(String pluginTypeName : m_pluginTypes.keySet()) {
			pluginType = m_pluginTypes.get(pluginTypeName);
			
			if(pluginType.getPluginClass().getName().equals(formattedPluginClassName)) {
				return pluginType;
			}
		}
		return null;
	}
	
	public boolean addPluginType(String pluginType, Class<?> pluginClass) {
		return addPluginType(new PluginType(pluginType, pluginClass), true);
	}

	public boolean addPluginType(String pluginType, Class<?> pluginClass, boolean replace) {
		return addPluginType(new PluginType(pluginType, pluginClass), replace);
	}
	
	public boolean addPluginType(PluginType pluginType) {
		return addPluginType(pluginType, true);
	}
	
	public boolean addPluginType(PluginType pluginType, boolean replace) {
		if(!PluginType.isValid(pluginType)) { return false; }
		
		String formattedPluginType = pluginType.getPluginType().trim().toUpperCase();
		if(formattedPluginType.length() == 0) { return false; }
		
		if(m_pluginTypes.containsKey(formattedPluginType) && !replace) { return false; }
		
		m_pluginTypes.put(formattedPluginType, pluginType);
		
		return true;
	}
	
	public boolean removePluginType(int index) {
		if(index < 0 || index >= m_pluginTypes.keySet().size()) { return false; }
		
		int i = 0;
		for(String currentPluginType : m_pluginTypes.keySet()) {
			if(i == index) {
				m_pluginTypes.remove(currentPluginType);
				return true;
			}
			i++;
		}
		return false;
	}
	
	public boolean removePluginType(String pluginType) {
		if(pluginType == null) { return false; }
		
		String formattedPluginType = pluginType.trim().toUpperCase();
		if(formattedPluginType.length() == 0) { return false; }
		
		if(!m_pluginTypes.containsKey(formattedPluginType)) { return false; }
		
		m_pluginTypes.remove(formattedPluginType);
		
		return true;
	}

	public boolean removePluginType(PluginType pluginType) {
		if(!PluginType.isValid(pluginType) || !m_pluginTypes.containsKey(pluginType.getPluginType())) { return false; }
		
		m_pluginTypes.remove(pluginType.getPluginType());
		
		return true;
	}
	
	public <T extends Plugin> boolean removePluginTypeWithClass(Class<T> pluginClass) {
		if(pluginClass == null) { return false; }
		
		for(String pluginTypeName : m_pluginTypes.keySet()) {
			if(m_pluginTypes.get(pluginTypeName).getPluginClass().equals(pluginClass)) {
				m_pluginTypes.remove(pluginTypeName);
				return true;
			}
		}
		return false;
	}

	public boolean removePluginTypeWithClass(String pluginClassName) {
		if(pluginClassName == null) { return false; }

		String formattedPluginClassName = pluginClassName.trim();
		if(formattedPluginClassName.length() == 0) { return false; }
		
		for(String pluginTypeName : m_pluginTypes.keySet()) {
			if(m_pluginTypes.get(pluginTypeName).getPluginClass().getName().equals(formattedPluginClassName)) {
				m_pluginTypes.remove(pluginTypeName);
				return true;
			}
		}
		return false;
	}
	
	public void clearPluginTypes() {
		m_pluginTypes.clear();
	}

	public <T extends Plugin> int numberOfPreferredPlugins(Class<T> pluginClass) {
		if(pluginClass == null) { return 0; }
		
		return numberOfPreferredPlugins(pluginClass.getName());
	}
	
	public int numberOfPreferredPlugins(String pluginClassName) {
		if(pluginClassName == null) { return 0; }
		
		PluginType pluginType = getPluginTypeWithClass(pluginClassName);
		if(pluginType == null) { return 0; }
		
		HashMap<String, String> preferredPluginsForPluginType = m_preferredPlugins.get(pluginType.getPluginType().toUpperCase());
		if(preferredPluginsForPluginType == null) { return 0; }
		
		return preferredPluginsForPluginType.keySet().size();
	}
	
	public boolean hasPreferredPlugin(String pluginName) {
		if(pluginName == null) { return false; }
		
		String formattedPluginName = pluginName.trim();
		if(formattedPluginName.length() == 0) { return false; }
		
		HashMap<String, String> preferredPluginsForPluginType = null;
		for(String pluginTypeName : m_preferredPlugins.keySet()) {
			preferredPluginsForPluginType = m_preferredPlugins.get(pluginTypeName);
			
			for(String extension : preferredPluginsForPluginType.keySet()) {
				if(preferredPluginsForPluginType.get(extension).equalsIgnoreCase(formattedPluginName)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public <T extends Plugin> boolean hasPreferredPluginForFileFormat(String fileFormat, Class<T> pluginClass) {
		if(fileFormat == null || pluginClass == null) { return false; }
		
		return hasPreferredPluginForFileFormat(fileFormat, pluginClass.getName());
	}
	
	public boolean hasPreferredPluginForFileFormat(String fileFormat, String pluginClassName) {
		if(fileFormat == null || pluginClassName == null) { return false; }
		
		String formattedFileFormat = fileFormat.trim().toUpperCase();
		if(formattedFileFormat.length() == 0) { return false; }
		
		PluginType pluginType = getPluginTypeWithClass(pluginClassName);
		if(pluginType == null) { return false; }
		
		HashMap<String, String> preferredPluginsForPluginType = m_preferredPlugins.get(pluginType.getPluginType().toUpperCase());
		if(preferredPluginsForPluginType == null) { return false; }
		
		return preferredPluginsForPluginType.containsKey(formattedFileFormat);
	}

	public <T extends Plugin> int indexOfPreferredPluginForFileFormat(String fileFormat, Class<T> pluginClass) {
		if(fileFormat == null || pluginClass == null) { return -1; }
		
		return indexOfPreferredPluginForFileFormat(fileFormat, pluginClass.getName());
	}
	
	public int indexOfPreferredPluginForFileFormat(String fileFormat, String pluginClassName) {
		if(fileFormat == null || pluginClassName == null) { return -1; }
		
		String formattedFileFormat = fileFormat.trim().toUpperCase();
		if(formattedFileFormat.length() == 0) { return -1; }
		
		PluginType pluginType = getPluginTypeWithClass(pluginClassName);
		if(pluginType == null) { return -1; }
		
		HashMap<String, String> preferredPluginsForPluginType = m_preferredPlugins.get(pluginType.getPluginType().toUpperCase());
		if(preferredPluginsForPluginType == null) { return -1; }
		
		int i = 0;
		for(String extension : preferredPluginsForPluginType.keySet()) {
			if(extension.contentEquals(formattedFileFormat)) {
				return i;
			}
			i++;
		}
		
		return -1;
	}
	
	public <T extends Plugin> Collection<String> getPreferredFileFormats(Class<T> pluginClass) {
		if(pluginClass == null) { return null; }
		
		return getPreferredFileFormats(pluginClass.getName());
	}
	
	public Collection<String> getPreferredFileFormats(String pluginClassName) {
		if(pluginClassName == null) { return null; }
		
		PluginType pluginType = getPluginTypeWithClass(pluginClassName);
		if(pluginType == null) { return null; }
		
		HashMap<String, String> preferredPluginsForPluginType = m_preferredPlugins.get(pluginType.getPluginType().toUpperCase());
		if(preferredPluginsForPluginType == null) { return null; }
		
		return preferredPluginsForPluginType.keySet();
	}
	
	public <T extends Plugin> String getPreferredPluginForFileFormat(String fileFormat, Class<T> pluginClass) {
		if(fileFormat == null || pluginClass == null) { return null; }
		
		return getPreferredPluginForFileFormat(fileFormat, pluginClass.getName());
	}
	
	public String getPreferredPluginForFileFormat(String fileFormat, String pluginClassName) {
		if(fileFormat == null || pluginClassName == null) { return null; }
		
		String formattedFileFormat = fileFormat.trim().toUpperCase();
		if(formattedFileFormat.length() == 0) { return null; }
		
		PluginType pluginType = getPluginTypeWithClass(pluginClassName);
		if(pluginType == null) { return null; }
		
		HashMap<String, String> preferredPluginsForPluginType = m_preferredPlugins.get(pluginType.getPluginType().toUpperCase());
		if(preferredPluginsForPluginType == null) { return null; }
		
		return preferredPluginsForPluginType.get(formattedFileFormat);
	}
	
	public <T extends Plugin> HashMap<String, String> getPreferredPluginsForPluginType(Class<T> pluginClass) {
		if(pluginClass == null) { return null; }
		
		return getPreferredPluginsForPluginType(pluginClass.getName());
	}
	
	public HashMap<String, String> getPreferredPluginsForPluginType(String pluginClassName) {
		if(pluginClassName == null) { return null; }
		
		PluginType pluginType = getPluginTypeWithClass(pluginClassName);
		if(pluginType == null) { return null; }
		
 		return m_preferredPlugins.get(pluginType.getPluginType().toUpperCase());
	}

	public VariableCollection getPreferredPluginsAsVariableCollection() {
		VariableCollection variables = new VariableCollection();
		
		PluginType pluginType = null;
		HashMap<String, String> preferredPluginsForPluginType = null;
		for(String pluginTypeName : m_preferredPlugins.keySet()) {
			preferredPluginsForPluginType = m_preferredPlugins.get(pluginTypeName);
			
			pluginType = getPluginType(pluginTypeName);
			if(pluginType == null) {
				SystemConsole.instance.writeLine("Unexpected missing plugin type: \"" + pluginTypeName + "\".");
				continue;
			}

			for(String extension : preferredPluginsForPluginType.keySet()) {
				variables.addVariable(extension, preferredPluginsForPluginType.get(extension), pluginType.getPluginType());
			}
		}
		
		return variables;
	}
	
	public <T extends Plugin> boolean setPreferredPluginForFileFormat(String fileFormat, String pluginName, Class<T> pluginClass) {
		if(fileFormat == null || pluginName == null || pluginClass == null) { return false; }
		
		return setPreferredPluginForFileFormat(fileFormat, pluginName, pluginClass.getName());
	}
	
	public boolean setPreferredPluginForFileFormat(String fileFormat, String pluginName, String pluginClassName) {
		if(fileFormat == null || pluginName == null || pluginClassName == null) { return false; }
		
		String formattedFileFormat = fileFormat.trim().toUpperCase();
		if(formattedFileFormat.length() == 0) { return false; }
		
		PluginType pluginType = getPluginTypeWithClass(pluginClassName);
		if(pluginType == null) { return false; }
		
		HashMap<String, String> preferredPluginsForPluginType = m_preferredPlugins.get(pluginType.getPluginType().toUpperCase());
		if(preferredPluginsForPluginType == null) {
			preferredPluginsForPluginType = new HashMap<String, String>();
			m_preferredPlugins.put(pluginType.getPluginType().toUpperCase(), preferredPluginsForPluginType);
		}
		
		preferredPluginsForPluginType.put(formattedFileFormat, pluginName);
		
		return true;
	}
	
	public boolean setPreferredPlugins(VariableCollection variables) {
		if(variables == null) { return false; }
		
		return setPreferredPlugins(variables, true);
	}
	
	public boolean setPreferredPlugins(VariableCollection variables, boolean clear) {
		if(variables == null) { return false; }
		
		if(clear) {
			clearPreferredPlugins();
		}
		
		String formattedPluginTypeName = null;
		for(int i=0;i<variables.numberOfCategories();i++) {
			formattedPluginTypeName = variables.getCategory(i).toUpperCase();
			
			if(!hasPluginType(variables.getCategory(i))) { continue; }
			
			if(!m_preferredPlugins.containsKey(formattedPluginTypeName)) {
				m_preferredPlugins.put(formattedPluginTypeName, new HashMap<String, String>());
			}
		}
		
		Variable v = null;
		HashMap<String, String> preferredPluginsForPluginType = null;
		for(int i=0;i<variables.numberOfVariables();i++) {
			v = variables.getVariable(i);
			formattedPluginTypeName = variables.getCategory(v.getCategory()).toUpperCase();
			
			if(!hasPluginType(variables.getCategory(v.getCategory()))) { continue; }
			
			preferredPluginsForPluginType = m_preferredPlugins.get(formattedPluginTypeName);
			if(preferredPluginsForPluginType == null) {
				SystemConsole.instance.writeLine("Failed to set preferred plugin \"" + v.getValue() + "\" (" + v.getID() + ") to plugin type: \"" + variables.getCategory(v.getCategory()) + "\" due to unexpected missing category.");
				continue;
			}
			
			preferredPluginsForPluginType.put(v.getID().toUpperCase(), v.getValue());
		}
		
		return true;
	}
	
	public boolean removePreferredPlugin(String pluginName) {
		if(pluginName == null) { return false; }
		
		boolean pluginRemoved = false;
		
		HashMap<String, String> preferredPluginsForPluginType = null;
		for(String pluginTypeName : m_preferredPlugins.keySet()) {
			preferredPluginsForPluginType = m_preferredPlugins.get(pluginTypeName);
			
			for(String extension : preferredPluginsForPluginType.keySet()) {
				if(preferredPluginsForPluginType.get(extension).equalsIgnoreCase(pluginName)) {
					preferredPluginsForPluginType.remove(extension);
					pluginRemoved = true;
				}
			}
		}
		return pluginRemoved;
	}
	
	public <T extends Plugin> boolean removePreferredPlugin(int index, Class<T> pluginClass) {
		if(index < 0 || pluginClass == null) { return false; }
		
		return removePreferredPlugin(index, pluginClass.getName());
	}
	
	public boolean removePreferredPlugin(int index, String pluginClassName) {
		if(index < 0 || pluginClassName == null) { return false; }
		
		PluginType pluginType = getPluginTypeWithClass(pluginClassName);
		if(pluginType == null) { return false; }
		
		HashMap<String, String> preferredPluginsForPluginType = m_preferredPlugins.get(pluginType.getPluginType().toUpperCase());
		if(preferredPluginsForPluginType == null || index >= preferredPluginsForPluginType.keySet().size()) { return false; }
		
		int i = 0;
		for(String extension : preferredPluginsForPluginType.keySet()) {
			if(i == index) {
				preferredPluginsForPluginType.remove(extension);
				return true;
			}
			i++;
		}
		
		return false;
	}
	
	public <T extends Plugin> boolean removePreferredPluginForFileFormat(String fileFormat, Class<T> pluginClass) {
		if(fileFormat == null || pluginClass == null) { return false; }
		
		return removePreferredPluginForFileFormat(fileFormat, pluginClass.getName());
	}

	public boolean removePreferredPluginForFileFormat(String fileFormat, String pluginClassName) {
		if(fileFormat == null || pluginClassName == null) { return false; }
		
		String formattedFileFormat = fileFormat.trim().toUpperCase();
		if(formattedFileFormat.length() == 0) { return false; }
		
		PluginType pluginType = getPluginTypeWithClass(pluginClassName);
		if(pluginType == null) { return false; }
		
		HashMap<String, String> preferredPluginsForPluginType = m_preferredPlugins.get(pluginType.getPluginType().toUpperCase());
		if(preferredPluginsForPluginType == null) { return false; }
		
		preferredPluginsForPluginType.remove(formattedFileFormat);
		
		return true;
	}
	
	public <T extends Plugin> boolean clearPreferredPluginsForPluginType(Class<T> pluginClass) {
		if(pluginClass == null) { return false; }
		
		return clearPreferredPluginsForPluginType(pluginClass.getName());
	}
	
	public boolean clearPreferredPluginsForPluginType(String pluginClassName) {
		if(pluginClassName == null) { return false; }
		
		PluginType pluginType = getPluginTypeWithClass(pluginClassName);
		if(pluginType == null) { return false; }
		
		String formattedPluginTypeName = pluginType.getPluginType().toUpperCase();
		
		if(!m_preferredPlugins.containsKey(formattedPluginTypeName)) { return false; }
		
		m_preferredPlugins.remove(formattedPluginTypeName);
		
		return true;
	}
	
	public boolean clearPreferredPluginsForFileFormat(String fileFormat) {
		if(fileFormat == null) { return false; }
		
		boolean pluginsCleared = false;
		
		String formattedFileFormat = fileFormat.trim().toUpperCase();
		if(formattedFileFormat.length() == 0) { return false; }
		
		HashMap<String, String> preferredPluginsForPluginType = null;
		for(String pluginType : m_preferredPlugins.keySet()) {
			preferredPluginsForPluginType = m_preferredPlugins.get(pluginType);
			
			if(preferredPluginsForPluginType.containsKey(formattedFileFormat)) {
				preferredPluginsForPluginType.remove(formattedFileFormat);
				
				pluginsCleared = true;
			}
		}
		
		return pluginsCleared;
	}
	
	public void clearPreferredPlugins() {
		m_preferredPlugins.clear();
	}
	
	public int numberOfPlugins() {
		return m_plugins.size();
	}
	
	public <T extends Plugin> int numberOfPlugins(Class<T> type) {
		if(type == null) { return 0; }
		
		int numberOfPlugins = 0;
		
		for(int i=0;i<m_plugins.size();i++) {
			if(type.isAssignableFrom(m_plugins.elementAt(i).getClass())) {
				numberOfPlugins++;
			}
		}
		return numberOfPlugins;
	}
	
	public Plugin getPlugin(int index) {
		if(index < 0 || index >= m_plugins.size()) { return null; }
		
		return m_plugins.elementAt(index);
	}
	
	public <T extends Plugin> T getPlugin(int index, Class<T> type) {
		if(index < 0 || index >= m_plugins.size()) { return null; }
		
		return type.isAssignableFrom(m_plugins.elementAt(index).getClass()) ? type.cast(m_plugins.elementAt(index)) : null;
	}
	
	public Plugin getPlugin(String name) {
		if(name == null) { return null; }
		String temp = name.trim();
		if(temp.length() == 0) { return null; }
		
		for(int i=0;i<m_plugins.size();i++) {
			if(m_plugins.elementAt(i).getName().equalsIgnoreCase(temp)) {
				return m_plugins.elementAt(i);
			}
		}
		return null;
	}
	
	public <T extends Plugin> T getPlugin(String name, Class<T> type) {
		if(name == null) { return null; }
		String temp = name.trim();
		if(temp.length() == 0) { return null; }
		
		for(int i=0;i<m_plugins.size();i++) {
			if(m_plugins.elementAt(i).getName().equalsIgnoreCase(temp)) {
				return type.isAssignableFrom(m_plugins.elementAt(i).getClass()) ? type.cast(m_plugins.elementAt(i)) : null;
			}
		}
		return null;
	}
	
	public boolean hasPluginWithFileName(String fileName) {
		if(fileName == null) { return false; }
		String temp = fileName.trim();
		if(temp.length() == 0) { return false; }
		
		String s;
		for(int i=0;i<m_plugins.size();i++) {
			s = m_plugins.elementAt(i).getConfigFileName();
			if(s != null && s.equalsIgnoreCase(temp)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean hasPlugin(String name) {
		if(name == null) { return false; }
		String temp = name.trim();
		if(temp.length() == 0) { return false; }
		
		for(int i=0;i<m_plugins.size();i++) {
			if(m_plugins.elementAt(i).getName().equalsIgnoreCase(temp)) {
				return true;
			}
		}
		return false;
	}

	public boolean hasPlugin(Plugin plugin) {
		if(plugin == null) { return false; }
		
		for(int i=0;i<m_plugins.size();i++) {
			if(m_plugins.elementAt(i).equals(plugin)) {
				return true;
			}
		}
		return false;
	}
	
	public int indexOfPlugin(String name) {
		if(name == null) { return -1; }
		String temp = name.trim();
		if(temp.length() == 0) { return -1; }
		
		for(int i=0;i<m_plugins.size();i++) {
			if(m_plugins.elementAt(i).getName().equalsIgnoreCase(temp)) {
				return i;
			}
		}
		return -1;
	}
	
	public int indexOfPlugin(Plugin plugin) {
		if(plugin == null) { return -1; }
		
		for(int i=0;i<m_plugins.size();i++) {
			if(m_plugins.elementAt(i).equals(plugin)) {
				return i;
			}
		}
		return -1;
	}
	
	public String getPluginsAsString() {
		String pluginList = new String();
		
		for(int i=0;i<m_plugins.size();i++) {
			if(pluginList.length() > 0) {
				pluginList += ", ";
			}
			
			pluginList += m_plugins.elementAt(i).getName();
		}
		
		return pluginList;
	}

	public <T extends Plugin> String getPluginsAsString(Class<T> type) {
		String pluginList = new String();
		
		for(int i=0;i<m_plugins.size();i++) {
			if(type.isAssignableFrom(m_plugins.elementAt(i).getClass())) {
				if(pluginList.length() > 0) {
					pluginList += ", ";
				}
				
				pluginList += m_plugins.elementAt(i).getName();
			}
		}
		
		return pluginList;
	}
	
	public <T extends Plugin> Vector<T> getPlugins(Class<T> type) {
		Vector<T> plugins = new Vector<T>();
		
		for(int i=0;i<m_plugins.size();i++) {
			if(type.isAssignableFrom(m_plugins.elementAt(i).getClass())) {
				plugins.add(type.cast(m_plugins.elementAt(i)));
			}
		}
		
		return plugins;
	}
	
	public int numberOfUnloadedPlugins(File file) {
		return numberOfUnloadedPlugins(file, 0);
	}
	
	protected int numberOfUnloadedPlugins(File file, int depth) {
		if(file == null || !file.exists()) { return 0; }
		
		if(file.isDirectory()) {
			File[] contents = file.listFiles();
			
			int count = 0;
			for(int i=0;i<contents.length;i++) {
				count += numberOfUnloadedPlugins(contents[i], depth + 1);
			}
			
			return count;
		}
		else {
			if(depth < 1) { return 0; }
			
			if(!hasPluginWithFileName(file.getName()) && Plugin.hasConfigFileType(Utilities.getFileExtension(file.getName())) && Plugin.isPlugin(file)) {
				return 1;
			}
			return 0;
		}
	}
	
	public VariableCollection getUnloadedPlugins(File file) {
		VariableCollection plugins = new VariableCollection();
		
		getUnloadedPlugins(file, 0, plugins);
		
		return plugins;
	}
	
	protected void getUnloadedPlugins(File file, int depth, VariableCollection plugins) {
		if(file == null || !file.exists() || plugins == null) { return; }
		
		if(file.isDirectory()) {
			File[] contents = file.listFiles();
			
			for(int i=0;i<contents.length;i++) {
				getUnloadedPlugins(contents[i], depth + 1, plugins);
			}
		}
		else {
			if(depth < 1) { return; }
			
			if(!hasPluginWithFileName(file.getName()) && Plugin.hasConfigFileType(Utilities.getFileExtension(file.getName()))) {
				String name = Plugin.getPluginName(file);
				
				if(name != null) {
					plugins.addVariable(name, file.getPath());
				}
			}
		}
	}
	
	public void loadPlugins(File file) {
		loadPlugins(file, null, 0);
	}
	
	public void loadPlugins(File file, Task task) {
		loadPlugins(file, task, 0);
		
		if(task != null && !task.isCompleted()) {
			task.setCompleted();
		}
	}
	
	protected void loadPlugins(File file, Task task, int depth) {
		if(!m_initialized || file == null || !file.exists() || (task != null && task.isCancelled())) { return; }
		
		if(file.isDirectory()) {
			File[] contents = file.listFiles();
			
			for(int i=0;i<contents.length;i++) {
				loadPlugins(contents[i], task, depth + 1);
			}
		}
		else {
			if(depth < 1) { return; }
			
			if(!hasPluginWithFileName(file.getName())) {
				if(Plugin.hasConfigFileType(Utilities.getFileExtension(file.getName())) && task != null) {
					try {
						if(loadPlugin(file)) {
							task.addProgress(1);
						}
					}
					catch(Exception e) {
						SystemConsole.instance.writeLine(e.getMessage());
						
						JOptionPane.showMessageDialog(m_targetFrame, e.getMessage(), "Plugin Loading Failed", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		}
	}
	
	public boolean loadPlugin(File file) throws PluginLoadException, PluginInstantiationException {
		return loadPlugin(file, null);
	}
	
	public boolean loadPlugin(File file, Task task) throws PluginLoadException, PluginInstantiationException {
		if(file == null || !file.exists() || !file.isFile()) {
			throw new PluginLoadException("Plugin definition file \"" + file.getName() + "\" is missing or invalid.");
		}
		
		String directoryPath = Utilities.getRelativizedPath(Utilities.getFilePath(file), "Plugins");
		
		String fileExtension = Utilities.getFileExtension(file.getName());
		if(fileExtension == null) {
			throw new PluginLoadException("Plugin definition file is missing extension: \"" + file.getName() + "\".");
		}
		
		Plugin newPlugin = null;
		if(fileExtension.equalsIgnoreCase("cfg")) {
			newPlugin = loadPluginFromCFGFile(file, directoryPath);
		}
		else if(fileExtension.equalsIgnoreCase("xml")) {
			newPlugin = loadPluginFromXMLFile(file, directoryPath);
		}
		else {
			throw new PluginLoadException("Unsupported plugin configuration file type: \"" + fileExtension + "\".");
		}
		
		if(newPlugin == null) {
			if(task != null) {
				task.cancel();
			}
			
			return false;
		}
		
		if(!hasPlugin(newPlugin)) {
			m_plugins.add(newPlugin);
		}
		else {
			SystemConsole.instance.writeLine("Plugin \"" + newPlugin.getName() + "\" is already loaded!");
		}
		
		if(task != null) {
			task.addProgress(1);
		}
		
		return true;
	}
	
	protected Plugin loadPluginFromCFGFile(File file, String directoryName) throws PluginLoadException, PluginInstantiationException {
		if(file == null || !file.exists() || !file.isFile()) {
			throw new PluginLoadException("Plugin definition file \"" + file.getName() + "\" is missing or invalid.");
		}
		if(directoryName == null) {
			throw new PluginLoadException("Plugin must have a non-null directory name.");
		}
		String formattedDirectoryName = directoryName.trim();
		if(formattedDirectoryName.length() == 0) {
			throw new PluginLoadException("Plugin must have a non-empty directory name.");
		}
		
		String input = null;
		String line = null;
		BufferedReader in = null;
		Variable v = null;
		String name = null;
		String version = null;
		String type = null;
		String jarFileName = null;
		
		try {
			in = new BufferedReader(new FileReader(file));
			
			try {
				if(!Plugin.verifyCFGPluginDefinitionFileVersion(Plugin.readCFGPluginDefinitionFileVersion(in, file))) {
					try { in.close(); } catch(IOException e) { }
					
					throw new PluginLoadException("Unsupported plugin definition file version, only version " + Plugin.CFG_PLUGIN_DEFINITION_FILE_VERSION + " is supported. Maybe check for updates, or verify your plugin definition files?");
				}
			}
			catch(IllegalArgumentException e) {
				try { in.close(); } catch(IOException e2) { }
				
				throw new PluginLoadException("Invalid plugin version specified in plugin definition file \"" + file.getName() + "\": " + e.getMessage());
			}
			
			while(true) {
				input = in.readLine();
				if(input == null) {
					try { in.close(); } catch(IOException e) { }
					
					throw new PluginLoadException("Unexpected end of file encountered while reading plugin definition file: \"" + file.getName() + "\".");
				}
				
				line = input.trim();
				if(line.length() == 0 || Utilities.isComment(line)) { continue; }
				
				v = Variable.parseFrom(line);
				if(v == null) { continue; }
				
				if(v.getID().equalsIgnoreCase("Plugin Name")) {
					if(name != null) {
						SystemConsole.instance.writeLine("Plugin definition file \"" + file.getName() + "\" contains multiple entries for plugin name.");
						
						return null;
					}
					
					name = v.getValue();
				}
				else if(v.getID().equalsIgnoreCase("Plugin Version")) {
					if(version != null) {
						SystemConsole.instance.writeLine("Plugin definition file \"" + file.getName() + "\" contains multiple entries for plugin version.");
						
						return null;
					}
					
					version = v.getValue();
				}
				else if(v.getID().equalsIgnoreCase("Plugin Type")) {
					if(type != null) {
						SystemConsole.instance.writeLine("Plugin definition file \"" + file.getName() + "\" contains multiple entries for plugin type.");
						
						return null;
					}
					
					type = v.getValue();
				}
				else if(v.getID().equalsIgnoreCase("Plugin Jar File Name")) {
					if(jarFileName != null) {
						SystemConsole.instance.writeLine("Plugin definition file \"" + file.getName() + "\" contains multiple entries for plugin jar file name.");
					}
					
					jarFileName = v.getValue();
				}
				else {
					SystemConsole.instance.writeLine("Encountered unexpected property \"" + v.getID() + "\" in plugin definition file \"" + file.getName() + "\".");
				}
				
				if(name != null && version != null && type != null && jarFileName != null) {
					break;
				}
			}
		}
		catch(FileNotFoundException e) {
			throw new PluginLoadException("Missing plugin definition file \"" + file.getName() + "\": " + e.getMessage());
		}
		catch(IOException e) {
			throw new PluginLoadException("Read exception thrown while parsing plugin definition file \"" + file.getName() + "\": " + e.getMessage());
		}
		
		PluginType pluginType = getPluginType(type);
		if(pluginType == null) {
			try { in.close(); } catch(IOException e) { }
			
			throw new PluginLoadException("Plugin type \"" + type + "\" is not supported.");
		}
		
		Plugin plugin = null;
		try {
			plugin = pluginType.getNewPluginInstance(name, version, jarFileName, file.getName(), formattedDirectoryName);
		}
		catch(PluginInstantiationException e) {
			try { in.close(); } catch(IOException e2) { }
			
			throw e;
		}
		
		if(!plugin.loadClasses()) {
			try { in.close(); } catch(IOException e) { }
			
			throw new PluginLoadException("Failed to load plugin class files from jar file: \"" + jarFileName + "\".");
		}
		
		if(!plugin.loadFromCFGFile(in)) {
			try { in.close(); } catch(IOException e) { }
			
			throw new PluginLoadException("Failed to load remaining plugin properties from plugin definition file \"" + file.getName() + "\".");
		}
		
		try { in.close(); } catch(IOException e) { }
		
		return plugin;
	}
	
	protected Plugin loadPluginFromXMLFile(File file, String directoryName) throws PluginLoadException, PluginInstantiationException {
		if(file == null || !file.exists() || !file.isFile()) {
			throw new PluginLoadException("Plugin definition file \"" + file.getName() + "\" is missing or invalid.");
		}
		if(directoryName == null) {
			throw new PluginLoadException("Plugin must have a non-null directory name.");
		}
		String formattedDirectoryName = directoryName.trim();
		if(formattedDirectoryName.length() == 0) {
			throw new PluginLoadException("Plugin must have a non-empty directory name.");
		}
		
		BufferedReader in = null;
		
		try {
			in = new BufferedReader(new FileReader(file));
		}
		catch(FileNotFoundException e) {
			throw new PluginLoadException("Missing plugin definition file \"" + file.getName() + "\": " + e.getMessage());
		}
		
		XMLEvent event = null;
		XMLInputFactory inputFactory = null;
		XMLEventReader eventReader = null;
		String node = null;
		Iterator<?> attributes = null;
		Attribute attribute = null;
		String attributeName = null;
		String attributeValue = null;
		String name = null;
		String version = null;
		String type = null;
		String jarFileName = null;
		
		try {
			inputFactory = XMLInputFactory.newInstance();
			eventReader = inputFactory.createXMLEventReader(in);
			
			while(eventReader.hasNext()) {
				event = eventReader.nextEvent();
				
				if(event.isStartElement()) {
					node = event.asStartElement().getName().getLocalPart();
					
					if(node.equalsIgnoreCase("plugin")) {
						attributes = event.asStartElement().getAttributes();
						
						while(attributes.hasNext()) {
							attribute = (Attribute) attributes.next();
							
							attributeName = attribute.getName().toString();
							attributeValue = attribute.getValue().toString().trim();
							
							if(attributeName.equalsIgnoreCase("name")) {
								if(name != null) {
									try { in.close(); } catch (IOException e) { }
									
									throw new PluginLoadException("Plugin definition file \"" + file.getName() + "\" contains multiple entries for plugin name.");
								}
								
								name = attributeValue;
							}
							else if(attributeName.equalsIgnoreCase("version")) {
								if(version != null) {
									try { in.close(); } catch (IOException e) { }
									
									throw new PluginLoadException("Plugin definition file \"" + file.getName() + "\" contains multiple entries for plugin version.");
								}
								
								version = attributeValue;
							}
							else if(attributeName.equalsIgnoreCase("type")) {
								if(type != null) {
									try { in.close(); } catch (IOException e) { }
									
									throw new PluginLoadException("Plugin definition file \"" + file.getName() + "\" contains multiple entries for plugin type.");
								}
								
								type = attributeValue;
							}
							else if(attributeName.equalsIgnoreCase("jar")) {
								if(jarFileName != null) {
									try { in.close(); } catch (IOException e) { }
									
									throw new PluginLoadException("Plugin definition file \"" + file.getName() + "\" contains multiple entries for plugin jar file name.");
								}
								
								jarFileName = attributeValue;
							}
							else {
								SystemConsole.instance.writeLine("Encountered unexpected attibute in plugin XML node: \"" + attributeName + "\".");
							}
						}
						
						if(name == null || version == null || type == null) {
							try { in.close(); } catch (IOException e) { }
							
							throw new PluginLoadException("Missing required attribute(s) in plugin XML node - name, version and type are required, jar is optional.");
						}
					}
					else {
						SystemConsole.instance.writeLine("Encountered unexpected start node with tag name: \"" + node + "\".");
					}
				}
				else if(event.isEndElement()) {
					node = event.asEndElement().getName().getLocalPart();
					
					SystemConsole.instance.writeLine("Encountered unexpected end node with tag name: \"" + node + "\".");
				}
				
				if(name != null && version != null && type != null) {
					break;
				}
			}
		}
		catch(XMLStreamException e) {
			try { in.close(); } catch (IOException e2) { }
			
			throw new PluginLoadException("XML exception thrown while reading plugin definition file \"" + file.getName() + "\": " + e.getMessage());
		}
		
		PluginType pluginType = getPluginType(type);
		if(pluginType == null) {
			try { in.close(); } catch(IOException e) { }
			
			throw new PluginLoadException("Plugin type \"" + type + "\" is not supported.");
		}
		
		Plugin plugin = null;
		try {
			plugin = pluginType.getNewPluginInstance(name, version, jarFileName, file.getName(), formattedDirectoryName);
		}
		catch(PluginInstantiationException e) {
			try { in.close(); } catch(IOException e2) { }
			
			throw e;
		}
		
		if(!plugin.loadClasses()) {
			try { in.close(); } catch(IOException e) { }
			
			throw new PluginLoadException("Failed to load plugin class files from jar file: \"" + jarFileName + "\".");
		}
		
		if(!plugin.loadFromXMLFile(in, eventReader)) {
			try { in.close(); } catch(IOException e) { }
			
			throw new PluginLoadException("Failed to load remaining plugin properties from plugin definition file \"" + file.getName() + "\".");
		}
		
		try { in.close(); } catch(IOException e) { }
		
		return plugin;
	}
	
	public void displayPlugins() {
		if(!m_initialized) { return; }
		
		if(m_plugins.size() > 0) {
			String listOfPlugins = new String();
			for(int i=0;i<m_plugins.size();i++) {
				listOfPlugins += (i + 1) + ": " + m_plugins.elementAt(i).getName() + (i < m_plugins.size() - 1 ? "\n" : "");
			}
			
			JOptionPane.showMessageDialog(m_targetFrame, "Detected " + m_plugins.size() + " loaded plugin" + (m_plugins.size() == 1 ? "" : "s") + ":\n" + listOfPlugins, m_plugins.size() + " Plugin" + (m_plugins.size() == 1 ? "" : "s") +" Loaded", JOptionPane.INFORMATION_MESSAGE);
		}
		else {
			JOptionPane.showMessageDialog(m_targetFrame, "No plugins currently loaded.", "No Plugins Loaded", JOptionPane.INFORMATION_MESSAGE);
		}
	}
	
	public boolean loadPluginPrompt() {
		if(!m_initialized) { return false; }
		
		VariableCollection plugins = getUnloadedPlugins(new File(SettingsManager.instance.pluginDirectoryName));
		
		if(plugins.numberOfVariables() == 0) {
			JOptionPane.showMessageDialog(m_targetFrame, "No unloaded plugins found.", "No Unloaded Plugins", JOptionPane.INFORMATION_MESSAGE);
			return false;
		}
		
		String[] choices = new String[plugins.numberOfVariables()];
		for(int i=0;i<plugins.numberOfVariables();i++) {
			choices[i] = plugins.getVariable(i).getID();
		}
		
		String choice = (String) JOptionPane.showInputDialog(m_targetFrame, "Choose a plugin to load:", "Load Plugin", JOptionPane.QUESTION_MESSAGE, null, choices, choices[0]);
		if(choice == null) { return false; }
		
		Variable plugin = plugins.getVariable(choice);
		if(plugin == null) { return false; }
		
		return loadPlugin(plugin.getID(), plugin.getValue());
	}
	
	public boolean loadPlugin(String pluginName, final String pluginConfigFileName) {
		if(!m_initialized || pluginConfigFileName == null) { return false; }
		
		int numberOfLoadedPlugins = m_plugins.size();
		
		final Task task = new Task(1, m_progressDialog);
		
		Thread pluginLoaderThread = new Thread(new Runnable() {
			public void run() {
				try {
					loadPlugin(new File(pluginConfigFileName), task);
				}
				catch(PluginLoadException e) {
					SystemConsole.instance.writeLine(e.getMessage());
				}
				catch(PluginInstantiationException e) {
					SystemConsole.instance.writeLine(e.getMessage());
				}
			}
		});
		
		m_progressDialog.display("Loading", "Loading plugin...", 0, 1, task, pluginLoaderThread);
		
		if(m_progressDialog.userCancelled() || !task.isCompleted()) {
			task.cancel();
			
			pluginLoaderThread.interrupt();
			try { pluginLoaderThread.join(); } catch(InterruptedException e) { }
			
			m_progressDialog.clear();
		}
		
		if(numberOfLoadedPlugins == m_plugins.size()) {
			if(!m_progressDialog.userCancelled()) {
				String message = "Failed to load plugin" + (pluginName == null ? "!" : ": " + pluginName);
				
				SystemConsole.instance.writeLine(message);
				
				JOptionPane.showMessageDialog(m_targetFrame, message, "Loading Failed", JOptionPane.ERROR_MESSAGE);
			}
			
			return false;
		}
		else {
			SystemConsole.instance.writeLine("Successfully loaded plugin" + (pluginName == null ? "" : ": " + pluginName));
			
			return true;
		}
	}
	
	public void loadPlugins() {
		if(!m_initialized) { return; }
		
		int numberOfUnloadedPlugins = numberOfUnloadedPlugins(new File(SettingsManager.instance.pluginDirectoryName));
		int numberOfLoadedPlugins = m_plugins.size();
		
		String[] pluginNames = new String[m_plugins.size()];
		for(int i=0;i<m_plugins.size();i++) {
			pluginNames[i] = m_plugins.elementAt(i).getName();
		}
		
		SystemConsole.instance.writeLine("Number of unloaded plugins detected: " + numberOfUnloadedPlugins);
		
		if(numberOfUnloadedPlugins > 0) {
			final Task task = new Task(numberOfUnloadedPlugins, m_progressDialog);
			
			Thread pluginLoaderThread = new Thread(new Runnable() {
				public void run() {
					loadPlugins(new File(SettingsManager.instance.pluginDirectoryName), task);
				}
			});
			
			m_progressDialog.display("Loading", "Loading plugins...", 0, numberOfUnloadedPlugins, task, pluginLoaderThread);
			
			if(m_progressDialog.userCancelled() || !task.isCompleted()) {
				task.cancel();
				
				pluginLoaderThread.interrupt();
				try { pluginLoaderThread.join(); } catch(InterruptedException e) { }
				
				m_progressDialog.clear();
			}
			
			if(m_plugins.size() == 0 || m_plugins.size() - numberOfLoadedPlugins == 0) {
				SystemConsole.instance.writeLine("No plugins were loaded.");
			}
			else {
				int totalPluginsLoaded = (m_plugins.size() - numberOfLoadedPlugins);
				
				boolean foundPlugin, firstPlugin = true;
				StringBuffer s = new StringBuffer();
				s.append("Successfully loaded " + totalPluginsLoaded + " plugin" + (totalPluginsLoaded == 1 ? "" : "s") + ": ");
				for(int i=0;i<m_plugins.size();i++) {
					foundPlugin = false;
					for(int j=0;j<pluginNames.length;j++) {
						if(pluginNames[j].equalsIgnoreCase(m_plugins.elementAt(i).getName())) {
							foundPlugin = true;
						}
					}
					
					if(!foundPlugin) {
						if(!firstPlugin) {
							s.append(", ");
						}
						s.append(m_plugins.elementAt(i).getName());
						
						firstPlugin = false;
					}
				}
				
				SystemConsole.instance.writeLine(s.toString());
			}
		}
		else {
			SystemConsole.instance.writeLine("No plugins to load.");
		}
	}
	
}
