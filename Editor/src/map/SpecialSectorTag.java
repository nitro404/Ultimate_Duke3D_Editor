package map;

public enum SpecialSectorTag {
	Water(1, (byte) 0, "Water"),
	Underwater(1, (byte) 0, "Underwater"),
	StarTrekDoors(9, (byte) 0, "Star Trek Doors"),
	ElevatorTransport(15, (byte) 0, "Elevator Transport"),
	ElevatorPlatformDown(16, (byte) 2, "Elevator Platform Down"),
	ElevatorPlatformUp(17, (byte) 2, "Elevator Platform Up"),
	ElevatorDown(18, (byte) 2, "Elevator Down"),
	ElevatorUp(19, (byte) 2, "Elevator Up"),
	CeilingDoor(20, (byte) 1, "Ceiling Door"),
	FloorDoor(21, (byte) 1, "Floor Door"),
	SplitDoor(22, (byte) 1, "Split Door"),
	SwingDoor(23, (byte) 2, "Swing Door"),
	SlideDoor(25, (byte) 1, "Slide Door"),
	SplitStarTrekDoor(26, (byte) 1, "Split Star Trek Door"),
	Bridge(27, (byte) 0, "Bridge"),
	DropFloor(28, (byte) 0, "Drop Floor"),
	TeethDoor(29, (byte) 1, "Teeth Door"),
	RotateRiseBridge(30, (byte) 1, "Rotate Rise Bridge"),
	TwoWayTrain(31, (byte) 0, "Two Way Train"),
	SecretRoom(32767, (byte) 0, "Secret Room"),
	EndOfLevel(65535, (byte) 0, "End of Level"),
	Invalid(0, (byte) 0, "Invalid");

	private SpecialSectorTagInformation m_tagInfo;

	private SpecialSectorTag(int tagNumber, byte numberOfSounds, String displayName) throws IllegalArgumentException {
		m_tagInfo = new SpecialSectorTagInformation(tagNumber, numberOfSounds, displayName);
	}

	private SpecialSectorTag(SpecialSectorTagInformation tagInfo) throws IllegalArgumentException {
		m_tagInfo = tagInfo.clone();
	}

	public static int numberOfSpecialSectorTags() {
		return values().length - 1;
	}

	public int getTagNumber() {
		return m_tagInfo.getTagNumber();
	}

	public boolean hasSound() {
		return m_tagInfo.hasSound();
	}

	public boolean hasMultipleSounds() {
		return m_tagInfo.hasMultipleSounds();
	}

	public byte numberOfSounds() {
		return m_tagInfo.numberOfSounds();
	}

	public SpecialSectorTagInformation getTagInformation() {
		return m_tagInfo;
	}

	public String getDisplayName() {
		return m_tagInfo.getDisplayName();
	}

	public static String getDisplayName(SpecialSectorTag tag) {
		if(tag == null) {
			return null;
		}

		return tag.getDisplayName();
	}

	public static String getValueList() {
		String valueList = new String();

		for(int i = 0; i < numberOfSpecialSectorTags(); i++) {
			valueList += values()[i];

			if(i < numberOfSpecialSectorTags() - 1) {
				valueList += ", ";
			}
		}

		return valueList;
	}

	public static String getDisplayNameList() {
		String displayNameList = new String();

		for(int i = 0; i < numberOfSpecialSectorTags(); i++) {
			displayNameList += values()[i].getDisplayName();

			if(i < numberOfSpecialSectorTags() - 1) {
				displayNameList += ", ";
			}
		}

		return displayNameList;
	}

	public static SpecialSectorTag getSpecialSectorTagByTagNumber(int tagNumber) {
		if(tagNumber < 0) {
			return Invalid;
		}

		SpecialSectorTag tags[] = values();

		for(int i = 0; i < tags.length; i++) {
			if(tags[i].getTagNumber() == tagNumber) {
				return tags[i];
			}
		}

		return Invalid;
	}

	public static SpecialSectorTag parseFrom(String data) {
		if(data == null) { return Invalid; }

		String temp = data.trim();

		if(temp.isEmpty()) { return Invalid; }
		
		for(int i = 0; i < numberOfSpecialSectorTags(); i++) {
			if(temp.equalsIgnoreCase(values()[i].name()) || temp.equalsIgnoreCase(values()[i].getDisplayName())) {
				return values()[i];
			}
		}

		return Invalid;
	}

	public boolean isValid() {
		return ordinal() >= 0 &&
			   ordinal() < numberOfSpecialSectorTags();
	}

	public static boolean isValid(SpecialSectorTag tag) {
		return tag != null &&
			   tag.isValid();
	}

}
