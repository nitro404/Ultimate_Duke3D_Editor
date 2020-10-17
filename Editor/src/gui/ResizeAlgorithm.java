package gui;

public enum ResizeAlgorithm {
	NearestNeighbour,
	Bilinear,
	Bicubic,
	BicubicSmoother,
	BicubicSharper,
	Invalid;
	
	public final static ResizeAlgorithm defaultResizeAlgorithm = NearestNeighbour;
	
	public final static String[] displayNames = {
		"Nearest Neighbour",
		"Bilinear",
		"Bicubic",
		"Bicubic Smoother",
		"Bicubic Sharper",
		"Invalid"
	};
	
	public static int numberOfResizeAlgorithms() {
		return values().length - 1;
	}
	
	public String getDisplayName() {
		return displayNames[ordinal()];
	}
	
	public static String getDisplayName(ResizeAlgorithm resizeAlgorithm) {
		return displayNames[resizeAlgorithm.ordinal()];
	}
	
	public static String getValueList() {
		String valueList = new String();
		for(int i=0;i<numberOfResizeAlgorithms();i++) {
			valueList += values()[i];
			if(i < numberOfResizeAlgorithms() - 1) {
				valueList += ", ";
			}
		}
		return valueList;
	}
	
	public static String getDisplayNameList() {
		String displayNameList = new String();
		for(int i=0;i<numberOfResizeAlgorithms();i++) {
			displayNameList += displayNames[i];
			if(i < numberOfResizeAlgorithms() - 1) {
				displayNameList += ", ";
			}
		}
		return displayNameList;
	}
	
	public static String[] getValidDisplayNames() {
		String[] names = new String[numberOfResizeAlgorithms()];
		for(int i=0;i<numberOfResizeAlgorithms();i++) {
			names[i] = displayNames[i];
		}
		return names;
	}
	
	public boolean isValid() {
		return ordinal() >= 0 && ordinal() < numberOfResizeAlgorithms();
	}
	
	public int getVersionValue() {
		return ordinal() < numberOfResizeAlgorithms() ? ordinal() + 1 : 0;
	}
	
	public static ResizeAlgorithm parseFrom(int version) {
		if(version - 1 < 0 || version - 1 >= numberOfResizeAlgorithms()) { return Invalid; }
		
		return values()[version - 1];
	}
	
	public static ResizeAlgorithm parseFrom(String data) {
		if(data == null) { return Invalid; }
		String temp = data.trim();
		if(temp.isEmpty()) { return Invalid; }
		
		for(int i=0;i<numberOfResizeAlgorithms();i++) {
			if(temp.equalsIgnoreCase(values()[i].name()) || temp.equalsIgnoreCase(displayNames[i])) {
				return values()[i];
			}
		}
		return Invalid;
	}
}
