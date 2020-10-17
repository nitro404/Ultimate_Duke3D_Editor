package gui;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import item.*;
import group.*;

public class GroupPanelSSI extends GroupPanelBasic implements ActionListener, DocumentListener {
	
	protected boolean m_ssiInitialized;
	protected boolean m_updatingDataUsingDocument;
	
	protected JPanel m_informationPanel;
	protected JScrollPane m_informationPanelScrollPane;
	protected GridBagLayout m_informationPanelLayout;
	protected JLabel m_titleLabel;
	protected JTextField m_titleTextField;
	protected JLabel m_versionLabel;
	protected JComboBox<String> m_versionComboBox;
	protected JLabel m_descriptionLabel;
	protected JTextField[] m_descriptionTextField;
	protected JLabel m_runFileLabel;
	protected JTextField m_runFileTextField;
	
	protected Vector<JLabel> m_labels;
	protected Vector<JTextField> m_textFields;
	
	private static final long serialVersionUID = -9139327010877190279L;
	
	public GroupPanelSSI() {
		super(null);
		
		m_ssiInitialized = false;
		m_updatingWindow = false;
		m_updatingDataUsingDocument = false;
	}
	
	public GroupPanelSSI(Item item) throws IllegalArgumentException {
		super(item);

		if(!(item instanceof Group)) {
			throw new IllegalArgumentException("Invalid item, expected group instance!");
		}

		m_ssiInitialized = false;
		m_updatingWindow = false;
		m_updatingDataUsingDocument = false;
	}
	
	public boolean init() {
		if(m_initialized) { return true; }
		
		if(!super.init()) { return false; }
		
		initComponents();
		initLayout();
		
		m_ssiInitialized = true;
		
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
		
		m_labels = new Vector<JLabel>();
		m_textFields = new Vector<JTextField>();
		
		m_labels.add(m_titleLabel);
		m_labels.add(m_versionLabel);
		m_labels.add(m_descriptionLabel);
		m_labels.add(m_runFileLabel);
		
		m_textFields.add(m_titleTextField);
		for(int i=0;i<m_descriptionTextField.length;i++) {
			m_textFields.add(m_descriptionTextField[i]);
		}
		m_textFields.add(m_runFileTextField);
	}
	
	private void initLayout() {
		setLayout(new GridLayout(1, 2));
		
		m_informationPanel.setLayout(null);
	}
	
	public boolean setGroup(Group group) {
		if(group == null || !(group instanceof GroupSSI)) { return false; }
		
		super.setGroup(group);
		
		return true;
	}
	
	public void updateGroup() {
		if(!m_ssiInitialized || m_group == null) { return; }
		
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
		if(!m_ssiInitialized || m_updatingWindow || m_updatingDataUsingDocument) { return; }
		
		super.updateWindow();
		
		m_updatingWindow = true;
		
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
		}
		
		updateLayout();
		
		m_updatingWindow = false;
	}
	
	public void updateLayout() {
		if(!m_ssiInitialized || m_group == null) { return; }
		
		super.updateLayout();
		
		Component parent = getParent();
		int panelWidth = parent == null ? 0 : parent.getWidth() / 2;
		int panelHeight = parent == null ? 0 : parent.getHeight();
		
		int padding = 4;
		Dimension preferredSize = null;
		
		int maxLabelWidth = 0;
		int maxLabelHeight = 0;
		for(int i=0;i<m_labels.size();i++) {
			preferredSize = m_labels.elementAt(i).getPreferredSize();
			if(preferredSize.width  > maxLabelWidth)  { maxLabelWidth  = preferredSize.width;  }
			if(preferredSize.height > maxLabelHeight) { maxLabelHeight = preferredSize.height; }
		}
		
		int maxTextFieldWidth = 0;
		int maxTextFieldHeight = 0;
		for(int i=0;i<m_textFields.size();i++) {
			preferredSize = m_textFields.elementAt(i).getPreferredSize();
			if(preferredSize.width  > maxTextFieldWidth)  { maxTextFieldWidth  = preferredSize.width;  }
			if(preferredSize.height > maxTextFieldHeight) { maxTextFieldHeight = preferredSize.height; }
		}
		
		int comboBoxHeight = m_versionComboBox.getPreferredSize().height;
		int elementHeight = maxLabelHeight < maxTextFieldHeight ? maxTextFieldHeight : (maxLabelHeight < comboBoxHeight ? comboBoxHeight : maxLabelHeight);
		int textFieldWidth = panelWidth - maxLabelWidth - (padding * 4);
		
		m_titleLabel.setPreferredSize(new Dimension(maxLabelWidth, elementHeight));
		m_titleLabel.setSize(m_titleLabel.getPreferredSize());
		m_titleLabel.setLocation(padding, padding);
		
		m_titleTextField.setPreferredSize(new Dimension(textFieldWidth, elementHeight));
		m_titleTextField.setSize(m_titleTextField.getPreferredSize());
		m_titleTextField.setLocation(m_titleLabel.getLocation().x + m_titleLabel.getSize().width + padding, m_titleLabel.getLocation().y);
		
		m_versionLabel.setPreferredSize(new Dimension(maxLabelWidth, elementHeight));
		m_versionLabel.setSize(m_versionLabel.getPreferredSize());
		m_versionLabel.setLocation(m_titleLabel.getLocation().x, m_titleLabel.getLocation().y + m_titleLabel.getSize().height + padding);
		
		m_versionComboBox.setSize(m_versionComboBox.getPreferredSize());
		m_versionComboBox.setLocation(m_versionLabel.getLocation().x + m_versionLabel.getSize().width + padding, m_versionLabel.getLocation().y);
		
		m_descriptionLabel.setPreferredSize(new Dimension(maxLabelWidth, elementHeight));
		m_descriptionLabel.setSize(m_descriptionLabel.getPreferredSize());
		m_descriptionLabel.setLocation(m_versionLabel.getLocation().x, m_versionLabel.getLocation().y + m_versionLabel.getSize().height + padding);
		
		for(int i=0;i<m_descriptionTextField.length;i++) {
			m_descriptionTextField[i].setPreferredSize(new Dimension(textFieldWidth, elementHeight));
			m_descriptionTextField[i].setSize(m_descriptionTextField[i].getPreferredSize());
			m_descriptionTextField[i].setLocation(m_descriptionLabel.getLocation().x + m_descriptionLabel.getSize().width + padding, m_descriptionLabel.getLocation().y + ((elementHeight + padding) * i));
		}
		
		m_runFileLabel.setPreferredSize(new Dimension(maxLabelWidth, elementHeight));
		m_runFileLabel.setSize(m_runFileLabel.getPreferredSize());
		m_runFileLabel.setLocation(m_descriptionLabel.getLocation().x, m_descriptionLabel.getLocation().y + ((elementHeight + padding) * 3));
		
		m_runFileTextField.setPreferredSize(new Dimension(textFieldWidth, elementHeight));
		m_runFileTextField.setSize(m_runFileTextField.getPreferredSize());
		m_runFileTextField.setLocation(m_runFileLabel.getLocation().x + m_runFileLabel.getSize().width + padding, m_runFileLabel.getLocation().y);
		
		panelHeight = m_runFileTextField.getLocation().y + m_runFileTextField.getSize().height + padding;
		
		m_informationPanel.setPreferredSize(new Dimension(panelWidth - padding, panelHeight));
		m_informationPanel.setSize(m_informationPanel.getPreferredSize());
	}
	
	protected boolean updateDataUsingDocument(Document d) {
		if(d == null) { return false; }
		
		m_updatingDataUsingDocument = true;
		
		try {
			if(m_group != null && m_group instanceof GroupSSI) {
				GroupSSI ssiGroup = (GroupSSI) m_group;
				
				if(d == m_titleTextField.getDocument()) {
					ssiGroup.setTitle(d.getText(0, d.getLength()));
				}
				else if(d == m_runFileTextField.getDocument()) {
					ssiGroup.setRunFile(d.getText(0, d.getLength()));
				}
				else {
					for(int i=0;i<m_descriptionTextField.length;i++) {
						if(d == m_descriptionTextField[i].getDocument()) {
							ssiGroup.setDescription(d.getText(0, d.getLength()), i);
						}
					}
				}
			}
		}
		catch(BadLocationException e) { }
		
		m_updatingDataUsingDocument = false;
		
		return false;
	}

	public void actionPerformed(ActionEvent e) {
		if(!m_ssiInitialized || m_group == null || m_updatingWindow || e == null || e.getSource() == null) { return; }
		
		super.actionPerformed(e);
		
		if(m_group != null && m_group instanceof GroupSSI) {
			GroupSSI ssiGroup = (GroupSSI) m_group;
			
			if(e.getSource() == m_versionComboBox) {
				if(m_versionComboBox.getSelectedItem() == null) { return; }
				
				SSIVersion ssiVersion = SSIVersion.parseFrom(m_versionComboBox.getSelectedItem().toString());
				
				if(ssiVersion.isValid()) {
					ssiGroup.setVersion(ssiVersion);
					
					m_runFileTextField.setEnabled(ssiVersion == SSIVersion.V2);
				}
			}
		}
	}

	public void insertUpdate(final DocumentEvent e) {
		if(!m_ssiInitialized || m_group == null || m_updatingWindow || e == null || e.getDocument() == null) { return; }
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				updateDataUsingDocument(e.getDocument());
			}
		});
	}
	
	public void removeUpdate(final DocumentEvent e) {
		if(!m_ssiInitialized || m_group == null || m_updatingWindow || e == null || e.getDocument() == null) { return; }
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				updateDataUsingDocument(e.getDocument());
			}
		});
	}
	
	public void changedUpdate(final DocumentEvent e) {
		if(!m_ssiInitialized || m_group == null || m_updatingWindow || e == null || e.getDocument() == null) { return; }
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				updateDataUsingDocument(e.getDocument());
			}
		});
	}
	
}
