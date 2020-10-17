package utilities;

import java.awt.*;
import java.io.*;
import java.nio.file.*;

public class Utilities {
	
	private Utilities() {
		
	}
	
	public static int randomInteger(int min, int max) {
		if(max <= min) { return min; }
		
		return (int) (Math.random() * (max - min + 1)) + min;
	}

	public static float randomFloat(float min, float max) {
		if(max <= min) { return min; }
		
		return (float) (Math.random() * (max - min)) + min;
	}
	
	public static int byteLength(byte n) {
		return n < 0 ? n < -99 ? 4 : n < -9 ? 3 : 2 : n < 10 ? 1 : n < 100 ? 2 : 3;
	}

	public static int shortLength(short n) {
		return n < 0 ? n < -99 ? n < -9999 ? 6 : n < -999 ? 5 : 4 : n < -9 ? 3 : 2 : n < 100 ? n < 10 ? 1 : 2 : n < 1000 ? 3 : n < 10000 ? 4 : 5;
	}

	public static int intLength(int n) {
		return n < 0 ? n < -999999 ? n < -99999999 ? n < -999999999 ? 11 : 10 : n < -9999999 ? 9 : 8 : n < -999 ? n < -99999 ? 7 : n < -9999 ? 6 : 5 : n < -99 ? 4 : n < -9 ? 3 : 2 : n < 100000 ? n < 100 ? n < 10 ? 1 : 2 : n < 1000 ? 3 : n < 10000 ? 4 : 5 : n < 10000000 ? n < 1000000 ? 6 : 7 : n < 100000000 ? 8 : n < 1000000000 ? 9 : 10;
	}

	public static int longLength(long n) {
		return n < 0 ? n < -999999999L ? n < -99999999999999L ? n < -9999999999999999L ? n < -999999999999999999L ? 20 : n < -99999999999999999L ? 19 : 18 : n < -999999999999999L ? 17 : 16 : n < -99999999999L ? n < -9999999999999L ? 15 : n < -999999999999L ? 14 : 13 : n < -9999999999L ? 12 : 11 : n < -9999L ? n < -999999L ? n < -99999999L ? 10 : n < -9999999L ? 9 : 8 : n < -99999L ? 7 : 6 : n < -99L ? n < -999L ? 5 : 4 : n < -9L ? 3 : 2 : n < 1000000000L ? n < 10000L ? n < 100L ? n < 10L ? 1 : 2 : n < 1000L ? 3 : 4 : n < 10000000L ? n < 100000L ? 5 : n < 1000000L ? 6 : 7 : n < 100000000L ? 8 : 9 : n < 100000000000000L ? n < 1000000000000L ? n < 100000000000L ? n < 10000000000L ? 10 : 11 : 12 : n < 10000000000000L ? 13 : 14 : n < 100000000000000000L ? n < 10000000000000000L ? n < 1000000000000000L ? 15 : 16 : 17 : n < 1000000000000000000L ? 18 : 19;
	}
	
	public static Point parsePoint(String data) {
		if(data == null) { return null; }
		
		String[] pointData = data.replaceAll("^[^-0-9]+", "").replaceAll("[^-0-9]+$", "").split("[^-0-9]+");
		if(pointData.length != 2) { return null; }
		
		int x, y;
		try { x = Integer.parseInt(pointData[0]); } catch(NumberFormatException e) { return null; }
		try { y = Integer.parseInt(pointData[1]); } catch(NumberFormatException e) { return null; }
		
		return new Point(x, y);
	}
	
	public static Dimension parseDimension(String data) {
		if(data == null) { return null; }
		
		String[] dimensionData = data.replaceAll("^[^-0-9]+", "").replaceAll("[^-0-9]+$", "").split("[^-0-9]+");
		if(dimensionData.length != 2) { return null; }
		
		int width, height;
		try { width = Integer.parseInt(dimensionData[0]); } catch(NumberFormatException e) { return null; }
		try { height = Integer.parseInt(dimensionData[1]); } catch(NumberFormatException e) { return null; }
		
		return new Dimension(width, height);
	}
	
	public static Color parseColour(String data) {
		if(data == null) { return null; }
		
		String[] colourData = data.replaceAll("^[^-0-9.]+", "").replaceAll("[^-0-9.]+$", "").split("[^-0-9.]+");
		if(colourData.length != 3) { return null; }
		
		boolean normalized = false;
		for(int i=0;i<colourData.length;i++) {
			if(colourData[i].contains(".")) {
				normalized = true;
			}
		}
		
		if(normalized) {
			float[] colours = new float[3];
			for(int i=0;i<colourData.length;i++) {
				try { colours[i] = Float.parseFloat(colourData[i]); } catch(NumberFormatException e) { return null; }
				if(colours[i] < 0.0f || colours[i] > 1.0f) { return null; }
			}
			
			return new Color(colours[0], colours[1], colours[2]);
		}
		else {
			int[] colours = new int[3];
			for(int i=0;i<colourData.length;i++) {
				try { colours[i] = Integer.parseInt(colourData[i]); } catch(NumberFormatException e) { return null; }
				if(colours[i] < 0 || colours[i] > 255) { return null; }
			}
			
			return new Color(colours[0], colours[1], colours[2]);
		}
	}
	
	public static int compareCasePercentage(String text) {
		if(text == null) { return 0; }
		
		int upper = 0;
		int lower = 0;
		for(int i=0;i<text.length();i++) {
			if(text.charAt(i) >= 'a' && text.charAt(i) <= 'z') { lower++; }
			if(text.charAt(i) >= 'A' && text.charAt(i) <= 'Z') { upper++; }
		}
		
		return upper - lower;
	}
	
	public static String reverseString(String data) {
		if(data == null) { return null; }
		
		String reverse = "";
		
		for(int i=0;i<data.length();i++) {
			reverse += data.charAt(data.length() - i - 1);
		}
		
		return reverse;
	}
	
	public static String getFileNameNoExtension(String fileName) {
		if(fileName == null) { return null; }
		
		int index = fileName.lastIndexOf('.');
		
		if(index >= 0) {
			return fileName.substring(0, index);
		}
		
		return fileName;
	}
	
	public static String getFileExtension(String fileName) {
		if(fileName == null) { return null; }
		
		int index = fileName.lastIndexOf('.');
		
		if(index >= 0) {
			return fileName.substring(index + 1, fileName.length());
		}
		
		return null;
	}
	
	public static boolean fileHasExtension(String fileName, String fileExtension) {
		if(fileName == null || fileExtension == null) { return false; }
		
		String actualFileExtension = getFileExtension(fileName);
		return actualFileExtension != null && actualFileExtension.equalsIgnoreCase(fileExtension);
	}

	public static String reverseFileExtension(String fileName) {
		if(fileName == null) { return null; }
		
		int index = fileName.lastIndexOf('.');
		
		if(index >= 0) {
			return fileName.substring(0, index) + "." + reverseString(fileName.substring(index + 1, fileName.length()));
		}
		
		return fileName;
	}
	
	public static String getFileNameNoPath(String filePath) {
		if(filePath == null) { return null; }
		
		String formattedFilePath = filePath.trim();
		if(formattedFilePath.length() == 0) { return null; }
		
		int lastFileSeparatorIndex = -1;
		for(int i=formattedFilePath.length()-1;i>=0;i--) {
			if(formattedFilePath.charAt(i) == '/' || formattedFilePath.charAt(i) == '\\') {
				lastFileSeparatorIndex = i;
				break;
			}
		}
		
		if(lastFileSeparatorIndex != -1) {
			return formattedFilePath.substring(lastFileSeparatorIndex + 1, formattedFilePath.length());
		}
		
		return filePath;
	}
	
	public static String getFilePath(File file) {
		if(file == null) { return null; }
		
		String path;
		try {
			path = file.getCanonicalPath();
		}
		catch(IOException e) {
			path = file.getAbsolutePath();
		}
		
		if(file.isFile()) {
			int index = -1;
			for(int i=path.length() - 1;i>=0;i--) {
				if(path.charAt(i) == '/' || path.charAt(i) == '\\') {
					index = i;
					break;
				}
			}
			
			if(index >= 0) {
				path = path.substring(0, index + 1);
			}
		}
		
		if(path.charAt(path.length() - 1) != '/' && path.charAt(path.length() - 1) != '\\') {
			path += System.getProperty("file.separator");
		}
		
		return path;
	}
	
	public static String getRelativizedPath(String currentPath, String basePath) {
		if(currentPath == null || basePath == null) { return null; }
		
		return Paths.get(new File(basePath).getAbsolutePath()).relativize(Paths.get(new File(currentPath).getAbsolutePath())).toString();
	}
	
	public static String appendSlash(String path) {
		if(path == null) { return null; }
		String data = path.trim();
		if(data.length() == 0) { return data; }
		
		if(data.charAt(data.length() - 1) != '/' && data.charAt(data.length() - 1) != '\\') {
			data  += System.getProperty("file.separator");
		}
		
		return data;
	}
	
	public static String truncateFileName(String fileName, int maxLength) {
		if(fileName == null) { return null; }
		if(maxLength < 0) { return null; }
		if(maxLength == 0) { return ""; }
		
		String formattedFileName = fileName.trim();
		
		if(formattedFileName.length() > maxLength) {
			int index = formattedFileName.lastIndexOf('.');
			
			String extension = "";
			String originalFileName = fileName;
			
			if(index >= 0) {
				extension = fileName.substring(index + 1, fileName.length());
				originalFileName = fileName.substring(0, index);
			}
			
			if(maxLength - (extension.length() + (extension.length() > 0 ? 1 : 0)) < 1) {
				return originalFileName.substring(0, originalFileName.length() >= maxLength ? maxLength : originalFileName.length()); 
			}
			
			return originalFileName.substring(0, maxLength - extension.length() - (extension.length() > 0 ? 1 : 0)) + (extension.length() > 0 ? "." + extension : "");
		}
		
		return formattedFileName;
	}
	
	public static int compareVersions(String v1, String v2) {
		if(v1 == null || v2 == null) {
			throw new IllegalArgumentException("Cannot compare to a null version.");
		}
		
		String version1 = v1.trim();
		String version2 = v2.trim();
		if(version1.length() == 0 || version2.length() == 0) {
			throw new IllegalArgumentException("Cannot compare empty versions.");
		}
		
		String matchRegex = "([0-9]\\.?)+";
		if(!version1.matches(matchRegex) || !version2.matches(matchRegex)) {
			throw new IllegalArgumentException("Cannot compare improperly formatted versions.");
		}
		
		String splitRegex = "[\\. \\t]+";
		String v1parts[] = version1.split(splitRegex);
		String v2parts[] = version2.split(splitRegex);
		
		int a, b;
		int index = 0;
		while(true) {
			if(index >= v1parts.length) {
				if(v1parts.length == v2parts.length) { return 0; }
				
				for(int i=index;i<v2parts.length;i++) {
					b = Integer.parseInt(v2parts[i]);
					
					if(b != 0) {
						return -1;
					}
				}
				return 0;
			}
			
			if(index >= v2parts.length) {
				if(v2parts.length == v1parts.length) { return 0; }
				
				for(int i=index;i<v1parts.length;i++) {
					a = Integer.parseInt(v1parts[i]);
					
					if(a != 0) {
						return 1;
					}
				}
				return 0;
			}
			
			a = Integer.parseInt(v1parts[index]);
			b = Integer.parseInt(v2parts[index]);
			
			     if(a > b) { return  1; }
			else if(a < b) { return -1; }
			
			index++;
		}
	}
	
	public static boolean isComment(String data) {
		return isComment(data, "//");
	}
	
	public static boolean isComment(String data, String comment) {
		if(data == null || comment == null || comment.length() == 0) { return false; }
		
		int commentStartIndex = -1;
		for(int i=0;i<data.length();i++) {
			if(data.charAt(i) == ' ' || data.charAt(i) == '\t') { continue; }
			
			if(data.charAt(i) == comment.charAt(0)) {
				commentStartIndex = i;
				break;
			}
			else {
				return false;
			}
		}
		
		if(commentStartIndex < 0 || data.length() - commentStartIndex < comment.length()) { return false; }
		
		for(int i=commentStartIndex;i<data.length();i++) {
			if(i - commentStartIndex >= comment.length()) { break; }
			
			if(data.charAt(i) != comment.charAt(i - commentStartIndex)) {
				return false;
			}
		}
		
		return true;
	}
	
	public static byte[] getResourceBytes(String resourcePath) {
		if(resourcePath == null) { return null; }
		InputStream in = null;
		byte data[] = null;
		try {
			in = System.class.getResourceAsStream(resourcePath);
			if(in == null) { return null; }
			data = new byte[in.available()];
			in.read(data);
			in.close();
		}
		catch(FileNotFoundException e) { return null; }
		catch(IOException e) { return null; }
		return data;
	}
	
}
