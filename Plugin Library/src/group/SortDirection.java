package group;

public enum SortDirection {
	Ascending,
	Descending,
	Invalid;
	
	public final static SortDirection defaultDirection = Ascending;
	
	public final static String[] displayNames = {
		"Ascending",
		"Descending",
		"Invalid"
	};
	
	public static int numberOfSortDirections() {
		return values().length - 1;
	}
	
	public String getDisplayName() {
		return displayNames[ordinal()];
	}
	
	public static String getDisplayName(SortDirection direction) {
		return displayNames[direction.ordinal()];
	}
	
	public static String getValueList() {
		String valueList = new String();
		for(int i=0;i<numberOfSortDirections();i++) {
			valueList += values()[i];
			if(i < numberOfSortDirections() - 1) {
				valueList += ", ";
			}
		}
		return valueList;
	}
	
	public static String getDisplayNameList() {
		String displayNameList = new String();
		for(int i=0;i<numberOfSortDirections();i++) {
			displayNameList += displayNames[i];
			if(i < numberOfSortDirections() - 1) {
				displayNameList += ", ";
			}
		}
		return displayNameList;
	}
	
	public boolean isValid() {
		return ordinal() >= 0 && ordinal() < numberOfSortDirections();
	}
	
	public static SortDirection parseFrom(String data) {
		if(data == null) { return Invalid; }
		String temp = data.trim();
		if(temp.length() == 0) { return Invalid; }
		
		for(int i=0;i<numberOfSortDirections();i++) {
			if(temp.equalsIgnoreCase(values()[i].name()) || temp.equalsIgnoreCase(displayNames[i])) {
				return values()[i];
			}
		}
		return Invalid;
	}
}
