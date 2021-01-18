package map;

public class SecondarySpriteSound implements Sound {

	protected Sprite m_sprite;

	public SecondarySpriteSound(Sprite sprite) throws IllegalArgumentException {
		if(sprite == null) { throw new IllegalArgumentException("Sprite cannot be null."); }
		if(!sprite.hasSecondarySound()) { throw new IllegalArgumentException("Sprite does not have a secondary sound."); }

		m_sprite = sprite;
	}

	public short getSoundNumber() {
		return m_sprite.getSecondarySoundNumber();
	}

	public void setSoundNumber(short soundNumber) throws IllegalArgumentException {
		m_sprite.setSecondarySoundNumber(soundNumber);
	}

	public String toString() {
		return "Secondary Sprite Sound: " + getSoundNumber();
	}

}
