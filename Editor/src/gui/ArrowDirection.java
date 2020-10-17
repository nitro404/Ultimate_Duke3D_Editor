package gui;

public enum ArrowDirection {
	North,
	NorthEast,
	East,
	SouthEast,
	South,
	SouthWest,
	West,
	NorthWest,
	Center,
	None,
	Invalid;
	
	public final static ArrowDirection defaultDirection = North;
	
	public final static String[] displayNames = {
		"North",
		"North East",
		"East",
		"South East",
		"South",
		"South West",
		"West",
		"North West",
		"Center",
		"None",
		"Invalid"
	};

	public static int numberOfArrowDirections() {
		return values().length - 1;
	}
	
	public String getDisplayName() {
		return displayNames[ordinal()];
	}
	
	public static String getDisplayName(ArrowDirection arrowDirection) {
		return displayNames[arrowDirection.ordinal()];
	}
	
	public static String getValueList() {
		String valueList = new String();
		for(int i=0;i<numberOfArrowDirections();i++) {
			valueList += values()[i];
			if(i < numberOfArrowDirections() - 1) {
				valueList += ", ";
			}
		}
		return valueList;
	}
	
	public static String getDisplayNameList() {
		String displayNameList = new String();
		for(int i=0;i<numberOfArrowDirections();i++) {
			displayNameList += displayNames[i];
			if(i < numberOfArrowDirections() - 1) {
				displayNameList += ", ";
			}
		}
		return displayNameList;
	}
	
	public boolean isValid() {
		return ordinal() >= 0 && ordinal() < numberOfArrowDirections();
	}
	
	public static ArrowDirection parseFrom(String data) {
		if(data == null) { return Invalid; }
		String temp = data.trim();
		if(temp.isEmpty()) { return Invalid; }
		
		for(int i=0;i<numberOfArrowDirections();i++) {
			if(temp.equalsIgnoreCase(values()[i].name()) || temp.equalsIgnoreCase(displayNames[i])) {
				return values()[i];
			}
		}
		return Invalid;
	}
}
