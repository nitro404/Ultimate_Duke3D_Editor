package gui;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import group.*;

public class PreferredGroupPluginEditorDialog extends JDialog implements ActionListener, KeyListener {
	
	private boolean m_submitted;
	private boolean m_cancelled;
	private JPanel m_mainPanel;
	private JButton m_submitButton;
	private JButton m_cancelButton;
	private Vector<JLabel> m_fileFormatLabels;
	private Vector<JComboBox<String>> m_pluginListComboBoxes;
	private Vector<JTextField> m_customPluginTextFields;
	
	private static final long serialVersionUID = -1588888847389098659L;
	
	public PreferredGroupPluginEditorDialog(Frame parent) {
		super(parent, true);
		setTitle("Preferred Plugins");
		setResizable(false);
		setDefaultCloseOperation(HIDE_ON_CLOSE);

		m_submitted = false;
		m_cancelled = false;
		m_fileFormatLabels = new Vector<JLabel>();
		m_pluginListComboBoxes = new Vector<JComboBox<String>>();
		m_customPluginTextFields = new Vector<JTextField>();
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				cancel();
			}
		});
		
		initComponents();
		updateLayout();
	}
	
	private void initComponents() {
		m_mainPanel = new JPanel();
		m_mainPanel.setLayout(null);
		
		m_submitButton = new JButton("Save");
		m_submitButton.setSize(m_submitButton.getPreferredSize());
		m_submitButton.addKeyListener(this);
		m_submitButton.addActionListener(this);
		
		m_cancelButton = new JButton("Cancel");
		m_cancelButton.setSize(m_cancelButton.getPreferredSize());
		m_cancelButton.addKeyListener(this);
		m_cancelButton.addActionListener(this);
		
		m_mainPanel.add(m_submitButton);
		m_mainPanel.add(m_cancelButton);
		
		setContentPane(m_mainPanel);
	}
	
	public boolean userSubmitted() {
		return m_submitted;
	}

	public boolean userCancelled() {
		return m_cancelled;
	}
	
	public void submit() {
		m_submitted = true;
		
		savePreferredPlugins();
		
		setVisible(false);
	}
	
	public void close() {
		clear();
		
		m_cancelled = false;
		
		setVisible(false);
	}
	
	private void cancel() {
		clear();
		
		m_cancelled = true;
		
		setVisible(false);
	}
	
	public void clear() {
		for(int i=0;i<m_fileFormatLabels.size();i++) {
			m_mainPanel.remove(m_fileFormatLabels.elementAt(i));
		}
		m_fileFormatLabels.clear();
		
		for(int i=0;i<m_pluginListComboBoxes.size();i++) {
			m_mainPanel.remove(m_pluginListComboBoxes.elementAt(i));
		}
		m_pluginListComboBoxes.clear();
		
		for(int i=0;i<m_customPluginTextFields.size();i++) {
			m_mainPanel.remove(m_customPluginTextFields.elementAt(i));
		}
		m_customPluginTextFields.clear();
	}
	
	public void display() {
		m_cancelled = false;
		m_submitted = false;
		
		loadPreferredPlugins();
		
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		
		setLocation((d.width / 2) - (getWidth() / 2), (d.height / 2) - (getHeight() / 2));
		
		setVisible(true);
	}
	
	public void keyTyped(KeyEvent e) { }
	public void keyReleased(KeyEvent e) { }
	
	public void keyPressed(KeyEvent e) {
		if(e.getSource() == m_submitButton) {
			if(e.getKeyChar() == KeyEvent.VK_ENTER || e.getKeyChar() == KeyEvent.VK_SPACE) {
				submit();
			}
		}
		else if(e.getSource() == m_cancelButton) {
			if(e.getKeyChar() == KeyEvent.VK_ENTER || e.getKeyChar() == KeyEvent.VK_SPACE) {
				cancel();
			}
		}
		
		if(e.getKeyChar() == KeyEvent.VK_ESCAPE) {
			cancel();
		}
	}
	
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == m_submitButton) {
			submit();
		}
		else if(e.getSource() == m_cancelButton) {
			cancel();
		}
		else {
			for(int i=0;i<m_pluginListComboBoxes.size();i++) {
				m_customPluginTextFields.elementAt(i).setEnabled(m_pluginListComboBoxes.elementAt(i).getSelectedIndex() == m_pluginListComboBoxes.elementAt(i).getItemCount() - 1);
			}
		}
	}
	
	public void updateLayout() {
		int padding = 4;
		Dimension preferredSize = null;
		
		int maxLabelWidth = 0;
		int maxLabelHeight = 0;
		for(int i=0;i<m_fileFormatLabels.size();i++) {
			preferredSize = m_fileFormatLabels.elementAt(i).getPreferredSize();
			if(preferredSize.width  > maxLabelWidth)  { maxLabelWidth  = preferredSize.width;  }
			if(preferredSize.height > maxLabelHeight) { maxLabelHeight = preferredSize.height; }
		}
		
		int maxComboBoxWidth = 0;
		int maxComboBoxHeight = 0;
		for(int i=0;i<m_pluginListComboBoxes.size();i++) {
			preferredSize = m_pluginListComboBoxes.elementAt(i).getPreferredSize();
			if(preferredSize.width  > maxComboBoxWidth)  { maxComboBoxWidth  = preferredSize.width;  }
			if(preferredSize.height > maxComboBoxHeight) { maxComboBoxHeight = preferredSize.height; }
		}
		
		int maxTextFieldWidth = 120;
		int maxTextFieldHeight = 0;
		for(int i=0;i<m_customPluginTextFields.size();i++) {
			preferredSize = m_customPluginTextFields.elementAt(i).getPreferredSize();
			if(preferredSize.width  > maxTextFieldWidth)  { maxTextFieldWidth  = preferredSize.width;  }
			if(preferredSize.height > maxTextFieldHeight) { maxTextFieldHeight = preferredSize.height; }
		}
		
		int elementHeight = 0;
		if(maxLabelHeight > elementHeight)     { elementHeight = maxLabelHeight;     }
		if(maxComboBoxHeight > elementHeight)  { elementHeight = maxComboBoxHeight;  }
		if(maxTextFieldHeight > elementHeight) { elementHeight = maxTextFieldHeight; }
		
		int elementYPosition = padding;
		for(int i=0;i<m_fileFormatLabels.size();i++) {
			m_fileFormatLabels.elementAt(i).setPreferredSize(new Dimension(maxLabelWidth, elementHeight));
			m_fileFormatLabels.elementAt(i).setSize(m_fileFormatLabels.elementAt(i).getPreferredSize());
			m_fileFormatLabels.elementAt(i).setLocation(padding, elementYPosition);
			
			m_pluginListComboBoxes.elementAt(i).setPreferredSize(new Dimension(maxComboBoxWidth, maxComboBoxHeight));
			m_pluginListComboBoxes.elementAt(i).setSize(m_pluginListComboBoxes.elementAt(i).getPreferredSize());
			m_pluginListComboBoxes.elementAt(i).setLocation(maxLabelWidth + (padding * 2), elementYPosition);
			
			m_customPluginTextFields.elementAt(i).setPreferredSize(new Dimension(maxTextFieldWidth, maxTextFieldHeight));
			m_customPluginTextFields.elementAt(i).setSize(m_customPluginTextFields.elementAt(i).getPreferredSize());
			m_customPluginTextFields.elementAt(i).setLocation(maxLabelWidth + maxComboBoxWidth + (padding * 3), elementYPosition);
			
			elementYPosition += elementHeight + padding;
		}
		
		int buttonHeight = m_submitButton.getHeight() > m_cancelButton.getHeight() ? m_submitButton.getHeight() : m_cancelButton.getHeight();
		int buttonOffset = (int) ((m_submitButton.getWidth() > m_cancelButton.getWidth() ? -1.0f : 1.0f) * (((m_submitButton.getWidth() + m_cancelButton.getWidth()) * 0.25f) + padding));

		int panelWidth = maxLabelWidth + maxComboBoxWidth + maxTextFieldWidth + (padding * 4);
		int panelHeight = (m_fileFormatLabels.size() * (elementHeight + padding)) + buttonHeight + (padding * 2);
		
		m_submitButton.setLocation((panelWidth / 2) - buttonOffset - (m_submitButton.getWidth() / 2), elementYPosition);
		m_cancelButton.setLocation((panelWidth / 2) + buttonOffset - (m_cancelButton.getWidth() / 2), elementYPosition);
		
		m_mainPanel.setPreferredSize(new Dimension(panelWidth, panelHeight));
		m_mainPanel.setSize(getPreferredSize());
		
		pack();
	}
	
	private void savePreferredPlugins() {
		int selectedIndex = -1;
		String preferredPluginName = null;
		String formattedPreferredPluginName = null;
		
		for(int i=0;i<m_pluginListComboBoxes.size();i++) {
			selectedIndex = m_pluginListComboBoxes.elementAt(i).getSelectedIndex();
			
			if(selectedIndex == 0) {
				preferredPluginName = null;
			}
			else if(selectedIndex == m_pluginListComboBoxes.elementAt(i).getItemCount() - 1) {
				preferredPluginName = m_customPluginTextFields.elementAt(i).getText();
			}
			else {
				preferredPluginName = (String) m_pluginListComboBoxes.elementAt(i).getSelectedItem();
			}
			
			if(preferredPluginName == null) {
				formattedPreferredPluginName = null;
			}
			else {
				formattedPreferredPluginName = preferredPluginName.trim();
				
				if(formattedPreferredPluginName.length() == 0) {
					formattedPreferredPluginName = null;
				}
			}
			
			if(formattedPreferredPluginName == null) {
				GroupPluginManager.instance.removePreferredPluginForFileFormat(m_fileFormatLabels.elementAt(i).getText(), GroupPlugin.class);
			}
			else {
				GroupPluginManager.instance.setPreferredPluginForFileFormat(m_fileFormatLabels.elementAt(i).getText(), formattedPreferredPluginName, GroupPlugin.class);
			}
		}
	}
	
	private void loadPreferredPlugins() {
		clear();
		
		Vector<String> groupFileFormats = GroupPluginManager.instance.getSupportedAndPreferredGroupFileFormats();
		
		String fileFormat = null;
		JLabel fileFormatLabel = null;
		JComboBox<String> pluginListComboBox = null;
		JTextField customPluginTextField = null;
		Vector<GroupPlugin> groupPlugins = null;
		String groupPluginNames[] = null;
		int preferredPluginIndex = -1;
		String preferredPluginName = null;
		for(int i=0;i<groupFileFormats.size();i++) {
			fileFormat = groupFileFormats.elementAt(i);
			
			groupPlugins = GroupPluginManager.instance.getGroupPluginsForFileFormat(fileFormat);
			if(groupPlugins == null || groupPlugins.size() == 0) { continue; }
			
			preferredPluginIndex = -1;
			preferredPluginName = GroupPluginManager.instance.getPreferredPluginForFileFormat(fileFormat, GroupPlugin.class);
			
			groupPluginNames = new String[groupPlugins.size() + 2];
			groupPluginNames[0] = "None";
			groupPluginNames[groupPluginNames.length - 1] = "Other";
			for(int j=0;j<groupPlugins.size();j++) {
				groupPluginNames[j + 1] = groupPlugins.elementAt(j).getName();
				
				if(groupPlugins.elementAt(j).getName().equalsIgnoreCase(preferredPluginName)) {
					preferredPluginIndex = j;
				}
			}
			
			fileFormatLabel = new JLabel(fileFormat);
			m_mainPanel.add(fileFormatLabel);
			m_fileFormatLabels.addElement(fileFormatLabel);
			
			pluginListComboBox = new JComboBox<String>(groupPluginNames);
			pluginListComboBox.setSelectedIndex(preferredPluginName == null ? 0 : preferredPluginIndex < 0 ? groupPluginNames.length - 1 : preferredPluginIndex + 1);
			pluginListComboBox.addActionListener(this);
			m_mainPanel.add(pluginListComboBox);
			m_pluginListComboBoxes.addElement(pluginListComboBox);
			
			customPluginTextField = new JTextField();
			customPluginTextField.setText(preferredPluginName == null ? "" : preferredPluginName);
			customPluginTextField.setEnabled(pluginListComboBox.getSelectedIndex() == pluginListComboBox.getItemCount() - 1);
			m_mainPanel.add(customPluginTextField);
			m_customPluginTextFields.addElement(customPluginTextField);
		}
		
		updateLayout();
	}
	
}
