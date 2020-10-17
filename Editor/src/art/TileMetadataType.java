package art;

public enum TileMetadataType {
	None,
	External,
	Embedded,
	Invalid;
	
	public final static TileMetadataType defaultMetadataType = External;
	
	public final static String[] displayNames = {
		"None",
		"External",
		"Embedded",
		"Invalid"
	};
	
	public static int numberOfMetadataTypes() {
		return values().length - 1;
	}
	
	public String getDisplayName() {
		return displayNames[ordinal()];
	}
	
	public static String getDisplayName(TileMetadataType metadataType) {
		return displayNames[metadataType.ordinal()];
	}
	
	public static String getValueList() {
		String valueList = new String();
		for(int i=0;i<numberOfMetadataTypes();i++) {
			valueList += values()[i];
			if(i < numberOfMetadataTypes() - 1) {
				valueList += ", ";
			}
		}
		return valueList;
	}
	
	public static String getDisplayNameList() {
		String displayNameList = new String();
		for(int i=0;i<numberOfMetadataTypes();i++) {
			displayNameList += displayNames[i];
			if(i < numberOfMetadataTypes() - 1) {
				displayNameList += ", ";
			}
		}
		return displayNameList;
	}
	
	public static String[] getValidDisplayNames() {
		String[] names = new String[numberOfMetadataTypes()];
		for(int i=0;i<numberOfMetadataTypes();i++) {
			names[i] = displayNames[i];
		}
		return names;
	}
	
	public boolean isValid() {
		return ordinal() >= 0 && ordinal() < numberOfMetadataTypes();
	}
	
	public int getVersionValue() {
		return ordinal() < numberOfMetadataTypes() ? ordinal() + 1 : 0;
	}
	
	public static TileMetadataType parseFrom(int version) {
		if(version - 1 < 0 || version - 1 >= numberOfMetadataTypes()) { return Invalid; }
		
		return values()[version - 1];
	}
	
	public static TileMetadataType parseFrom(String data) {
		if(data == null) { return Invalid; }
		String temp = data.trim();
		if(temp.isEmpty()) { return Invalid; }
		
		for(int i=0;i<numberOfMetadataTypes();i++) {
			if(temp.equalsIgnoreCase(values()[i].name()) || temp.equalsIgnoreCase(displayNames[i])) {
				return values()[i];
			}
		}
		return Invalid;
	}
}
