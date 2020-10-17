package action;

public enum DuplicateFileAction {
	Skip,
	SkipAll,
	Replace,
	ReplaceAll,
	Invalid;
	
	public final static DuplicateFileAction defaultAction = Skip;
	
	public final static String[] displayNames = {
		"Skip",
		"Skip All",
		"Replace",
		"Replace All",
		"Invalid"
	};

	public static int numberOfDuplicateFileActions() {
		return values().length - 1;
	}
	
	public String getDisplayName() {
		return displayNames[ordinal()];
	}
	
	public static String getDisplayName(DuplicateFileAction action) {
		return displayNames[action.ordinal()];
	}
	
	public static String getValueList() {
		String valueList = new String();
		for(int i=0;i<numberOfDuplicateFileActions();i++) {
			valueList += values()[i];
			if(i < numberOfDuplicateFileActions() - 1) {
				valueList += ", ";
			}
		}
		return valueList;
	}
	
	public static String getDisplayNameList() {
		String displayNameList = new String();
		for(int i=0;i<numberOfDuplicateFileActions();i++) {
			displayNameList += displayNames[i];
			if(i < numberOfDuplicateFileActions() - 1) {
				displayNameList += ", ";
			}
		}
		return displayNameList;
	}
	
	public static String[] getValidDisplayNames() {
		String[] validDisplayNames = new String[numberOfDuplicateFileActions()];
		for(int i=0;i<numberOfDuplicateFileActions();i++) {
			validDisplayNames[i] = displayNames[i];
		}
		return validDisplayNames;
	}
	
	public boolean isValid() {
		return ordinal() >= 0 && ordinal() < numberOfDuplicateFileActions();
	}
	
	public static DuplicateFileAction parseFrom(String data) {
		if(data == null) { return Invalid; }
		String temp = data.trim();
		if(temp.isEmpty()) { return Invalid; }
		
		for(int i=0;i<numberOfDuplicateFileActions();i++) {
			if(temp.equalsIgnoreCase(values()[i].name()) || temp.equalsIgnoreCase(displayNames[i])) {
				return values()[i];
			}
		}
		return Invalid;
	}
}
