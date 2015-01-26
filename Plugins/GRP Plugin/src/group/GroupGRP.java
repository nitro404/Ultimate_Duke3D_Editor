package group;

import java.util.*;
import java.io.*;
import exception.*;
import utilities.*;
import console.*;

public class GroupGRP extends Group {
	
	public static final Endianness FILE_ENDIANNESS = Endianness.LittleEndian;
	public static GroupFileType[] FILE_TYPES = {
		new GroupFileType("Duke Nukem 3D Group", "GRP")
	};
	
	public static final String HEADER_TEXT = "KenSilverman";
	
	public static final int HEADER_TEXT_LENGTH = 12;
	public static final int NUMBER_OF_FILES_LENGTH = 4;
	public static final int GROUP_FILE_NAME_LENGTH = 12;
	public static final int GROUP_FILE_SIZE_LENGTH = 4;
	
	public static final int HEADER_LENGTH = HEADER_TEXT_LENGTH + NUMBER_OF_FILES_LENGTH;
	public static final int GROUP_FILE_HEADER_LENGTH = GROUP_FILE_NAME_LENGTH + GROUP_FILE_SIZE_LENGTH;
	
	public static final byte GROUP_HEADER_TEXT_DATA[] = new byte[] { // 12 bytes (total)
		0x4b, 0x65, 0x6e, // Ken (3 bytes)
		0x53, 0x69, 0x6c, 0x76, 0x65, 0x72, 0x6d, 0x61, 0x6e // Silverman (9 bytes)
	};
	
	public GroupGRP() throws InvalidGroupFileTypeException, UnsupportedGroupFileTypeException {
		super(FILE_TYPES[0]);
	}
	
	public GroupGRP(String fileName) throws InvalidGroupFileTypeException, UnsupportedGroupFileTypeException {
		super(fileName, FILE_TYPES[0]);
	}
	
	public GroupGRP(File file) throws InvalidGroupFileTypeException, UnsupportedGroupFileTypeException {
		super(file, FILE_TYPES[0]);
	}
	
	public Endianness getFileEndianness() {
		return FILE_ENDIANNESS;
	}
	
	public boolean isInstantiable() {
		return true;
	}
	
	public GroupFileType getDefaultGroupFileType() {
		return FILE_TYPES[0];
	}
	
	public String getDefaultGroupFileExtension() {
		return FILE_TYPES[0].getExtension(0);
	}
	
	public GroupFileType[] getGroupFileTypes() {
		return FILE_TYPES;
	}
	
	public int getGroupFileSize() {
		int fileSize = HEADER_LENGTH;
		for(int i=0;i<m_files.size();i++) {
			fileSize += GROUP_FILE_HEADER_LENGTH + m_files.elementAt(i).getDataSize();
		}
		return fileSize;
	}
	
	public boolean load() throws GroupReadException {
		if(m_file == null || !m_file.exists()) { return false; }
		
		// verify that the file has an extension
		String extension = Utilities.getFileExtension(m_file.getName());
		if(extension == null) {
			throw new GroupReadException("File \"" + m_file.getName() + "\" has no extension.");
		}
		
		// verify that the file extension is supported
		if(!hasGroupFileTypeWithExtension(extension)) {
			throw new GroupReadException("File \"" + m_file.getName() +  "\" has unsupported extension: " + extension);
		}
		
		// check to make sure that the file is not too big to be stored in memory
		if(m_file.length() > Integer.MAX_VALUE) {
			throw new GroupReadException("File \"" + m_file.getName() +  "\" is too large to store in memory.");
		}
		
		// read the file into memory
		InputStream in = null;
		byte data[] = new byte[(int) m_file.length()];
		try {
			in = new FileInputStream(m_file);
			in.read(data);
			in.close();
		}
		catch(FileNotFoundException e) {
			throw new GroupReadException("File \"" + m_file.getName() +  "\" not found.");
		}
		catch(IOException e) {
			throw new GroupReadException("Error reading file \"" + m_file.getName() +  "\": " + e.getMessage());
		}
		
		SystemConsole.instance.writeLine("Opened " + FILE_TYPES[0].getName() + " file: \"" + m_file.getName() + "\", loaded " + data.length + " bytes into memory.");
		
		int offset = 0;
		
		// verify that the data is long enough to contain header information
		if(data.length < offset + HEADER_LENGTH) {
			throw new GroupReadException("Group file \"" + m_file.getName() + "\" is incomplete or corrupted: missing header text.");
		}
		
		// verify that the header text is specified in the header
		String headerText = Serializer.deserializeByteString(Arrays.copyOfRange(data, offset, offset + HEADER_TEXT_LENGTH));
		
		if(!headerText.trim().equalsIgnoreCase(HEADER_TEXT)) {
			throw new GroupReadException(FILE_TYPES[0].getName() + " file \"" + m_file.getName() +  "\" is not a valid format, missing " + HEADER_TEXT + " specification in header.");
		}
		
		offset += HEADER_TEXT_LENGTH;
		
		SystemConsole.instance.writeLine("Verified group file header text.");
		
		// verify that the data is long enough to contain the number of files specification
		if(data.length < offset + NUMBER_OF_FILES_LENGTH) {
			throw new GroupReadException(FILE_TYPES[0].getName() + " file \"" + m_file.getName() + "\" is incomplete or corrupted: missing number of files value.");
		}
		
		// read and verify the number of files
		int numberOfFiles = Serializer.deserializeInteger(Arrays.copyOfRange(data, offset, offset + NUMBER_OF_FILES_LENGTH), FILE_ENDIANNESS);
		
		if(numberOfFiles < 0) {
			throw new GroupReadException(FILE_TYPES[0].getName() + " file \"" + m_file.getName() + "\" has a negative number of files value.");
		}
		
		SystemConsole.instance.writeLine("Number of files detected in group: " + numberOfFiles);

		offset += NUMBER_OF_FILES_LENGTH;
		
		String fileName = null;
		int fileSize = -1;
		GroupFile g = null;
		Vector<GroupFile> groupFiles = new Vector<GroupFile>(numberOfFiles == 0 ? 1 : numberOfFiles);
		for(int i=0;i<numberOfFiles;i++) {
			// verify that the data is long enough to contain the file name
			if(data.length < offset + GROUP_FILE_NAME_LENGTH) {
				throw new GroupReadException(FILE_TYPES[0].getName() + " file \"" + m_file.getName() + "\" is incomplete or corrupted: missing file #" + (i + 1) + " name.");
			}
			
			// read the file name
			fileName = Serializer.deserializeByteString(Arrays.copyOfRange(data, offset, offset + GROUP_FILE_NAME_LENGTH));
			
			offset += GROUP_FILE_NAME_LENGTH;
			
			// verify that the data is long enough to contain the file size value
			if(data.length < offset + GROUP_FILE_SIZE_LENGTH) {
				throw new GroupReadException(FILE_TYPES[0].getName() + " file \"" + m_file.getName() + "\" is incomplete or corrupted: missing file #" + (i + 1) + " size value.");
			}
			
			// read and verify the file size
			fileSize = Serializer.deserializeInteger(Arrays.copyOfRange(data, offset, offset + GROUP_FILE_SIZE_LENGTH), FILE_ENDIANNESS);
			
			if(fileSize < 0) {
				throw new GroupReadException(FILE_TYPES[0].getName() + " file \"" + m_file.getName() + "\" file #" + (i + 1) + " size can not be negative.");
			}
			
			offset += GROUP_FILE_SIZE_LENGTH;
			
			groupFiles.add(new GroupFile(fileName, fileSize));
		}
		
		SystemConsole.instance.writeLine("All file information parsed.");
		
		for(int i=0;i<numberOfFiles;i++) {
			g = groupFiles.elementAt(i);
			
			if(data.length < offset + g.getFileSize()) {
				throw new GroupReadException(FILE_TYPES[0].getName() + " file \"" + m_file.getName() + "\" is incomplete or corrupted: missing file #" + (i + 1) + " data.");
			}
			
			g.setData(Arrays.copyOfRange(data, offset, offset + g.getFileSize()));
			
			offset += g.getFileSize();
		}
		
		m_files = groupFiles;
		
		m_loaded = true;
		
		SystemConsole.instance.writeLine(FILE_TYPES[0].getName() + " file parsed successfully, " + groupFiles.size() + " files loaded into memory.");
		
		return true;
	}
	
	public boolean save() throws GroupWriteException {
		if(m_file == null) { return false; }
		
		if(!verifyAllFiles()) {
			throw new GroupWriteException("Failed to verify files.");
		}
		
		SystemConsole.instance.writeLine("Generating group file data.");
		
		byte data[] = new byte[getGroupFileSize()];

		int offset = 0;

		GroupFile g = null;
		
		System.arraycopy(GROUP_HEADER_TEXT_DATA, 0, data, 0, HEADER_TEXT_LENGTH);
		
		offset += HEADER_TEXT_LENGTH;
		
		System.arraycopy(Serializer.serializeInteger(m_files.size(), FILE_ENDIANNESS), 0, data, offset, NUMBER_OF_FILES_LENGTH);
		
		offset += NUMBER_OF_FILES_LENGTH;
		
		for(int i=0;i<m_files.size();i++) {
			g = m_files.elementAt(i);
			
			if(g.getFileName() != null && g.getFileName().length() > 0) {
				System.arraycopy(Serializer.serializeByteString(g.getFileName()), 0, data, offset, g.getFileName().length());
			}
			
			offset += GROUP_FILE_NAME_LENGTH;
			
			System.arraycopy(Serializer.serializeInteger(g.getDataSize(), FILE_ENDIANNESS), 0, data, offset, GROUP_FILE_SIZE_LENGTH);
			
			offset += GROUP_FILE_SIZE_LENGTH;
		}
		
		for(int i=0;i<m_files.size();i++) {
			g = m_files.elementAt(i);
			
			if(g.getData() != null && g.getData().length > 0) {
				System.arraycopy(g.getData(), 0, data, offset, g.getDataSize());
				
				offset += g.getDataSize();
			}
		}
		
		SystemConsole.instance.writeLine("Writing " + FILE_TYPES[0].getName() + " data to file: \"" + m_file.getName() + "\".");
		
		OutputStream out = null;
		try {
			out = new BufferedOutputStream(new FileOutputStream(m_file));
			out.write(data);
			out.close();
		}
		catch(IOException e) {
			throw new GroupWriteException("Error writing to file: " + m_file.getName() +  ": " + e.getMessage());
		}
		
		SystemConsole.instance.writeLine(FILE_TYPES[0].getName() + " file writing complete!");
		
		return true;
	}
	
	public boolean equals(Object o) {
		if(o == null || !(o instanceof GroupGRP)) {
			return false;
		}
		
		return super.equals(o);
	}
	
}
