package group;

import java.util.*;
import java.io.*;
import item.*;
import exception.*;
import utilities.*;
import console.*;

public class GroupSSI extends Group {
	
	public static final Endianness FILE_ENDIANNESS = Endianness.LittleEndian;
	public static String FILE_TYPE = "SSI Group";
	public static ItemFileType[] FILE_TYPES = {
		new ItemFileType("SSI Group Version 1", "SSI"),
		new ItemFileType("SSI Group Version 2", "SSI")
	};
	
	public static final int MAX_TITLE_LENGTH = 32;
	public static final int MAX_DESCRIPTION_LENGTH = 70;
	public static final int MAX_RUNFILE_LENGTH = 12;
	public static final int MAX_FILE_NAME_LENGTH = 12;
	
	public static final int VERSION_LENGTH = 4;
	public static final int NUMBER_OF_FILES_LENGTH = 4;
	public static final int TITLE_SIZE_LENGTH = 1;
	public static final int TITLE_LENGTH = MAX_TITLE_LENGTH;
	public static final int RUNFILE_SIZE_LENGTH = 1;
	public static final int RUNFILE_LENGTH = MAX_RUNFILE_LENGTH;
	public static final int NUMBER_OF_DESCRIPTIONS = 3;
	public static final int DESCRIPTION_SIZE_LENGTH = 1;
	public static final int DESCRIPTION_LENGTH = MAX_DESCRIPTION_LENGTH;
	public static final int FILE_NAME_SIZE_LENGTH = 1;
	public static final int FILE_NAME_LENGTH = MAX_FILE_NAME_LENGTH;
	public static final int FILE_SIZE_LENGTH = 4;
	public static final int UNKNOWN_DATA_LENGTH = 34 + 1 + 69;
	
	public static final int HEADER_LENGTH = VERSION_LENGTH + NUMBER_OF_FILES_LENGTH + TITLE_SIZE_LENGTH + TITLE_LENGTH + ((DESCRIPTION_SIZE_LENGTH + DESCRIPTION_LENGTH) * NUMBER_OF_DESCRIPTIONS);
	public static final int HEADER_RUNFILE_LENGTH = RUNFILE_SIZE_LENGTH + RUNFILE_LENGTH;
	public static final int FILE_HEADER_LENGTH = FILE_NAME_SIZE_LENGTH + FILE_NAME_LENGTH + FILE_SIZE_LENGTH + UNKNOWN_DATA_LENGTH;
	
	protected SSIVersion m_version;
	protected String m_title;
	protected String[] m_description;
	protected String m_runFile;
	
	public GroupSSI() throws InvalidFileTypeException, UnsupportedFileTypeException {
		super(FILE_TYPES[1]);
		
		m_version = SSIVersion.V2;
		m_title = null;
		m_description = new String[3];
		m_runFile = null;
	}
	
	public GroupSSI(String fileName) throws InvalidFileTypeException, UnsupportedFileTypeException {
		super(fileName, FILE_TYPES[1]);
		
		m_version = SSIVersion.V2;
		m_title = null;
		m_description = new String[3];
		m_runFile = null;
	}
	
	public GroupSSI(File file) throws InvalidFileTypeException, UnsupportedFileTypeException {
		super(file, FILE_TYPES[1]);
		
		m_version = SSIVersion.V2;
		m_title = null;
		m_description = new String[3];
		m_runFile = null;
	}
	
	public Endianness getFileEndianness() {
		return FILE_ENDIANNESS;
	}
	
	public boolean isInstantiable() {
		return true;
	}

	public boolean isInitialized() {
		return true;
	}

	public ItemFileType getDefaulFileType() {
		return FILE_TYPES[1];
	}
	
	public String getDefaultFileExtension() {
		return FILE_TYPES[1].getExtension();
	}
	
	public boolean setFileType(ItemFileType fileType) {
		if(super.setFileType(fileType)) {
			m_version = getVersionFromFileType(fileType);
			return true;
		}
		return false;
	}
	
	public static SSIVersion getVersionFromFileType(ItemFileType fileType) {
		for(int i=0;i<FILE_TYPES.length;i++) {
			if(fileType.equals(FILE_TYPES[i])) {
				return SSIVersion.values()[i];
			}
		}
		return SSIVersion.Invalid;
	}
	
	public ItemFileType[] getFileTypes() {
		return FILE_TYPES;
	}
	
	public SSIVersion getVersion() {
		return m_version;
	}
	
	public String getTitle() {
		return m_title;
	}

	public String getDescription() {
		String s = "";
		for(int i=0;i<NUMBER_OF_DESCRIPTIONS;i++) {
			if(m_description[i] == null || m_description[i].isEmpty()) { break; }
			
			if(i != 0 && s.charAt(s.length() - 1) != ' ' && m_description[i].charAt(0) != ' ') { s += " "; }
			
			s += m_description[i];
		}
		return s;
	}
	
	public String getDescription(int index) {
		if(index < 0 || index >= NUMBER_OF_DESCRIPTIONS) { return null; }
		
		return m_description[index];
	}
	
	public String getRunFile() {
		return m_runFile;
	}
	
	public boolean setVersion(SSIVersion version) {
		if(!version.isValid()) { return false; }
		
		boolean versionChanged = m_version != version;
		
		m_version = version;
		m_fileType = version.getFileType();
		
		if(versionChanged) {
			reverseAllFileExtensions();
			
			setChanged(true);
		}
		
		return true;
	}
	
	public void setTitle(String title) {
		boolean titleChanged = false;
		
		if(title == null) {
			titleChanged = m_title != null;
			
			m_title = null;
		}
		else {
			String formattedTitle = title.trim();
			if(formattedTitle.length() > MAX_TITLE_LENGTH) {
				formattedTitle = formattedTitle.substring(0, MAX_TITLE_LENGTH - 1);
			}
			
			titleChanged = !formattedTitle.equals(m_title);
			
			m_title = formattedTitle;
		}
		
		if(titleChanged) {
			setChanged(true);
		}
	}
	
	public void setDescription(String description, int index) {
		if(index < 0 || index >= NUMBER_OF_DESCRIPTIONS) { return; }
		
		boolean descriptionChanged = false; 
		
		if(description == null) {
			descriptionChanged = m_description[index] != null;
			
			m_description[index] = null;
		}
		else {
			String formattedDescription = description.trim();
			if(formattedDescription.length() > MAX_DESCRIPTION_LENGTH) {
				formattedDescription = formattedDescription.substring(0, MAX_DESCRIPTION_LENGTH - 1);
			}
			
			descriptionChanged = !formattedDescription.equals(m_description[index]);
			
			m_description[index] = formattedDescription;
		}
		
		if(descriptionChanged) {
			setChanged(true);
		}
	}
	
	public void setRunFile(String runFile) {
		boolean runFileChanged = false;
		
		String newRunFile = Utilities.truncateFileName(runFile, MAX_RUNFILE_LENGTH);
		
		runFileChanged = (m_runFile == null && newRunFile != null) ||
						 (m_runFile != null && newRunFile == null) ||
						 (m_runFile != null && newRunFile != null && !m_runFile.equals(newRunFile));
		
		m_runFile = newRunFile;
		
		if(runFileChanged) {
			setChanged(true);
		}
	}
	
	public int getGroupFileSize() {
		int fileSize = HEADER_LENGTH;
		if(m_version == SSIVersion.V2) {
			fileSize += HEADER_RUNFILE_LENGTH;
		}
		for(int i=0;i<m_files.size();i++) {
			fileSize += FILE_HEADER_LENGTH + m_files.elementAt(i).getDataSize();
		}
		return fileSize;
	}
	
	protected void reverseAllFileExtensions() {
		for(int i=0;i<m_files.size();i++) {
			m_files.elementAt(i).reverseFileExtension();
		}
	}
	
	public boolean load() throws GroupReadException {
		if(m_file == null || !m_file.exists()) { return false; }

		m_loading = true;
		
		// verify that the file has an extension
		String extension = Utilities.getFileExtension(m_file.getName());
		if(extension == null) {
			m_loading = false;
			throw new GroupReadException("File " + m_file.getName() + "\" has no extension.");
		}
		
		// verify that the file extension is supported
		if(!hasFileTypeWithExtension(extension)) {
			m_loading = false;
			throw new GroupReadException("File " + m_file.getName() +  " has unsupported extension: " + extension);
		}
		
		// check to make sure that the file is not too big to be stored in memory
		if(m_file.length() > Integer.MAX_VALUE) {
			m_loading = false;
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
			m_loading = false;
			throw new GroupReadException("File \"" + m_file.getName() +  "\" not found.");
		}
		catch(IOException e) {
			m_loading = false;
			throw new GroupReadException("Error reading file \"" + m_file.getName() +  "\": " + e.getMessage());
		}

		SystemConsole.instance.writeLine("Opened " + FILE_TYPE + " file: \"" + m_file.getName() + "\", loaded " + data.length + " bytes into memory.");
		
		int numberOfFiles = 0;
		int titleSize = 0;
		String title = null;
		int runFileSize = 0;
		String runFile = null;
		int descriptionSize[] = new int[NUMBER_OF_DESCRIPTIONS];
		String description[] = new String[NUMBER_OF_DESCRIPTIONS];
		int fileNameSize = 0;
		String fileName = null;
		int fileSize = 0;
		GroupFile g = null;
		Vector<GroupFile> groupFiles = new Vector<GroupFile>();
		
		int offset = 0;
		
		// verify that the data is long enough to contain version information
		if(data.length < offset + VERSION_LENGTH) {
			m_loading = false;
			throw new GroupReadException(FILE_TYPE + " file \"" + m_file.getName() + "\" is incomplete or corrupted: missing version.");
		}
		
		// read and verify the file version
		int versionValue = Serializer.deserializeInteger(Arrays.copyOfRange(data, offset, offset + VERSION_LENGTH), FILE_ENDIANNESS);
		
		SSIVersion version = SSIVersion.parseFrom(versionValue);
		
		if(!version.isValid()) {
			m_loading = false;
			throw new GroupReadException(FILE_TYPE + " file \"" + m_file.getName() + "\" is an invalid or unsupported file version: " + versionValue + ". Supported versions include: " + SSIVersion.getDisplayNameList() + ".");
		}
		
		SystemConsole.instance.writeLine("Detected " + FILE_TYPE + " version: " + version.getDisplayName() + ".");
		
		offset += VERSION_LENGTH;
		
		// verify that the data is long enough to contain the number of files specification
		if(data.length < offset + NUMBER_OF_FILES_LENGTH) {
			m_loading = false;
			throw new GroupReadException(FILE_TYPE + " file \"" + m_file.getName() + "\" is incomplete or corrupted: missing number of files value.");
		}
		
		// read and verify the file version
		numberOfFiles = Serializer.deserializeInteger(Arrays.copyOfRange(data, offset, offset + NUMBER_OF_FILES_LENGTH), FILE_ENDIANNESS);
		
		if(numberOfFiles < 0) {
			m_loading = false;
			throw new GroupReadException(FILE_TYPE + " file \"" + m_file.getName() + "\" has a negative number of files value.");
		}
		
		SystemConsole.instance.writeLine("Number of files detected in group: " + numberOfFiles);
		
		offset += NUMBER_OF_FILES_LENGTH;

		// verify that the data is long enough to contain the title size value
		if(data.length < offset + TITLE_SIZE_LENGTH) {
			m_loading = false;
			throw new GroupReadException(FILE_TYPE + " file \"" + m_file.getName() + "\" is incomplete or corrupted: missing title size value.");
		}
		
		// read and verify the title size
		titleSize = data[offset] & 0xff;
		
		if(titleSize < 0 || titleSize > MAX_TITLE_LENGTH) {
			m_loading = false;
			throw new GroupReadException(FILE_TYPE + " file \"" + m_file.getName() + "\" has invalid title length: " + titleSize + ". Value must be between 0 and " + MAX_TITLE_LENGTH + ".");
		}
		
		offset += TITLE_SIZE_LENGTH;
		
		// verify that the data is long enough to contain the title
		if(data.length < offset + TITLE_LENGTH) {
			m_loading = false;
			throw new GroupReadException(FILE_TYPE + " file \"" + m_file.getName() + "\" is incomplete or corrupted: missing title.");
		}
		
		// read the title
		if(titleSize != 0) {
			title = Serializer.deserializeByteString(Arrays.copyOfRange(data, offset, offset + titleSize));
			
			SystemConsole.instance.writeLine("Parsed title: " + title);
		}
		
		offset += TITLE_LENGTH;
		
		// read run file (version 2 only)
		if(version == SSIVersion.V2) {
			// verify that the data is long enough to contain the title size value
			if(data.length < offset + RUNFILE_SIZE_LENGTH) {
				m_loading = false;
				throw new GroupReadException(FILE_TYPE + " file \"" + m_file.getName() + "\" is incomplete or corrupted: missing run file size value.");
			}
			
			// read and verify the run file size
			runFileSize = data[offset] & 0xff;
			
			if(runFileSize < 0 || runFileSize > MAX_RUNFILE_LENGTH) {
				m_loading = false;
				throw new GroupReadException(FILE_TYPE + " file \"" + m_file.getName() + "\" has invalid run file length: " + runFileSize + ". Value must be between 0 and " + MAX_RUNFILE_LENGTH + ".");
			}
			
			offset += RUNFILE_SIZE_LENGTH;
			
			// verify that the data is long enough to contain the run file
			if(data.length < offset + RUNFILE_LENGTH) {
				m_loading = false;
				throw new GroupReadException(FILE_TYPE + " file \"" + m_file.getName() + "\" is incomplete or corrupted: missing run file.");
			}
			
			// read the run file
			if(runFileSize != 0) {
				runFile = Serializer.deserializeByteString(Arrays.copyOfRange(data, offset, offset + runFileSize));
				
				SystemConsole.instance.writeLine("Parsed run file: " + runFile);
			}
			
			offset += RUNFILE_LENGTH;
		}
		
		// read description (3x)
		for(int i=0;i<NUMBER_OF_DESCRIPTIONS;i++) {
			// verify that the data is long enough to contain the description size value
			if(data.length < offset + DESCRIPTION_SIZE_LENGTH) {
				m_loading = false;
				throw new GroupReadException(FILE_TYPE + " file \"" + m_file.getName() + "\" is incomplete or corrupted: missing description #" + (i + 1) + " size value.");
			}
			
			// read and verify the description size
			descriptionSize[i] = data[offset] & 0xff;
			
			if(descriptionSize[i] < 0 || descriptionSize[i] > MAX_DESCRIPTION_LENGTH) {
				m_loading = false;
				throw new GroupReadException(FILE_TYPE + " file \"" + m_file.getName() + "\" has invalid description #" + (i + 1) + " length: " + descriptionSize[i] + ". Value must be between 0 and " + MAX_DESCRIPTION_LENGTH + ".");
			}
			
			offset += DESCRIPTION_SIZE_LENGTH;
			
			// verify that the data is long enough to contain the description
			if(data.length < offset + DESCRIPTION_LENGTH) {
				m_loading = false;
				throw new GroupReadException(FILE_TYPE + " file \"" + m_file.getName() + "\" is incomplete or corrupted: missing description #" + (i + 1) + ".");
			}
			
			// read the description
			if(descriptionSize[i] != 0) {
				description[i] = Serializer.deserializeByteString(Arrays.copyOfRange(data, offset, offset + descriptionSize[i]));
				
				SystemConsole.instance.writeLine("Parsed description #" + (i + 1) + ": " + description[i]);
			}
			
			offset += DESCRIPTION_LENGTH;
		}
		
		// read the files
		for(int i=0;i<numberOfFiles;i++) {
			// verify that the data is long enough to contain the file name size value
			if(data.length < offset + FILE_NAME_SIZE_LENGTH) {
				m_loading = false;
				throw new GroupReadException(FILE_TYPE + " file \"" + m_file.getName() + "\" is incomplete or corrupted: missing file #" + (i + 1) + " name size value.");
			}
			
			// read and verify the file name size
			fileNameSize = data[offset] & 0xff;
			
			if(fileNameSize < 0 || fileNameSize > MAX_FILE_NAME_LENGTH) {
				m_loading = false;
				throw new GroupReadException(FILE_TYPE + " file \"" + m_file.getName() + "\" has invalid file #" + (i + 1) + " name length: " + fileNameSize + ". Value must be between 0 and " + MAX_FILE_NAME_LENGTH + ".");
			}
			
			offset += FILE_NAME_SIZE_LENGTH;
			
			// verify that the data is long enough to contain the file name
			if(data.length < offset + FILE_NAME_LENGTH) {
				m_loading = false;
				throw new GroupReadException(FILE_TYPE + " file \"" + m_file.getName() + "\" is incomplete or corrupted: missing file #" + (i + 1) + " name.");
			}
			
			// read the file name
			if(fileNameSize != 0) {
				fileName = Serializer.deserializeByteString(Arrays.copyOfRange(data, offset, offset + fileNameSize));
			}
			
			offset += FILE_NAME_LENGTH;
			
			// verify that the data is long enough to contain the file size value
			if(data.length < offset + FILE_SIZE_LENGTH) {
				m_loading = false;
				throw new GroupReadException(FILE_TYPE + " file \"" + m_file.getName() + "\" is incomplete or corrupted: missing file #" + (i + 1) + " size value.");
			}
			
			// read and verify the file version
			fileSize = Serializer.deserializeInteger(Arrays.copyOfRange(data, offset, offset + FILE_SIZE_LENGTH), FILE_ENDIANNESS);
			
			if(fileSize < 0) {
				m_loading = false;
				throw new GroupReadException(FILE_TYPE + " file \"" + m_file.getName() + "\" file #" + (i + 1) + " size can not be negative.");
			}
			
			offset += FILE_SIZE_LENGTH;
			
			g = new GroupFile(m_version == SSIVersion.V2 ? Utilities.reverseFileExtension(fileName) : fileName, fileSize);
			g.setTrailingData(Arrays.copyOfRange(data, offset, offset + UNKNOWN_DATA_LENGTH));

			offset += UNKNOWN_DATA_LENGTH;
			
			groupFiles.add(g);
		}
		
		for(int i=0;i<numberOfFiles;i++) {
			g = groupFiles.elementAt(i);
			
			if(data.length < offset + g.getFileSize()) {
				m_loading = false;
				throw new GroupReadException(FILE_TYPE + " file \"" + m_file.getName() + "\" is incomplete or corrupted: missing file #" + (i + 1) + " data.");
			}
			
			g.setData(Arrays.copyOfRange(data, offset, offset + g.getFileSize()));
			
			offset += g.getFileSize();
		}
		
		m_version = version;
		m_fileType = version.getFileType();
		m_title = title;
		m_runFile = runFile;
		m_description = description;
		addFiles(groupFiles);

		m_loading = false;
		
		setChanged(!checkSameFileOrder(groupFiles));

		SystemConsole.instance.writeLine(FILE_TYPE + " file parsed successfully, " + groupFiles.size() + " files loaded into memory.");
		
		return true;
	}
	
	public boolean save() throws GroupWriteException {
		if(m_file == null) { return false; }
		
		if(!verifyAllFiles()) {
			throw new GroupWriteException("Failed to verify files: in group " + m_file.getName() + ".");
		}
		
		SystemConsole.instance.writeLine("Generating " + m_fileType.getName() + " file data.");
		
		byte data[] = new byte[getGroupFileSize()];

		int offset = 0;

		GroupFile g = null;
		
		System.arraycopy(Serializer.serializeInteger(m_version.getVersionValue(), FILE_ENDIANNESS), 0, data, offset, VERSION_LENGTH);
		
		offset += VERSION_LENGTH;
		
		System.arraycopy(Serializer.serializeInteger(m_files.size(), FILE_ENDIANNESS), 0, data, offset, NUMBER_OF_FILES_LENGTH);
		
		offset += NUMBER_OF_FILES_LENGTH;
		
		data[offset] = (byte) (m_title == null ? 0 : m_title.length());
		
		offset += TITLE_SIZE_LENGTH;
		
		if(Utilities.isNonEmptyString(m_title)) {
			System.arraycopy(Serializer.serializeByteString(m_title), 0, data, offset, m_title.length());
		}
		
		offset += TITLE_LENGTH;
		
		if(m_version == SSIVersion.V2) {
			data[offset] = (byte) (m_runFile == null ? 0 : m_runFile.length());
			
			offset += RUNFILE_SIZE_LENGTH;
			
			if(Utilities.isNonEmptyString(m_runFile)) {
				byte runFileData[] = Serializer.serializeByteString(m_runFile);
				
				System.arraycopy(runFileData, 0, data, offset, runFileData.length);
			}
			
			offset += RUNFILE_LENGTH;
		}
		
		for(int i=0;i<NUMBER_OF_DESCRIPTIONS;i++) {
			data[offset] = (byte) (m_description[i] == null ? 0 : m_description[i].length());
			
			offset += DESCRIPTION_SIZE_LENGTH;
			
			if(Utilities.isNonEmptyString(m_description[i])) {
				System.arraycopy(Serializer.serializeByteString(m_description[i]), 0, data, offset, m_description[i].length());
			}
			
			offset += DESCRIPTION_LENGTH;
		}
		
		for(int i=0;i<m_files.size();i++) {
			g = m_files.elementAt(i);
			
			data[offset] = (byte) (g.getFileName() == null ? 0 : g.getFileName().length());
			
			offset += FILE_NAME_SIZE_LENGTH;
			
			if(Utilities.isNonEmptyString(g.getFileName())) { 
				System.arraycopy(Serializer.serializeByteString(m_version == SSIVersion.V2 ? Utilities.reverseFileExtension(g.getFileName()) : g.getFileName()), 0, data, offset, g.getFileName().length());
			}
			
			offset += FILE_NAME_LENGTH;
			
			System.arraycopy(Serializer.serializeInteger(g.getFileSize(), FILE_ENDIANNESS), 0, data, offset, FILE_SIZE_LENGTH);
			
			offset += FILE_SIZE_LENGTH;
			
			if(g.getTrailingData() != null && g.getTrailingData().length > 0) {
				System.arraycopy(g.getTrailingData(), 0, data, offset, UNKNOWN_DATA_LENGTH);
			}
			
			offset += UNKNOWN_DATA_LENGTH;
		}
		
		for(int i=0;i<m_files.size();i++) {
			g = m_files.elementAt(i);
			
			if(g.getData() != null && g.getDataSize() > 0) {
				System.arraycopy(g.getData(), 0, data, offset, g.getDataSize());
				
				offset += g.getDataSize();
			}
		}
		
		SystemConsole.instance.writeLine("Writing " + m_fileType.getName() + " data to file: \"" + m_file.getName() + "\".");
		
		OutputStream out = null;
		try {
			out = new BufferedOutputStream(new FileOutputStream(m_file));
			out.write(data);
			out.close();
		}
		catch(IOException e) {
			throw new GroupWriteException("Error writing " + m_fileType.getName() + " to file: " + m_file.getName() +  ": " + e.getMessage());
		}
		
		SystemConsole.instance.writeLine(m_fileType.getName()  + " file writing complete!");
		
		setChanged(false);
		
		return true;
	}
	
	public boolean equals(Object o) {
		if(o == null || !(o instanceof GroupSSI)) {
			return false;
		}
		
		return super.equals(o);
	}
	
	public String toString() {
		String groupString = getFileType().getName() + (m_file == null ? "" : " \"" + m_file.getName() + "\"");
		
		groupString += " Title: " + m_title;
		groupString += " Description: ";
		for(int i=0;i<m_description.length;i++) {
			groupString += m_description[i];
		}
		if(m_version == SSIVersion.V2) {
			groupString += " Run File: " + m_runFile;
		}
		
		groupString += " Files (" + m_files.size() + "): ";
		
		for(int i=0;i<m_files.size();i++) {
			groupString += m_files.elementAt(i).getFileName();
			if(i < m_files.size() - 1) {
				groupString += ", ";
			}
		}
		
		return groupString;
	}
	
}
