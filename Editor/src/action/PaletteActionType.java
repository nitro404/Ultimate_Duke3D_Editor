package action;

public enum PaletteActionType {
	DoNothing,
	Save,
	SaveAs,
	AddFiles,
	RemoveFiles,
	ReplaceFile,
	ExtractFiles,
	Import,
	Export,
	Close,
	Invalid;
	
	public final static PaletteActionType defaultAction = DoNothing;
	
	public final static String[] displayNames = {
		"Do Nothing",
		"Save",
		"Save As",
		"Add Files",
		"Remove Files",
		"Replace File",
		"Extract Files",
		"Import",
		"Export",
		"Close",
		"Invalid"
	};

	public static int numberOfPaletteActionTypes() {
		return values().length - 1;
	}
	
	public String getDisplayName() {
		return displayNames[ordinal()];
	}
	
	public static String getDisplayName(PaletteActionType action) {
		return displayNames[action.ordinal()];
	}
	
	public static String getValueList() {
		String valueList = new String();
		for(int i=0;i<numberOfPaletteActionTypes();i++) {
			valueList += values()[i];
			if(i < numberOfPaletteActionTypes() - 1) {
				valueList += ", ";
			}
		}
		return valueList;
	}
	
	public static String getDisplayNameList() {
		String displayNameList = new String();
		for(int i=0;i<numberOfPaletteActionTypes();i++) {
			displayNameList += displayNames[i];
			if(i < numberOfPaletteActionTypes() - 1) {
				displayNameList += ", ";
			}
		}
		return displayNameList;
	}
	
	public boolean isValid() {
		return ordinal() >= 0 && ordinal() < numberOfPaletteActionTypes();
	}
	
	public static PaletteActionType parseFrom(String data) {
		if(data == null) { return Invalid; }
		String temp = data.trim();
		if(temp.length() == 0) { return Invalid; }
		
		for(int i=0;i<numberOfPaletteActionTypes();i++) {
			if(temp.equalsIgnoreCase(values()[i].name()) || temp.equalsIgnoreCase(displayNames[i])) {
				return values()[i];
			}
		}
		return Invalid;
	}
}
