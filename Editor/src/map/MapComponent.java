package map;

public class MapComponent {

	protected Map m_map;

	public MapComponent() {
		this(null);
	}

	public MapComponent(Map map) {
		setMap(map);
	}

	public Map getMap() {
		return m_map;
	}

	public void setMap(Map map) {
		m_map = map;
	}

}
