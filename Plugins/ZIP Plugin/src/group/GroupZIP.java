package group;

import java.util.*;
import java.util.zip.*;
import java.io.*;
import exception.*;
import utilities.*;
import console.*;

public class GroupZIP extends Group {
	
	public static GroupFileType[] FILE_TYPES = {
		new GroupFileType("Zip Group", "ZIP")
	};
	
	public GroupZIP() throws InvalidGroupFileTypeException, UnsupportedGroupFileTypeException {
		super(FILE_TYPES[0]);
	}
	
	public GroupZIP(String fileName) throws InvalidGroupFileTypeException, UnsupportedGroupFileTypeException {
		super(fileName, FILE_TYPES[0]);
	}
	
	public GroupZIP(File file) throws InvalidGroupFileTypeException, UnsupportedGroupFileTypeException {
		super(file, FILE_TYPES[0]);
	}
	
	public Endianness getFileEndianness() {
		return Endianness.defaultEndianness;
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
		int fileSize = 0;
		for(int i=0;i<m_files.size();i++) {
			fileSize += m_files.elementAt(i).getDataSize();
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
		
		byte fileData[] = null;
		
		Vector<GroupFile> groupFiles = new Vector<GroupFile>();
		
		ZipFile zipFile = null;
		try {
			zipFile = new ZipFile(m_file);
		}
		catch(ZipException e) {
			throw new GroupReadException("Error decompressing " + FILE_TYPES[0].getName() + " file \"" + m_file.getName() + "\": " + e.getMessage());
		}
		catch(IOException e) {
			throw new GroupReadException("Error reading " + FILE_TYPES[0].getName() + " file \"" + m_file.getName() + "\": " + e.getMessage());
		}

	    Enumeration<? extends ZipEntry> zipFileEntries = zipFile.entries();
	    
		SystemConsole.instance.writeLine("Opened " + FILE_TYPES[0].getName() + " file: \"" + m_file.getName() + "\", detected " + zipFile.size() + " files.");
		
		ZipEntry zipFileEntry = null;
		InputStream zipFileEntryStream = null;
		String zipEntryFileName;
	    int fileNumber = 1;
	    try {
		    while(zipFileEntries.hasMoreElements()) {
		        zipFileEntry = zipFileEntries.nextElement();
		        zipFileEntryStream = zipFile.getInputStream(zipFileEntry);
		        
		        if(zipFileEntry.getSize() < 0) {
		        	zipFile.close();
		        	
		        	throw new GroupReadException("Error decompressing file #" + fileNumber + " in " + FILE_TYPES[0].getName() + " file \"" + m_file.getName() + "\": Unknown decompressed data size.");
		        }
		        
		        fileData = new byte[(int) zipFileEntry.getSize()];
		        zipFileEntryStream.read(fileData);
		        
		        zipEntryFileName = Utilities.getFileNameNoPath(zipFileEntry.getName());
		        
		        if(zipEntryFileName.length() > GroupFile.MAX_FILE_NAME_LENGTH) {
		        	SystemConsole.instance.writeLine("Warning: Truncating file #" + fileNumber + " name (" + zipEntryFileName + "), exceeds max length of " + GroupFile.MAX_FILE_NAME_LENGTH + ".");
		        }
		        
		        groupFiles.add(new GroupFile(zipEntryFileName, fileData));
		        
		        fileNumber++;
		    }
	    }
	    catch(ZipException e) {
	    	throw new GroupReadException("Error decompressing file #" + fileNumber + " in " + FILE_TYPES[0].getName() + " file \"" + m_file.getName() + "\": " + e.getMessage());
	    }
	    catch(IOException e) {
	    	throw new GroupReadException("Error reading file #" + fileNumber + " in " + FILE_TYPES[0].getName() + " file \"" + m_file.getName() + "\": " + e.getMessage());
	    }
	    
	    try { zipFile.close(); }
	    catch(IOException e) { }
		
	    addFiles(groupFiles);
	    
	    if(!(shouldSortFiles() && shouldAutoSortFiles())) {
			setChanged(false);
		}
		
		m_loaded = true;
		
		SystemConsole.instance.writeLine(FILE_TYPES[0].getName() + " file \"" + m_file.getName() + "\" decompressed successfully, " + groupFiles.size() + " files loaded into memory.");
		
		return true;
	}
	
	public boolean save() throws GroupWriteException {
		if(m_file == null) { return false; }
		
		if(!verifyAllFiles()) {
			throw new GroupWriteException("Failed to verify files.");
		}
		
		SystemConsole.instance.writeLine("Generating " + FILE_TYPES[0].getName() + " file data.");
		
		GroupFile g = null;
		
		ZipOutputStream zipFile = null;
		
		try {
			zipFile = new ZipOutputStream(new FileOutputStream(m_file));
		}
		catch(FileNotFoundException e) {
			throw new GroupWriteException("Failed to write " + FILE_TYPES[0].getName() + " file to file \"" + m_file.getName() + "\": " + e.getMessage());
		}
		
		int fileNumber = 1;
		try {
			for(int i=0;i<m_files.size();i++) {
				g = m_files.elementAt(i);
				
				zipFile.putNextEntry(new ZipEntry(g.getFileName()));
				
				if(g.getData() != null) {
					zipFile.write(g.getData(), 0, g.getDataSize());
				}
				
				fileNumber++;
			}
		}
		catch(ZipException e) {
			throw new GroupWriteException("Error compressing file #" + fileNumber + " while writing " + FILE_TYPES[0].getName() + " to file \"" + m_file.getName() + "\": " + e.getMessage());
		}
		catch(IOException e) {
			throw new GroupWriteException("Error writing file #" + fileNumber + " from " + FILE_TYPES[0].getName() + " to file \"" + m_file.getName() + "\": " + e.getMessage());
		}
		
		try { zipFile.close(); }
		catch(ZipException e) { }
		catch(IOException e) { }
		
		SystemConsole.instance.writeLine(FILE_TYPES[0].getName() + " file writing complete!");
		
		setChanged(false);
		
		return true;
	}
	
	public boolean equals(Object o) {
		if(o == null || !(o instanceof GroupZIP)) {
			return false;
		}
		
		return super.equals(o);
	}

	public String toString() {
		return super.toString();
	}
	
}
