package action;

public enum MapActionType {
	DoNothing,
	Save,
	SaveAs,
	Close,
	Invalid;
	
	public final static MapActionType defaultAction = DoNothing;
	
	public final static String[] displayNames = {
		"Do Nothing",
		"Save",
		"Save As",
		"Close",
		"Invalid"
	};

	public static int numberOfMapActionTypes() {
		return values().length - 1;
	}
	
	public String getDisplayName() {
		return displayNames[ordinal()];
	}
	
	public static String getDisplayName(MapActionType action) {
		return displayNames[action.ordinal()];
	}
	
	public static String getValueList() {
		String valueList = new String();
		for(int i=0;i<numberOfMapActionTypes();i++) {
			valueList += values()[i];
			if(i < numberOfMapActionTypes() - 1) {
				valueList += ", ";
			}
		}
		return valueList;
	}
	
	public static String getDisplayNameList() {
		String displayNameList = new String();
		for(int i=0;i<numberOfMapActionTypes();i++) {
			displayNameList += displayNames[i];
			if(i < numberOfMapActionTypes() - 1) {
				displayNameList += ", ";
			}
		}
		return displayNameList;
	}
	
	public boolean isValid() {
		return ordinal() >= 0 && ordinal() < numberOfMapActionTypes();
	}
	
	public static MapActionType parseFrom(String data) {
		if(data == null) { return Invalid; }
		String temp = data.trim();
		if(temp.isEmpty()) { return Invalid; }
		
		for(int i=0;i<numberOfMapActionTypes();i++) {
			if(temp.equalsIgnoreCase(values()[i].name()) || temp.equalsIgnoreCase(displayNames[i])) {
				return values()[i];
			}
		}
		return Invalid;
	}
}
