package art;

public interface TileNameChangeListener {

	public void handleTileNamesChanged(TileNames tileNames);

	public void handleTileNameChanged(TileNames tileNames, int tileNumber, String previousTileName, String newTileName);

}
