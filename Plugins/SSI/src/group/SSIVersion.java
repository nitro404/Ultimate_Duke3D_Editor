package group;

import item.*;

public enum SSIVersion {
	V1,
	V2,
	Invalid;
	
	public final static SSIVersion defaultVersion = V2;
	
	public final static String[] displayNames = {
		"1",
		"2",
		"Invalid"
	};
	
	public static int numberOfSSIVersions() {
		return values().length - 1;
	}
	
	public String getDisplayName() {
		return displayNames[ordinal()];
	}
	
	public static String getDisplayName(SSIVersion version) {
		return displayNames[version.ordinal()];
	}
	
	public static String getValueList() {
		String valueList = new String();
		for(int i=0;i<numberOfSSIVersions();i++) {
			valueList += values()[i];
			if(i < numberOfSSIVersions() - 1) {
				valueList += ", ";
			}
		}
		return valueList;
	}
	
	public static String getDisplayNameList() {
		String displayNameList = new String();
		for(int i=0;i<numberOfSSIVersions();i++) {
			displayNameList += displayNames[i];
			if(i < numberOfSSIVersions() - 1) {
				displayNameList += ", ";
			}
		}
		return displayNameList;
	}
	
	public static String[] getValidDisplayNames() {
		String[] names = new String[numberOfSSIVersions()];
		for(int i=0;i<numberOfSSIVersions();i++) {
			names[i] = displayNames[i];
		}
		return names;
	}
	
	public boolean isValid() {
		return ordinal() >= 0 && ordinal() < numberOfSSIVersions();
	}
	
	public int getVersionValue() {
		return ordinal() < numberOfSSIVersions() ? ordinal() + 1 : 0;
	}
	
	public ItemFileType getFileType() {
		switch(this) {
			case V1:
				return GroupSSI.FILE_TYPES[0];
			
			case V2:
				return GroupSSI.FILE_TYPES[1];
			
			default:
				return null;
		}
	}
	
	public static ItemFileType getfileType(SSIVersion version) {
		return version.getFileType();
	}
	
	public static SSIVersion parseFrom(int version) {
		if(version - 1 < 0 || version - 1 >= numberOfSSIVersions()) { return Invalid; }
		
		return values()[version - 1];
	}
	
	public static SSIVersion parseFrom(String data) {
		if(data == null) { return Invalid; }
		String temp = data.trim();
		if(temp.length() == 0) { return Invalid; }
		
		for(int i=0;i<numberOfSSIVersions();i++) {
			if(temp.equalsIgnoreCase(values()[i].name()) || temp.equalsIgnoreCase(displayNames[i])) {
				return values()[i];
			}
		}
		return Invalid;
	}
}
