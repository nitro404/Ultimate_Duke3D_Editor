package group;

import java.util.*;
import java.io.*;
import exception.*;
import utilities.*;
import settings.*;
import console.*;

public abstract class Group {
	
	protected GroupFileType m_fileType;
	protected File m_file;
	protected Vector<GroupFile> m_files;
	protected boolean m_changed;
	protected GroupFileSortType m_sortType;
	protected SortDirection m_sortDirection;
	protected boolean m_autoSortFiles;
	protected boolean m_loaded;
	protected Vector<GroupChangeListener> m_groupChangeListeners;
	protected Vector<GroupSortListener> m_groupSortListeners;
	
	public static final boolean DEFAULT_REPLACE_FILES = true;
	public static final boolean DEFAULT_SORT_FILES = true;
	
	public Group() {
		m_fileType = getDefaultGroupFileType();
		m_file = null;
		m_files = new Vector<GroupFile>();
		m_changed = false;
		m_sortType = GroupFileSortType.defaultSortType;
		m_sortDirection = SortDirection.defaultDirection;
		m_autoSortFiles = SettingsManager.defaultAutoSortFiles;
		m_loaded = false;
		m_groupChangeListeners = new Vector<GroupChangeListener>();
		m_groupSortListeners = new Vector<GroupSortListener>();
	}
	
	public Group(String fileName) {
		m_fileType = getDefaultGroupFileType();
		m_file = fileName == null ? null : new File(fileName);
		m_files = new Vector<GroupFile>();
		m_changed = false;
		m_sortType = GroupFileSortType.defaultSortType;
		m_sortDirection = SortDirection.defaultDirection;
		m_autoSortFiles = SettingsManager.defaultAutoSortFiles;
		m_loaded = false;
		m_groupChangeListeners = new Vector<GroupChangeListener>();
		m_groupSortListeners = new Vector<GroupSortListener>();
	}
	
	public Group(File file) {
		m_fileType = getDefaultGroupFileType();
		m_file = file;
		m_files = new Vector<GroupFile>();
		m_changed = false;
		m_sortType = GroupFileSortType.defaultSortType;
		m_sortDirection = SortDirection.defaultDirection;
		m_autoSortFiles = SettingsManager.defaultAutoSortFiles;
		m_loaded = false;
		m_groupChangeListeners = new Vector<GroupChangeListener>();
		m_groupSortListeners = new Vector<GroupSortListener>();
	}
	
	public Group(GroupFileType fileType) throws InvalidGroupFileTypeException, UnsupportedGroupFileTypeException {
		if(!fileType.isValid()) { throw new InvalidGroupFileTypeException("Cannot create group with " + fileType == null ? "null file type" : (fileType.getName().length() == 0 ? "empty name" : "no file extensions") + "."); }
		if(fileType != null && !hasGroupFileType(fileType)) { throw new UnsupportedGroupFileTypeException("Unsupported group file type: \"" + fileType.getName() + "\" for group class: \"" + getClass().getName() + "\"."); }
		m_fileType = fileType == null ? getDefaultGroupFileType() : fileType;
		m_file = null;
		m_files = new Vector<GroupFile>();
		m_changed = false;
		m_sortType = GroupFileSortType.defaultSortType;
		m_sortDirection = SortDirection.defaultDirection;
		m_autoSortFiles = SettingsManager.defaultAutoSortFiles;
		m_loaded = false;
		m_groupChangeListeners = new Vector<GroupChangeListener>();
		m_groupSortListeners = new Vector<GroupSortListener>();
	}

	public Group(String fileName, GroupFileType fileType) throws InvalidGroupFileTypeException, UnsupportedGroupFileTypeException {
		if(!fileType.isValid()) { throw new InvalidGroupFileTypeException("Cannot create group with " + fileType == null ? "null file type" : (fileType.getName().length() == 0 ? "empty name" : "no file extensions") + "."); }
		if(fileType != null && !hasGroupFileType(fileType)) { throw new UnsupportedGroupFileTypeException("Unsupported group file type: \"" + fileType.getName() + "\" for group class: \"" + getClass().getName() + "\"."); }
		m_fileType = fileType == null ? getDefaultGroupFileType() : fileType;
		m_file = fileName == null ? null : new File(fileName);
		m_files = new Vector<GroupFile>();
		m_changed = false;
		m_sortType = GroupFileSortType.defaultSortType;
		m_sortDirection = SortDirection.defaultDirection;
		m_autoSortFiles = SettingsManager.defaultAutoSortFiles;
		m_loaded = false;
		m_groupChangeListeners = new Vector<GroupChangeListener>();
		m_groupSortListeners = new Vector<GroupSortListener>();
	}
	
	public Group(File file, GroupFileType fileType) throws InvalidGroupFileTypeException, UnsupportedGroupFileTypeException {
		if(!fileType.isValid()) { throw new InvalidGroupFileTypeException("Cannot create group with " + fileType == null ? "null file type" : (fileType.getName().length() == 0 ? "empty name" : "no file extensions") + "."); }
		if(fileType != null && !hasGroupFileType(fileType)) { throw new UnsupportedGroupFileTypeException("Unsupported group file type: \"" + fileType.getName() + "\" for group class: \"" + getClass().getName() + "\"."); }
		m_fileType = fileType == null ? getDefaultGroupFileType() : fileType;
		m_file = file;
		m_files = new Vector<GroupFile>();
		m_changed = false;
		m_sortType = GroupFileSortType.defaultSortType;
		m_sortDirection = SortDirection.defaultDirection;
		m_autoSortFiles = SettingsManager.defaultAutoSortFiles;
		m_loaded = false;
		m_groupChangeListeners = new Vector<GroupChangeListener>();
		m_groupSortListeners = new Vector<GroupSortListener>();
	}
	
	public File getFile() {
		return m_file;
	}
	
	public void setFile(File newFile) {
		m_file = newFile;
	}
	
	public String getFileExtension() {
		return m_file == null ? getDefaultGroupFileExtension() : Utilities.getFileExtension(m_file.getName());
	}

	public Endianness getFileEndianness() {
		return Endianness.defaultEndianness;
	}
	
	public boolean isChanged() {
		return m_changed;
	}
	
	public void setChanged(boolean changed) {
		m_changed = changed;
		
		notifyGroupChanged();
	}
	
	public GroupFileSortType getSortType() {
		return m_sortType;
	}
	
	public boolean setSortType(GroupFileSortType sortType) {
		if(!sortType.isValid()) { return false; }
		
		boolean sortTypeChanged = m_sortType != sortType;
		
		m_sortType = sortType;
		
		notifyGroupSortStateChanged();
		
		if(sortTypeChanged) {
			if(shouldAutoSortFiles()) {
				sortFiles();
			}
		}
		
		return true;
	}
	
	public SortDirection getSortDirection() {
		return m_sortDirection;
	}
	
	public boolean setSortDirection(SortDirection sortDirection) {
		if(!sortDirection.isValid()) { return false; }
		
		boolean sortDirectionChanged = m_sortDirection != sortDirection;
		
		m_sortDirection = sortDirection;
		
		notifyGroupSortStateChanged();
		
		if(sortDirectionChanged) {
			if(shouldAutoSortFiles()) {
				sortFiles();
			}
		}
		
		return true;
	}
	
	public boolean getAutoSortFiles() {
		return m_autoSortFiles;
	}
	
	public boolean shouldSortFiles() {
		return (SettingsManager.instance.sortAllGroups && SettingsManager.instance.sortType != GroupFileSortType.Unsorted) ||
			   (!SettingsManager.instance.sortAllGroups && m_sortType != GroupFileSortType.Unsorted);
	}
	
	public boolean shouldAutoSortFiles() {
		return ( SettingsManager.instance.sortAllGroups && SettingsManager.instance.autoSortFiles) ||
			   (!SettingsManager.instance.sortAllGroups && m_autoSortFiles);
	}
	
	public void enableAutoSortFiles() {
		setAutoSortFiles(true);
	}
	
	public void disableAutoSortFiles() {
		setAutoSortFiles(false);
	}
	
	public void toggleAutoSortFiles() {
		setAutoSortFiles(!m_autoSortFiles);
	}

	public void setAutoSortFiles(boolean autoSortFiles) {
		m_autoSortFiles = autoSortFiles;
		
		notifyGroupSortStateChanged();
		
		if(m_autoSortFiles != autoSortFiles) {
			if(shouldAutoSortFiles()) {
				sortFiles();
			}
		}
	}
	
	public boolean isLoaded() {
		return m_loaded;
	}
	
	public boolean isInstantiable() {
		return true;
	}
	
	public GroupFileType getGroupFileType() {
		return m_fileType;
	}
	
	public String getGroupFileTypeName() {
		return m_fileType.getName();
	}
	
	public String getGroupFileTypeDefaultExtension() {
		return m_fileType.getExtension(0);
	}
	
	public boolean setGroupFileType(GroupFileType fileType) {
		if(!GroupFileType.isValid(fileType) || !hasGroupFileType(fileType)) { return false; }
		
		m_fileType = fileType;
		
		return true;
	}
	
	public int numberOfGroupFileTypes() {
		return getGroupFileTypes().length;
	}
	
	public GroupFileType getDefaultGroupFileType() {
		return getGroupFileTypes().length > 0 ? getGroupFileTypes()[0] : null;
	}
	
	public String getDefaultGroupFileExtension() {
		GroupFileType defaultFileType = getDefaultGroupFileType();
		if(defaultFileType == null) { return null; }
		
		return defaultFileType.numberOfExtensions() == 0 ? null : defaultFileType.getExtension(0);
	}
	
	public abstract GroupFileType[] getGroupFileTypes();
	
	public boolean hasGroupFileType(String fileType) {
		if(fileType == null || fileType.length() == 0) { return false; }
		
		String formattedType = fileType.trim();
		if(formattedType.length() == 0) { return false; }
		
		for(int i=0;i<getGroupFileTypes().length;i++) {
			if(formattedType.equalsIgnoreCase(getGroupFileTypes()[i].getName())) {
				return true;
			}
		}
		return false;
	}
	
	public boolean hasGroupFileType(GroupFileType fileType) {
		if(!GroupFileType.isValid(fileType)) { return false; }
		
		for(int i=0;i<getGroupFileTypes().length;i++) {
			if(getGroupFileTypes()[i].equals(fileType)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean hasGroupFileTypeWithExtension(String extension) {
		if(extension == null || extension.length() == 0) { return false; }
		
		String formattedExtension = extension.trim();
		if(formattedExtension.length() == 0) { return false; }
		
		for(int i=0;i<getGroupFileTypes().length;i++) {
			if(getGroupFileTypes()[i].hasExtension(formattedExtension)) {
				return true;
			}
		}
		return false;
	}
	
	public int indexOfGroupFileType(String fileType) {
		if(fileType == null || fileType.length() == 0) { return -1; }
		
		String formattedType = fileType.trim();
		if(formattedType.length() == 0) { return -1; }
		
		for(int i=0;i<getGroupFileTypes().length;i++) {
			if(formattedType.equalsIgnoreCase(getGroupFileTypes()[i].getName())) {
				return i;
			}
		}
		return -1;
	}
	
	public int indexOfGroupFileType(GroupFileType fileType) {
		if(!GroupFileType.isValid(fileType)) { return -1; }
		
		for(int i=0;i<getGroupFileTypes().length;i++) {
			if(getGroupFileTypes()[i].equals(fileType)) {
				return i;
			}
		}
		return -1;
	}
	
	public int indexOfFirstGroupFileTypeWithExtension(String extension) {
		if(extension == null || extension.length() == 0) { return -1; }
		
		String formattedExtension = extension.trim();
		if(formattedExtension.length() == 0) { return -1; }
		
		for(int i=0;i<getGroupFileTypes().length;i++) {
			if(getGroupFileTypes()[i].hasExtension(formattedExtension)) {
				return i;
			}
		}
		return -1;
	}

	public GroupFileType getGroupFileType(int index) {
		if(index < 0 || index >= getGroupFileTypes().length) { return null; }
		
		return getGroupFileTypes()[index];
	}
	
	public GroupFileType getGroupFileType(String fileType) {
		if(fileType == null || fileType.length() == 0) { return null; }
		
		String formattedType = fileType.trim();
		if(formattedType.length() == 0) { return null; }
		
		for(int i=0;i<getGroupFileTypes().length;i++) {
			if(formattedType.equalsIgnoreCase(getGroupFileTypes()[i].getName())) {
				return getGroupFileTypes()[i];
			}
		}
		return null;
	}
	
	public GroupFileType getGroupFirstFileTypeWithExtension(String extension) {
		if(extension == null || extension.length() == 0) { return null; }
		
		String formattedExtension = extension.trim();
		if(formattedExtension.length() == 0) { return null; }
		
		for(int i=0;i<getGroupFileTypes().length;i++) {
			if(getGroupFileTypes()[i].hasExtension(formattedExtension)) {
				return getGroupFileTypes()[i];
			}
		}
		return null;
	}
	
	public Vector<GroupFileType> getGroupFileTypesWithExtension(String extension) {
		if(extension == null || extension.length() == 0) { return null; }
		
		String formattedExtension = extension.trim();
		if(formattedExtension.length() == 0) { return null; }
		
		Vector<GroupFileType> fileTypesWithExtension = new Vector<GroupFileType>();
		
		for(int i=0;i<getGroupFileTypes().length;i++) {
			if(getGroupFileTypes()[i].hasExtension(formattedExtension)) {
				fileTypesWithExtension.add(getGroupFileTypes()[i]);
			}
		}
		return fileTypesWithExtension;
	}
	
	public boolean verifyAllFiles() {
		for(int i=0;i<m_files.size();i++) {
			if(!m_files.elementAt(i).isValid()) {
				return false;
			}
		}
		return true;
	}

	public int getGroupFileSize() {
		int fileSize = 0;
		for(int i=0;i<m_files.size();i++) {
			fileSize += m_files.elementAt(i).getDataSize();
		}
		return fileSize;
	}
	
	public int numberOfFiles() {
		return m_files.size();
	}
	
	public boolean hasFile(String fileName) {
		if(fileName == null) { return false; }
		
		String formattedFileName = fileName.trim();
		if(formattedFileName.length() == 0 || formattedFileName.length() > GroupFile.MAX_FILE_NAME_LENGTH) { return false; }
		
		String currentFileName;
		for(int i=0;i<m_files.size();i++) {
			currentFileName = m_files.elementAt(i).getFileName();
			if(currentFileName != null && currentFileName.equalsIgnoreCase(formattedFileName)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean hasFile(File file) {
		if(file == null) { return false; }
		
		return hasFile(file.getName());
	}
	
	public boolean hasFile(GroupFile file) {
		if(file == null) {
			return false;
		}
		
		return hasFile(file.getFileName());
	}
	
	public int indexOfFile(String fileName) {
		if(fileName == null) { return -1; }
		
		String formattedFileName = fileName.trim();
		if(formattedFileName.length() == 0 || formattedFileName.length() > GroupFile.MAX_FILE_NAME_LENGTH) { return -1; }
		
		String currentFileName;
		for(int i=0;i<m_files.size();i++) {
			currentFileName = m_files.elementAt(i).getFileName();
			if(currentFileName != null && currentFileName.equalsIgnoreCase(formattedFileName)) {
				return i;
			}
		}
		return -1;
	}
	
	public int indexOfFile(File file) {
		if(file == null) {
			return -1;
		}
		
		return indexOfFile(file.getName());
	}
	
	public int indexOfFile(GroupFile file) {
		if(file == null || !file.isValid()) { return -1; }
		
		return indexOfFile(file.getFileName());
	}
	
	public int indexOfFirstFileWithExtension(String extension) {
		if(extension == null || extension.length() == 0) { return -1; }
		
		String formattedExtension = extension.trim();
		if(formattedExtension.length() == 0) { return -1; }
		
		String currentFileExtension = null;
		
		for(int i=0;i<m_files.size();i++) {
			currentFileExtension = Utilities.getFileExtension(m_files.elementAt(i).getFileName());
			if(currentFileExtension == null || currentFileExtension.length() == 0) { continue; }
			
			if(formattedExtension.equalsIgnoreCase(currentFileExtension)) {
				return i;
			}
		}
		
		return -1;
	}
	
	public int indexOfLastFileWithExtension(String extension) {
		if(extension == null || extension.length() == 0) { return -1; }
		
		String formattedExtension = extension.trim();
		if(formattedExtension.length() == 0) { return -1; }
		
		String currentFileExtension = null;
		
		for(int i=m_files.size()-1;i>=0;i--) {
			currentFileExtension = Utilities.getFileExtension(m_files.elementAt(i).getFileName());
			if(currentFileExtension == null || currentFileExtension.length() == 0) { continue; }
			
			if(formattedExtension.equalsIgnoreCase(currentFileExtension)) {
				return i;
			}
		}
		
		return -1;
	}
	
	public GroupFile getFile(int index) {
		if(index < 0 || index >= m_files.size()) { return null; }
		
		return m_files.elementAt(index);
	}
	
	public GroupFile getFile(String fileName) {
		if(fileName == null) { return null; }
		
		String formattedFileName = fileName.trim();
		if(formattedFileName.length() == 0 || formattedFileName.length() > GroupFile.MAX_FILE_NAME_LENGTH) { return null; }
		
		String currentFileName;
		for(int i=0;i<m_files.size();i++) {
			currentFileName = m_files.elementAt(i).getFileName();
			if(currentFileName != null && currentFileName.equalsIgnoreCase(formattedFileName)) {
				return m_files.elementAt(i);
			}
		}
		return null;
	}
	
	public GroupFile getFile(File file) {
		if(file == null) {
			return null;
		}
		
		return getFile(file.getName());
	}
	
	public GroupFile getFirstFileWithExtension(String extension) {
		if(extension == null || extension.length() == 0) { return null; }
		
		String formattedExtension = extension.trim();
		if(formattedExtension.length() == 0) { return null; }
		
		String currentFileExtension = null;
		
		for(int i=0;i<m_files.size();i++) {
			currentFileExtension = Utilities.getFileExtension(m_files.elementAt(i).getFileName());
			if(currentFileExtension == null || currentFileExtension.length() == 0) { continue; }
			
			if(formattedExtension.equalsIgnoreCase(currentFileExtension)) {
				return m_files.elementAt(i);
			}
		}
		
		return null;
	}
	
	public GroupFile getLastFileWithExtension(String extension) {
		if(extension == null || extension.length() == 0) { return null; }
		
		String formattedExtension = extension.trim();
		if(formattedExtension.length() == 0) { return null; }
		
		String currentFileExtension = null;
		
		for(int i=m_files.size()-1;i>=0;i--) {
			currentFileExtension = Utilities.getFileExtension(m_files.elementAt(i).getFileName());
			if(currentFileExtension == null || currentFileExtension.length() == 0) { continue; }
			
			if(formattedExtension.equalsIgnoreCase(currentFileExtension)) {
				return m_files.elementAt(i);
			}
		}
		
		return null;
	}
	
	public Vector<GroupFile> getFiles() {
		return m_files;
	}
	
	public Vector<GroupFile> getFilesWithExtension(String extension) {
		if(extension == null || extension.length() == 0) { return null; }
		
		String formattedExtension = extension.trim();
		if(formattedExtension.length() == 0) { return null; }
		
		Vector<GroupFile> matchingFiles = new Vector<GroupFile>();
		String currentFileExtension = null;
		
		for(int i=0;i<m_files.size();i++) {
			currentFileExtension = Utilities.getFileExtension(m_files.elementAt(i).getFileName());
			if(currentFileExtension == null || currentFileExtension.length() == 0) { continue; }
			
			if(formattedExtension.equalsIgnoreCase(currentFileExtension)) {
				matchingFiles.add(m_files.elementAt(i));
			}
		}
		
		return matchingFiles;
	}
	
	public Vector<String> getFileExtensions() {
		Vector<String> extensions = new Vector<String>();
		
		if(m_files.size() == 0) { return extensions; }
		
		HashMap<String, Integer> extensionMap = new HashMap<String, Integer>();
		
		String extension = null;
		for(int i=0;i<m_files.size();i++) {
			extension = Utilities.getFileExtension(m_files.elementAt(i).getFileName());
			if(extension == null) { continue; }
			
			if(extensionMap.containsKey(extension)) {
				extensionMap.put(extension, extensionMap.get(extension) + 1);
			}
			else {
				extensionMap.put(extension, 1);
			}
		}
		
		for(String key : extensionMap.keySet()) {
			extensions.add(key);
		}
		
		return extensions;
	}
	
	public boolean extractFile(int index, String directory) throws IOException {
		return extractFile(index, directory == null || directory.length() == 0 ? null : new File(directory));
	}
	
	public boolean extractFile(String fileName, String directory) throws IOException {
		return extractFile(indexOfFile(fileName), directory == null || directory.length() == 0 ? null : new File(directory));
	}
	
	public boolean extractFile(File file, String directory) throws IOException {
		return extractFile(indexOfFile(file), directory == null || directory.length() == 0 ? null : new File(directory));
	}
	
	public boolean extractFile(int index, File directory) throws IOException {
		if(index < 0 || index >= m_files.size()) { return false; }
		
		return m_files.elementAt(index).writeTo(directory);
	}
	
	public boolean extractFile(String fileName, File directory) throws IOException {
		return extractFile(indexOfFile(fileName), directory);
	}
	
	public boolean extractFile(File file, File directory) throws IOException {
		return extractFile(indexOfFile(file), directory);
	}
	
	public boolean extractFirstFileWithExtension(String extension, String directory) throws IOException {
		return extractFirstFileWithExtension(extension, directory == null || directory.length() == 0 ? null : new File(directory));
	}
	
	public boolean extractFirstFileWithExtension(String extension, File directory) throws IOException {
		if(extension == null || extension.length() == 0) { return false; }
		
		String formattedExtension = extension.trim();
		if(formattedExtension.length() == 0) { return false; }
		
		String currentFileExtension = null;
		
		for(int i=0;i<m_files.size();i++) {
			currentFileExtension = Utilities.getFileExtension(m_files.elementAt(i).getFileName());
			if(currentFileExtension == null || currentFileExtension.length() == 0) { continue; }
			
			if(formattedExtension.equalsIgnoreCase(currentFileExtension)) {
				return extractFile(i, directory);
			}
		}
		
		return false;
	}
	
	public boolean extractLastFileWithExtension(String extension, String directory) throws IOException {
		return extractFirstFileWithExtension(extension, directory == null || directory.length() == 0 ? null : new File(directory));
	}
	
	public boolean extractLastFileWithExtension(String extension, File directory) throws IOException {
		if(extension == null || extension.length() == 0) { return false; }
		
		String formattedExtension = extension.trim();
		if(formattedExtension.length() == 0) { return false; }
		
		String currentFileExtension = null;
		
		for(int i=m_files.size()-1;i>=0;i--) {
			currentFileExtension = Utilities.getFileExtension(m_files.elementAt(i).getFileName());
			if(currentFileExtension == null || currentFileExtension.length() == 0) { continue; }
			
			if(formattedExtension.equalsIgnoreCase(currentFileExtension)) {
				return extractFile(i, directory);
			}
		}
		
		return false;
	}
	
	public int extractAllFiles(String directory) throws IOException {
		return extractAllFiles(directory == null || directory.length() == 0 ? null : new File(directory));
	}
	
	public int extractAllFiles(File directory) throws IOException {
		int numberOfExtractedFiles = 0;
		
		for(int i=0;i<m_files.size();i++) {
			if(extractFile(i, directory)) {
				numberOfExtractedFiles++;
			}
		}
		
		return numberOfExtractedFiles;
	}
	
	public int extractAllFilesWithExtension(String extension, String directory) throws IOException {
		return extractAllFilesWithExtension(extension, directory == null || directory.length() == 0 ? null : new File(directory));
	}

	public int extractAllFilesWithExtension(String extension, File directory) throws IOException {
		if(extension == null || extension.length() == 0) { return 0; }
		
		String formattedExtension = extension.trim();
		if(formattedExtension.length() == 0) { return 0; }
		
		int numberOfExtractedFiles = 0;
		String currentFileExtension = null;
		
		for(int i=0;i<m_files.size();i++) {
			currentFileExtension = Utilities.getFileExtension(m_files.elementAt(i).getFileName());
			if(currentFileExtension == null || currentFileExtension.length() == 0) { continue; }
			
			if(formattedExtension.equalsIgnoreCase(currentFileExtension)) {
				if(extractFile(i, directory)) {
					numberOfExtractedFiles++;
				}
			}
		}
		
		return numberOfExtractedFiles;
	}
	
	public boolean addFile(GroupFile file) {
		return addFile(file, DEFAULT_REPLACE_FILES);
	}
	
	public boolean addFile(File file) {
		return addFile(file, DEFAULT_REPLACE_FILES);
	}
	
	public boolean addFile(GroupFile file, boolean replace) {
		if(file == null || !file.isValid()) { return false; }
		
		boolean fileAdded = false;
		int fileIndex = indexOfFile(file);
		
		if(fileIndex == -1) {
			m_files.add(file);
			
			fileAdded = true;
		}
		else {
			if(replace) {
				m_files.set(fileIndex, file);
				
				fileAdded = true;
			}
		}
		
		if(fileAdded) {
			setChanged(true);
			
			if(shouldAutoSortFiles()) {
				sortFiles();
			}
		}
		
		return fileAdded;
	}
	
	public boolean addFile(File file, boolean replace) {
		if(file == null || !file.isFile() || !file.exists()) { return false; }
		
		if(file.getName().length() > GroupFile.MAX_FILE_NAME_LENGTH) {
        	SystemConsole.instance.writeLine("Warning: Truncating file name \"" + file.getName() + "\", exceeds max length of " + GroupFile.MAX_FILE_NAME_LENGTH + ".");
        }
		
		GroupFile newGroupFile = null;
		try { newGroupFile = GroupFile.readFrom(file); }
		catch(FileNotFoundException e) { return false; }
		catch(IOException e) { return false; }
		if(newGroupFile == null) { return false; }
		
		boolean fileAdded = false;
		int fileIndex = indexOfFile(newGroupFile);
		
		if(fileIndex == -1) {
			m_files.add(newGroupFile);
			
			fileAdded = true;
		}
		else {
			if(replace) {
				m_files.add(newGroupFile);
				
				fileAdded = true;
			}
		}
		
		if(fileAdded) {
			setChanged(true);
			
			if(shouldAutoSortFiles()) {
				sortFiles();
			}
		}
		
		return fileAdded;
	}
	
	public int addFiles(GroupFile[] files) {
		if(files == null || files.length == 0) { return 0; }
		
		return addFiles(files, DEFAULT_REPLACE_FILES);
	}
	
	public int addFiles(Vector<GroupFile> files) {
		if(files == null || files.size() == 0) { return 0; }
		
		return addFiles(files, DEFAULT_REPLACE_FILES);
	}
	
	public int addFiles(Group group) {
		if(group == null || group.numberOfFiles() == 0) { return 0; }
		
		return addFiles(group, DEFAULT_REPLACE_FILES);
	}
	
	public int addFiles(File[] files) {
		if(files == null || files.length == 0) { return 0; }
		
		return addFiles(files, DEFAULT_REPLACE_FILES);
	}
	
	public int addFiles(GroupFile[] files, boolean replace) {
		if(files == null) { return 0; }
		
		int numberOfFilesAdded = 0;
		int fileIndex = -1;
		
		for(int i=0;i<files.length;i++) {
			if(files[i] != null && files[i].isValid()) {
				fileIndex = indexOfFile(files[i]);
				
				if(fileIndex == -1) {
					m_files.add(files[i]);
					
					numberOfFilesAdded++;
				}
				else {
					if(replace) {
						m_files.set(fileIndex, files[i]);
						
						numberOfFilesAdded++;
					}
				}
			}
		}
		
		if(numberOfFilesAdded > 0) {
			setChanged(true);
			
			if(shouldAutoSortFiles()) {
				sortFiles();
			}
		}
		
		return numberOfFilesAdded;
	}
	
	public int addFiles(Vector<GroupFile> files, boolean replace) {
		if(files == null) { return 0; }
		
		int numberOfFilesAdded = 0;
		int fileIndex = -1;
		
		for(int i=0;i<files.size();i++) {
			if(files.elementAt(i) != null && files.elementAt(i).isValid()) {
				fileIndex = indexOfFile(files.elementAt(i));
				
				if(fileIndex == -1) {
					m_files.add(files.elementAt(i));
					
					numberOfFilesAdded++;
				}
				else {
					if(replace) {
						m_files.set(fileIndex, files.elementAt(i));
						
						numberOfFilesAdded++;
					}
				}
			}
		}
		
		if(numberOfFilesAdded > 0) {
			setChanged(true);
			
			if(shouldAutoSortFiles()) {
				sortFiles();
			}
		}
		
		return numberOfFilesAdded;
	}

	public int addFiles(Group group, boolean replace) {
		if(group == null) { return 0; }
		
		return addFiles(group.m_files, replace);
	}
	
	public int addFiles(File[] files, boolean replace) {
		if(files == null || files.length == 0) { return 0; }
		
		GroupFile g = null;
		
		int numberOfFilesAdded = 0;
		int fileIndex = -1;
		
		for(int i=0;i<files.length;i++) {
			if(files[i] == null || !files[i].exists() || !files[i].isFile()) { continue; }
			
			try {
				g = GroupFile.readFrom(files[i]);
			}
			catch(IOException e) {
				SystemConsole.instance.writeLine("Warning: Failed to read file \"" + files[i].getName() + "\" while adding files to group" + (m_file == null ? "." : "\"" + m_file.getName() + "\"."));
				 
				continue;
			}
			
			if(g == null || !g.isValid()) {
				SystemConsole.instance.writeLine("Warning: Encountered invalid file \"" + files[i].getName() + "\" while adding files to group" + (m_file == null ? "." : "\"" + m_file.getName() + "\"."));
				
				continue;
			}
			
			fileIndex = indexOfFile(g);
			 
			if(fileIndex == -1) {
				m_files.add(g);
				
				numberOfFilesAdded++;
			}
			else {
				if(replace) {
					m_files.set(fileIndex, g);
					
					numberOfFilesAdded++;
				}
			} 
		}
		
		if(numberOfFilesAdded > 0) {
			setChanged(true);
			
			if(shouldAutoSortFiles()) {
				sortFiles();
			}
		}
		
		return numberOfFilesAdded;
	}
	
	public boolean renameFile(int index, String newFileName) {
		if(index < 0 || index >= m_files.size() || newFileName == null || newFileName.length() == 0) { return false; }
		
		String formattedFileName = newFileName.trim();
		if(formattedFileName.length() == 0 || formattedFileName.length() > GroupFile.MAX_FILE_NAME_LENGTH) { return false; }
		
		m_files.elementAt(index).setFileName(formattedFileName);
		
		setChanged(true);
		
		if(shouldAutoSortFiles()) {
			sortFiles();
		}
		
		return true;
	}
	
	public boolean renameFile(String oldFileName, String newFileName) {
		return renameFile(indexOfFile(oldFileName), newFileName);
	}
	
	public boolean renameFile(GroupFile file, String newFileName) {
		return renameFile(indexOfFile(file), newFileName);
	}
	
	public boolean renameFile(File file, String newFileName) {
		return renameFile(indexOfFile(file), newFileName);
	}
	
	public boolean replaceFile(int index, GroupFile newFile) {
		if(index < 0 || index >= m_files.size() || newFile == null || !newFile.isValid()) { return false; }
		
		m_files.set(index, newFile);
		
		setChanged(true);
		
		if(shouldAutoSortFiles()) {
			sortFiles();
		}
		
		return true;
	}

	public boolean replaceFile(int index, File newFile, boolean sort) {
		if(index < 0 || index >= m_files.size() || newFile == null || !newFile.exists() || !newFile.isFile()) { return false; }
		
		GroupFile newGroupFile = null;
		
		try {
			newGroupFile = GroupFile.readFrom(newFile);
		}
		catch(IOException e) {
			SystemConsole.instance.writeLine("Warning: Failed to read file \"" + newFile.getName() + "\" while adding files to group" + (m_file == null ? "." : "\"" + m_file.getName() + "\"."));
			 
			return false;
		}
		
		if(newGroupFile == null || !newGroupFile.isValid()) {
			SystemConsole.instance.writeLine("Warning: Encountered invalid file \"" + newFile.getName() + "\" while adding files to group" + (m_file == null ? "." : "\"" + m_file.getName() + "\"."));
			
			return false;
		}
		
		m_files.set(index, newGroupFile);
		
		setChanged(true);
		
		if(shouldAutoSortFiles()) {
			sortFiles();
		}
		
		return true;
	}
	
	public boolean replaceFile(GroupFile oldFile, GroupFile newFile) {
		if(oldFile == null || newFile == null || !oldFile.isValid() || !newFile.isValid()) { return false; }
		
		int fileIndex = indexOfFile(oldFile);
		
		if(fileIndex != -1) {
			m_files.set(fileIndex, newFile);
			
			setChanged(true);
			
			if(shouldAutoSortFiles()) {
				sortFiles();
			}
			
			return true;
		}
		
		return false;
	}

	public boolean replaceFile(GroupFile oldFile, File newFile) {
		if(oldFile == null || newFile == null || !oldFile.isValid() || !newFile.exists() || !newFile.isFile()) { return false; }
		
		int fileIndex = indexOfFile(oldFile);
		GroupFile newGroupFile = null;
		
		if(fileIndex != -1) {
			try {
				newGroupFile = GroupFile.readFrom(newFile);
			}
			catch(IOException e) {
				SystemConsole.instance.writeLine("Warning: Failed to read file \"" + newFile.getName() + "\" while adding files to group" + (m_file == null ? "." : "\"" + m_file.getName() + "\"."));
				 
				return false;
			}
			
			if(newGroupFile == null || !newGroupFile.isValid()) {
				SystemConsole.instance.writeLine("Warning: Encountered invalid file \"" + newFile.getName() + "\" while adding files to group" + (m_file == null ? "." : "\"" + m_file.getName() + "\"."));
				
				return false;
			}
			
			m_files.set(fileIndex, newGroupFile);

			setChanged(true);
			
			if(shouldAutoSortFiles()) {
				sortFiles();
			}
			
			return true;
		}
		
		return false;
	}
	
	public boolean replaceFile(String fileName, GroupFile newFile) {
		if(fileName == null || fileName.length() == 0 || newFile == null || !newFile.isValid()) { return false; }
		
		String formattedFileName = fileName.trim();
		if(formattedFileName.length() == 0 || formattedFileName.length() > GroupFile.MAX_FILE_NAME_LENGTH) { return false; }

		int fileIndex = indexOfFile(formattedFileName);
		
		if(fileIndex != -1) {
			m_files.set(fileIndex, newFile);

			setChanged(true);
			
			if(shouldAutoSortFiles()) {
				sortFiles();
			}
			
			return true;
		}
		
		return false;
	}

	public boolean replaceFile(String fileName, File newFile) {
		if(fileName == null || fileName.length() == 0 || newFile == null || !newFile.exists() || !newFile.isFile()) { return false; }
		
		String formattedFileName = fileName.trim();
		if(formattedFileName.length() == 0 || formattedFileName.length() > GroupFile.MAX_FILE_NAME_LENGTH) { return false; }
		
		int fileIndex = indexOfFile(formattedFileName);
		GroupFile newGroupFile = null;
		
		if(fileIndex != -1) {
			try {
				newGroupFile = GroupFile.readFrom(newFile);
			}
			catch(IOException e) {
				SystemConsole.instance.writeLine("Warning: Failed to read file \"" + newFile.getName() + "\" while adding files to group" + (m_file == null ? "." : "\"" + m_file.getName() + "\"."));
				 
				return false;
			}
			
			if(newGroupFile == null || !newGroupFile.isValid()) {
				SystemConsole.instance.writeLine("Warning: Encountered invalid file \"" + newFile.getName() + "\" while adding files to group" + (m_file == null ? "." : "\"" + m_file.getName() + "\"."));
				
				return false;
			}
			
			m_files.set(fileIndex, newGroupFile);

			setChanged(true);
			
			if(shouldAutoSortFiles()) {
				sortFiles();
			}
			
			return true;
		}
		
		return false;
	}
	
	public boolean replaceFile(File oldFile, GroupFile newFile) {
		if(oldFile == null || oldFile.getName().length() == 0 || newFile == null || !newFile.isValid()) { return false; }
		
		return replaceFile(oldFile.getName(), newFile);
	}
	
	public boolean replaceFile(File oldFile, File newFile) {
		if(oldFile == null || oldFile.getName().length() == 0 || newFile == null || !newFile.exists() || !newFile.isFile()) { return false; }
		
		return replaceFile(oldFile.getName(), newFile);
	}
	
	public boolean removeFile(int index) {
		return removeFile(index, true);
	}
	
	public boolean removeFile(String fileName) {
		return removeFile(fileName, true);
	}
	
	public boolean removeFile(File file) {
		return removeFile(file, true);
	}
	
	public boolean removeFile(GroupFile file) {
		return removeFile(file, true);
	}
	
	protected boolean removeFile(int index, boolean update) {
		if(index < 0 || index >= m_files.size()) { return false; }
		
		m_files.remove(index);
		
		if(update) {
			setChanged(true);
		}
		
		return true;
	}
	
	protected boolean removeFile(String fileName, boolean update) {
		if(fileName == null) { return false; }
		
		String formattedFileName = fileName.trim();
		if(formattedFileName.length() == 0 || formattedFileName.length() > GroupFile.MAX_FILE_NAME_LENGTH) { return false; }
		
		String currentFileName;
		for(int i=0;i<m_files.size();i++) {
			currentFileName = m_files.elementAt(i).getFileName();
			if(currentFileName != null && currentFileName.equalsIgnoreCase(formattedFileName)) {
				m_files.remove(i);
				
				if(update) {
					setChanged(true);
				}
				
				return true;
			}
		}
		return false;
	}
	
	protected boolean removeFile(File file, boolean update) {
		if(file == null) {
			return false;
		}
		
		return removeFile(file.getName(), update);
	}
	
	protected boolean removeFile(GroupFile file, boolean update) {
		if(file == null || !file.isValid()) { return false; }
		
		return removeFile(file.getFileName(), update);
	}

	public int removeFiles(String[] fileNames) {
		if(fileNames == null || fileNames.length == 0) { return 0; }

		int numberOfFilesRemoved = 0;
		
		for(int i=0;i<fileNames.length;i++) {
			if(removeFile(fileNames[i], false)) {
				numberOfFilesRemoved++;
			}
		}
		
		if(numberOfFilesRemoved > 0) {
			setChanged(true);
		}
		
		return numberOfFilesRemoved;
	}
	
	public int removeFiles(File[] files) {
		if(files == null || files.length == 0) { return 0; }
		
		int numberOfFilesRemoved = 0;
		
		for(int i=0;i<files.length;i++) {
			if(removeFile(files[i], false)) {
				numberOfFilesRemoved++;
			}
		}
		
		if(numberOfFilesRemoved > 0) {
			setChanged(true);
		}
		
		return numberOfFilesRemoved;
	}
	
	public int removeFiles(GroupFile[] files) {
		if(files == null || files.length == 0) { return 0; }

		int numberOfFilesRemoved = 0;
		
		for(int i=0;i<files.length;i++) {
			if(removeFile(files[i], false)) {
				numberOfFilesRemoved++;
			}
		}
		
		if(numberOfFilesRemoved > 0) {
			setChanged(true);
		}
		
		return numberOfFilesRemoved;
	}

	public int removeFiles(Vector<GroupFile> files) {
		if(files == null || files.size() == 0) { return 0; }

		int numberOfFilesRemoved = 0;
		
		for(int i=0;i<files.size();i++) {
			if(removeFile(files.elementAt(i), false)) {
				numberOfFilesRemoved++;
			}
		}
		
		if(numberOfFilesRemoved > 0) {
			setChanged(true);
		}
		
		return numberOfFilesRemoved;
	}
	
	public void clearFiles() {
		m_files.clear();
		
		setChanged(true);
	}
	
	public boolean createFrom(File directory) {
		if(directory == null || !directory.exists() || !directory.isDirectory()) { return false; }
		
		return addFiles(directory.listFiles()) > 0;
	}
	
	public abstract boolean load() throws GroupReadException;
	
	public abstract boolean save() throws GroupWriteException;
	
	public void sortFiles() {
		if(!shouldSortFiles()) { return; }
		
		notifySortStarted();
		
		Vector<GroupFile> sortedFiles = mergeSortFiles(m_files);
		
		if(!checkSameFileOrder(m_files, sortedFiles)) {
			m_files = sortedFiles;
			
			setChanged(true);
		}
		
		notifySortFinished();
	}
	
	protected Vector<GroupFile> mergeSortFiles(Vector<GroupFile> groupFiles) {
		if(groupFiles == null) { return null; }
		
		if(groupFiles.size() == 0) {
			return new Vector<GroupFile>();
		}
		
		if(groupFiles.size() == 1) {
			Vector<GroupFile> groupFile = new Vector<GroupFile>();
			groupFile.add(groupFiles.elementAt(0));
			
			return groupFile;
		}
		
		Vector<GroupFile> left = new Vector<GroupFile>();
		Vector<GroupFile> right = new Vector<GroupFile>();
		
		int mid = groupFiles.size() / 2;
		
		for(int i=0;i<mid;i++) {
			left.add(groupFiles.elementAt(i));
		}
		
		for(int i=mid;i<groupFiles.size();i++) {
			right.add(groupFiles.elementAt(i));
		}
		
		left = mergeSortFiles(left);
		right = mergeSortFiles(right);
		
		return mergeFiles(left, right);
	}
	
	protected Vector<GroupFile> mergeFiles(Vector<GroupFile> left, Vector<GroupFile> right) {
		Vector<GroupFile> result = new Vector<GroupFile>();
		
		boolean addLeft = true;
		
		SortDirection sortDirection = SettingsManager.instance.sortAllGroups ? SettingsManager.instance.sortDirection : m_sortDirection;
		
		while(left.size() > 0 && right.size() > 0) {
			switch(SettingsManager.instance.sortAllGroups ? SettingsManager.instance.sortType : m_sortType) {
				case FileName:
					if(sortDirection == SortDirection.Ascending) {
						addLeft = left.elementAt(0).getFileName().compareToIgnoreCase(right.elementAt(0).getFileName()) <= 0;
					}
					else {
						addLeft = left.elementAt(0).getFileName().compareToIgnoreCase(right.elementAt(0).getFileName()) > 0;
					}
					break;
					
				case FileExtension:
					String extensionLeft = Utilities.getFileExtension(left.elementAt(0).getFileName());
					String extensionRight = Utilities.getFileExtension(right.elementAt(0).getFileName());
					
					if(extensionLeft != null && extensionRight != null && extensionLeft.length() > 0 && extensionRight.length() > 0) {
						if(sortDirection == SortDirection.Ascending) {
							addLeft = extensionLeft.compareToIgnoreCase(extensionRight) <= 0;
						}
						else {
							addLeft = extensionLeft.compareToIgnoreCase(extensionRight) > 0;
						}
					}
					break;
					
				case FileSize:
					if(sortDirection == SortDirection.Ascending) {
						addLeft = left.elementAt(0).getFileSize() <= right.elementAt(0).getFileSize();
					}
					else {
						addLeft = left.elementAt(0).getFileSize() > right.elementAt(0).getFileSize();
					}
					break;
				
				default:
					break;
			}
			

			if(addLeft) {
				result.add(left.elementAt(0));
				left.remove(0);
			}
			else {
				result.add(right.elementAt(0));
				right.remove(0);
			}
		}
		
		for(int i=0;i<left.size();i++) {
			result.add(left.elementAt(i));
		}
	
		for(int i=0;i<right.size();i++) {
			result.add(right.elementAt(i));
		}
	
		return result;
	}
	
	public int numberOfGroupChangeListeners() {
		return m_groupChangeListeners.size();
	}
	
	public GroupChangeListener getGroupChangeListener(int index) {
		if(index < 0 || index >= m_groupChangeListeners.size()) { return null; }
		
		return m_groupChangeListeners.elementAt(index);
	}
	
	public boolean hasGroupChangeListener(GroupChangeListener c) {
		return m_groupChangeListeners.contains(c);
	}
	
	public int indexOfGroupChangeListener(GroupChangeListener c) {
		return m_groupChangeListeners.indexOf(c);
	}
	
	public boolean addGroupChangeListener(GroupChangeListener c) {
		if(c == null || m_groupChangeListeners.contains(c)) { return false; }
		
		m_groupChangeListeners.add(c);
		
		return true;
	}
	
	public boolean removeGroupChangeListener(int index) {
		if(index < 0 || index >= m_groupChangeListeners.size()) { return false; }
		
		m_groupChangeListeners.remove(index);
		
		return true;
	}
	
	public boolean removeGroupChangeListener(GroupChangeListener c) {
		if(c == null) { return false; }
		
		return m_groupChangeListeners.remove(c);
	}
	
	public void clearGroupChangeListeners() {
		m_groupChangeListeners.clear();
	}
	
	public void notifyGroupChanged() {
		for(int i=0;i<m_groupChangeListeners.size();i++) {
			m_groupChangeListeners.elementAt(i).handleGroupChange(this);
		}
	}
	
	public int numberOfGroupSortListeners() {
		return m_groupSortListeners.size();
	}
	
	public GroupSortListener getGroupSortListener(int index) {
		if(index < 0 || index >= m_groupSortListeners.size()) { return null; }
		
		return m_groupSortListeners.elementAt(index);
	}
	
	public boolean hasGroupSortListener(GroupSortListener s) {
		return m_groupSortListeners.contains(s);
	}
	
	public int indexOfGroupSortListener(GroupSortListener s) {
		return m_groupSortListeners.indexOf(s);
	}
	
	public boolean addGroupSortListener(GroupSortListener s) {
		if(s == null || m_groupSortListeners.contains(s)) { return false; }
		
		m_groupSortListeners.add(s);
		
		return true;
	}
	
	public boolean removeGroupSortListener(int index) {
		if(index < 0 || index >= m_groupSortListeners.size()) { return false; }
		
		m_groupSortListeners.remove(index);
		
		return true;
	}
	
	public boolean removeGroupSortListener(GroupSortListener s) {
		if(s == null) { return false; }
		
		return m_groupSortListeners.remove(s);
	}
	
	public void clearGroupSortListeners() {
		m_groupSortListeners.clear();
	}
	
	public void notifyGroupSortStateChanged() {
		for(int i=0;i<m_groupSortListeners.size();i++) {
			m_groupSortListeners.elementAt(i).handleGroupSortStateChanged(this);
		}
	}
	
	public void notifySortStarted() {
		for(int i=0;i<m_groupSortListeners.size();i++) {
			m_groupSortListeners.elementAt(i).handleGroupSortStarted(this);
		}
	}
	
	public void notifySortFinished() {
		for(int i=0;i<m_groupSortListeners.size();i++) {
			m_groupSortListeners.elementAt(i).handleGroupSortFinished(this);
		}
	}
	
	public boolean checkSameFileOrder(Vector<GroupFile> files) {
		if(files == null) { return false; }
		
		if(m_files.size() != files.size()) { return false; }
		
		for(int i=0;i<m_files.size();i++) {
			if(m_files.elementAt(i) != files.elementAt(i)) {
				return false;
			}
		}
		return true;
	}
	
	public static boolean checkSameFileOrder(Vector<GroupFile> a, Vector<GroupFile> b) {
		if(a == null) { return b == null; }
		
		if(a.size() != b.size()) { return false; }
		
		for(int i=0;i<a.size();i++) {
			if(a.elementAt(i) != b.elementAt(i)) {
				return false;
			}
		}
		return true;
	}
	
	public boolean equals(Object o) {
		if(o == null || !(o instanceof Group)) {
			return false;
		}
		
		Group g = (Group) o;
		
		if(m_files.size() != g.m_files.size()) {
			return false;
		}
		
		for(int i=0;i<m_files.size();i++) {
			if(!g.hasFile(m_files.elementAt(i))) {
				return false;
			}
		}
		return true;
	}
	
	public String toString() {
		String groupString = getGroupFileType().getName() + (m_file == null ? "" : " \"" + m_file.getName() + "\"") + " Files (" + m_files.size() + "): " ;
		
		for(int i=0;i<m_files.size();i++) {
			groupString += m_files.elementAt(i).getFileName();
			if(i < m_files.size() - 1) {
				groupString += ", ";
			}
		}
		
		return groupString;
	}
	
}
