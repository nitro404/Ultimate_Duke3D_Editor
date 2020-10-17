package item;

import java.util.*;

public class ItemFileType {
	
	protected String m_name;
	protected Vector<String> m_extensions;
	
	public ItemFileType(String name, String extension) {
		setName(name);
		m_extensions = new Vector<String>();
		addExtension(extension);
	}
	
	public ItemFileType(String name, Vector<String> extensions) {
		setName(name);
		m_extensions = new Vector<String>();
		addExtensions(extensions);
	}
	
	public ItemFileType(String name, String[] extensions) {
		setName(name);
		m_extensions = new Vector<String>();
		addExtensions(extensions);
	}
	
	public String getName() {
		return m_name;
	}
	
	public void setName(String name) {
		m_name = name == null ? "" : name.trim();
	}
	
	public int numberOfExtensions() {
		return m_extensions.size();
	}
	
	public boolean hasExtension(String extension) {
		if(extension == null || extension.length() == 0) { return false; }
		
		String formattedExtension = extension.trim();
		if(formattedExtension.length() == 0) { return false; }
		
		for(int i=0;i<m_extensions.size();i++) {
			if(m_extensions.elementAt(i).equalsIgnoreCase(formattedExtension)) {
				return true;
			}
		}
		return false;
	}
	
	public int indexOfExtension(String extension) {
		if(extension == null || extension.length() == 0) { return -1; }
		
		String formattedExtension = extension.trim();
		if(formattedExtension.length() == 0) { return -1; }
		
		for(int i=0;i<m_extensions.size();i++) {
			if(m_extensions.elementAt(i).equalsIgnoreCase(formattedExtension)) {
				return i;
			}
		}
		return -1;
	}
	
	public String getExtension(int index) {
		if(index < 0 || index >= m_extensions.size()) { return null; }
		
		return m_extensions.elementAt(index);
	}
	
	public boolean addExtension(String extension) {
		if(extension == null || extension.length() == 0) { return false; }
		
		String formattedExtension = extension.trim();
		if(formattedExtension.length() == 0 || hasExtension(formattedExtension)) { return false; }
		
		m_extensions.add(formattedExtension);
		
		return true;
	}
	
	public boolean addExtensions(String[] extensions) {
		if(extensions == null) { return false; }
		
		boolean extensionAdded = false;
		
		for(int i=0;i<extensions.length;i++) {
			if(addExtension(extensions[i])) {
				extensionAdded = true;
			}
		}
		return extensionAdded;
	}
	
	public boolean addExtensions(Vector<String> extensions) {
		if(extensions == null) { return false; }
		
		boolean extensionAdded = false;
		
		for(int i=0;i<extensions.size();i++) {
			if(addExtension(extensions.elementAt(i))) {
				extensionAdded = true;
			}
		}
		return extensionAdded;
	}
	
	public boolean removeExtension(int index) {
		if(index < 0 || index >= m_extensions.size()) { return false; }
		
		m_extensions.remove(index);
		
		return true;
	}
	
	public boolean removeExtension(String extension) {
		if(extension == null || extension.length() == 0) { return false; }
		
		String formattedExtension = extension.trim();
		if(formattedExtension.length() == 0) { return false; }
		
		for(int i=0;i<m_extensions.size();i++) {
			if(m_extensions.elementAt(i).equalsIgnoreCase(formattedExtension)) {
				m_extensions.remove(i);
				
				return true;
			}
		}
		return false;
	}
	
	public void clearExtensions() {
		m_extensions.clear();
	}
	
	public boolean isValid() {
		return m_name.length() > 0 &&
			   m_extensions.size() > 0;
	}
	
	public static boolean isValid(ItemFileType fileType) {
		return fileType != null &&
			   fileType.isValid();
	}
	
	public boolean equals(Object o) {
		if(o == null || !(o instanceof ItemFileType)) { return false; }
		ItemFileType t = (ItemFileType) o;
		
		if(!m_name.equalsIgnoreCase(t.m_name)) { return false; }
		
		if(m_extensions.size() != t.m_extensions.size()) { return false; }
		
		for(int i=0;i<m_extensions.size();i++) {
			if(!t.hasExtension(m_extensions.elementAt(i))) {
				return false;
			}
		}
		return true;
	}
	
	public String toString() {
		String fileType = m_name;
		
		if(m_extensions.size() > 0) {
			fileType += " [";
			
			for(int i=0;i<m_extensions.size();i++) {
				fileType += m_extensions.elementAt(i);
				if(i < m_extensions.size() - 1) {
					fileType += ", ";
				}
			}
			
			fileType += "]";
		}
		
		return fileType;
	}
	
}
