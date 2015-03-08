package gui;

import javax.swing.*;
import group.*;

public abstract class GroupProcessorPanel extends JPanel {
	
	protected GroupProcessor m_groupProcessor;
	
	private static final long serialVersionUID = 413668084900794387L;
	
	public GroupProcessorPanel(GroupProcessor groupProcessor) {
		setGroupProcessor(groupProcessor);
	}
	
	public void setGroupProcessor(GroupProcessor groupProcessor) {
		m_groupProcessor = groupProcessor;
	}
	
}
