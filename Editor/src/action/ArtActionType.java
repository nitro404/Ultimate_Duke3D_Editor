package action;

public enum ArtActionType {
	DoNothing,
	Save,
	SaveAs,
	ClearTile,
	SwapTiles,
	ReplaceTile,
	ImportTiles,
	ExtractTile,
	ExtractTiles,
	ExtractAllTiles,
	SetNumber,
	SetNumberOfTiles,
	SetLegacyTileCount,
	Close,
	Invalid;
	
	public final static ArtActionType defaultAction = DoNothing;
	
	public final static String[] displayNames = {
		"Do Nothing",
		"Save",
		"Save As",
		"Clear Tile",
		"Swap Tiles",
		"Replace Tile",
		"Import Tiles",
		"Extract Tile",
		"Extract Tiles",
		"Extract All Tiles",
		"Set Number",
		"Set Number of Tiles",
		"Set Legacy Tile Count",
		"Close",
		"Invalid"
	};

	public static int numberOfArtActionTypes() {
		return values().length - 1;
	}
	
	public String getDisplayName() {
		return displayNames[ordinal()];
	}
	
	public static String getDisplayName(ArtActionType action) {
		return displayNames[action.ordinal()];
	}
	
	public static String getValueList() {
		String valueList = new String();
		for(int i=0;i<numberOfArtActionTypes();i++) {
			valueList += values()[i];
			if(i < numberOfArtActionTypes() - 1) {
				valueList += ", ";
			}
		}
		return valueList;
	}
	
	public static String getDisplayNameList() {
		String displayNameList = new String();
		for(int i=0;i<numberOfArtActionTypes();i++) {
			displayNameList += displayNames[i];
			if(i < numberOfArtActionTypes() - 1) {
				displayNameList += ", ";
			}
		}
		return displayNameList;
	}
	
	public boolean isValid() {
		return ordinal() >= 0 && ordinal() < numberOfArtActionTypes();
	}
	
	public static ArtActionType parseFrom(String data) {
		if(data == null) { return Invalid; }
		String temp = data.trim();
		if(temp.isEmpty()) { return Invalid; }
		
		for(int i=0;i<numberOfArtActionTypes();i++) {
			if(temp.equalsIgnoreCase(values()[i].name()) || temp.equalsIgnoreCase(displayNames[i])) {
				return values()[i];
			}
		}
		return Invalid;
	}
}
