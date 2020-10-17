package action;

import gui.*;

public class PaletteAction {
	
	protected PalettePanel m_source;
	protected PaletteActionType m_action;
	
	public PaletteAction(PalettePanel source, PaletteActionType action) {
		setSource(source);
		setAction(action);
	}
	
	public PalettePanel getSource() {
		return m_source;
	}
	
	public void setSource(PalettePanel source) {
		m_source = source;
	}
	
	public PaletteActionType getAction() {
		return m_action;
	}
	
	public void setAction(PaletteActionType action) {
		m_action = action;
	}
	
	public boolean isValid() {
		return m_source != null &&
			   m_action.isValid() &&
			   m_action != PaletteActionType.DoNothing;
	}
	
	public static boolean isvalid(PaletteAction action) {
		return action != null && action.isValid();
	}
	
}
