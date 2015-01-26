package group;

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
	
	public String getDisplayName() {
		return displayNames[ordinal()];
	}
	
	public static String getDisplayName(SSIVersion version) {
		return displayNames[version.ordinal()];
	}
	
	public static String getValueList() {
		String valueList = new String();
		for(int i=0;i<values().length-1;i++) {
			valueList += values()[i];
			if(i < values().length - 2) {
				valueList += ", ";
			}
		}
		return valueList;
	}
	
	public static String getDisplayNameList() {
		String displayNameList = new String();
		for(int i=0;i<values().length-1.;i++) {
			displayNameList += displayNames[i];
			if(i < values().length - 2) {
				displayNameList += ", ";
			}
		}
		return displayNameList;
	}
	
	public static String[] getValidDisplayNames() {
		String[] names = new String[values().length - 1];
		for(int i=0;i<values().length-1;i++) {
			names[i] = displayNames[i];
		}
		return names;
	}
	
	public boolean isValid() {
		return ordinal() >= 0 && ordinal() < values().length - 1;
	}
	
	public int getVersionValue() {
		return ordinal() < values().length - 1 ? ordinal() + 1 : 0;
	}
	
	public GroupFileType getFileType() {
		switch(this) {
			case V1:
				return GroupSSI.FILE_TYPES[0];
			
			case V2:
				return GroupSSI.FILE_TYPES[1];
			
			default:
				return null;
		}
	}
	
	public static GroupFileType getfileType(SSIVersion version) {
		return version.getFileType();
	}
	
	public static SSIVersion parseFrom(int version) {
		if(version - 1 < 0 || version - 1 >= values().length - 1) { return Invalid; }
		
		return values()[version - 1];
	}
	
	public static SSIVersion parseFrom(String data) {
		if(data == null) { return Invalid; }
		String temp = data.trim();
		if(temp.length() == 0) { return Invalid; }
		
		for(int i=0;i<values().length-1;i++) {
			if(temp.equalsIgnoreCase(values()[i].name()) || temp.equalsIgnoreCase(displayNames[i])) {
				return values()[i];
			}
		}
		return Invalid;
	}
}
