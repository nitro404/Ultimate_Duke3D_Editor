package map;

public enum SwitchSprite {
	AccessSwitch((short) 130, "Access Switch", true),
	SlotDoor((short) 132, "Slot Door", true),
	LightSwitch((short) 134, "Light Switch", true),
	SpaceDoorSwitch((short) 136, "Space Door Switch", true),
	SpaceLightSwitch((short) 138, "Space Light Switch", true),
	FrankensteinSwitch((short) 140, "Frankenstein Switch", true),
	MultiSwitch((short) 146, "Multi Switch", true),
	DipSwitch((short) 162, "Dip Switch", false),
	DipSwitch2((short) 164, "Dip Switch 2", true),
	TechSwitch((short) 166, "Tech Switch", false),
	DipSwitch3((short) 168, "Dip Switch 3", true),
	AccessSwitch2((short) 170, "Access Switch 2", true),
	LightSwitch2((short) 712, "Light Switch 2", true),
	PowerSwitch1((short) 860, "Power Switch 1", true),
	LockSwitch1((short) 862, "Lock Switch 1", true),
	PowerSwitch2((short) 864, "Power Switch 2", true),
	HandSwitch((short) 1111, "Hand Switch", true),
	PullSwitch((short) 1122, "Pull Switch", true),
	AlienSwitch((short) 1142, "Alien Switch", false),
	Invalid((short) 0, "Invalid", false);

	private SwitchSpriteInformation m_spriteInfo;

	private SwitchSprite(short tileNumber, String displayName, boolean hasSoundOverride) throws IllegalArgumentException {
		m_spriteInfo = new SwitchSpriteInformation(tileNumber, displayName, hasSoundOverride);
	}

	private SwitchSprite(SwitchSpriteInformation spriteInfo) throws IllegalArgumentException {
		m_spriteInfo = spriteInfo.clone();
	}

	public static int numberOfSwitchSprites() {
		return values().length - 1;
	}

	public short getTileNumber() {
		return m_spriteInfo.getTileNumber();
	}

	public SwitchSpriteInformation getSpriteInformation() {
		return m_spriteInfo;
	}

	public String getDisplayName() {
		return m_spriteInfo.getDisplayName();
	}

	public static String getDisplayName(SwitchSprite tag) {
		if(tag == null) {
			return null;
		}

		return tag.getDisplayName();
	}

	public boolean hasSoundOverride() {
		return m_spriteInfo.hasSoundOverride();
	}

	public static String getValueList() {
		String valueList = new String();

		for(int i = 0; i < numberOfSwitchSprites(); i++) {
			valueList += values()[i];

			if(i < numberOfSwitchSprites() - 1) {
				valueList += ", ";
			}
		}

		return valueList;
	}

	public static String getDisplayNameList() {
		String displayNameList = new String();

		for(int i = 0; i < numberOfSwitchSprites(); i++) {
			displayNameList += values()[i].getDisplayName();

			if(i < numberOfSwitchSprites() - 1) {
				displayNameList += ", ";
			}
		}

		return displayNameList;
	}

	public static SwitchSprite getSwitchSpriteByTileNumber(short tileNumber) {
		if(tileNumber < 0) {
			return Invalid;
		}

		SwitchSprite tags[] = values();

		for(int i = 0; i < tags.length; i++) {
			if(tags[i].getTileNumber() == tileNumber) {
				return tags[i];
			}
		}

		return Invalid;
	}

	public static SwitchSprite parseFrom(String data) {
		if(data == null) { return Invalid; }

		String temp = data.trim();

		if(temp.isEmpty()) { return Invalid; }
		
		for(int i = 0; i < numberOfSwitchSprites(); i++) {
			if(temp.equalsIgnoreCase(values()[i].name()) || temp.equalsIgnoreCase(values()[i].getDisplayName())) {
				return values()[i];
			}
		}

		return Invalid;
	}

	public boolean isValid() {
		return ordinal() >= 0 &&
			   ordinal() < numberOfSwitchSprites();
	}

	public static boolean isValid(SwitchSprite tag) {
		return tag != null &&
			   tag.isValid();
	}

}
