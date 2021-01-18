package map;

public class SwitchSpriteSound extends PrimarySpriteSound {

	public SwitchSpriteSound(Sprite sprite) throws IllegalArgumentException {
		super(sprite);
	}

	public String toString() {
		return "Switch Sprite Sound: " + getSoundNumber();
	}

}
