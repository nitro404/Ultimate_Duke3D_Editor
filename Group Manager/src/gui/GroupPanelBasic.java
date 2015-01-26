package gui;

import java.awt.*;
import javax.swing.*;
import group.*;

public class GroupPanelBasic extends GroupPanel {
	
	private JList<GroupFile> m_fileList;
	private JScrollPane m_fileListScrollPane;
	
	private static final long serialVersionUID = -53066122432650525L;
	
	public GroupPanelBasic() {
		super(null);
	}
	
	public GroupPanelBasic(Group group) {
		super(group);
	}
	
	public boolean init() {
		if(m_initialized) { return true; }
		
		setLayout(new GridLayout(1, 1));
		
		m_fileList = new JList<GroupFile>();
		m_fileList.addMouseListener(this);
		m_fileListScrollPane = new JScrollPane(m_fileList);
		add(m_fileListScrollPane);
		
		m_initialized = true;
		
		updateWindow();
		
		return true;
	}
	
	public boolean setGroup(Group group) {
		m_group = group;
		
		updateWindow();
		
		return true;
	}
	
	public void updateGroup() {
		
	}
	
	public void updateWindow() {
		if(!m_initialized) { return; }
		
		m_fileList.clearSelection();
		
		if(m_group != null) {
			m_fileList.setListData(m_group.getFiles());
			
			updateLayout();
		}
	}
	
	public void updateLayout() {
		
	}
	
}
