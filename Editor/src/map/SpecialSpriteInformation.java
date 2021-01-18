package map;

import utilities.*;

public class SpecialSpriteInformation {

	protected short m_tileNumber;
	protected String m_displayName;

	public SpecialSpriteInformation(short tileNumber, String displayName) throws IllegalArgumentException {
		if(tileNumber < 0) { throw new IllegalArgumentException("Invalid tile number, expected positive number."); }
		if(Utilities.isEmptyString(displayName)) { throw new IllegalArgumentException("Invalid display name, expected non-empty string."); }

		m_tileNumber = tileNumber;
		m_displayName = displayName.trim();
	}

	public short getTileNumber() {
		return m_tileNumber;
	}

	public String getDisplayName() {
		return m_displayName;
	}

	public SpecialSpriteInformation clone() throws IllegalArgumentException {
		return new SpecialSpriteInformation(m_tileNumber, m_displayName);
	}

	public boolean equals(Object o) {
		if(o == null || !(o instanceof SpecialSpriteInformation)) {
			return false;
		}

		SpecialSpriteInformation info = (SpecialSpriteInformation) o;
	
		return m_tileNumber == info.m_tileNumber;
	}

	public String toString() {
		return m_displayName + " (" + m_tileNumber + ")";
	}

}
