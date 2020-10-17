package palette;

public enum DATType {
	Palette,
	Lookup,
	Unknown;
	
	final public static String[] displayNames = {
		"Palette",
		"Lookup",
		"Unknown"
	};
	
	public String getDisplayName() {
		return displayNames[ordinal()];
	}
	
	public static String getDisplayName(DATType rewardtype) {
		return displayNames[rewardtype.ordinal()];
	}
	
	public static String getValueList() {
		String valueList = new String();
		for(int i=0;i<Unknown.ordinal();i++) {
			valueList += values()[i];
			if(i < Unknown.ordinal() - 1) {
				valueList += ", ";
			}
		}
		return valueList;
	}
	
	public static String getDisplayNameList() {
		String displayNameList = new String();
		for(int i=0;i<Unknown.ordinal();i++) {
			displayNameList += displayNames[i];
			if(i < Unknown.ordinal() - 1) {
				displayNameList += ", ";
			}
		}
		return displayNameList;
	}
	
	public static DATType parseFrom(String data) {
		if(data == null) { return Unknown; }
		String temp = data.trim();
		if(temp.length() == 0) { return Unknown; }
		
		for(int i=0;i<values().length;i++) {
			if(temp.equalsIgnoreCase(values()[i].name()) || temp.equalsIgnoreCase(displayNames[i])) {
				return values()[i];
			}
		}
		return Unknown;
	}
}
