package action;

import gui.*;

public class MapAction {
	
	protected MapPanel m_source;
	protected MapActionType m_action;
	
	public MapAction(MapPanel source, MapActionType action) {
		setSource(source);
		setAction(action);
	}
	
	public MapPanel getSource() {
		return m_source;
	}
	
	public void setSource(MapPanel source) {
		m_source = source;
	}
	
	public MapActionType getAction() {
		return m_action;
	}
	
	public void setAction(MapActionType action) {
		m_action = action;
	}
	
	public boolean isValid() {
		return m_source != null &&
			   m_action.isValid() &&
			   m_action != MapActionType.DoNothing;
	}
	
	public static boolean isvalid(MapAction action) {
		return action != null && action.isValid();
	}
	
}
