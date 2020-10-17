package plugin;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import javax.xml.stream.*;
import javax.xml.stream.events.*;
import item.*;
import exception.*;
import console.*;
import utilities.*;
import variable.*;
import gui.*;

abstract public class FilePlugin extends Plugin {

	protected Vector<String> m_supportedFileFormats;
	protected String m_itemClassName;
	protected String m_panelClassName;
	protected Class<?> m_itemClass;
	protected Class<?> m_panelClass;
	
	public FilePlugin(String pluginName, String pluginVersion, String jarFileName, String configFileName, String directoryPath) {
		super(pluginName, pluginVersion, jarFileName, configFileName, directoryPath);

		m_supportedFileFormats = new Vector<String>();
		m_itemClassName = null;
		m_panelClassName = null;
		m_itemClass = null;
		m_panelClass = null;
	}
	
	abstract public String getType();

	public int numberOfSupportedFileFormats() {
		return m_supportedFileFormats.size();
	}
	
	public String getSupportedFileFormat(int index) {
		if(index < 0 || index >= m_supportedFileFormats.size()) { return null; }
		return m_supportedFileFormats.elementAt(index);
	}
	
	public String getSupportedFileFormatsAsString() {
		String listOfSupportedFileFormats = "";
		
		for(int i=0;i<m_supportedFileFormats.size();i++) {
			listOfSupportedFileFormats += m_supportedFileFormats.elementAt(i);
			
			if(i < m_supportedFileFormats.size() - 1) {
				listOfSupportedFileFormats += ", ";
			}
		}
		
		return listOfSupportedFileFormats;
	}
	
	public Vector<String> getSupportedFileFormats() {
		return m_supportedFileFormats;
	}
	
	public boolean hasSupportedFileFormat(String fileFormat) {
		if(fileFormat == null) { return false; }
		String formattedFileFormat = fileFormat.trim();
		if(formattedFileFormat.length() == 0) { return false; }
		
		for(int i=0;i<m_supportedFileFormats.size();i++) {
			if(m_supportedFileFormats.elementAt(i).equalsIgnoreCase(formattedFileFormat)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean hasSharedSupportedFileFormat(FilePlugin filePlugin) {
		if(filePlugin == null) { return false; }
		
		for(int i=0;i<filePlugin.numberOfSupportedFileFormats();i++) {
			if(hasSupportedFileFormat(filePlugin.getSupportedFileFormat(i))) {
				return true;
			}
		}
		return false;
	}
	
	public int numberOfSharedSupportedFileFormats(FilePlugin filePlugin) {
		if(filePlugin == null) { return 0; }
		
		int numberOfSharedSupportedFileFormats = 0;
		for(int i=0;i<filePlugin.numberOfSupportedFileFormats();i++) {
			if(hasSupportedFileFormat(filePlugin.getSupportedFileFormat(i))) {
				numberOfSharedSupportedFileFormats++;
			}
		}
		return numberOfSharedSupportedFileFormats;
	}

	public Vector<String> getSharedSupportedFileFormats(FilePlugin filePlugin) {
		if(filePlugin == null) { return null; }
		
		Vector<String> sharedSupportedFileFormats = new Vector<String>();
		for(int i=0;i<filePlugin.numberOfSupportedFileFormats();i++) {
			if(hasSupportedFileFormat(filePlugin.getSupportedFileFormat(i))) {
				sharedSupportedFileFormats.add(filePlugin.getSupportedFileFormat(i));
			}
		}
		return sharedSupportedFileFormats;
	}
	
	public String getSharedSupportedFileFormatsAsString(FilePlugin filePlugin) {
		if(filePlugin == null) { return null; }
		
		String sharedSupportedFileFormats = new String();
		for(int i=0;i<filePlugin.numberOfSupportedFileFormats();i++) {
			if(hasSupportedFileFormat(filePlugin.getSupportedFileFormat(i))) {
				if(sharedSupportedFileFormats.length() > 0) {
					sharedSupportedFileFormats += ", ";
				}
				
				sharedSupportedFileFormats += filePlugin.getSupportedFileFormat(i);
			}
		}
		return sharedSupportedFileFormats;
	}
	
	public int indexOfSupportedFileFormat(String fileFormat) {
		if(fileFormat == null) { return -1; }
		String formattedFileFormat = fileFormat.trim();
		if(formattedFileFormat.length() == 0) { return -1; }
		
		for(int i=0;i<m_supportedFileFormats.size();i++) {
			if(m_supportedFileFormats.elementAt(i).equalsIgnoreCase(formattedFileFormat)) {
				return i;
			}
		}
		return -1;
	}
	
	public boolean addSupportedFileFormat(String fileFormat) {
		if(fileFormat == null) { return false; }
		String formattedFileFormat = fileFormat.trim();
		if(formattedFileFormat.length() == 0) { return false; }
		
		for(int i=0;i<m_supportedFileFormats.size();i++) {
			if(m_supportedFileFormats.elementAt(i).equalsIgnoreCase(formattedFileFormat)) {
				return false;
			}
		}
		
		m_supportedFileFormats.add(formattedFileFormat);
		
		return true;
	}

	public String getItemClassName() {
		return m_itemClassName;
	}
	
	public String getPanelClassName() {
		return m_panelClassName;
	}
	
	public Class<?> getItemClass() {
		return m_itemClass;
	}
	
	public Class<?> getPanelClass() {
		return m_panelClass;
	}
	
	public Item getNewItemInstance(File file) throws ItemInstantiationException {
		if(m_itemClass == null) { return null; }
		
		Constructor<?> constructor = null;
		try { constructor = m_itemClass.getDeclaredConstructor(File.class); }
		catch(Exception e) { }
		
		if(constructor == null) {
			throw new ItemInstantiationException("Class \"" + m_itemClassName + "\" must contain a constructor which takes a File as an argument.");
		}
		
		Item newItem = null;
		try {
			newItem = (Item) constructor.newInstance(file);
		}
		catch(Exception e) {
			throw new ItemInstantiationException("Failed to instantiate class \"" + m_itemClassName + "\": " + e.getMessage());
		}
		
		return newItem;
	}
	
	public ItemPanel getNewItemPanelInstance(Item item) throws ItemPanelInstantiationException {
		if(m_panelClass == null || item == null) { return null; }
		
		Constructor<?> constructor = null;
		try { constructor = m_panelClass.getDeclaredConstructor(Item.class); }
		catch(Exception e) { }
		
		if(constructor == null) {
			throw new ItemPanelInstantiationException("Panel class \"" + m_panelClassName + "\" must contain a constructor which takes an Item as an argument.");
		}
		
		ItemPanel newItemPanel = null;
		try {
			newItemPanel = (ItemPanel) constructor.newInstance(item);
		}
		catch(Exception e) {
			throw new ItemPanelInstantiationException("Failed to instantiate item panel class \"" + m_panelClassName + "\": " + e.getMessage());
		}
		
		return newItemPanel;
	}
	
	protected boolean loadFromCFGFile(BufferedReader in) throws PluginLoadException {
		if(in == null) { return false; }
		
		String input = null;
		String line = null;
		Variable v = null;
		String supportedFileFormats = null;
		String itemClassName = null;
		String panelClassName = null;
		
		try {
			while(true) {
				input = in.readLine();
				if(input == null) {
					try { in.close(); } catch(IOException e) { }
					
					throw new FilePluginLoadException("Unexpected end of file encountered when reading \"" + m_name + "\" plugin definition file.");
				}
				
				line = input.trim();
				if(line.length() == 0 || Utilities.isComment(line)) { continue; }
				
				v = Variable.parseFrom(line);
				if(v == null) { continue; }
				
				if(v.getID().equalsIgnoreCase("Supported File Formats")) {
					supportedFileFormats = v.getValue();
					
					String supportedFileFormat = null;
					String supportedFileFormatList[] = supportedFileFormats.split("[;, \t]");
					for(int i=0;i<supportedFileFormatList.length;i++) {
						supportedFileFormat = supportedFileFormatList[i].trim();
						if(supportedFileFormat.length() > 0) {
							addSupportedFileFormat(supportedFileFormat);
						}
					}
					
					if(numberOfSupportedFileFormats() == 0) {
						try { in.close(); } catch(IOException e) { }
						
						throw new FilePluginLoadException("File plugin \"" + m_name + "\" must support at least one file format.");
					}
				}
				else if(v.getID().equalsIgnoreCase("Item Class Name")) {
					if(itemClassName != null) {
						SystemConsole.instance.writeLine("Multiple entries found for item class name in \"" + m_name + "\" plugin definition file.");
					}
					
					itemClassName = v.getValue();
				}
				else if(v.getID().equalsIgnoreCase("Item Panel Class Name")) {
					if(panelClassName != null) {
						SystemConsole.instance.writeLine("Multiple entries found for item panel class name in \"" + m_name + "\" plugin definition file.");
					}
					
					panelClassName = v.getValue();
				}
				else {
					SystemConsole.instance.writeLine("Encountered unexpected property \"" + v.getID() + "\" in \"" + m_name + "\" plugin definition file.");
				}
				
				if(supportedFileFormats != null && itemClassName != null && panelClassName != null) {
					break;
				}
			}
		}
		catch(IOException e) {
			try { in.close(); } catch(IOException e2) { }
			
			throw new FilePluginLoadException("Read exception thrown while reading \"" + m_name + "\" plugin definition file: " + e.getMessage());
		}
		
		m_itemClassName = itemClassName;
		m_panelClassName = panelClassName;
		
		m_itemClass = null;
		try { m_itemClass = ExtendedClassLoader.instance.loadClass(m_itemClassName); }
		catch(ClassNotFoundException e) {
			try { in.close(); } catch(IOException e2) { }
			
			throw new FilePluginLoadException("Class " + m_itemClassName + " is missing or not loaded.");
		}
		if(!(Item.class.isAssignableFrom(m_itemClass))) {
			try { in.close(); } catch(IOException e) { }
			
			throw new FilePluginLoadException("Class " + m_itemClassName + " does not extend Item class.");
		}
		
		m_panelClass = null;
		try { m_panelClass = ExtendedClassLoader.instance.loadClass(m_panelClassName); }
		catch(ClassNotFoundException e) {
			try { in.close(); } catch(IOException e2) { }
			
			throw new FilePluginLoadException("Class " + m_panelClassName + " is missing or not loaded.");
		}
		if(!(ItemPanel.class.isAssignableFrom(m_panelClass))) {
			try { in.close(); } catch(IOException e) { }
			
			throw new FilePluginLoadException("Class " + m_panelClassName + " does not extend ItemPanel class.");
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
		String itemClassName = null;
		String panelClassName = null;
		
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
												SystemConsole.instance.writeLine("Attribute \"extension\" specified multiple times inside format XML node of plugin definition file for file plugin \"" + m_name + "\".");
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
										SystemConsole.instance.writeLine("Missing \"extension\" attribute in format XML node of plugin definition file for file plugin \"" + m_name + "\".");
									}
									else if(extension.length() == 0) {
										SystemConsole.instance.writeLine("Encountered empty file extension in format XML node of plugin definition file for file plugin \"" + m_name + "\".");
									}
									else {
										addSupportedFileFormat(extension);
										
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
												SystemConsole.instance.writeLine("Attribute \"type\" specified multiple times inside class XML node of plugin definition file for file plugin \"" + m_name + "\".");
											}
											
											classType = attributeValue;
										}
										else if(attributeName.equalsIgnoreCase("name")) {
											if(className != null) {
												SystemConsole.instance.writeLine("Attribute \"name\" specified multiple times inside class XML node of plugin definition file for file plugin \"" + m_name + "\".");
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
									else if(classType.equalsIgnoreCase("Item")) {
										if(itemClassName != null) {
											SystemConsole.instance.writeLine("Item class specified multiple times inside classes XML node of plugin definition file for file plugin \"" + m_name + "\".");
										}
										
										itemClassName = className;
										
										if(itemClassName.length() == 0) {
											try { in.close(); } catch (IOException e2) { }
											
											throw new PluginLoadException("Empty item class name specified inside classes XML node of plugin definition file for file plugin \"" + m_name + "\".");
										}
									}
									else if(classType.equalsIgnoreCase("Item Panel")) {
										if(panelClassName != null) {
											SystemConsole.instance.writeLine("Item panel class specified multiple times inside classes XML node of plugin definition file for file plugin \"" + m_name + "\".");
										}
										
										panelClassName = className;
										
										if(panelClassName.length() == 0) {
											try { in.close(); } catch (IOException e2) { }
											
											throw new PluginLoadException("Empty panel class name specified inside classes XML node of plugin definition file for file plugin \"" + m_name + "\".");
										}
									}
									else {
										SystemConsole.instance.writeLine("Unexpected class type encountered inside classes node: \"" + node + "\", expected \"Item\" or \"Item Panel\".");
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
		
		if(numberOfSupportedFileFormats() == 0) {
			try { in.close(); } catch(IOException e) { }
			
			throw new FilePluginLoadException("File plugin \"" + m_name + "\" must support at least one file format.");
		}
		
		if(itemClassName == null) {
			try { in.close(); } catch(IOException e) { }
			
			throw new FilePluginLoadException("File plugin \"" + m_name + "\" missing item class name specification in plugin definition file.");
		}
		
		if(panelClassName == null) {
			try { in.close(); } catch(IOException e) { }
			
			throw new FilePluginLoadException("File plugin \"" + m_name + "\" missing item panel class name specification in plugin definition file.");
		}
		
		m_itemClassName = itemClassName;
		m_panelClassName = panelClassName;
		
		m_itemClass = null;
		try { m_itemClass = ExtendedClassLoader.instance.loadClass(m_itemClassName); }
		catch(ClassNotFoundException e) {
			try { in.close(); } catch(IOException e2) { }
			
			throw new FilePluginLoadException("Class " + m_itemClassName + " is missing or not loaded.");
		}
		if(!(Item.class.isAssignableFrom(m_itemClass))) {
			try { in.close(); } catch(IOException e) { }
			
			throw new FilePluginLoadException("Class " + m_itemClassName + " does not extend Item class.");
		}
		
		m_panelClass = null;
		try { m_panelClass = ExtendedClassLoader.instance.loadClass(m_panelClassName); }
		catch(ClassNotFoundException e) {
			try { in.close(); } catch(IOException e2) { }
			
			throw new FilePluginLoadException("Class " + m_panelClassName + " is missing or not loaded.");
		}
		if(!(ItemPanel.class.isAssignableFrom(m_panelClass))) {
			try { in.close(); } catch(IOException e) { }
			
			throw new FilePluginLoadException("Class " + m_panelClassName + " does not extend ItemPanel class.");
		}
		
		return true;
	}
	
	public boolean equals(Object o) {
		if(o == null || !(o instanceof FilePlugin)) { return false; }
		
		FilePlugin p = ((FilePlugin) o);
		
		if(m_name == null || p.m_name == null) { return false; }
		
		return m_name.equalsIgnoreCase(p.m_name);
	}
	
}
