package group;

import java.util.*;
import java.io.*;
import java.lang.reflect.*;
import javax.xml.stream.*;
import javax.xml.stream.events.*;
import exception.*;
import utilities.*;
import variable.*;
import console.*;
import plugin.*;
import gui.*;

public class GroupPlugin extends Plugin {
	
	protected Vector<String> m_supportedGroupFileFormats;
	protected String m_groupClassName;
	protected String m_groupPanelClassName;
	protected Class<?> m_groupClass;
	protected Class<?> m_groupPanelClass;
	
	public static final String PLUGIN_TYPE = "Group";
	
	public GroupPlugin(String pluginName, String pluginVersion, String jarFileName, String configFileName, String directoryName) {
		super(pluginName, pluginVersion, jarFileName, configFileName, directoryName);
		m_supportedGroupFileFormats = new Vector<String>();
		m_groupClassName = null;
		m_groupPanelClassName = null;
		m_groupClass = null;
		m_groupPanelClass = null;
	}
	
	public String getType() {
		return PLUGIN_TYPE;
	}
	
	public int numberOfSupportedGroupFileFormats() {
		return m_supportedGroupFileFormats.size();
	}
	
	public String getSupportedGroupFileFormat(int index) {
		if(index < 0 || index >= m_supportedGroupFileFormats.size()) { return null; }
		return m_supportedGroupFileFormats.elementAt(index);
	}
	
	public String getSupportedGroupFileFormatsAsString() {
		String listOfSupportedGroupFileFormats = "";
		
		for(int i=0;i<m_supportedGroupFileFormats.size();i++) {
			listOfSupportedGroupFileFormats += m_supportedGroupFileFormats.elementAt(i);
			
			if(i < m_supportedGroupFileFormats.size() - 1) {
				listOfSupportedGroupFileFormats += ", ";
			}
		}
		
		return listOfSupportedGroupFileFormats;
	}
	
	public Vector<String> getSupportedGroupFileFormats() {
		return m_supportedGroupFileFormats;
	}
	
	public boolean hasSupportedGroupFileFormat(String fileFormat) {
		if(fileFormat == null) { return false; }
		String formattedFileFormat = fileFormat.trim();
		if(formattedFileFormat.length() == 0) { return false; }
		
		for(int i=0;i<m_supportedGroupFileFormats.size();i++) {
			if(m_supportedGroupFileFormats.elementAt(i).equalsIgnoreCase(formattedFileFormat)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean hasSharedSupportedGroupFileFormat(GroupPlugin groupPlugin) {
		if(groupPlugin == null) { return false; }
		
		for(int i=0;i<groupPlugin.numberOfSupportedGroupFileFormats();i++) {
			if(hasSupportedGroupFileFormat(groupPlugin.getSupportedGroupFileFormat(i))) {
				return true;
			}
		}
		return false;
	}
	
	public int numberOfSharedSupportedGroupFileFormats(GroupPlugin groupPlugin) {
		if(groupPlugin == null) { return 0; }
		
		int numberOfSharedSupportedGroupFileFormats = 0;
		for(int i=0;i<groupPlugin.numberOfSupportedGroupFileFormats();i++) {
			if(hasSupportedGroupFileFormat(groupPlugin.getSupportedGroupFileFormat(i))) {
				numberOfSharedSupportedGroupFileFormats++;
			}
		}
		return numberOfSharedSupportedGroupFileFormats;
	}

	public Vector<String> getSharedSupportedGroupFileFormats(GroupPlugin groupPlugin) {
		if(groupPlugin == null) { return null; }
		
		Vector<String> sharedSupportedGroupFileFormats = new Vector<String>();
		for(int i=0;i<groupPlugin.numberOfSupportedGroupFileFormats();i++) {
			if(hasSupportedGroupFileFormat(groupPlugin.getSupportedGroupFileFormat(i))) {
				sharedSupportedGroupFileFormats.add(groupPlugin.getSupportedGroupFileFormat(i));
			}
		}
		return sharedSupportedGroupFileFormats;
	}
	
	public String getSharedSupportedGroupFileFormatsAsString(GroupPlugin groupPlugin) {
		if(groupPlugin == null) { return null; }
		
		String sharedSupportedGroupFileFormats = new String();
		for(int i=0;i<groupPlugin.numberOfSupportedGroupFileFormats();i++) {
			if(hasSupportedGroupFileFormat(groupPlugin.getSupportedGroupFileFormat(i))) {
				if(sharedSupportedGroupFileFormats.length() > 0) {
					sharedSupportedGroupFileFormats += ", ";
				}
				
				sharedSupportedGroupFileFormats += groupPlugin.getSupportedGroupFileFormat(i);
			}
		}
		return sharedSupportedGroupFileFormats;
	}
	
	public int indexOfSupportedGroupFileFormat(String fileFormat) {
		if(fileFormat == null) { return -1; }
		String formattedFileFormat = fileFormat.trim();
		if(formattedFileFormat.length() == 0) { return -1; }
		
		for(int i=0;i<m_supportedGroupFileFormats.size();i++) {
			if(m_supportedGroupFileFormats.elementAt(i).equalsIgnoreCase(formattedFileFormat)) {
				return i;
			}
		}
		return -1;
	}
	
	public boolean addSupportedGroupFileFormat(String fileFormat) {
		if(fileFormat == null) { return false; }
		String formattedFileFormat = fileFormat.trim();
		if(formattedFileFormat.length() == 0) { return false; }
		
		for(int i=0;i<m_supportedGroupFileFormats.size();i++) {
			if(m_supportedGroupFileFormats.elementAt(i).equalsIgnoreCase(formattedFileFormat)) {
				return false;
			}
		}
		
		m_supportedGroupFileFormats.add(formattedFileFormat);
		
		return true;
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
	
	public Group getNewGroupInstance(File groupFile) throws GroupInstantiationException {
		if(m_groupClass == null) { return null; }
		
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
	
	public GroupPanel getNewGroupPanelInstance(Group group) throws GroupPanelInstantiationException {
		if(m_groupPanelClass == null) { return null; }
		
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
	
	public static boolean isGroupPlugin(File file) {
		return Plugin.isPluginOfType(file, PLUGIN_TYPE);
	}
	
	protected boolean loadFromCFGFile(BufferedReader in) throws PluginLoadException {
		if(in == null) { return false; }
		
		String input = null;
		String line = null;
		Variable v = null;
		String supportedGroupFileFormats = null;
		String groupClassName = null;
		String groupPanelClassName = null;
		
		try {
			while(true) {
				input = in.readLine();
				if(input == null) {
					try { in.close(); } catch(IOException e) { }
					
					throw new GroupPluginLoadException("Unexpected end of file encountered when reading \"" + m_name + "\" plugin definition file.");
				}
				
				line = input.trim();
				if(line.length() == 0 || Utilities.isComment(line)) { continue; }
				
				v = Variable.parseFrom(line);
				if(v == null) { continue; }
				
				if(v.getID().equalsIgnoreCase("Supported Group File Formats")) {
					supportedGroupFileFormats = v.getValue();
					
					String supportedGroupFileFormat = null;
					String supportedGroupFileFormatList[] = supportedGroupFileFormats.split("[;, \t]");
					for(int i=0;i<supportedGroupFileFormatList.length;i++) {
						supportedGroupFileFormat = supportedGroupFileFormatList[i].trim();
						if(supportedGroupFileFormat.length() > 0) {
							addSupportedGroupFileFormat(supportedGroupFileFormat);
						}
					}
					
					if(numberOfSupportedGroupFileFormats() == 0) {
						try { in.close(); } catch(IOException e) { }
						
						throw new GroupPluginLoadException("Group plugin \"" + m_name + "\" must support at least one file format.");
					}
				}
				else if(v.getID().equalsIgnoreCase("Group Class Name")) {
					if(groupClassName != null) {
						SystemConsole.instance.writeLine("Multiple entries found for group class name in \"" + m_name + "\" plugin definition file.");
					}
					
					groupClassName = v.getValue();
				}
				else if(v.getID().equalsIgnoreCase("Group Panel Class Name")) {
					if(groupPanelClassName != null) {
						SystemConsole.instance.writeLine("Multiple entries found for group panel class name in \"" + m_name + "\" plugin definition file.");
					}
					
					groupPanelClassName = v.getValue();
				}
				else {
					SystemConsole.instance.writeLine("Encountered unexpected property \"" + v.getID() + "\" in \"" + m_name + "\" plugin definition file.");
				}
				
				if(supportedGroupFileFormats != null && groupClassName != null && groupPanelClassName != null) {
					break;
				}
			}
		}
		catch(IOException e) {
			try { in.close(); } catch(IOException e2) { }
			
			throw new GroupPluginLoadException("Read exception thrown while reading \"" + m_name + "\" plugin definition file: " + e.getMessage());
		}
		
		m_groupClassName = groupClassName;
		m_groupPanelClassName = groupPanelClassName;
		
		m_groupClass = null;
		try { m_groupClass = ExtendedClassLoader.instance.loadClass(m_groupClassName); }
		catch(ClassNotFoundException e) {
			try { in.close(); } catch(IOException e2) { }
			
			throw new GroupPluginLoadException("Class " + m_groupClassName + " is missing or not loaded.");
		}
		if(!(Group.class.isAssignableFrom(m_groupClass))) {
			try { in.close(); } catch(IOException e) { }
			
			throw new GroupPluginLoadException("Class " + m_groupClassName + " does not extend Group class.");
		}
		
		m_groupPanelClass = null;
		try { m_groupPanelClass = ExtendedClassLoader.instance.loadClass(m_groupPanelClassName); }
		catch(ClassNotFoundException e) {
			try { in.close(); } catch(IOException e2) { }
			
			throw new GroupPluginLoadException("Class " + m_groupPanelClassName + " is missing or not loaded.");
		}
		if(!(GroupPanel.class.isAssignableFrom(m_groupPanelClass))) {
			try { in.close(); } catch(IOException e) { }
			
			throw new GroupPluginLoadException("Class " + m_groupPanelClassName + " does not extend GroupPanel class.");
		}
		
		return true;
	}
	
	protected boolean loadFromXMLFile(BufferedReader in, XMLEventReader eventReader) throws PluginLoadException {
		if(in == null || eventReader == null) { return false; }
		
		XMLEvent event = null;
		String node = null;
		Iterator<?> attributes = null;
		Attribute attribute = null;
		String attributeName = null;
		String attributeValue = null;
		String extension = null;
		String className = null;
		String classType = null;
		String groupClassName = null;
		String groupPanelClassName = null;
		
		try {
			while(eventReader.hasNext()) {
				event = eventReader.nextEvent();
				
				if(event.isStartElement()) {
					node = event.asStartElement().getName().getLocalPart();
					
					if(node.equalsIgnoreCase("formats")) {
						while(eventReader.hasNext()) {
							event = eventReader.nextEvent();
							
							if(event.isStartElement()) {
								node = event.asStartElement().getName().getLocalPart();
								
								if(node.equalsIgnoreCase("format")) {
									attributes = event.asStartElement().getAttributes();
									
									while(attributes.hasNext()) {
										attribute = (Attribute) attributes.next();
										
										attributeName = attribute.getName().toString();
										attributeValue = attribute.getValue().toString().trim();
										
										if(attributeName.equalsIgnoreCase("extension")) {
											if(extension != null) {
												SystemConsole.instance.writeLine("Attribute \"extension\" specified multiple times inside format XML node of plugin definition file for group plugin \"" + m_name + "\".");
											}
											
											extension = attributeValue;
										}
										else {
											SystemConsole.instance.writeLine("Unexpected XML node attribute encounted inside format node: \"" + attributeName + "\", expected \"extension\"."); 
										}
									}
								}
								else {
									SystemConsole.instance.writeLine("Unexpected XML node start tag encountered inside formats node: \"" + node + "\", expected \"format\".");
								}
							}
							else if(event.isEndElement()) {
								node = event.asEndElement().getName().getLocalPart();
								
								if(node.equalsIgnoreCase("format")) {
									if(extension == null) {
										SystemConsole.instance.writeLine("Missing \"extension\" attribute in format XML node of plugin definition file for group plugin \"" + m_name + "\".");
									}
									else if(extension.length() == 0) {
										SystemConsole.instance.writeLine("Encountered empty file extension in format XML node of plugin definition file for group plugin \"" + m_name + "\".");
									}
									else {
										addSupportedGroupFileFormat(extension);
										
										extension = null;
									}
								}
								else if(node.equalsIgnoreCase("formats")) {
									break;
								}
								else {
									SystemConsole.instance.writeLine("Unexpected XML node close tag encountered inside formats node: \"" + node + "\", expected \"format\" or \"formats\".");
								}
							}
						}
					}
					else if(node.equalsIgnoreCase("classes")) {
						while(eventReader.hasNext()) {
							event = eventReader.nextEvent();
							
							if(event.isStartElement()) {
								node = event.asStartElement().getName().getLocalPart();
								
								if(node.equalsIgnoreCase("class")) {
									attributes = event.asStartElement().getAttributes();
									
									while(attributes.hasNext()) {
										attribute = (Attribute) attributes.next();
										
										attributeName = attribute.getName().toString();
										attributeValue = attribute.getValue().toString().trim();
										
										if(attributeName.equalsIgnoreCase("type")) {
											if(classType != null) {
												SystemConsole.instance.writeLine("Attribute \"type\" specified multiple times inside class XML node of plugin definition file for group plugin \"" + m_name + "\".");
											}
											
											classType = attributeValue;
										}
										else if(attributeName.equalsIgnoreCase("name")) {
											if(className != null) {
												SystemConsole.instance.writeLine("Attribute \"name\" specified multiple times inside class XML node of plugin definition file for group plugin \"" + m_name + "\".");
											}
											
											className = attributeValue;
										}
										else {
											SystemConsole.instance.writeLine("Unexpected XML node attribute encounted inside class node: \"" + attributeName + "\", expected \"type\" or \"name\"."); 
										}
									}
								}
								else {
									SystemConsole.instance.writeLine("Unexpected XML node start tag encountered inside classes node: \"" + node + "\", expected \"class\".");
								}
							}
							else if(event.isEndElement()) {
								node = event.asEndElement().getName().getLocalPart();
								
								if(node.equalsIgnoreCase("class")) {
									if(classType == null || className == null) {
										try { in.close(); } catch(IOException e) { }
										
										throw new GroupPluginLoadException("Missing attribute in class XML node, both \"type\" and \"name\" must be specified.");
									}
									else if(classType.equalsIgnoreCase("Group")) {
										if(groupClassName != null) {
											SystemConsole.instance.writeLine("Group class specified multiple times inside classes XML node of plugin definition file for group plugin \"" + m_name + "\".");
										}
										
										groupClassName = className;
										
										if(groupClassName.length() == 0) {
											try { in.close(); } catch (IOException e2) { }
											
											throw new PluginLoadException("Empty group class name specified inside classes XML node of plugin definition file for group plugin \"" + m_name + "\".");
										}
									}
									else if(classType.equalsIgnoreCase("Group Panel")) {
										if(groupPanelClassName != null) {
											SystemConsole.instance.writeLine("Group panel class specified multiple times inside classes XML node of plugin definition file for group plugin \"" + m_name + "\".");
										}
										
										groupPanelClassName = className;
										
										if(groupPanelClassName.length() == 0) {
											try { in.close(); } catch (IOException e2) { }
											
											throw new PluginLoadException("Empty group panel class name specified inside classes XML node of plugin definition file for group plugin \"" + m_name + "\".");
										}
									}
									else {
										SystemConsole.instance.writeLine("Unexpected class type encountered inside classes node: \"" + node + "\", expected \"Group\" or \"Group Panel\".");
									}
									
									className = null;
									classType = null;
								}
								else if(node.equalsIgnoreCase("classes")) {
									break;
								}
								else {
									SystemConsole.instance.writeLine("Unexpected XML node close tag encountered inside classes node: \"" + node + "\", expected \"class\" or \"classes\".");
								}
							}
						}
					}
					else {
						SystemConsole.instance.writeLine("Unexpected XML node start tag encountered inside plugin node: \"" + node + "\", expected \"formats\" or \"classes\".");
					}
				}
				else if(event.isEndElement()) {
					node = event.asEndElement().getName().getLocalPart();
					
					if(node.equalsIgnoreCase("plugin")) {
						break;
					}
					else {
						SystemConsole.instance.writeLine("Unexpected XML node close tag encountered: \"" + node + "\", expected \"plugin\".");
					}
				}
			}
		}
		catch(XMLStreamException e) {
			try { in.close(); } catch (IOException e2) { }
			
			throw new PluginLoadException("XML exception thrown while reading plugin definition file: " + e.getMessage());
		}
		
		if(numberOfSupportedGroupFileFormats() == 0) {
			try { in.close(); } catch(IOException e) { }
			
			throw new GroupPluginLoadException("Group plugin \"" + m_name + "\" must support at least one file format.");
		}
		
		if(groupClassName == null) {
			try { in.close(); } catch(IOException e) { }
			
			throw new GroupPluginLoadException("Group plugin \"" + m_name + "\" missing group class name specification in plugin definition file.");
		}
		
		if(groupPanelClassName == null) {
			try { in.close(); } catch(IOException e) { }
			
			throw new GroupPluginLoadException("Group plugin \"" + m_name + "\" missing group panel class name specification in plugin definition file.");
		}
		
		m_groupClassName = groupClassName;
		m_groupPanelClassName = groupPanelClassName;
		
		m_groupClass = null;
		try { m_groupClass = ExtendedClassLoader.instance.loadClass(m_groupClassName); }
		catch(ClassNotFoundException e) {
			try { in.close(); } catch(IOException e2) { }
			
			throw new GroupPluginLoadException("Class " + m_groupClassName + " is missing or not loaded.");
		}
		if(!(Group.class.isAssignableFrom(m_groupClass))) {
			try { in.close(); } catch(IOException e) { }
			
			throw new GroupPluginLoadException("Class " + m_groupClassName + " does not extend Group class.");
		}
		
		m_groupPanelClass = null;
		try { m_groupPanelClass = ExtendedClassLoader.instance.loadClass(m_groupPanelClassName); }
		catch(ClassNotFoundException e) {
			try { in.close(); } catch(IOException e2) { }
			
			throw new GroupPluginLoadException("Class " + m_groupPanelClassName + " is missing or not loaded.");
		}
		if(!(GroupPanel.class.isAssignableFrom(m_groupPanelClass))) {
			try { in.close(); } catch(IOException e) { }
			
			throw new GroupPluginLoadException("Class " + m_groupPanelClassName + " does not extend GroupPanel class.");
		}
		
		return true;
	}
	
	public boolean equals(Object o) {
		if(o == null || !(o instanceof GroupPlugin)) { return false; }
		
		GroupPlugin p = ((GroupPlugin) o);
		
		if(m_name == null || p.m_name == null) { return false; }
		
		return m_name.equalsIgnoreCase(p.m_name);
	}
	
}
