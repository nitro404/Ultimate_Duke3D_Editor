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

public class GroupProcessorPlugin extends Plugin {
	
	protected String m_groupProcessorClassName;
	protected String m_groupProcessorPanelClassName;
	protected Class<?> m_groupProcessorClass;
	protected Class<?> m_groupProcessorPanelClass;
	
	public static final String PLUGIN_TYPE = "Group Processor";
	
	public GroupProcessorPlugin(String pluginName, String pluginVersion, String jarFileName, String configFileName, String directoryPath) {
		super(pluginName, pluginVersion, jarFileName, configFileName, directoryPath);
		m_groupProcessorClassName = null;
		m_groupProcessorPanelClassName = null;
		m_groupProcessorClass = null;
		m_groupProcessorPanelClass = null;
	}
	
	public String getType() {
		return PLUGIN_TYPE;
	}
	
	public String getGroupProcessorClassName() {
		return m_groupProcessorClassName;
	}
	
	public String getGroupProcessorPanelClassName() {
		return m_groupProcessorClassName;
	}
	
	public Class<?> getGroupProcessorClass() {
		return m_groupProcessorClass;
	}
	
	public Class<?> getGroupProcessorPanelClass() {
		return m_groupProcessorPanelClass;
	}
	
	public GroupProcessor getNewGroupProcessorInstance() throws GroupProcessorInstantiationException {
		if(m_groupProcessorClass == null) { return null; }
		
		Constructor<?> constructor = null;
		try { constructor = m_groupProcessorClass.getDeclaredConstructor(); }
		catch(Exception e) { }
		
		if(constructor == null) {
			throw new GroupProcessorInstantiationException("Group processor class \"" + m_groupProcessorClassName + "\" must contain a constructor which takes no arguments.");
		}
		
		GroupProcessor newGroupProcessor = null;
		try {
			newGroupProcessor = (GroupProcessor) constructor.newInstance();
		}
		catch(Exception e) {
			SystemConsole.instance.writeException(e);

			throw new GroupProcessorInstantiationException("Failed to instantiate group processor class \"" + m_groupProcessorClassName + "\": " + e.getMessage());
		}
		
		return newGroupProcessor;
	}
	
	public GroupProcessorPanel getNewGroupProcessorPanelInstance(GroupProcessor groupProcessor) throws GroupProcessorPanelInstantiationException {
		if(m_groupProcessorPanelClass == null || groupProcessor == null) { return null; }
		
		Constructor<?> constructor = null;
		try { constructor = m_groupProcessorPanelClass.getDeclaredConstructor(GroupProcessor.class); }
		catch(Exception e) { }
		
		if(constructor == null) {
			throw new GroupProcessorPanelInstantiationException("Group processor panel class \"" + m_groupProcessorPanelClassName + "\" must contain a constructor which takes a GroupProcessor as an argument.");
		}
		
		GroupProcessorPanel newGroupProcessorPanel = null;
		try {
			newGroupProcessorPanel = (GroupProcessorPanel) constructor.newInstance(groupProcessor);
		}
		catch(Exception e) {
			SystemConsole.instance.writeException(e);

			throw new GroupProcessorPanelInstantiationException("Failed to instantiate group processor panel class \"" + m_groupProcessorPanelClassName + "\": " + e.getMessage());
		}
		
		return newGroupProcessorPanel;
	}
	
	public static boolean isGroupProcessorPlugin(File file) throws GroupProcessorPluginLoadException {
		return Plugin.isPluginOfType(file, PLUGIN_TYPE);
	}
	
	protected boolean loadFromCFGFile(BufferedReader in) throws PluginLoadException {
		if(in == null) { return false; }
		
		String input = null;
		String line = null;
		Variable v = null;
		String groupProcessorClassName = null;
		String groupProcessorPanelClassName = null;
		try {
			while(true) {
				input = in.readLine();
				if(input == null) {
					try { in.close(); } catch(IOException e) { }
					
					throw new GroupProcessorPluginLoadException("Unexpected end of file encountered when reading \"" + m_name + "\" plugin definition file.");
				}
				
				line = input.trim();
				if(line.isEmpty() || Utilities.isComment(line)) { continue; }
				
				v = Variable.parseFrom(line);
				if(v == null) { continue; }
				
				if(v.getID().equalsIgnoreCase("Group Processor Class Name")) {
					if(groupProcessorClassName != null) {
						SystemConsole.instance.writeLine("Multiple entries found for group processor class name in \"" + m_name + "\" plugin definition file.");
					}
					
					groupProcessorClassName = v.getValue();
				}
				else if(v.getID().equalsIgnoreCase("Group Processor Panel Class Name")) {
					if(groupProcessorPanelClassName != null) {
						SystemConsole.instance.writeLine("Multiple entries found for group processor panel class name in \"" + m_name + "\" plugin definition file.");
					}
					
					groupProcessorPanelClassName = v.getValue();
				}
				else {
					SystemConsole.instance.writeLine("Encountered unexpected property \"" + v.getID() + "\" in \"" + m_name + "\" plugin definition file.");
				}
				
				if(groupProcessorClassName != null && groupProcessorPanelClassName != null) {
					break;
				}
			}
		}
		catch(IOException e) {
			try { in.close(); } catch(IOException e2) { }
			
			throw new GroupProcessorPluginLoadException("Read exception thrown while reading \"" + m_name + "\" plugin definition file: " + e.getMessage());
		}
		
		m_groupProcessorClassName = groupProcessorClassName;
		m_groupProcessorPanelClassName = groupProcessorPanelClassName == null ? null : (groupProcessorPanelClassName.isEmpty() ? null : groupProcessorPanelClassName);
		
		m_groupProcessorClass = null;
		try { m_groupProcessorClass = ExtendedClassLoader.instance.loadClass(m_groupProcessorClassName); }
		catch(ClassNotFoundException e) {
			try { in.close(); } catch(IOException e2) { }
			
			throw new GroupProcessorPluginLoadException("Class " + m_groupProcessorClassName + " is missing or not loaded.");
		}
		if(!(GroupProcessor.class.isAssignableFrom(m_groupProcessorClass))) {
			try { in.close(); } catch(IOException e) { }
			
			throw new GroupProcessorPluginLoadException("Class " + m_groupProcessorClassName + " does not extend GroupProcessor class.");
		}
		
		m_groupProcessorPanelClass = null;
		if(m_groupProcessorPanelClassName != null && m_groupProcessorPanelClassName.length() > 0) {
			try { m_groupProcessorPanelClass = ExtendedClassLoader.instance.loadClass(m_groupProcessorPanelClassName); }
			catch(ClassNotFoundException e) {
				try { in.close(); } catch(IOException e2) { }
				
				throw new GroupProcessorPluginLoadException("Class " + m_groupProcessorPanelClassName + " is missing or not loaded.");
			}
			if(!(GroupProcessorPanel.class.isAssignableFrom(m_groupProcessorPanelClass))) {
				try { in.close(); } catch(IOException e) { }
				
				throw new GroupProcessorPluginLoadException("Class " + m_groupProcessorPanelClassName + " does not extend GroupProcessorPanel class.");
			}
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
		String className = null;
		String classType = null;
		String groupProcessorClassName = null;
		String groupProcessorPanelClassName = null;
		
		try {
			while(eventReader.hasNext()) {
				event = eventReader.nextEvent();
				
				if(event.isStartElement()) {
					node = event.asStartElement().getName().getLocalPart();
					
					if(node.equalsIgnoreCase("classes")) {
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
										
										throw new FilePluginLoadException("Missing attribute in class XML node, both \"type\" and \"name\" must be specified.");
									}
									else if(classType.equalsIgnoreCase("Group Processor")) {
										if(groupProcessorClassName != null) {
											SystemConsole.instance.writeLine("Group processor class specified multiple times inside classes XML node of plugin definition file for group plugin \"" + m_name + "\".");
										}
										
										groupProcessorClassName = className;
										
										if(groupProcessorClassName.isEmpty()) {
											try { in.close(); } catch (IOException e2) { }
											
											throw new PluginLoadException("Empty group processor class name specified inside classes XML node of plugin definition file for group plugin \"" + m_name + "\".");
										}
									}
									else if(classType.equalsIgnoreCase("Group Processor Panel")) {
										if(groupProcessorPanelClassName != null) {
											SystemConsole.instance.writeLine("Group processor panel class specified multiple times inside classes XML node of plugin definition file for group plugin \"" + m_name + "\".");
										}
										
										groupProcessorPanelClassName = className;
										
										if(groupProcessorPanelClassName.isEmpty()) {
											try { in.close(); } catch (IOException e2) { }
											
											throw new PluginLoadException("Empty group processor panel class name specified inside classes XML node of plugin definition file for group plugin \"" + m_name + "\".");
										}
									}
									else {
										SystemConsole.instance.writeLine("Unexpected class type encountered inside classes node: \"" + node + "\", expected \"Group Processor\" or \"Group Processor Panel\".");
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
		
		if(groupProcessorClassName == null) {
			try { in.close(); } catch(IOException e) { }
			
			throw new FilePluginLoadException("Group plugin \"" + m_name + "\" missing group processor class name specification in plugin definition file.");
		}
		
		m_groupProcessorClassName = groupProcessorClassName;
		m_groupProcessorPanelClassName = groupProcessorPanelClassName == null ? null : (groupProcessorPanelClassName.isEmpty() ? null : groupProcessorPanelClassName);
		
		m_groupProcessorClass = null;
		try { m_groupProcessorClass = ExtendedClassLoader.instance.loadClass(m_groupProcessorClassName); }
		catch(ClassNotFoundException e) {
			try { in.close(); } catch(IOException e2) { }
			
			throw new GroupProcessorPluginLoadException("Class " + m_groupProcessorClassName + " is missing or not loaded.");
		}
		if(!(GroupProcessor.class.isAssignableFrom(m_groupProcessorClass))) {
			try { in.close(); } catch(IOException e) { }
			
			throw new GroupProcessorPluginLoadException("Class " + m_groupProcessorClassName + " does not extend GroupProcessor class.");
		}
		
		m_groupProcessorPanelClass = null;
		if(m_groupProcessorPanelClassName != null && m_groupProcessorPanelClassName.length() > 0) {
			try { m_groupProcessorPanelClass = ExtendedClassLoader.instance.loadClass(m_groupProcessorPanelClassName); }
			catch(ClassNotFoundException e) {
				try { in.close(); } catch(IOException e2) { }
				
				throw new GroupProcessorPluginLoadException("Class " + m_groupProcessorPanelClassName + " is missing or not loaded.");
			}
			if(!(GroupProcessorPanel.class.isAssignableFrom(m_groupProcessorPanelClass))) {
				try { in.close(); } catch(IOException e) { }
				
				throw new GroupProcessorPluginLoadException("Class " + m_groupProcessorPanelClassName + " does not extend GroupProcessorPanel class.");
			}
		}
		
		return true;
	}
	
	public boolean equals(Object o) {
		if(o == null || !(o instanceof GroupProcessorPlugin)) { return false; }
		
		GroupProcessorPlugin p = ((GroupProcessorPlugin) o);
		
		if(m_name == null || p.m_name == null) { return false; }
		
		return m_name.equalsIgnoreCase(p.m_name);
	}
	
}
