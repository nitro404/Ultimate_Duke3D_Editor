package item;

import java.util.*;
import java.io.*;
import exception.*;
import utilities.Utilities;

abstract public class Item {

	protected File m_file;
	protected ItemFileType m_fileType;
	protected boolean m_changed;
	protected boolean m_loading;
	protected Vector<ItemChangeListener> m_itemChangeListeners;
	
	public Item() {
		m_file = null;
		m_fileType = getDefaultFileType();
		m_changed = false;
		m_itemChangeListeners = new Vector<ItemChangeListener>();
	}

	public Item(String fileName) {
		m_file = fileName == null ? null : new File(fileName);
		m_fileType = getDefaultFileType();
		m_changed = false;
		m_itemChangeListeners = new Vector<ItemChangeListener>();
	}

	public Item(File file) {
		m_file = file;
		m_fileType = getDefaultFileType();
		m_changed = false;
		m_itemChangeListeners = new Vector<ItemChangeListener>();
	}

	public Item(ItemFileType fileType) throws InvalidFileTypeException, UnsupportedFileTypeException {
		if(fileType == null) { throw new InvalidFileTypeException("Cannot create item without a file type."); }
		if(!hasFileType(fileType)) { throw new UnsupportedFileTypeException("Unsupported group file type: \"" + fileType.getName() + "\" for item class: \"" + getClass().getName() + "\"."); }

		m_file = null;
		m_fileType = fileType == null ? getDefaultFileType() : fileType;
		m_changed = false;
		m_itemChangeListeners = new Vector<ItemChangeListener>();
	}
	
	public Item(String fileName, ItemFileType fileType) throws InvalidFileTypeException, UnsupportedFileTypeException {
		if(fileType == null) { throw new InvalidFileTypeException("Cannot create item without a file type."); }
		if(!hasFileType(fileType)) { throw new UnsupportedFileTypeException("Unsupported group file type: \"" + fileType.getName() + "\" for item class: \"" + getClass().getName() + "\"."); }

		m_file = fileName == null ? null : new File(fileName);
		m_fileType = fileType == null ? getDefaultFileType() : fileType;
		m_changed = false;
		m_itemChangeListeners = new Vector<ItemChangeListener>();
	}

	public Item(File file, ItemFileType fileType) throws InvalidFileTypeException, UnsupportedFileTypeException {
		if(fileType == null) { throw new InvalidFileTypeException("Cannot create item without a file type."); }
		if(!hasFileType(fileType)) { throw new UnsupportedFileTypeException("Unsupported group file type: \"" + fileType.getName() + "\" for item class: \"" + getClass().getName() + "\"."); }

		m_file = file;
		m_fileType = fileType == null ? getDefaultFileType() : fileType;
		m_changed = false;
		m_itemChangeListeners = new Vector<ItemChangeListener>();
	}

	public String getFileExtension() {
		return m_file == null ? getDefaultFileExtension() : Utilities.getFileExtension(m_file.getName());
	}

	public File getFile() {
		return m_file;
	}
	
	public void setFile(File newFile) {
		m_file = newFile;
	}

	abstract public boolean isInstantiable();

	abstract public boolean isInitialized();
	
	public boolean isLoading() {
		return m_loading;
	}

	public boolean isChanged() {
		return m_changed;
	}
	
	public void setChanged(boolean changed) {
		if(!isInitialized() || isLoading()) {
			return;
		}

		m_changed = changed;
		
		notifyItemChanged();
	}

	public ItemFileType getFileType() {
		return m_fileType;
	}
	
	public String getFileTypeName() {
		return m_fileType.getName();
	}
	
	public String getFileTypeDefaultExtension() {
		return m_fileType.getExtension();
	}
	
	public boolean setFileType(ItemFileType fileType) {
		if(fileType == null || !hasFileType(fileType)) { return false; }
		
		m_fileType = fileType;
		
		return true;
	}
	
	public int numberOfFileTypes() {
		return getFileTypes().length;
	}
	
	public ItemFileType getDefaultFileType() {
		return getFileTypes().length > 0 ? getFileTypes()[0] : null;
	}
	
	public String getDefaultFileExtension() {
		ItemFileType defaultFileType = getDefaultFileType();
		if(defaultFileType == null) { return null; }
		
		return defaultFileType.getExtension();
	}
	
	public abstract ItemFileType[] getFileTypes();
	
	public boolean hasFileType(String fileType) {
		if(fileType == null || fileType.isEmpty()) { return false; }
		
		String formattedType = fileType.trim();
		if(formattedType.isEmpty()) { return false; }
		
		for(int i=0;i<getFileTypes().length;i++) {
			if(formattedType.equalsIgnoreCase(getFileTypes()[i].getName())) {
				return true;
			}
		}
		return false;
	}
	
	public boolean hasFileType(ItemFileType fileType) {
		if(fileType == null) { return false; }
		
		for(int i=0;i<getFileTypes().length;i++) {
			if(getFileTypes()[i].equals(fileType)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean hasFileTypeWithExtension(String extension) {
		if(extension == null || extension.isEmpty()) { return false; }
		
		String formattedExtension = extension.trim();
		if(formattedExtension.isEmpty()) { return false; }
		
		for(int i=0;i<getFileTypes().length;i++) {
			if(getFileTypes()[i].hasExtension(formattedExtension)) {
				return true;
			}
		}
		return false;
	}
	
	public int indexOfFileType(String fileType) {
		if(fileType == null || fileType.isEmpty()) { return -1; }
		
		String formattedType = fileType.trim();
		if(formattedType.isEmpty()) { return -1; }
		
		for(int i=0;i<getFileTypes().length;i++) {
			if(formattedType.equalsIgnoreCase(getFileTypes()[i].getName())) {
				return i;
			}
		}
		return -1;
	}
	
	public int indexOfFileType(ItemFileType fileType) {
		if(fileType == null) { return -1; }
		
		for(int i=0;i<getFileTypes().length;i++) {
			if(getFileTypes()[i].equals(fileType)) {
				return i;
			}
		}
		return -1;
	}
	
	public int indexOfFirstFileTypeWithExtension(String extension) {
		if(extension == null || extension.isEmpty()) { return -1; }
		
		String formattedExtension = extension.trim();
		if(formattedExtension.isEmpty()) { return -1; }
		
		for(int i=0;i<getFileTypes().length;i++) {
			if(getFileTypes()[i].hasExtension(formattedExtension)) {
				return i;
			}
		}
		return -1;
	}

	public ItemFileType getFileType(int index) {
		if(index < 0 || index >= getFileTypes().length) { return null; }
		
		return getFileTypes()[index];
	}
	
	public ItemFileType getFileType(String fileType) {
		if(fileType == null || fileType.isEmpty()) { return null; }
		
		String formattedType = fileType.trim();
		if(formattedType.isEmpty()) { return null; }
		
		for(int i=0;i<getFileTypes().length;i++) {
			if(formattedType.equalsIgnoreCase(getFileTypes()[i].getName())) {
				return getFileTypes()[i];
			}
		}
		return null;
	}
	
	public ItemFileType getFirstFileTypeWithExtension(String extension) {
		if(extension == null || extension.isEmpty()) { return null; }
		
		String formattedExtension = extension.trim();
		if(formattedExtension.isEmpty()) { return null; }
		
		for(int i=0;i<getFileTypes().length;i++) {
			if(getFileTypes()[i].hasExtension(formattedExtension)) {
				return getFileTypes()[i];
			}
		}
		return null;
	}
	
	public Vector<ItemFileType> getFileTypesWithExtension(String extension) {
		if(extension == null || extension.isEmpty()) { return null; }
		
		String formattedExtension = extension.trim();
		if(formattedExtension.isEmpty()) { return null; }
		
		Vector<ItemFileType> fileTypesWithExtension = new Vector<ItemFileType>();
		
		for(int i=0;i<getFileTypes().length;i++) {
			if(getFileTypes()[i].hasExtension(formattedExtension)) {
				fileTypesWithExtension.add(getFileTypes()[i]);
			}
		}
		return fileTypesWithExtension;
	}

	public abstract boolean load() throws ItemReadException, DeserializationException;
	
	public abstract boolean save() throws ItemWriteException;
	
	public int numberOfItemChangeListeners() {
		return m_itemChangeListeners.size();
	}
	
	public ItemChangeListener getItemChangeListener(int index) {
		if(index < 0 || index >= m_itemChangeListeners.size()) { return null; }
		
		return m_itemChangeListeners.elementAt(index);
	}
	
	public boolean hasItemChangeListener(ItemChangeListener c) {
		return m_itemChangeListeners.contains(c);
	}
	
	public int indexOfItemChangeListener(ItemChangeListener c) {
		return m_itemChangeListeners.indexOf(c);
	}
	
	public boolean addItemChangeListener(ItemChangeListener c) {
		if(c == null || m_itemChangeListeners.contains(c)) { return false; }
		
		m_itemChangeListeners.add(c);
		
		return true;
	}
	
	public boolean removeItemChangeListener(int index) {
		if(index < 0 || index >= m_itemChangeListeners.size()) { return false; }
		
		m_itemChangeListeners.remove(index);
		
		return true;
	}
	
	public boolean removeItemChangeListener(ItemChangeListener c) {
		if(c == null) { return false; }
		
		return m_itemChangeListeners.remove(c);
	}
	
	public void clearItemChangeListeners() {
		m_itemChangeListeners.clear();
	}
	
	public void notifyItemChanged() {
		for(int i=0;i<m_itemChangeListeners.size();i++) {
			m_itemChangeListeners.elementAt(i).handleItemChange(this);
		}
	}
	
}
