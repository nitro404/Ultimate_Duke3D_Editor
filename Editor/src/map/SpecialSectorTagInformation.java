package map;

import utilities.*;

public class SpecialSectorTagInformation {

	protected int m_tagNumber;
	protected byte m_numberOfSounds;
	protected String m_displayName;

	public SpecialSectorTagInformation(int tagNumber, byte numberOfSounds, String displayName) throws IllegalArgumentException {
		if(tagNumber < 0 || tagNumber > 65535) { throw new IllegalArgumentException("Invalid tag: " + tagNumber + ", expected number between 0 and 65535."); }
		if(numberOfSounds < 0) { throw new IllegalArgumentException("Invalid number of sounds, expected positive number."); }
		if(Utilities.isEmptyString(displayName)) { throw new IllegalArgumentException("Invalid display name, expected non-empty string."); }

		m_tagNumber = tagNumber;
		m_numberOfSounds = numberOfSounds;
		m_displayName = displayName.trim();
	}

	public int getTagNumber() {
		return m_tagNumber;
	}

	public boolean hasSound() {
		return m_numberOfSounds > 0;
	}

	public boolean hasMultipleSounds() {
		return m_numberOfSounds > 1;
	}

	public byte numberOfSounds() {
		return m_numberOfSounds;
	}

	public String getDisplayName() {
		return m_displayName;
	}

	public SpecialSectorTagInformation clone() throws IllegalArgumentException {
		return new SpecialSectorTagInformation(m_tagNumber, m_numberOfSounds, m_displayName);
	}

	public boolean equals(Object o) {
		if(o == null || !(o instanceof SpecialSectorTagInformation)) {
			return false;
		}

		SpecialSectorTagInformation info = (SpecialSectorTagInformation) o;
	
		return m_tagNumber == info.m_tagNumber;
	}

	public String toString() {
		return m_displayName + " (" + m_tagNumber + ")";
	}

}
