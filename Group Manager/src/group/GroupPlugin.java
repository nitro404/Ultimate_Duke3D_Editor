package group;

import java.util.*;
import java.util.regex.*;
import java.util.jar.*;
import java.io.*;
import java.lang.reflect.*;
import exception.*;
import utilities.*;
import variable.*;
import console.*;
import gui.*;

public class GroupPlugin {
	
	protected String m_name;
	protected Vector<String> m_supportedGroupFileTypes;
	protected boolean m_instantiable;
	protected double m_pluginVersion;
	protected String m_directoryName;
	protected String m_configFileName;
	protected String m_jarFileName;
	protected String m_groupClassName;
	protected String m_groupPanelClassName;
	protected Class<?> m_groupClass;
	protected Class<?> m_groupPanelClass;
	protected HashMap<String, Class<?>> m_classes;
	protected boolean m_loaded;
	
	public static final String GROUP_PLUGIN_DEFINITION_FILE_HEADER = "Group Plugin Definition File";
	public static final String GROUP_PLUGIN_DEFINITION_FILE_VERSION = "1.0";
	public static final String PLUGIN_VERSION = "1.0";
	public static final String PLUGIN_TYPE = "Group";
	public static final String CONFIG_FILE_TYPES[] = new String[] { "CFG" };
	
	protected GroupPlugin(String configFileName, String directoryName) {
		m_name = null;
		m_supportedGroupFileTypes = new Vector<String>();
		m_instantiable = false;
		m_pluginVersion = 0.0;
		m_directoryName = directoryName == null ? null : directoryName.trim();
		m_configFileName = configFileName == null ? null : configFileName.trim();
		m_jarFileName = null;
		m_groupClassName = null;
		m_groupPanelClassName = null;
		m_groupClass = null;
		m_groupPanelClass = null;
		m_classes = new HashMap<String, Class<?>>();
		m_loaded = false;
	}
	
	public String getName() {
		return m_name;
	}
	
	public double getPluginVersion() {
		return m_pluginVersion;
	}
	
	public int numberOfSupportedGroupFileTypes() {
		return m_supportedGroupFileTypes.size();
	}
	
	public String getSupportedGroupFileType(int index) {
		if(index < 0 || index >= m_supportedGroupFileTypes.size()) { return null; }
		return m_supportedGroupFileTypes.elementAt(index);
	}
	
	public String getSupportedGroupFileTypesAsString() {
		String listOfSupportedGroupFileTypes = "";
		
		for(int i=0;i<m_supportedGroupFileTypes.size();i++) {
			listOfSupportedGroupFileTypes += m_supportedGroupFileTypes.elementAt(i);
			
			if(i < m_supportedGroupFileTypes.size() - 1) {
				listOfSupportedGroupFileTypes += ", ";
			}
		}
		
		return listOfSupportedGroupFileTypes;
	}
	
	public Vector<String> getSupportedGroupFileTypes() {
		return m_supportedGroupFileTypes;
	}
	
	public boolean hasSupportedGroupFileType(String fileType) {
		if(fileType == null) { return false; }
		String type = fileType.trim();
		if(type.length() == 0) { return false; }
		
		for(int i=0;i<m_supportedGroupFileTypes.size();i++) {
			if(m_supportedGroupFileTypes.elementAt(i).equalsIgnoreCase(type)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean hasSharedSupportedGroupFileType(GroupPlugin groupPlugin) {
		if(groupPlugin == null) { return false; }
		
		for(int i=0;i<groupPlugin.numberOfSupportedGroupFileTypes();i++) {
			if(hasSupportedGroupFileType(groupPlugin.getSupportedGroupFileType(i))) {
				return true;
			}
		}
		return false;
	}
	
	public int numberOfSharedSupportedGroupFileTypes(GroupPlugin groupPlugin) {
		if(groupPlugin == null) { return 0; }
		
		int numberOfSharedSupportedGroupFileTypes = 0;
		for(int i=0;i<groupPlugin.numberOfSupportedGroupFileTypes();i++) {
			if(hasSupportedGroupFileType(groupPlugin.getSupportedGroupFileType(i))) {
				numberOfSharedSupportedGroupFileTypes++;
			}
		}
		return numberOfSharedSupportedGroupFileTypes;
	}

	public Vector<String> getSharedSupportedGroupFileTypes(GroupPlugin groupPlugin) {
		if(groupPlugin == null) { return null; }
		
		Vector<String> sharedSupportedGroupFileTypes = new Vector<String>();
		for(int i=0;i<groupPlugin.numberOfSupportedGroupFileTypes();i++) {
			if(hasSupportedGroupFileType(groupPlugin.getSupportedGroupFileType(i))) {
				sharedSupportedGroupFileTypes.add(groupPlugin.getSupportedGroupFileType(i));
			}
		}
		return sharedSupportedGroupFileTypes;
	}
	
	public String getSharedSupportedGroupFileTypesAsString(GroupPlugin groupPlugin) {
		if(groupPlugin == null) { return null; }
		
		String sharedSupportedGroupFileTypes = new String();
		for(int i=0;i<groupPlugin.numberOfSupportedGroupFileTypes();i++) {
			if(hasSupportedGroupFileType(groupPlugin.getSupportedGroupFileType(i))) {
				if(sharedSupportedGroupFileTypes.length() > 0) {
					sharedSupportedGroupFileTypes += ", ";
				}
				
				sharedSupportedGroupFileTypes += groupPlugin.getSupportedGroupFileType(i);
			}
		}
		return sharedSupportedGroupFileTypes;
	}
	
	public int indexOfSupportedGroupFileType(String fileType) {
		if(fileType == null) { return -1; }
		String type = fileType.trim();
		if(type.length() == 0) { return -1; }
		
		for(int i=0;i<m_supportedGroupFileTypes.size();i++) {
			if(m_supportedGroupFileTypes.elementAt(i).equalsIgnoreCase(type)) {
				return i;
			}
		}
		return -1;
	}
	
	public boolean addSupportedGroupFileType(String fileType) {
		if(fileType == null) { return false; }
		String type = fileType.trim();
		if(type.length() == 0) { return false; }
		
		for(int i=0;i<m_supportedGroupFileTypes.size();i++) {
			if(m_supportedGroupFileTypes.elementAt(i).equalsIgnoreCase(type)) {
				return false;
			}
		}
		
		m_supportedGroupFileTypes.add(type);
		
		return true;
	}
	
	public boolean isInstantiable() {
		return m_instantiable;
	}
	
	public String getDirectoryName() {
		return m_directoryName;
	}
	
	public String getConfigFileName() {
		return m_configFileName;
	}
	
	public boolean setConfigFileName(String configFileName) {
		if(configFileName == null) { return false; }
		String tempName = configFileName.trim();
		if(tempName.length() == 0) { return false; }
		
		m_configFileName = configFileName;
		
		return true;
	}
	
	public String getJarFileName() {
		return m_jarFileName;
	}

	public String getGroupClassName() {
		return m_groupClassName;
	}
	
	public String getGroupPanelClassName() {
		return m_groupClassName;
	}
	
	public Class<?> getGroupClass() {
		return m_groupClass;
	}
	
	public Class<?> getGroupPanelClass() {
		return m_groupPanelClass;
	}
	
	public int numberOfClasses() {
		return m_classes.size();
	}
	
	public Class<?> getLoadedClass(String className) {
		if(className == null) { return null; }
		
		return m_classes.get(className.replaceAll("[\\\\/]", ".").replaceAll("\\.[Cc][Ll][Aa][Ss][Ss]$", ""));
	}
	
	public Group getGroupInstance(File groupFile) throws GroupInstantiationException {
		Constructor<?> constructor = null;
		try { constructor = m_groupClass.getDeclaredConstructor(File.class); }
		catch(Exception e) { }
		
		if(constructor == null) {
			throw new GroupInstantiationException("Group class \"" + m_groupClassName + "\" must contain a constructor which takes a File as an argument.");
		}
		
		Group newGroup = null;
		try {
			newGroup = (Group) constructor.newInstance(groupFile);
		}
		catch(Exception e) {
			throw new GroupInstantiationException("Failed to instantiate group class \"" + m_groupClassName + "\": " + e.getMessage());
		}
		
		return newGroup;
	}
	
	public GroupPanel getGroupPanelInstance(Group group) throws GroupPanelInstantiationException {
		Constructor<?> constructor = null;
		try { constructor = m_groupPanelClass.getDeclaredConstructor(Group.class); }
		catch(Exception e) { }
		
		if(constructor == null) {
			throw new GroupPanelInstantiationException("Group panel class \"" + m_groupPanelClassName + "\" must contain a constructor which takes a Group as an argument.");
		}
		
		GroupPanel newGroupPanel = null;
		try {
			newGroupPanel = (GroupPanel) constructor.newInstance(group);
		}
		catch(Exception e) {
			throw new GroupPanelInstantiationException("Failed to instantiate group panel class \"" + m_groupPanelClassName + "\": " + e.getMessage());
		}
		
		return newGroupPanel;
	}
	
	public boolean isLoaded() {
		return m_loaded;
	}
	
	public boolean load() {
		if(m_loaded) { return true; }
		
		if(!loadClasses()) { return false; }
		
		m_loaded = true;
		
		return true;
	}
	
	protected boolean loadClasses() {
		if(m_loaded) { return true; }
		if(m_jarFileName == null) { return false; }
		
		InputStream in;
		String name;
		byte[] data;
		JarFile jarFile = null;
		JarEntry e;
		Class<?> c;
		
		try {
			jarFile = new JarFile(Utilities.appendSlash(GroupManager.settings.pluginDirectoryName) + Utilities.appendSlash(m_directoryName) + "/" + m_jarFileName);
			
			Pattern p = Pattern.compile(".*\\.class$", Pattern.CASE_INSENSITIVE);
			
			Enumeration<JarEntry> contents = jarFile.entries();
			while(contents.hasMoreElements()) {
				e = contents.nextElement();
				if(p.matcher(e.getName()).matches()) {
					in = jarFile.getInputStream(e);
					if(in.available() < 1) {
						jarFile.close();
						
						return false;
					}
					data = new byte[in.available()];
					in.read(data);
					
					name = e.getName().replaceAll("[\\\\/]", ".").replaceAll("\\.[Cc][Ll][Aa][Ss][Ss]$", "");
					
					c = GroupManager.classLoader.deserializeClass(name, data);
					if(c == null) {
						jarFile.close();
						
						SystemConsole.getInstance().writeLine("Failed to deserialize class from jar file \"" + m_jarFileName + "\": \"" + name + "\".");
						
						return false;
					}
					
					m_classes.put(name, c);
				}
			}
			
			jarFile.close();
			
			return true;
		}
		catch(IOException e2) {
			SystemConsole.getInstance().writeLine("Exception thrown while deserializing classes from jar file \"" + m_jarFileName + "\": " + e2.getMessage());
			
			try { jarFile.close(); } catch(Exception e3) { }
			return false;
		}
	}
	
	protected static String readPluginDefinitionFileVersion(BufferedReader in, File file) throws IOException, GroupPluginLoadException {
		if(in == null || file == null) { return null; }
		
		String input, header;
		while(true) {
			input = in.readLine();
			if(input == null) {
				in.close();
				throw new GroupPluginLoadException("Group plugin definition file \"" + file.getName() + "\" incomplete or corrupted, no header found.");
			}
			
			header = input.trim();
			if(header.length() == 0) { continue; }
			
			if(!header.matches("^.* ([0-9]\\.?)+$")) {
				in.close();
				throw new GroupPluginLoadException("Group plugin definition file \"" + file.getName() + "\" has an invalid header: \"" + header + "\".");
			}
			String[] headerData = new String[2];
			int separatorIndex = header.lastIndexOf(' ');
			if(separatorIndex < 0) {
				in.close();
				throw new GroupPluginLoadException("Group plugin definition file \"" + file.getName() + "\" is missing version number in header.");
			}
			headerData[0] = header.substring(0, separatorIndex);
			headerData[1] = header.substring(separatorIndex + 1, header.length());
			
			if(!headerData[0].trim().equalsIgnoreCase(GROUP_PLUGIN_DEFINITION_FILE_HEADER)) {
				in.close();
				throw new GroupPluginLoadException("Group plugin definition file \"" + file.getName() + "\" has an invalid header: \"" + headerData[0] + "\", expected \"" + GROUP_PLUGIN_DEFINITION_FILE_HEADER + "\".");
			}
			
			return headerData[1];
		}
	}
	
	protected static boolean verifyDefinitionFileVersion(String version) {
		if(version == null) { return false; }
		String cfgVersion = version.trim();
		if(cfgVersion.length() == 0) { return false; }
		
		return Utilities.compareVersions(GROUP_PLUGIN_DEFINITION_FILE_VERSION, cfgVersion) == 0;
	}
	
	public static boolean isGroupPlugin(File file) throws GroupPluginLoadException {
		if(file == null || !file.exists() || !file.isFile()) {
			throw new GroupPluginLoadException("Group plugin definition file \"" + file.getName() + "\" is missing or invalid.");
		}
		
		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader(file));
			
			try {
				if(!verifyDefinitionFileVersion(readPluginDefinitionFileVersion(in, file))) {
					in.close();
					
					throw new GroupPluginLoadException("Unsupported plugin definition file version, only version " + GROUP_PLUGIN_DEFINITION_FILE_VERSION + " is supported. Maybe check for updates, or verify your plugin definition files?");
				}
			}
			catch(IllegalArgumentException e) {
				in.close();
				
				throw new GroupPluginLoadException("Invalid plugin version specified in plugin definition file \"" + file.getName() + "\": " + e.getMessage());
			}
			
			in.close();
			
			return true;
		}
		catch(FileNotFoundException e) {
			throw new GroupPluginLoadException("Missing group plugin definition file \"" + file.getName() + "\": " + e.getMessage());
		}
		catch(IOException e) {
			throw new GroupPluginLoadException("Read exception thrown while parsing group plugin definition file \"" + file.getName() + "\": " + e.getMessage());
		}
	}
	
	public static String getGroupPluginName(File file) throws GroupPluginLoadException {
		if(file == null || !file.exists() || !file.isFile()) {
			throw new GroupPluginLoadException("Group plugin definition file \"" + file.getName() + "\" is missing or invalid.");
		}
		
		String input, temp;
		Variable v;
		String name = null;
		BufferedReader in = null;
		
		try {
			in = new BufferedReader(new FileReader(file));
			
			try {
				if(!verifyDefinitionFileVersion(readPluginDefinitionFileVersion(in, file))) {
					in.close();
					
					throw new GroupPluginLoadException("Unsupported plugin definition file version, only version " + GROUP_PLUGIN_DEFINITION_FILE_VERSION + " is supported. Maybe check for updates, or verify your plugin definition files?");
				}
			}
			catch(IllegalArgumentException e) {
				in.close();
				
				throw new GroupPluginLoadException("Invalid plugin version specified in plugin definition file \"" + file.getName() + "\": " + e.getMessage());
			}
			
			while(true) {
				input = in.readLine();
				if(input == null) {
					in.close();
					throw new GroupPluginLoadException("Group plugin definition file \"" + file.getName() + "\" incomplete or corrupted, no plugin name found.");
				}
				temp = input.trim();
				if(temp.length() == 0) { continue; }
				
				v = Variable.parseFrom(temp);
				if(v == null) {
					in.close();
					throw new GroupPluginLoadException("Failed to parse group plugin name variable in group definition file: \"" + file.getName() + "\".");
				}
				
				if(!v.getID().equalsIgnoreCase("Name")) {
					in.close();
					throw new GroupPluginLoadException("Expected group plugin name variable, found \"" + v.getID() + "\" instead, in group definition file: \"" + file.getName() + "\".");
				}
				
				name = v.getValue();
				if(name.length() == 0) {
					in.close();
					throw new GroupPluginLoadException("Invalid empty group plugin name found in group definition file: \"" + file.getName() + "\".");
				}
				
				break;
			}
			
			in.close();
			
			return name;
		}
		catch(FileNotFoundException e) {
			throw new GroupPluginLoadException("Missing group plugin definition file \"" + file.getName() + "\": " + e.getMessage());
		}
		catch(IOException e) {
			throw new GroupPluginLoadException("Read exception thrown while parsing group plugin definition file \"" + file.getName() + "\": " + e.getMessage());
		}
	}
	
	public static GroupPlugin loadFrom(File file) throws GroupPluginLoadException {
		if(file == null || !file.exists() || !file.isFile()) {
			throw new GroupPluginLoadException("Group plugin definition file \"" + file.getName() + "\" is missing or invalid.");
		}
		
		GroupPlugin groupPlugin = null;
		
		String directoryName;
		if(file.getPath().matches(".*[\\\\/].*")) {
			directoryName = file.getPath().replaceAll("[\\\\/][^\\\\/]*$", "").replaceAll("^.*[\\\\/]", "");
		}
		else {
			directoryName = "/";
		}
		
		String fileExtension = Utilities.getFileExtension(file.getName());
		
		if(fileExtension.equalsIgnoreCase("cfg")) {
			groupPlugin = loadFromCFGFile(file, directoryName);
		}
		else {
			throw new GroupPluginLoadException("Unsupported group plugin configuration file type: \"" + fileExtension + "\".");
		}
		
		return groupPlugin;
	}
	
	protected static GroupPlugin loadFromCFGFile(File file, String directory) throws GroupPluginLoadException {
		if(file == null || !file.exists() || !file.isFile()) {
			throw new GroupPluginLoadException("Group plugin definition file \"" + file.getName() + "\" is missing or invalid.");
		}
		if(directory == null) {
			throw new GroupPluginLoadException("Group plugin must have a non-null directory name.");
		}
		String directoryName = directory.trim();
		if(directoryName.length() == 0) {
			throw new GroupPluginLoadException("Group plugin must have a non-empty directory name.");
		}
		
		String input, temp;
		BufferedReader in;
		Variable v;
		GroupPlugin groupPlugin = new GroupPlugin(file.getName(), directoryName);
		try {
			in = new BufferedReader(new FileReader(file));
			
			try {
				if(!verifyDefinitionFileVersion(readPluginDefinitionFileVersion(in, file))) {
					in.close();
					
					throw new GroupPluginLoadException("Unsupported plugin definition file version, only version " + GROUP_PLUGIN_DEFINITION_FILE_VERSION + " is supported. Maybe check for updates, or verify your plugin definition files?");
				}
			}
			catch(IllegalArgumentException e) {
				in.close();
				
				throw new GroupPluginLoadException("Invalid plugin version specified in plugin definition file \"" + file.getName() + "\": " + e.getMessage());
			}
			
			while(true) {
				input = in.readLine();
				if(input == null) {
					in.close();
					throw new GroupPluginLoadException("Group plugin definition file \"" + file.getName() + "\" incomplete or corrupted, no plugin version found.");
				}
				temp = input.trim();
				if(temp.length() == 0) { continue; }
				
				v = Variable.parseFrom(temp);
				if(v == null) {
					in.close();
					throw new GroupPluginLoadException("Failed to parse group plugin version variable in group definition file: \"" + file.getName() + "\".");
				}
				
				if(!v.getID().equalsIgnoreCase("Plugin Version")) {
					in.close();
					throw new GroupPluginLoadException("Expected group plugin version variable, found \"" + v.getID() + "\" instead, in group definition file: \"" + file.getName() + "\".");
				}
				
				String pluginVersionData = v.getValue();
				if(pluginVersionData.length() == 0) {
					in.close();
					throw new GroupPluginLoadException("Empty group plugin version found in group definition file: \"" + file.getName() + "\".");
				}
				
				try {
					if(Utilities.compareVersions(PLUGIN_VERSION, pluginVersionData) != 0) {
						in.close();
						
						throw new GroupPluginLoadException("Unsupported plugin version, only version " + PLUGIN_VERSION + " is supported. Maybe check for updates, or verify your config files?");
					}
				}
				catch(IllegalArgumentException e) {
					in.close();
					
					throw new GroupPluginLoadException("Invalid plugin version specified in plugin definition file \"" + file.getName() + "\": " + e.getMessage());
				}
				
				break;
			}
			
			while(true) {
				input = in.readLine();
				if(input == null) {
					in.close();
					throw new GroupPluginLoadException("Group plugin definition file \"" + file.getName() + "\" incomplete or corrupted, no plugin type found.");
				}
				temp = input.trim();
				if(temp.length() == 0) { continue; }
				
				v = Variable.parseFrom(temp);
				if(v == null) {
					in.close();
					throw new GroupPluginLoadException("Failed to parse group plugin type variable in group definition file: \"" + file.getName() + "\".");
				}
				
				if(!v.getID().equalsIgnoreCase("Plugin Type")) {
					in.close();
					throw new GroupPluginLoadException("Expected group plugin type variable, found \"" + v.getID() + "\" instead, in group definition file: \"" + file.getName() + "\".");
				}
				
				String pluginType = v.getValue();
				if(pluginType.length() == 0) {
					in.close();
					throw new GroupPluginLoadException("Empty plugin type found in group definition file: \"" + file.getName() + "\".");
				}
				
				if(!pluginType.equalsIgnoreCase(PLUGIN_TYPE)) {
					in.close();
					throw new GroupPluginLoadException("Unsupported plugin type \"" + pluginType + "\" found in group definition file: \"" + file.getName() + "\", only type " + PLUGIN_TYPE + " is supported.");
				}
				
				break;
			}
			
			while(true) {
				input = in.readLine();
				if(input == null) {
					in.close();
					throw new GroupPluginLoadException("Group plugin definition file \"" + file.getName() + "\" incomplete or corrupted, no plugin name found.");
				}
				temp = input.trim();
				if(temp.length() == 0) { continue; }
				
				v = Variable.parseFrom(temp);
				if(v == null) {
					in.close();
					throw new GroupPluginLoadException("Failed to parse group plugin name variable in group definition file: \"" + file.getName() + "\".");
				}
				
				if(!v.getID().equalsIgnoreCase("Plugin Name")) {
					in.close();
					throw new GroupPluginLoadException("Expected group plugin name variable, found \"" + v.getID() + "\" instead, in group definition file: \"" + file.getName() + "\".");
				}
				
				groupPlugin.m_name = v.getValue();
				if(groupPlugin.m_name.length() == 0) {
					in.close();
					throw new GroupPluginLoadException("Empty group plugin name found in group definition file: \"" + file.getName() + "\".");
				}
				
				break;
			}
			
			while(true) {
				input = in.readLine();
				if(input == null) {
					in.close();
					throw new GroupPluginLoadException("Group plugin definition file \"" + file.getName() + "\" incomplete or corrupted, no suppported group file type list found.");
				}
				temp = input.trim();
				if(temp.length() == 0) { continue; }
				
				v = Variable.parseFrom(temp);
				if(v == null) {
					in.close();
					throw new GroupPluginLoadException("Failed to parse suppported group file type list variable in group definition file: \"" + file.getName() + "\".");
				}
				
				if(!v.getID().equalsIgnoreCase("Supported Group File Types")) {
					in.close();
					throw new GroupPluginLoadException("Expected suppported group file type list variable, found \"" + v.getID() + "\" instead, in group definition file: \"" + file.getName() + "\".");
				}
				
				String supportedGroupFileTypes = v.getValue();
				if(supportedGroupFileTypes.length() == 0) {
					in.close();
					throw new GroupPluginLoadException("Empty supported group file type list found in group definition file: \"" + file.getName() + "\".");
				}
				
				String supportedGroupFileType = null;
				String supportedGroupFileTypeList[] = supportedGroupFileTypes.split("[;, \t]");
				for(int i=0;i<supportedGroupFileTypeList.length;i++) {
					supportedGroupFileType = supportedGroupFileTypeList[i].trim();
					if(supportedGroupFileType.length() > 0) {
						groupPlugin.addSupportedGroupFileType(supportedGroupFileType);
					}
				}
				
				if(groupPlugin.numberOfSupportedGroupFileTypes() == 0) {
					throw new GroupPluginLoadException("Group plugin \"" + groupPlugin.getName() + "\" in group definition file: \"" + file.getName() + "\" must support at least one file type.");
				}
				
				break;
			}
			
			while(true) {
				input = in.readLine();
				if(input == null) {
					in.close();
					throw new GroupPluginLoadException("Group plugin definition file \"" + file.getName() + "\" incomplete or corrupted, no instantiable property found.");
				}
				temp = input.trim();
				if(temp.length() == 0) { continue; }
				
				v = Variable.parseFrom(temp);
				if(v == null) {
					in.close();
					throw new GroupPluginLoadException("Failed to parse group instantiable property in group definition file: \"" + file.getName() + "\".");
				}
				
				if(!v.getID().equalsIgnoreCase("Instantiable")) {
					in.close();
					throw new GroupPluginLoadException("Expected group instantiable variable, found \"" + v.getID() + "\" instead, in group definition file: \"" + file.getName() + "\".");
				}
				
				String instantiable = v.getValue();
				if(instantiable.length() == 0) {
					in.close();
					throw new GroupPluginLoadException("Empty group instantiable property found in group definition file: \"" + file.getName() + "\".");
				}
				
				if(instantiable.equalsIgnoreCase("true") || instantiable.equalsIgnoreCase("1") || instantiable.equalsIgnoreCase("yes") || instantiable.equalsIgnoreCase("enabled")) {
					groupPlugin.m_instantiable = true;
				}
				else if(instantiable.equalsIgnoreCase("false") || instantiable.equalsIgnoreCase("0") || instantiable.equalsIgnoreCase("no") || instantiable.equalsIgnoreCase("disabled")) {
					groupPlugin.m_instantiable = false;
				}
				else {
					throw new GroupPluginLoadException("Invalid group instantiable property value found in group definition file: \"" + file.getName() + "\", espected one of: true, false, 1, 0, yes, no, enabled, disabled.");
				}
				
				break;
			}
			
			while(true) {
				input = in.readLine();
				if(input == null) {
					in.close();
					throw new GroupPluginLoadException("Group plugin definition file \"" + file.getName() + "\" incomplete or corrupted, no jar file name found.");
				}
				temp = input.trim();
				if(temp.length() == 0) { continue; }
				
				v = Variable.parseFrom(temp);
				if(v == null) {
					in.close();
					throw new GroupPluginLoadException("Failed to parse group plugin jar file name in group definition file: \"" + file.getName() + "\".");
				}
				
				if(!v.getID().equalsIgnoreCase("Plugin Jar File Name")) {
					in.close();
					throw new GroupPluginLoadException("Expected group plugin jar file name variable, found \"" + v.getID() + "\" instead, in group definition file: \"" + file.getName() + "\".");
				}
				
				groupPlugin.m_jarFileName = v.getValue();
				if(groupPlugin.m_jarFileName.length() == 0) {
					in.close();
					throw new GroupPluginLoadException("Empty group plugin jar file name found in group definition file: \"" + file.getName() + "\".");
				}
				
				break;
			}
			
			while(true) {
				input = in.readLine();
				if(input == null) {
					in.close();
					throw new GroupPluginLoadException("Group plugin definition file \"" + file.getName() + "\" incomplete or corrupted, no group class name found.");
				}
				temp = input.trim();
				if(temp.length() == 0) { continue; }
				
				v = Variable.parseFrom(temp);
				if(v == null) {
					in.close();
					throw new GroupPluginLoadException("Failed to parse group class name in group definition file: \"" + file.getName() + "\".");
				}
				
				if(!v.getID().equalsIgnoreCase("Group Class Name")) {
					in.close();
					throw new GroupPluginLoadException("Expected group class name variable, found \"" + v.getID() + "\" instead, in group definition file: \"" + file.getName() + "\".");
				}
				
				groupPlugin.m_groupClassName = v.getValue();
				if(groupPlugin.m_groupClassName.length() == 0) {
					in.close();
					throw new GroupPluginLoadException("Empty group class name found in group definition file: \"" + file.getName() + "\".");
				}
				
				break;
			}
			
			while(true) {
				input = in.readLine();
				if(input == null) {
					in.close();
					throw new GroupPluginLoadException("Group plugin definition file \"" + file.getName() + "\" incomplete or corrupted, no group panel class name found.");
				}
				temp = input.trim();
				if(temp.length() == 0) { continue; }
				
				v = Variable.parseFrom(temp);
				if(v == null) {
					in.close();
					throw new GroupPluginLoadException("Failed to parse group panel class name in group definition file: \"" + file.getName() + "\".");
				}
				
				if(!v.getID().equalsIgnoreCase("Group Panel Class Name")) {
					in.close();
					throw new GroupPluginLoadException("Expected group panel class name variable, found \"" + v.getID() + "\" instead, in group definition file: \"" + file.getName() + "\".");
				}
				
				groupPlugin.m_groupPanelClassName = v.getValue();
				if(groupPlugin.m_groupPanelClassName.length() == 0) {
					in.close();
					throw new GroupPluginLoadException("Empty group panel class name found in group definition file: \"" + file.getName() + "\".");
				}
				
				break;
			}
			
			in.close();
		}
		catch(FileNotFoundException e) {
			throw new GroupPluginLoadException("Missing group plugin definition file \"" + file.getName() + "\": " + e.getMessage());
		}
		catch(IOException e) {
			throw new GroupPluginLoadException("Read exception thrown while parsing group plugin definition file \"" + file.getName() + "\": " + e.getMessage());
		}
		
		if(!groupPlugin.load()) {
			throw new GroupPluginLoadException("Failed to load group plugin \"" + groupPlugin.m_jarFileName + "\".");
		}
		
		groupPlugin.m_groupClass = null;
		try { groupPlugin.m_groupClass = GroupManager.classLoader.loadClass(groupPlugin.m_groupClassName); }
		catch(ClassNotFoundException e) { throw new GroupPluginLoadException("Class " + groupPlugin.m_groupClassName + " is missing or not loaded."); }
		if(!(Group.class.isAssignableFrom(groupPlugin.m_groupClass))) {
			throw new GroupPluginLoadException("Class " + groupPlugin.m_groupClassName + " does not extend Group class.");
		}
		
		groupPlugin.m_groupPanelClass = null;
		try { groupPlugin.m_groupPanelClass = GroupManager.classLoader.loadClass(groupPlugin.m_groupPanelClassName); }
		catch(ClassNotFoundException e) { throw new GroupPluginLoadException("Class " + groupPlugin.m_groupPanelClassName + " is missing or not loaded."); }
		if(!(GroupPanel.class.isAssignableFrom(groupPlugin.m_groupPanelClass))) {
			throw new GroupPluginLoadException("Class " + groupPlugin.m_groupPanelClassName + " does not extend GroupPanel class.");
		}
		
		return groupPlugin;
	}
	
	public static int numberOfConfigFileTypes() {
		return CONFIG_FILE_TYPES.length;
	}
	
	public static String getConfigFileType(int index) {
		if(index < 0 || index >= CONFIG_FILE_TYPES.length) { return null; }
		return CONFIG_FILE_TYPES[index];
	}
	
	public static String getConfigFileTypesAsString() {
		String listOfConfigFileTypes = "";
		
		for(int i=0;i<CONFIG_FILE_TYPES.length;i++) {
			listOfConfigFileTypes += CONFIG_FILE_TYPES[i];
			
			if(i < CONFIG_FILE_TYPES.length - 1) {
				listOfConfigFileTypes += ", ";
			}
		}
		
		return listOfConfigFileTypes;
	}
	
	public static boolean hasConfigFileType(String fileType) {
		if(fileType == null) { return false; }
		String type = fileType.trim();
		if(type.length() == 0) { return false; }
		
		for(int i=0;i<CONFIG_FILE_TYPES.length;i++) {
			if(CONFIG_FILE_TYPES[i].equalsIgnoreCase(type)) {
				return true;
			}
		}
		return false;
	}
	
	public static int indexOfConfigFileType(String fileType) {
		if(fileType == null) { return -1; }
		String type = fileType.trim();
		if(type.length() == 0) { return -1; }
		
		for(int i=0;i<CONFIG_FILE_TYPES.length;i++) {
			if(CONFIG_FILE_TYPES[i].equalsIgnoreCase(type)) {
				return i;
			}
		}
		return -1;
	}
	
	public boolean equals(Object o) {
		if(o == null || !(o instanceof GroupPlugin)) { return false; }
		
		GroupPlugin p = ((GroupPlugin) o);
		
		if(m_name == null || p.m_name == null) { return false; }
		
		return m_name.equalsIgnoreCase(p.m_name);
	}
	
	public String toString() {
		return m_name;
	}
	
}
