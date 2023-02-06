package map;

public enum MapType {
	Unknown,
	RegularVersion,
	AtomicEdition,
	JFDuke3D,
	eDuke32;
	
	public final static MapType defaultType = Unknown;
	
	public final static String[] displayNames = {
		"Unknown",
		"Regular Version",
		"Atomic Edition",
		"JFDuke3D",
		"eDuke32"
	};
	
	public static int numberOfTypes() {
		return values().length - 1;
	}
	
	public String getDisplayName() {
		return displayNames[ordinal()];
	}
	
	public static String getDisplayName(MapType type) {
		return displayNames[type.ordinal()];
	}
	
	public static String getValueList() {
		String valueList = new String();
		for(int i=0;i<numberOfTypes();i++) {
			valueList += values()[i];
			if(i < numberOfTypes() - 1) {
				valueList += ", ";
			}
		}
		return valueList;
	}
	
	public static String getDisplayNameList() {
		String displayNameList = new String();
		for(int i=0;i<numberOfTypes();i++) {
			displayNameList += displayNames[i];
			if(i < numberOfTypes() - 1) {
				displayNameList += ", ";
			}
		}
		return displayNameList;
	}
	
	public static String[] getValidDisplayNames() {
		String[] names = new String[numberOfTypes()];
		for(int i=0;i<numberOfTypes();i++) {
			names[i] = displayNames[i];
		}
		return names;
	}
	
	public boolean isValid() {
		return ordinal() >= 0 && ordinal() < numberOfTypes();
	}
	
	public int getVersionValue() {
		return ordinal() < numberOfTypes() ? ordinal() + 1 : 0;
	}
	
	public static MapType parseFrom(int version) {
		if(version - 1 < 0 || version - 1 >= numberOfTypes()) { return Unknown; }
		
		return values()[version - 1];
	}
	
	public static MapType parseFrom(String data) {
		if(data == null) { return Unknown; }
		String temp = data.trim();
		if(temp.isEmpty()) { return Unknown; }
		
		for(int i=0;i<numberOfTypes();i++) {
			if(temp.equalsIgnoreCase(values()[i].name()) || temp.equalsIgnoreCase(displayNames[i])) {
				return values()[i];
			}
		}
		return Unknown;
	}
}
