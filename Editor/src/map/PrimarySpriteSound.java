package map;

public class PrimarySpriteSound extends Sound {

	protected Sprite m_sprite;

	public PrimarySpriteSound(Sprite sprite) throws IllegalArgumentException {
		if(sprite == null) { throw new IllegalArgumentException("Sprite cannot be null."); }
		if(!sprite.hasPrimarySound()) { throw new IllegalArgumentException("Sprite does not have a primary sound."); }

		m_sprite = sprite;
	}

	public short getSoundNumber() {
		return m_sprite.getPrimarySoundNumber();
	}

	public void setSoundNumber(short soundNumber) throws IllegalArgumentException {
		m_sprite.setPrimarySoundNumber(soundNumber);
	}

	public String toString() {
		return "Primary Sprite Sound: " + getSoundNumber();
	}

}
