package map;

public enum SpecialSprite {
	SectorEffector((short) 1, "Sector Effector"),
	Activator((short) 2, "Activator"),
	Touchplate((short) 3, "Touchplate"),
	ActivatorLocked((short) 4, "Activator Locked"),
	MusicAndSFX((short) 5, "Music and SFX"),
	Locators((short) 6, "Locators"),
	Cycler((short) 7, "Cycler"),
	MasterSwitch((short) 8, "Master Switch"),
	Respawn((short) 9, "Respawn"),
	Invalid((short) 0, "Invalid");

	private SpecialSpriteInformation m_spriteInfo;

	private SpecialSprite(short tileNumber, String displayName) throws IllegalArgumentException {
		m_spriteInfo = new SpecialSpriteInformation(tileNumber, displayName);
	}

	private SpecialSprite(SpecialSpriteInformation spriteInfo) throws IllegalArgumentException {
		m_spriteInfo = spriteInfo.clone();
	}

	public static int numberOfSpecialSprites() {
		return values().length - 1;
	}

	public short getTileNumber() {
		return m_spriteInfo.getTileNumber();
	}

	public SpecialSpriteInformation getSpriteInformation() {
		return m_spriteInfo;
	}

	public String getDisplayName() {
		return m_spriteInfo.getDisplayName();
	}

	public static String getDisplayName(SpecialSprite tag) {
		if(tag == null) {
			return null;
		}

		return tag.getDisplayName();
	}

	public static String getValueList() {
		String valueList = new String();

		for(int i = 0; i < numberOfSpecialSprites(); i++) {
			valueList += values()[i];

			if(i < numberOfSpecialSprites() - 1) {
				valueList += ", ";
			}
		}

		return valueList;
	}

	public static String getDisplayNameList() {
		String displayNameList = new String();

		for(int i = 0; i < numberOfSpecialSprites(); i++) {
			displayNameList += values()[i].getDisplayName();

			if(i < numberOfSpecialSprites() - 1) {
				displayNameList += ", ";
			}
		}

		return displayNameList;
	}

	public static SpecialSprite getSpecialSpriteByTileNumber(short tileNumber) {
		if(tileNumber < 0) {
			return Invalid;
		}

		SpecialSprite tags[] = values();

		for(int i = 0; i < tags.length; i++) {
			if(tags[i].getTileNumber() == tileNumber) {
				return tags[i];
			}
		}

		return Invalid;
	}

	public static SpecialSprite parseFrom(String data) {
		if(data == null) { return Invalid; }

		String temp = data.trim();

		if(temp.isEmpty()) { return Invalid; }
		
		for(int i = 0; i < numberOfSpecialSprites(); i++) {
			if(temp.equalsIgnoreCase(values()[i].name()) || temp.equalsIgnoreCase(values()[i].getDisplayName())) {
				return values()[i];
			}
		}

		return Invalid;
	}

	public boolean isValid() {
		return ordinal() >= 0 &&
			   ordinal() < numberOfSpecialSprites();
	}

	public static boolean isValid(SpecialSprite tag) {
		return tag != null &&
			   tag.isValid();
	}

}
