package action;

public enum GroupActionType {
	DoNothing,
	Save,
	SaveAs,
	AddFiles,
	RemoveFiles,
	ReplaceFile,
	RenameFile,
	ExtractFiles,
	ExtractAllFiles,
	Import,
	Export,
	Close,
	CloseAll,
	Invalid;
	
	public final static GroupActionType defaultAction = DoNothing;
	
	public final static String[] displayNames = {
		"Do Nothing",
		"Save",
		"Save As",
		"Add Files",
		"Remove Files",
		"Replace File",
		"Rename File",
		"Extract Files",
		"Extract All Files",
		"Import",
		"Export",
		"Close",
		"CloseAll",
		"Invalid"
	};

	public static int numberOfGroupActionTypes() {
		return values().length - 1;
	}
	
	public String getDisplayName() {
		return displayNames[ordinal()];
	}
	
	public static String getDisplayName(GroupActionType action) {
		return displayNames[action.ordinal()];
	}
	
	public static String getValueList() {
		String valueList = new String();
		for(int i=0;i<numberOfGroupActionTypes();i++) {
			valueList += values()[i];
			if(i < numberOfGroupActionTypes() - 1) {
				valueList += ", ";
			}
		}
		return valueList;
	}
	
	public static String getDisplayNameList() {
		String displayNameList = new String();
		for(int i=0;i<numberOfGroupActionTypes();i++) {
			displayNameList += displayNames[i];
			if(i < numberOfGroupActionTypes() - 1) {
				displayNameList += ", ";
			}
		}
		return displayNameList;
	}
	
	public boolean isValid() {
		return ordinal() >= 0 && ordinal() < numberOfGroupActionTypes();
	}
	
	public static GroupActionType parseFrom(String data) {
		if(data == null) { return Invalid; }
		String temp = data.trim();
		if(temp.isEmpty()) { return Invalid; }
		
		for(int i=0;i<numberOfGroupActionTypes();i++) {
			if(temp.equalsIgnoreCase(values()[i].name()) || temp.equalsIgnoreCase(displayNames[i])) {
				return values()[i];
			}
		}
		return Invalid;
	}
}
