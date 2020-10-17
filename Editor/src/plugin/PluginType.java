package plugin;

import java.lang.reflect.*;
import console.*;
import exception.*;

public class PluginType {
	
	protected String m_type;
	protected Class<?> m_class;
	
	public PluginType(String pluginType, Class<?> pluginClass) {
		setPluginType(pluginType);
		setPluginClass(pluginClass);
	}
	
	public String getPluginType() {
		return m_type;
	}
	
	public Class<?> getPluginClass() {
		return m_class;
	}
	
	public void setPluginType(String pluginType) {
		m_type = pluginType == null ? null : pluginType.trim();
	}
	
	public void setPluginClass(Class<?> pluginClass) {
		m_class = pluginClass;
	}
	
	public Plugin getNewPluginInstance(String pluginName, String pluginVersion, String jarFileName, String configFileName, String directoryName) throws PluginInstantiationException {
		if(m_class == null) { return null; }
		
		Constructor<?> constructor = null;
		try { constructor = m_class.getDeclaredConstructor(String.class, String.class, String.class, String.class, String.class); }
		catch(Exception e) { }
		
		if(constructor == null) {
			throw new PluginInstantiationException("Plugin class \"" + m_class.getName() + "\" must contain a constructor which takes five strings as arguments.");
		}
		
		Plugin newPlugin = null;
		try {
			newPlugin = (Plugin) constructor.newInstance(pluginName, pluginVersion, jarFileName, configFileName, directoryName);
		}
		catch(Exception e) {
			SystemConsole.instance.writeException(e);

			throw new PluginInstantiationException("Failed to instantiate plugin class \"" + m_class.getName() + "\": " + e.getMessage());
		}
		
		return newPlugin;
	}
	
	public boolean isValid() {
		return m_type != null &&
			   m_type.length() > 0 &&
			   m_class != null;
				
	}
	
	public static boolean isValid(PluginType pluginType) {
		return pluginType != null &&
			   pluginType.isValid();
	}
	
	public boolean equals(Object o) {
		if(o == null || !(o instanceof PluginType)) { return false; }
		
		PluginType p = (PluginType) o;
		
		if((m_type == null && p.m_type != null) ||
		   (m_type != null && p.m_type == null)) {
			return false;
		}
		
		if((m_type != null && p.m_type != null) &&
		   !m_type.equalsIgnoreCase(p.m_type)) {
			return false;
		}
		
		if((m_class == null && p.m_class != null) ||
		   (m_class != null && p.m_class == null)) {
			return false;
		}
		
		if(m_class == null && p.m_class == null) {
			return true;
		}
		
		return m_class.equals(p.m_class);
	}
	
	public String toString() {
		return m_type;
	}
	
}
