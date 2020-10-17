package item;

public class ItemFileType {
	
	protected String m_name;
	protected String m_extension;
	
	public ItemFileType(String name, String extension) throws IllegalArgumentException {
		if(name == null) { throw new IllegalArgumentException("Missing item file type name!"); }
		if(extension == null) { throw new IllegalArgumentException("Missing item file type extension!"); }
		
		String formattedName = name.trim();
		
		if(formattedName.isEmpty()) { throw new IllegalArgumentException("Empty item file type name!"); }

		String formattedExtension = extension.trim();
		
		if(formattedExtension.isEmpty()) { throw new IllegalArgumentException("Empty item file type extension!"); }
		
		m_name = name.trim();
		m_extension = extension.trim();
	}
	
	public String getName() {
		return m_name;
	}
	
	public boolean setName(String name) {
		if(name == null) { return false; }

		String formattedName = name.trim();

		if(formattedName.isEmpty()) { return false; }
		
		m_name = formattedName;
		
		return true;
	}
	
	public boolean hasExtension(String extension) {
		if(extension == null) { return false; }
		
		return m_extension.equalsIgnoreCase(extension.trim());
	}
	
	public String getExtension() {
		return m_extension;
	}

	public boolean setExtension(String extension) {
		if(extension == null) { return false; }

		String formattedExtension = extension.trim().toUpperCase();

		if(formattedExtension.isEmpty()) { return false; }
		
		m_extension = formattedExtension;
		
		return true;
	}
	
	public boolean equals(Object o) {
		if(o == null || !(o instanceof ItemFileType)) { return false; }

		ItemFileType t = (ItemFileType) o;
		
		return m_name.equalsIgnoreCase(t.m_name) &&
			   m_extension.equalsIgnoreCase(t.m_extension);
	}
	
	public String toString() {
		return m_name + " [" + m_extension + "]";
	}
	
}
