package plugin;

import java.util.*;
import java.util.regex.*;
import java.util.jar.*;
import java.io.*;
import javax.xml.stream.*;
import javax.xml.stream.events.*;
import exception.*;
import utilities.*;
import settings.*;
import console.*;

public abstract class Plugin {
	
	protected String m_name;
	protected String m_version;
	protected String m_directoryName;
	protected String m_configFileName;
	protected String m_jarFileName;
	protected HashMap<String, Class<?>> m_classes;
	protected boolean m_loaded;
	
	public static final String CFG_PLUGIN_DEFINITION_FILE_HEADER = "Plugin Definition File";
	public static final String CFG_PLUGIN_DEFINITION_FILE_VERSION = "1.0";
	public static final String CONFIG_FILE_TYPES[] = new String[] { "CFG", "XML" };
	
	protected Plugin(String pluginName, String pluginVersion, String jarFileName, String configFileName, String directoryName) throws IllegalArgumentException {
		if(pluginName == null || pluginName.trim().length() == 0) { throw new IllegalArgumentException("Plugin name cannot be null or empty."); }
		if(pluginVersion == null || pluginVersion.trim().length() == 0) { throw new IllegalArgumentException("Plugin version cannot be null or empty."); }
		if(configFileName == null || configFileName.trim().length() == 0) { throw new IllegalArgumentException("Plugin config file name cannot be null or empty."); }
		
		m_name = pluginName.trim();
		m_version = pluginVersion.trim();
		m_directoryName = directoryName == null ? null : directoryName.trim();
		m_configFileName = configFileName.trim();
		m_jarFileName = jarFileName;
		m_classes = new HashMap<String, Class<?>>();
		m_loaded = false;
	}
	
	public String getName() {
		return m_name;
	}
	
	public String getVersion() {
		return m_version;
	}
	
	public abstract String getType();
	
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
	
	public int numberOfLoadedClasses() {
		return m_classes.size();
	}
	
	public Class<?> getLoadedClass(String className) {
		if(className == null) { return null; }
		
		return m_classes.get(className.replaceAll("[\\\\/]", ".").replaceAll("\\.[Cc][Ll][Aa][Ss][Ss]$", ""));
	}
	
	public boolean isLoaded() {
		return m_loaded;
	}
	
	protected boolean loadClasses() {
		if(m_loaded) { return true; }
		
		if(m_jarFileName == null || m_jarFileName.length() == 0) {
			m_loaded = true;
			
			return true;
		}
		
		InputStream in;
		String name;
		byte[] data;
		JarFile jarFile = null;
		JarEntry entry;
		Class<?> c;
		
		try {
			jarFile = new JarFile(Utilities.appendSlash(SettingsManager.instance.pluginDirectoryName) + Utilities.appendSlash(m_directoryName) + "/" + m_jarFileName);
			
			Pattern p = Pattern.compile(".*\\.class$", Pattern.CASE_INSENSITIVE);
			
			Enumeration<JarEntry> contents = jarFile.entries();
			while(contents.hasMoreElements()) {
				entry = contents.nextElement();
				if(p.matcher(entry.getName()).matches()) {
					in = jarFile.getInputStream(entry);
					if(in.available() < 1) {
						jarFile.close();
						
						return false;
					}
					data = new byte[in.available()];
					in.read(data);
					
					name = entry.getName().replaceAll("[\\\\/]", ".").replaceAll("\\.[Cc][Ll][Aa][Ss][Ss]$", "");
					
					c = ExtendedClassLoader.instance.deserializeClass(name, data);
					if(c == null) {
						jarFile.close();
						
						SystemConsole.instance.writeLine("Failed to deserialize class from jar file \"" + m_jarFileName + "\": \"" + name + "\".");
						
						return false;
					}
					
					m_classes.put(name, c);
				}
			}
			
			m_loaded = true;
			
			jarFile.close();
			
			return true;
		}
		catch(IOException e) {
			SystemConsole.instance.writeLine("Exception thrown while deserializing classes from jar file \"" + m_jarFileName + "\": " + e.getMessage());
			
			try { jarFile.close(); } catch(Exception x) { }
			
			return false;
		}
	}
	
	protected static String readCFGPluginDefinitionFileVersion(BufferedReader in, File file) throws IOException, PluginLoadException {
		if(in == null || file == null) { return null; }
		
		String input, line;
		while(true) {
			input = in.readLine();
			if(input == null) {
				in.close();
				throw new PluginLoadException("Plugin definition file \"" + file.getName() + "\" incomplete or corrupted, no header found.");
			}
			
			line = input.trim();
			if(line.length() == 0 || Utilities.isComment(line)) { continue; }
			
			if(!line.matches("^.* ([0-9]\\.?)+$")) {
				in.close();
				throw new PluginLoadException("Plugin definition file \"" + file.getName() + "\" has an invalid header: \"" + line + "\".");
			}
			String[] headerData = new String[2];
			int separatorIndex = line.lastIndexOf(' ');
			if(separatorIndex < 0) {
				in.close();
				throw new PluginLoadException("Plugin definition file \"" + file.getName() + "\" is missing version number in header.");
			}
			headerData[0] = line.substring(0, separatorIndex);
			headerData[1] = line.substring(separatorIndex + 1, line.length());
			
			if(!headerData[0].trim().equalsIgnoreCase(CFG_PLUGIN_DEFINITION_FILE_HEADER)) {
				in.close();
				throw new PluginLoadException("Plugin definition file \"" + file.getName() + "\" has an invalid header: \"" + headerData[0] + "\", expected \"" + CFG_PLUGIN_DEFINITION_FILE_HEADER + "\".");
			}
			
			return headerData[1];
		}
	}
	
	protected static boolean verifyCFGPluginDefinitionFileVersion(String version) {
		if(version == null) { return false; }
		String formattedVersion = version.trim();
		if(formattedVersion.length() == 0) { return false; }
		
		return Utilities.compareVersions(CFG_PLUGIN_DEFINITION_FILE_VERSION, formattedVersion) == 0;
	}
	
	public static boolean isPlugin(File file) {
		if(file == null || !file.exists() || !file.isFile()) { return false; }
		
		String fileExtension = Utilities.getFileExtension(file.getName());
		if(fileExtension == null) {
			SystemConsole.instance.writeLine("Plugin definition file \"" + file.getName() + "\" has no file extension.");
			
			return false;
		}
		
		if(fileExtension.equalsIgnoreCase("cfg")) {
			return isCFGPlugin(file);
		}
		else if(fileExtension.equalsIgnoreCase("xml")) {
			return isXMLPlugin(file);
		}
		return false;
	}
	
	protected static boolean isCFGPlugin(File file) {
		if(file == null || !file.exists() || !file.isFile()) { return false; }
		
		BufferedReader in = null;
		
		try {
			in = new BufferedReader(new FileReader(file));
			
			try {
				if(!verifyCFGPluginDefinitionFileVersion(readCFGPluginDefinitionFileVersion(in, file))) {
					in.close();
					
					return false;
				}
			}
			catch(IllegalArgumentException e) {
				in.close();
				
				return false;
			}
			
			in.close();
			
			return true;
		}
		catch(Exception e) {
			return false;
		}
	}
	
	public static boolean isXMLPlugin(File file) {
		if(file == null || !file.exists() || !file.isFile()) { return false; }
		
		BufferedReader in = null;
		
		try { in = new BufferedReader(new FileReader(file)); }
		catch(FileNotFoundException e) { return false; }
		
		String node = null;
		
		try {
			XMLEvent event = null;
			XMLInputFactory inputFactory = XMLInputFactory.newInstance();
			XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
			
			while(eventReader.hasNext()) {
				event = eventReader.nextEvent();
				
				if(event.isStartElement() || event.isEndElement()) {
					node = event.asStartElement().getName().getLocalPart();
					
					try { in.close(); } catch (IOException e) { }
					
					return node.equalsIgnoreCase("plugin");
				}
			}
		}
		catch(XMLStreamException e) { }
		
		try { in.close(); } catch (IOException e) { }
		
		return false;
	}
	
	public static boolean isPluginOfType(File file, String type) {
		if(file == null || type == null) { return false; }
		
		String formattedType = type.trim();
		if(formattedType.length() == 0) { return false; }
		
		String pluginType = getPluginType(file);
		return pluginType != null && pluginType.equalsIgnoreCase(formattedType);
	}
	
	public static String getPluginName(File file) {
		PluginInfo pluginInfo = PluginInfo.getPluginInfo(file);
		if(!PluginInfo.isValid(pluginInfo)) { return null; }
		
		return pluginInfo.getName();
	}

	public static String getPluginVersion(File file) {
		PluginInfo pluginInfo = PluginInfo.getPluginInfo(file);
		if(!PluginInfo.isValid(pluginInfo)) { return null; }
		
		return pluginInfo.getVersion();
	}
	
	public static String getPluginType(File file) {
		PluginInfo pluginInfo = PluginInfo.getPluginInfo(file);
		if(!PluginInfo.isValid(pluginInfo)) { return null; }
		
		return pluginInfo.getType();
	}
	
	protected abstract boolean loadFromCFGFile(BufferedReader in) throws PluginLoadException;
	
	protected abstract boolean loadFromXMLFile(BufferedReader in, XMLEventReader eventReader) throws PluginLoadException;
	
	public boolean equals(Object o) {
		if(o == null || !(o instanceof Plugin)) { return false; }
		
		Plugin p = ((Plugin) o);
		
		if(m_name == null || p.m_name == null) { return false; }
		
		return m_name.equalsIgnoreCase(p.m_name);
	}
	
	public String toString() {
		return m_name;
	}
	
}
