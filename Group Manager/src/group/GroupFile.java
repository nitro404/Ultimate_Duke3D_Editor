package group;

import java.util.*;
import java.io.*;
import utilities.*;

public class GroupFile {

	protected String m_fileName;
	protected int m_fileSize;
	protected byte m_leadingData[];
	protected byte m_data[];
	protected byte m_trailingData[];
	
	public static final int MAX_FILE_NAME_LENGTH = 12;
	
	public GroupFile() {
		this(null, 0);
	}
	
	public GroupFile(String fileName, int fileSize) {
		setFileName(fileName);
		m_fileSize = fileSize < 0 ? 0 : fileSize;
		m_data = null;
		m_leadingData = null;
		m_trailingData = null;
	}
	
	public GroupFile(String fileName, byte data[]) {
		setFileName(fileName);
		setData(data);
		m_leadingData = null;
		m_trailingData = null;
	}
	
	public GroupFile(File file, byte data[]) {
		setFileName(file == null ? null : file.getName());
		setData(data);
		m_leadingData = null;
		m_trailingData = null;
	}
	
	public String getFileName() {
		return m_fileName;
	}
	
	public int getFileSize() {
		return m_fileSize;
	}
	
	public int getDataSize() {
		return m_data == null ? 0 : m_data.length;
	}
	
	public byte[] getData() {
		return m_data;
	}
	
	public byte[] getLeadingData() {
		return m_leadingData;
	}
	
	public byte[] getTrailingData() {
		return m_trailingData;
	}
	
	public void setFileName(String fileName) {
		m_fileName = Utilities.truncateFileName(fileName, MAX_FILE_NAME_LENGTH);
	}
	
	public void setData(byte data[]) {
		m_data = data;
		m_fileSize = data == null ? 0 : data.length;
	}
	
	public void setLeadingData(byte data[]) {
		m_leadingData = data;
	}
	
	public void setTrailingData(byte data[]) {
		m_trailingData = data;
	}
	
	public void clearAllData() {
		m_leadingData = null;
		m_data = null;
		m_trailingData = null;
	}
	
	public boolean isValid() {
		return m_fileName != null &&
			   m_fileName.length() > 0 &&
			   m_fileName.length() <= MAX_FILE_NAME_LENGTH &&
			   m_data != null;
	}
	
	public static GroupFile readFrom(File file) throws FileNotFoundException, IOException {
		if(file == null || !file.isFile() || !file.exists()) { return null; }
		
		// read the file into memory
		byte data[] = new byte[(int) file.length()];
		InputStream in = new FileInputStream(file);
		in.read(data);
		in.close();
		
		return new GroupFile(file, data);
	}
	
	public boolean writeTo(File directory) throws IOException {
		return writeTo(directory, null);
	}
	
	public boolean writeTo(File directory, File alternateFileName) throws IOException {
		if(!isValid()) { return false; }
		
		if(directory != null) {
			if(!directory.exists()) {
				if(!directory.mkdirs()) { return false; }
			}
		}
		
		File file = new File((directory == null ? "" : Utilities.appendSlash(directory.getPath())) + alternateFileName == null ? m_fileName : alternateFileName.getName());

		OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
		out.write(m_data);
		out.close();
		
		return true;
	}
	
	public boolean equals(Object o) {
		if(o == null || !(o instanceof GroupFile)) {
			return false;
		}
		
		GroupFile g = (GroupFile) o;
		
		if(m_fileName == null || g.m_fileName == null || m_data == null || g.m_data == null) {
			return false;
		}
		
		return m_fileName.equalsIgnoreCase(g.m_fileName) && Arrays.equals(m_data, g.m_data);
	}
	
	public String toString() {
		return m_fileName + (m_data == null ? "" : " (" + m_data.length + " bytes)");
	}
	
}
