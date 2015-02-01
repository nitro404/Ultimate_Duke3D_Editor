package gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import group.*;

public class GroupPanelSSI extends GroupPanelBasic implements ActionListener, DocumentListener {
	
	private JPanel m_informationPanel;
	private JScrollPane m_informationPanelScrollPane;
	private GridBagLayout m_informationPanelLayout;
	private JLabel m_titleLabel;
	private JTextField m_titleTextField;
	private JLabel m_versionLabel;
	private JComboBox<String> m_versionComboBox;
	private JLabel m_descriptionLabel;
	private JTextField[] m_descriptionTextField;
	private JLabel m_runFileLabel;
	private JTextField m_runFileTextField;
	
	boolean m_updatingWindow;
	
	private static final long serialVersionUID = -9139327010877190279L;
	
	public GroupPanelSSI() {
		super(null);
		
		m_updatingWindow = false;
	}
	
	public GroupPanelSSI(Group group) {
		super(group);
		
		m_updatingWindow = false;
	}
	
	public boolean init() {
		if(m_initialized) { return true; }
		
		if(!super.init(false)) { return false; }
		
		initComponents();
		initLayout();
		
		m_initialized = true;
		
		updateWindow();
		
		return true;
	}
	
	private void initComponents() {
		m_informationPanel = new JPanel();
		m_informationPanel.addMouseListener(this);
		m_informationPanelScrollPane = new JScrollPane(m_informationPanel);
		add(m_informationPanelScrollPane);
		
		m_titleLabel = new JLabel("Title:");
		m_titleLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		m_titleTextField = new JTextField();
		m_titleTextField.setDocument(new PlainDocumentLimit(GroupSSI.MAX_TITLE_LENGTH));
		m_titleTextField.getDocument().addDocumentListener(this);
		
		m_versionLabel = new JLabel("Version:");
		m_versionLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		m_versionComboBox = new JComboBox<String>(SSIVersion.getValidDisplayNames());
		m_versionComboBox.setSelectedIndex(SSIVersion.defaultVersion.ordinal());
		m_versionComboBox.addActionListener(this);
		
		m_descriptionLabel = new JLabel("Description:");
		m_descriptionLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		m_descriptionTextField = new JTextField[3];
		for(int i=0;i<m_descriptionTextField.length;i++) {
			m_descriptionTextField[i] = new JTextField();
			m_descriptionTextField[i].setDocument(new PlainDocumentLimit(GroupSSI.MAX_DESCRIPTION_LENGTH));
			m_descriptionTextField[i].getDocument().addDocumentListener(this);
		}
		
		m_runFileLabel = new JLabel("Run File:");
		m_runFileLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		m_runFileTextField = new JTextField();
		m_runFileTextField.setDocument(new PlainDocumentLimit(GroupSSI.MAX_RUNFILE_LENGTH));
		m_runFileTextField.getDocument().addDocumentListener(this);
		
		m_informationPanel.add(m_titleLabel);
		m_informationPanel.add(m_titleTextField);
		m_informationPanel.add(m_versionLabel);
		m_informationPanel.add(m_versionComboBox);
		m_informationPanel.add(m_descriptionLabel);
		for(int i=0;i<m_descriptionTextField.length;i++) {
			m_informationPanel.add(m_descriptionTextField[i]);
		}
		m_informationPanel.add(m_runFileLabel);
		m_informationPanel.add(m_runFileTextField);
	}
	
	private void initLayout() {
		setLayout(new GridLayout(1, 2));
		
		m_informationPanelLayout = new GridBagLayout();
		m_informationPanel.setLayout(m_informationPanelLayout);
		
		GridBagConstraints c = null;
		
		int insetValue = 4;
		int insetTop = insetValue;
		int insetLeft = insetValue;
		int insetBottom = insetValue;
		int insetRight = insetValue;
		
		float leftColumnWeight = 0.2f;
		float rightColumnWeight = 0.8f;
		
		c = new GridBagConstraints();
		c.insets = new Insets(insetTop, insetLeft, insetBottom, insetRight);
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.weightx = leftColumnWeight;
		c.weighty = 1;
		c.anchor = GridBagConstraints.EAST;
		c.fill = GridBagConstraints.NONE;
		m_informationPanelLayout.setConstraints(m_titleLabel, c);

		c = new GridBagConstraints();
		c.insets = new Insets(insetTop, insetLeft, insetBottom, insetRight);
		c.gridx = 1;
		c.gridy = 0;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.weightx = rightColumnWeight;
		c.weighty = 1;
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		m_informationPanelLayout.setConstraints(m_titleTextField, c);

		c = new GridBagConstraints();
		c.insets = new Insets(insetTop, insetLeft, insetBottom, insetRight);
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.weightx = leftColumnWeight;
		c.weighty = 1;
		c.anchor = GridBagConstraints.EAST;
		c.fill = GridBagConstraints.NONE;
		m_informationPanelLayout.setConstraints(m_versionLabel, c);

		c = new GridBagConstraints();
		c.insets = new Insets(insetTop, insetLeft, insetBottom, insetRight);
		c.gridx = 1;
		c.gridy = 1;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.weightx = rightColumnWeight;
		c.weighty = 1;
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.NONE;
		m_informationPanelLayout.setConstraints(m_versionComboBox, c);
		
		c = new GridBagConstraints();
		c.insets = new Insets(insetTop, insetLeft, insetBottom, insetRight);
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.weightx = leftColumnWeight;
		c.weighty = 1;
		c.anchor = GridBagConstraints.EAST;
		c.fill = GridBagConstraints.NONE;
		m_informationPanelLayout.setConstraints(m_descriptionLabel, c);
		
		for(int i=0;i<m_descriptionTextField.length;i++) {
			c = new GridBagConstraints();
			c.insets = new Insets(insetTop, insetLeft, insetBottom, insetRight);
			c.gridx = 1;
			c.gridy = 2 + i;
			c.gridwidth = 1;
			c.gridheight = 1;
			c.weightx = rightColumnWeight;
			c.weighty = 1;
			c.anchor = GridBagConstraints.WEST;
			c.fill = GridBagConstraints.HORIZONTAL;
			m_informationPanelLayout.setConstraints(m_descriptionTextField[i], c);
		}
		
		c = new GridBagConstraints();
		c.insets = new Insets(insetTop, insetLeft, insetBottom, insetRight);
		c.gridx = 0;
		c.gridy = 5;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.weightx = leftColumnWeight;
		c.weighty = 1;
		c.anchor = GridBagConstraints.EAST;
		c.fill = GridBagConstraints.NONE;
		m_informationPanelLayout.setConstraints(m_runFileLabel, c);

		c = new GridBagConstraints();
		c.insets = new Insets(insetTop, insetLeft, insetBottom, insetRight);
		c.gridx = 1;
		c.gridy = 5;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.weightx = rightColumnWeight;
		c.weighty = 1;
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		m_informationPanelLayout.setConstraints(m_runFileTextField, c);
	}
	
	public boolean setGroup(Group group) {
		if(group != null && !(group instanceof GroupSSI)) { return false; }
		
		super.setGroup(group, false);
		
		updateWindow();
		
		return true;
	}
	
	public void updateGroup() {
		if(!m_initialized || m_group == null) { return; }
		
		super.updateGroup();
		
		GroupSSI ssiGroup = (GroupSSI) m_group;
		
		ssiGroup.setTitle(m_titleTextField.getText());
		
		ssiGroup.setVersion(SSIVersion.parseFrom(m_titleTextField.getText()));
		
		for(int i=0;i<m_descriptionTextField.length;i++) {
			ssiGroup.setDescription(m_descriptionTextField[i].getText(), i);
		}
		
		if(ssiGroup.getVersion() == SSIVersion.V2) {
			ssiGroup.setRunFile(m_runFileTextField.getText());
		}
	}
	
	public void updateWindow() {
		if(!m_initialized) { return; }
		
		m_updatingWindow = true;
		
		super.updateWindow(false);
		
		if(m_group != null && m_group instanceof GroupSSI) {
			GroupSSI ssiGroup = (GroupSSI) m_group;
			
			m_titleTextField.setText(ssiGroup.getTitle());
			
			m_versionComboBox.setSelectedIndex(ssiGroup.getVersion().ordinal());
			
			for(int i=0;i<m_descriptionTextField.length;i++) {
				m_descriptionTextField[i].setText(ssiGroup.getDescription(i));
			}
			
			if(ssiGroup.getVersion() == SSIVersion.V2) {
				m_runFileTextField.setText(ssiGroup.getRunFile());
			}
			
			m_runFileTextField.setEnabled(ssiGroup.getVersion() == SSIVersion.V2);
			
			updateLayout();
		}
		
		m_updatingWindow = false;
	}
	
	public void updateLayout() {
		if(m_group == null || !m_initialized) { return; }
		
		super.updateLayout();
	}

	public void actionPerformed(ActionEvent e) {
		if(!m_initialized || m_group == null || m_updatingWindow || e == null || e.getSource() == null) { return; }
		
		if(e.getSource() == m_versionComboBox) {
			if(m_versionComboBox.getSelectedItem() == null) { return; }
			
			m_runFileTextField.setEnabled(SSIVersion.parseFrom(m_versionComboBox.getSelectedItem().toString()) == SSIVersion.V2);
			
			setChanged(true);
		}
	}

	public void insertUpdate(DocumentEvent e) {
		if(!m_initialized || m_group == null || m_updatingWindow || e == null || e.getDocument() == null) { return; }
		
		setChanged(true);
	}
	
	public void removeUpdate(DocumentEvent e) {
		if(!m_initialized || m_group == null || m_updatingWindow || e == null || e.getDocument() == null) { return; }
		
		setChanged(true);
	}
	
	public void changedUpdate(DocumentEvent e) {
		if(!m_initialized || m_group == null || m_updatingWindow || e == null || e.getDocument() == null) { return; }
		
		setChanged(true);
	}
	
}
