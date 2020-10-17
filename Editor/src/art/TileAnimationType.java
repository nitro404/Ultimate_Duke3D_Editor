package art;

public enum TileAnimationType {
	None,
	Oscillating,
	Forward,
	Backward,
	Invalid;
	
	public final static TileAnimationType defaultAnimationType = None;
	
	public final static String[] displayNames = {
		"None",
		"Oscillating",
		"Forward",
		"Backward",
		"Invalid"
	};
	
	public static int numberOfAnimationTypes() {
		return values().length - 1;
	}
	
	public String getDisplayName() {
		return displayNames[ordinal()];
	}
	
	public static String getDisplayName(TileAnimationType animationType) {
		return displayNames[animationType.ordinal()];
	}
	
	public static String getValueList() {
		String valueList = new String();
		for(int i=0;i<numberOfAnimationTypes();i++) {
			valueList += values()[i];
			if(i < numberOfAnimationTypes() - 1) {
				valueList += ", ";
			}
		}
		return valueList;
	}
	
	public static String getDisplayNameList() {
		String displayNameList = new String();
		for(int i=0;i<numberOfAnimationTypes();i++) {
			displayNameList += displayNames[i];
			if(i < numberOfAnimationTypes() - 1) {
				displayNameList += ", ";
			}
		}
		return displayNameList;
	}
	
	public static String[] getValidDisplayNames() {
		String[] names = new String[numberOfAnimationTypes()];
		for(int i=0;i<numberOfAnimationTypes();i++) {
			names[i] = displayNames[i];
		}
		return names;
	}
	
	public boolean isValid() {
		return ordinal() >= 0 && ordinal() < numberOfAnimationTypes();
	}
	
	public int getVersionValue() {
		return ordinal() < numberOfAnimationTypes() ? ordinal() + 1 : 0;
	}
	
	public static TileAnimationType parseFrom(int version) {
		if(version - 1 < 0 || version - 1 >= numberOfAnimationTypes()) { return Invalid; }
		
		return values()[version - 1];
	}
	
	public static TileAnimationType parseFrom(String data) {
		if(data == null) { return Invalid; }
		String temp = data.trim();
		if(temp.isEmpty()) { return Invalid; }
		
		for(int i=0;i<numberOfAnimationTypes();i++) {
			if(temp.equalsIgnoreCase(values()[i].name()) || temp.equalsIgnoreCase(displayNames[i])) {
				return values()[i];
			}
		}
		return Invalid;
	}
}
