package map;

public class OneTimeSectorSound implements Sound {

	protected Sector m_sector;

	public OneTimeSectorSound(Sector sector) throws IllegalArgumentException {
		if(sector == null) { throw new IllegalArgumentException("Sector cannot be null."); }
		if(!sector.hasOneTimeSound()) { throw new IllegalArgumentException("Sector does not have a one time sound."); }

		m_sector = sector;
	}

	public short getSoundNumber() {
		return m_sector.getOneTimeSoundNumber();
	}

	public void setSoundNumber(short soundNumber) throws IllegalArgumentException {
		m_sector.setOneTimeSoundNumber(soundNumber);
	}

	public String toString() {
		return "One Time Sector Sound: " + getSoundNumber();
	}

}
