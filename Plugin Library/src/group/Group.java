package group;

import java.util.*;
import java.io.*;
import exception.*;
import utilities.*;
import console.*;

public abstract class Group {
	
	protected GroupFileType m_fileType;
	protected File m_file;
	protected Vector<GroupFile> m_files;
	protected boolean m_loaded;
	
	public Group() {
		m_fileType = getDefaultGroupFileType();
		m_file = null;
		m_files = new Vector<GroupFile>();
		m_loaded = false;
	}
	
	public Group(String fileName) {
		m_fileType = getDefaultGroupFileType();
		m_file = fileName == null ? null : new File(fileName);
		m_files = new Vector<GroupFile>();
		m_loaded = false;
	}
	
	public Group(File file) {
		m_fileType = getDefaultGroupFileType();
		m_file = file;
		m_files = new Vector<GroupFile>();
		m_loaded = false;
	}
	
	public Group(GroupFileType fileType) throws InvalidGroupFileTypeException, UnsupportedGroupFileTypeException {
		if(!fileType.isValid()) { throw new InvalidGroupFileTypeException("Cannot create group with " + fileType == null ? "null file type" : (fileType.getName().length() == 0 ? "empty name" : "no file extensions") + "."); }
		if(fileType != null && !hasGroupFileType(fileType)) { throw new UnsupportedGroupFileTypeException("Unsupported group file type: \"" + fileType.getName() + "\" for group class: \"" + getClass().getName() + "\"."); }
		m_fileType = fileType == null ? getDefaultGroupFileType() : fileType;
		m_file = null;
		m_files = new Vector<GroupFile>();
		m_loaded = false;
	}

	public Group(String fileName, GroupFileType fileType) throws InvalidGroupFileTypeException, UnsupportedGroupFileTypeException {
		if(!fileType.isValid()) { throw new InvalidGroupFileTypeException("Cannot create group with " + fileType == null ? "null file type" : (fileType.getName().length() == 0 ? "empty name" : "no file extensions") + "."); }
		if(fileType != null && !hasGroupFileType(fileType)) { throw new UnsupportedGroupFileTypeException("Unsupported group file type: \"" + fileType.getName() + "\" for group class: \"" + getClass().getName() + "\"."); }
		m_fileType = fileType == null ? getDefaultGroupFileType() : fileType;
		m_file = fileName == null ? null : new File(fileName);
		m_files = new Vector<GroupFile>();
		m_loaded = false;
	}
	
	public Group(File file, GroupFileType fileType) throws InvalidGroupFileTypeException, UnsupportedGroupFileTypeException {
		if(!fileType.isValid()) { throw new InvalidGroupFileTypeException("Cannot create group with " + fileType == null ? "null file type" : (fileType.getName().length() == 0 ? "empty name" : "no file extensions") + "."); }
		if(fileType != null && !hasGroupFileType(fileType)) { throw new UnsupportedGroupFileTypeException("Unsupported group file type: \"" + fileType.getName() + "\" for group class: \"" + getClass().getName() + "\"."); }
		m_fileType = fileType == null ? getDefaultGroupFileType() : fileType;
		m_file = file;
		m_files = new Vector<GroupFile>();
		m_loaded = false;
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
		if(formattedFileName.length() == 0) { return false; }
		if(formattedFileName.length() > GroupFile.MAX_FILE_NAME_LENGTH) {
			formattedFileName = formattedFileName.substring(0, GroupFile.MAX_FILE_NAME_LENGTH - 1);
		}
		
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
		if(formattedFileName.length() == 0) { return -1; }
		if(formattedFileName.length() > GroupFile.MAX_FILE_NAME_LENGTH) {
			formattedFileName = formattedFileName.substring(0, GroupFile.MAX_FILE_NAME_LENGTH - 1);
		}
		
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
		if(formattedFileName.length() == 0) { return null; }
		if(formattedFileName.length() > GroupFile.MAX_FILE_NAME_LENGTH) {
			formattedFileName = formattedFileName.substring(0, GroupFile.MAX_FILE_NAME_LENGTH - 1);
		}
		
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
	
	public boolean addFile(File file) {
		if(file == null || !file.isFile() || !file.exists()) { return false; }
		
		if(file.getName().length() > GroupFile.MAX_FILE_NAME_LENGTH) {
        	SystemConsole.instance.writeLine("Warning: Truncating file name \"" + file.getName() + "\", exceeds max length of " + GroupFile.MAX_FILE_NAME_LENGTH + ".");
        }
		
		GroupFile newGroupFile = null;
		try { newGroupFile = GroupFile.readFrom(file); }
		catch(FileNotFoundException e) { return false; }
		catch(IOException e) { return false; }
		if(newGroupFile == null) { return false; }
		
		m_files.add(newGroupFile);
		
		return true;
	}
	
	public boolean addFile(GroupFile file) {
		if(file == null || !file.isValid()) { return false; }
		
		m_files.add(file);
		
		return true;
	}
	
	public int addFiles(GroupFile files[]) {
		return addFiles(files, true);
	}
	
	public int addFiles(Vector<GroupFile> files) {
		return addFiles(files, true);
	}
	
	public int addFiles(Group group) {
		return addFiles(group, true);
	}
	
	public int addFiles(GroupFile files[], boolean replace) {
		if(files == null) { return 0; }
		
		int filesAdded = 0;
		int fileIndex = -1;
		
		for(int i=0;i<files.length;i++) {
			if(files[i] != null && files[i].isValid()) {
				fileIndex = indexOfFile(files[i]);
				
				if(fileIndex == -1) {
					m_files.add(files[i]);
					
					filesAdded++;
				}
				else {
					if(replace) {
						m_files.set(i, files[i]);
						
						filesAdded++;
					}
				}
			}
		}
		
		return filesAdded;
	}
	
	public int addFiles(Vector<GroupFile> files, boolean replace) {
		if(files == null) { return 0; }
		
		int filesAdded = 0;
		int fileIndex = -1;
		
		for(int i=0;i<files.size();i++) {
			if(files.elementAt(i) != null && files.elementAt(i).isValid()) {
				fileIndex = indexOfFile(files.elementAt(i));
				
				if(fileIndex == -1) {
					m_files.add(files.elementAt(i));
					
					filesAdded++;
				}
				else {
					if(replace) {
						m_files.set(i, files.elementAt(i));
						
						filesAdded++;
					}
				}
			}
		}
		
		return filesAdded;
	}
	
	public int addFiles(Group group, boolean replace) {
		if(group == null) { return 0; }
		
		return addFiles(group.m_files);
	}

	public int addFiles(File files[]) {
		if(files == null || files.length == 0) { return 0; }
		
		GroupFile g = null;
		
		int numberOfFilesAdded = 0;
		
		for(int i=0;i<files.length;i++) {
			if(files[i] == null || !files[i].exists() || !files[i].isFile()) { continue; }
			
			 try {
				 g = GroupFile.readFrom(files[i]);
			 }
			 catch(IOException e) {
				 SystemConsole.instance.writeLine("Warning: Failed to read file \"" + files[i].getName() + "\" while adding files to group \"" + m_file.getName() + "\".");
				 
				 continue;
			 }
			 
			 if(!g.isValid()) {
				 SystemConsole.instance.writeLine("Warning: Encountered invalid file \"" + files[i].getName() + "\" while adding files to group \"" + m_file.getName() + "\".");
				 
				 continue;
			 }
			 
			 if(!addFile(g)) {
				 SystemConsole.instance.writeLine("Warning: Failed to add file \"" + files[i].getName() + "\" to group \"" + m_file.getName() + "\".");
			 }
			 
			 numberOfFilesAdded++;
		}
		
		return numberOfFilesAdded;
	}
	
	public boolean removeFile(int index) {
		if(index < 0 || index >= m_files.size()) { return false; }
		
		m_files.remove(index);
		
		return true;
	}
	
	public boolean removeFile(String fileName) {
		if(fileName == null) { return false; }
		
		String formattedFileName = fileName.trim();
		if(formattedFileName.length() == 0) { return false; }
		if(formattedFileName.length() > GroupFile.MAX_FILE_NAME_LENGTH) {
			formattedFileName = formattedFileName.substring(0, GroupFile.MAX_FILE_NAME_LENGTH - 1);
		}
		
		String currentFileName;
		for(int i=0;i<m_files.size();i++) {
			currentFileName = m_files.elementAt(i).getFileName();
			if(currentFileName != null && currentFileName.equalsIgnoreCase(formattedFileName)) {
				m_files.remove(i);
				
				return true;
			}
		}
		return false;
	}
	
	public boolean removeFile(File file) {
		if(file == null) {
			return false;
		}
		
		return removeFile(file.getName());
	}
	
	public boolean removeFile(GroupFile file) {
		if(file == null || !file.isValid()) { return false; }
		
		return removeFile(file.getFileName());
	}
	
	public void clearFiles() {
		m_files.clear();
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
	
	public boolean createFrom(File directory) {
		if(directory == null || !directory.exists() || !directory.isDirectory()) { return false; }
		
		return addFiles(directory.listFiles()) > 0;
	}
	
	public abstract boolean load() throws GroupReadException;
	
	public abstract boolean save() throws GroupWriteException;
	
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
	
}
