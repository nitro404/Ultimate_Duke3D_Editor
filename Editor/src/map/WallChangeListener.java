package map;

public interface WallChangeListener extends MapComponentChangeListener {

	public void handleWallChange(Wall wall);

}
