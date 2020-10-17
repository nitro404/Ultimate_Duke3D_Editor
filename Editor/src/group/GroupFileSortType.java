package group;

public enum GroupFileSortType {
	Unsorted,
	FileName,
	FileExtension,
	FileSize,
	Invalid;
	
	public final static GroupFileSortType defaultSortType = Unsorted;
	
	public final static String[] displayNames = {
		"Unsorted",
		"File Name",
		"File Extension",
		"File Size",
		"Invalid"
	};
	
	public static int numberOfSortTypes() {
		return values().length - 1;
	}
	
	public String getDisplayName() {
		return displayNames[ordinal()];
	}
	
	public static String getDisplayName(GroupFileSortType action) {
		return displayNames[action.ordinal()];
	}
	
	public static String getValueList() {
		String valueList = new String();
		for(int i=0;i<numberOfSortTypes();i++) {
			valueList += values()[i];
			if(i < numberOfSortTypes() - 1) {
				valueList += ", ";
			}
		}
		return valueList;
	}
	
	public static String getDisplayNameList() {
		String displayNameList = new String();
		for(int i=0;i<numberOfSortTypes();i++) {
			displayNameList += displayNames[i];
			if(i < numberOfSortTypes() - 1) {
				displayNameList += ", ";
			}
		}
		return displayNameList;
	}
	
	public boolean isValid() {
		return ordinal() >= 0 && ordinal() < numberOfSortTypes();
	}
	
	public static GroupFileSortType parseFrom(String data) {
		if(data == null) { return Invalid; }
		String temp = data.trim();
		if(temp.length() == 0) { return Invalid; }
		
		for(int i=0;i<numberOfSortTypes();i++) {
			if(temp.equalsIgnoreCase(values()[i].name()) || temp.equalsIgnoreCase(displayNames[i])) {
				return values()[i];
			}
		}
		return Invalid;
	}
}
