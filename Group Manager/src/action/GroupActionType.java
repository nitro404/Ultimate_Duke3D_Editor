package action;

public enum GroupActionType {
	DoNothing,
	Save,
	SaveAs,
	Import,
	Export,
	Close,
	Invalid;
	
	public final static GroupActionType defaultAction = DoNothing;
	
	public final static String[] displayNames = {
		"Do Nothing",
		"Save",
		"Save As",
		"Import",
		"Export",
		"Close",
		"Invalid"
	};
	
	public String getDisplayName() {
		return displayNames[ordinal()];
	}
	
	public static String getDisplayName(GroupActionType action) {
		return displayNames[action.ordinal()];
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
	
	public boolean isValid() {
		return ordinal() >= 0 && ordinal() < values().length - 1;
	}
	
	public static GroupActionType parseFrom(String data) {
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
