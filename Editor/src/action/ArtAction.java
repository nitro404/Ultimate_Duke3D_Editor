package action;

import gui.*;

public class ArtAction {
	
	protected ArtPanel m_source;
	protected TileButton m_target;
	protected ArtActionType m_action;
	
	public ArtAction(ArtPanel source, TileButton target, ArtActionType action) {
		setSource(source);
		setTarget(target);
		setAction(action);
	}
	
	public ArtPanel getSource() {
		return m_source;
	}
	
	public void setSource(ArtPanel source) {
		m_source = source;
	}

	public TileButton getTarget() {
		return m_target;
	}
	
	public void setTarget(TileButton target) {
		m_target = target;
	}
	
	public ArtActionType getAction() {
		return m_action;
	}
	
	public void setAction(ArtActionType action) {
		m_action = action;
	}
	
	public boolean isValid() {
		return m_source != null &&
			   m_action.isValid() &&
			   m_action != ArtActionType.DoNothing;
	}
	
	public static boolean isvalid(ArtAction action) {
		return action != null && action.isValid();
	}
	
}
