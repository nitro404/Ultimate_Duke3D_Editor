package map;

import utilities.*;

public class SwitchSpriteInformation {

	protected short m_tileNumber;
	protected String m_displayName;
	protected boolean m_hasSoundOverride;

	public SwitchSpriteInformation(short tileNumber, String displayName, boolean hasSoundOverride) throws IllegalArgumentException {
		if(tileNumber < 0) { throw new IllegalArgumentException("Invalid tile number, expected positive number."); }
		if(Utilities.isEmptyString(displayName)) { throw new IllegalArgumentException("Invalid display name, expected non-empty string."); }

		m_tileNumber = tileNumber;
		m_displayName = displayName.trim();
		m_hasSoundOverride = hasSoundOverride;
	}

	public short getTileNumber() {
		return m_tileNumber;
	}

	public String getDisplayName() {
		return m_displayName;
	}

	public boolean hasSoundOverride() {
		return m_hasSoundOverride;
	}

	public SwitchSpriteInformation clone() throws IllegalArgumentException {
		return new SwitchSpriteInformation(m_tileNumber, m_displayName, m_hasSoundOverride);
	}

	public boolean equals(Object o) {
		if(o == null || !(o instanceof SwitchSpriteInformation)) {
			return false;
		}

		SwitchSpriteInformation info = (SwitchSpriteInformation) o;
	
		return m_tileNumber == info.m_tileNumber;
	}

	public String toString() {
		return m_displayName + " (" + m_tileNumber + ")" + (m_hasSoundOverride ? " *" : "");
	}

}
