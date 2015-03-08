package plugin;

import java.util.*;
import java.io.*;

import javax.xml.stream.*;
import javax.xml.stream.events.*;

import utilities.*;
import variable.*;
import console.*;

public class PluginInfo {
	
	public String m_name;
	public String m_version;
	public String m_type;
	
	public PluginInfo(String name, String version, String type) {
		setName(name);
		setVersion(version);
		setType(type);
	}
	
	public String getName() {
		return m_name;
	}

	public String getVersion() {
		return m_version;
	}

	public String getType() {
		return m_type;
	}
	
	public void setName(String name) {
		m_name = name == null ? null : name.trim();
	}
	
	public void setVersion(String version) {
		m_version = version == null ? null : version.trim();
	}
	
	public void setType(String type) {
		m_type = type == null ? null : type.trim();
	}
	
	public static PluginInfo getPluginInfo(File file) {
		if(file == null || !file.isFile() || !file.exists()) {
			SystemConsole.instance.writeLine("Plugin definition file \"" + file.getName() + "\" is missing or invalid.");
			
			return null;
		}
		
		String fileExtension = Utilities.getFileExtension(file.getName());
		if(fileExtension == null) {
			SystemConsole.instance.writeLine("Plugin definition file \"" + file.getName() + "\" has no file extension.");
			
			return null;
		}
		
		if(fileExtension.equalsIgnoreCase("cfg")) {
			return getCFGPluginInfo(file);
		}
		else if(fileExtension.equalsIgnoreCase("xml")) {
			return getXMLPluginInfo(file);
		}
		else {
			SystemConsole.instance.writeLine("Plugin definition file \"" + file.getName() + "\" has unsupported file extension: \"" + fileExtension + "\".");
			
			return null;
		}
	}
	
	protected static PluginInfo getCFGPluginInfo(File file) {
		if(file == null || !file.isFile() || !file.exists()) {
			SystemConsole.instance.writeLine("Plugin definition file \"" + file.getName() + "\" is missing or invalid.");
			
			return null;
		}
		
		String input, line;
		BufferedReader in = null;
		Variable v = null;
		String name = null;
		String version = null;
		String type = null;
		
		try {
			in = new BufferedReader(new FileReader(file));
			
			try {
				if(!Plugin.verifyCFGPluginDefinitionFileVersion(Plugin.readCFGPluginDefinitionFileVersion(in, file))) {
					in.close();
					return null;
				}
			}
			catch(IllegalArgumentException e) {
				in.close();
				return null;
			}
			
			while(true) {
				input = in.readLine();
				if(input == null) {
					in.close();
					return null;
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
				
				if(name != null && version != null && type != null) {
					break;
				}
			}
			
			in.close();
		}
		catch(Exception e) {
			return null;
		}
		
		return new PluginInfo(name, version, type);
	}
	
	protected static PluginInfo getXMLPluginInfo(File file) {
		if(file == null || !file.isFile() || !file.exists()) {
			SystemConsole.instance.writeLine("Plugin definition file \"" + file.getName() + "\" is missing or invalid.");
			
			return null;
		}
		
		BufferedReader in = null;
		
		try {
			in = new BufferedReader(new FileReader(file));
		}
		catch(FileNotFoundException e) {
			SystemConsole.instance.writeLine("Missing plugin definition file \"" + file.getName() + "\": " + e.getMessage());
			
			return null;
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
									
									SystemConsole.instance.writeLine("Plugin definition file \"" + file.getName() + "\" contains multiple entries for plugin name.");
									
									return null;
								}
								
								name = attributeValue;
							}
							else if(attributeName.equalsIgnoreCase("version")) {
								if(version != null) {
									try { in.close(); } catch (IOException e) { }
									
									SystemConsole.instance.writeLine("Plugin definition file \"" + file.getName() + "\" contains multiple entries for plugin version.");
									
									return null;
								}
								
								version = attributeValue;
							}
							else if(attributeName.equalsIgnoreCase("type")) {
								if(type != null) {
									try { in.close(); } catch (IOException e) { }
									
									SystemConsole.instance.writeLine("Plugin definition file \"" + file.getName() + "\" contains multiple entries for plugin type.");
									
									return null;
								}
								
								type = attributeValue;
							}
							else {
								SystemConsole.instance.writeLine("Encountered unexpected attibute in plugin XML node: \"" + attributeName + "\".");
							}
						}
						
						if(name == null || version == null || type == null) {
							try { in.close(); } catch (IOException e) { }
							
							SystemConsole.instance.writeLine("Missing required attribute(s) in plugin XML node - name, version and type are required, jar is optional.");
							
							return null;
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
			
			SystemConsole.instance.writeLine("XML exception thrown while reading plugin definition file \"" + file.getName() + "\": " + e.getMessage());
			
			return null;
		}
		
		return new PluginInfo(name, version, type);
	}
	
	public boolean isValid() {
		return m_name != null &&
			   m_name.length() > 0 &&
			   m_version != null &&
			   m_version.length() > 0 &&
			   m_type != null &&
			   m_type.length() > 0;
	}
	
	public static boolean isValid(PluginInfo pluginInfo) {
		return pluginInfo != null &&
			   pluginInfo.isValid();
	}
	
	public boolean equals(Object o) {
		if(o == null || !(o instanceof PluginInfo)) { return false; }
		
		PluginInfo p = (PluginInfo) o;
		
		String localData = null;
		String externalData = null;
		for(int i=0;i<3;i++) {
			     if(i == 0) { localData = m_name;    externalData = p.m_name;    }
			else if(i == 0) { localData = m_version; externalData = p.m_version; }
			else if(i == 0) { localData = m_type;    externalData = p.m_type;    }
			
			if((localData == null && externalData != null) ||
			   (localData != null && externalData == null) ||
			   (localData != null && externalData != null && !localData.equalsIgnoreCase(externalData))) {
				return false;
			}
		}
		return true;
	}
	
	public String toString() {
		return (m_name == null ? "Unnamed Plugin" : m_name) +
			   (m_version == null ? "" : " " + m_version) +
			   (m_type == null ? "" : " (" + m_type + " Plugin)");
	}
	
}
