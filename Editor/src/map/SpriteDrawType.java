package map;

import utilities.*;

public enum SpriteDrawType {
	Face("Face"),
	Wall("Wall"),
	Floor("Floor"),
	Sloped("Sloped"),
	Invalid("Invalid");

	private String m_displayName;

	public final static SpriteDrawType defaultDrawType = Face;

	private SpriteDrawType(String displayName) throws IllegalArgumentException {
		if(Utilities.isEmptyString(displayName)) { throw new IllegalArgumentException("Invalid display name, expected non-empty string."); }

		m_displayName = displayName.trim();
	}
	
	public final static String[] displayNames = {
		"Face",
		"Wall",
		"Floor",
		"Sloped",
		"Invalid"
	};
	
	public static int numberOfDrawTypes() {
		return values().length - 1;
	}
	
	public String getDisplayName() {
		return m_displayName;
	}
	
	public static String getDisplayName(SpriteDrawType action) {
		if(action == null) {
			return null;
		}

		return action.getDisplayName();
	}
	
	public static String getValueList() {
		String valueList = new String();

		for(int i = 0; i < numberOfDrawTypes(); i++) {
			valueList += values()[i];

			if(i < numberOfDrawTypes() - 1) {
				valueList += ", ";
			}
		}

		return valueList;
	}
	
	public static String getDisplayNameList() {
		String displayNameList = new String();

		for(int i = 0; i < numberOfDrawTypes(); i++) {
			displayNameList += displayNames[i];

			if(i < numberOfDrawTypes() - 1) {
				displayNameList += ", ";
			}
		}

		return displayNameList;
	}

	public static SpriteDrawType parseFrom(String data) {
		if(data == null) { return Invalid; }

		String temp = data.trim();

		if(temp.isEmpty()) { return Invalid; }

		for(int i = 0; i < numberOfDrawTypes(); i++) {
			if(temp.equalsIgnoreCase(values()[i].name()) || temp.equalsIgnoreCase(displayNames[i])) {
				return values()[i];
			}
		}

		return Invalid;
	}
	
	public boolean isValid() {
		return ordinal() >= 0 &&
			   ordinal() < numberOfDrawTypes();
	}
	
	public static boolean isValid(SpriteDrawType type) {
		return type != null &&
			   type.isValid();
	}
}
