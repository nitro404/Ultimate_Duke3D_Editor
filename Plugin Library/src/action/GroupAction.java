package action;

import gui.*;

public class GroupAction {
	
	protected GroupPanel m_source;
	protected GroupActionType m_action;
	
	public GroupAction(GroupPanel source, GroupActionType action) {
		m_source = source;
		m_action = action;
	}
	
	public GroupPanel getSource() {
		return m_source;
	}
	
	public GroupActionType getAction() {
		return m_action;
	}
	
	public boolean isValid() {
		return m_source != null &&
			   m_action.isValid() &&
			   m_action != GroupActionType.DoNothing;
	}
	
	public static boolean isvalid(GroupAction action) {
		return action != null && action.isValid();
	}
	
}
