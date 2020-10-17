package gui;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import plugin.*;
import editor.*;

public class PreferredPluginEditorDialog extends JDialog implements ActionListener {
	
	private boolean m_submitted;
	private boolean m_cancelled;
	private JPanel m_mainPanel;
	private JButton m_submitButton;
	private JButton m_addButton;
	private JButton m_clearButton;
	private JButton m_cancelButton;
	private Vector<JLabel> m_fileFormatLabels;
	private Vector<JComboBox<String>> m_pluginListComboBoxes;
	private Vector<JTextField> m_customPluginTextFields;

	private static final long serialVersionUID = -1251096101250398419L;
	
	public PreferredPluginEditorDialog(Frame parent) {
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

		getRootPane().registerKeyboardAction(
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					cancel();
				}
			},
			KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
			JComponent.WHEN_IN_FOCUSED_WINDOW
		);

		initComponents();
		updateLayout();
	}
	
	private void initComponents() {
		m_mainPanel = new JPanel();
		m_mainPanel.setLayout(null);
		
		m_submitButton = new JButton("Save");
		m_submitButton.setSize(m_submitButton.getPreferredSize());
		m_submitButton.addActionListener(this);

		m_addButton = new JButton("Add");
		m_addButton.setSize(m_addButton.getPreferredSize());
		m_addButton.addActionListener(this);
		
		m_clearButton = new JButton("Clear");
		m_clearButton.setSize(m_clearButton.getPreferredSize());
		m_clearButton.addActionListener(this);
		
		m_cancelButton = new JButton("Cancel");
		m_cancelButton.setSize(m_cancelButton.getPreferredSize());
		m_cancelButton.addActionListener(this);
		
		m_mainPanel.add(m_submitButton);
		m_mainPanel.add(m_addButton);
		m_mainPanel.add(m_clearButton);
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
	
	public boolean addEntryPrompt() {
		String fileFormat = null;
		String input = null;
		boolean duplicateFileExtension = false;
		
		while(true) {
			input = JOptionPane.showInputDialog(this, "Enter a file extension:");
			if(input == null) { return false; }
			
			fileFormat = input.trim().toUpperCase();
			if(fileFormat.length() < 1) {
				JOptionPane.showMessageDialog(this, "File extension cannot be empty.");
				continue;
			}
			
			duplicateFileExtension = false;
			
			for(int i=0;i<m_fileFormatLabels.size();i++) {
				if(fileFormat.equalsIgnoreCase(m_fileFormatLabels.elementAt(i).getText())) {
					duplicateFileExtension = true;
					
					JOptionPane.showMessageDialog(this, "File extension is already preset, please try another!", "Duplicate File Extension", JOptionPane.WARNING_MESSAGE);
					
					break;
				}
			}
			
			if(duplicateFileExtension) { continue; }
			
			break;
		}
		
		String pluginNames[] = new String[2];
		pluginNames[0] = "None";
		pluginNames[1] = "Other";
		
		JLabel fileFormatLabel = new JLabel(fileFormat);
		m_mainPanel.add(fileFormatLabel);
		m_fileFormatLabels.addElement(fileFormatLabel);
		
		JComboBox<String> pluginListComboBox = new JComboBox<String>(pluginNames);
		pluginListComboBox.setSelectedIndex(1);
		pluginListComboBox.addActionListener(this);
		m_mainPanel.add(pluginListComboBox);
		m_pluginListComboBoxes.addElement(pluginListComboBox);
		
		JTextField customPluginTextField = new JTextField();
		customPluginTextField.setEnabled(pluginListComboBox.getSelectedIndex() == pluginListComboBox.getItemCount() - 1);
		m_mainPanel.add(customPluginTextField);
		m_customPluginTextFields.addElement(customPluginTextField);
		
		updateLayout();
		
		return true;
	}
	
	public void close() {
		reset();
		
		m_cancelled = false;
		
		setVisible(false);
	}
	
	private void cancel() {
		reset();
		
		m_cancelled = true;
		
		setVisible(false);
	}
	
	public void clear() {
		for(int i=0;i<m_pluginListComboBoxes.size();i++) {
			m_pluginListComboBoxes.elementAt(i).setSelectedIndex(0);
		}
		
		for(int i=0;i<m_customPluginTextFields.size();i++) {
			m_customPluginTextFields.elementAt(i).setText("");
		}
	}
	
	public void reset() {
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
		
		setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == m_submitButton) {
			submit();
		}
		else if(e.getSource() == m_addButton) {
			addEntryPrompt();
		}
		else if(e.getSource() == m_clearButton) {
			clear();
		}
		else if(e.getSource() == m_cancelButton) {
			cancel();
		}
		else {
			for(int i=0;i<m_pluginListComboBoxes.size();i++) {
				m_customPluginTextFields.elementAt(i).setEnabled(m_pluginListComboBoxes.elementAt(i).getSelectedIndex() == m_pluginListComboBoxes.elementAt(i).getItemCount() - 1);

				if(m_pluginListComboBoxes.elementAt(i).getSelectedIndex() >= 0 && m_pluginListComboBoxes.elementAt(i).getSelectedIndex() < m_pluginListComboBoxes.elementAt(i).getItemCount() - 1) {
					if(m_pluginListComboBoxes.elementAt(i).getSelectedIndex() == 0) {
						m_customPluginTextFields.elementAt(i).setText("");
					}
					else {
						m_customPluginTextFields.elementAt(i).setText(m_pluginListComboBoxes.elementAt(i).getSelectedItem().toString());
					}
				}
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
		
		int buttonHeight = 0;
		if(m_submitButton.getHeight() > buttonHeight) { buttonHeight = m_submitButton.getHeight(); }
		if(m_addButton.getHeight()    > buttonHeight) { buttonHeight = m_addButton.getHeight();    }
		if(m_clearButton.getHeight()  > buttonHeight) { buttonHeight = m_clearButton.getHeight();  }
		if(m_cancelButton.getHeight() > buttonHeight) { buttonHeight = m_cancelButton.getHeight(); }
		
		int buttonAreaWidth = m_submitButton.getWidth() + m_clearButton.getWidth() + m_cancelButton.getWidth() + (padding * 2);
		
		int panelWidth = maxLabelWidth + maxComboBoxWidth + maxTextFieldWidth + (padding * 4);
		int panelHeight = (m_fileFormatLabels.size() * (elementHeight + padding)) + buttonHeight + (padding * 2);

		int buttonXPosition = (panelWidth / 2) - (buttonAreaWidth / 2);
		m_submitButton.setLocation(buttonXPosition, elementYPosition);
		buttonXPosition += m_submitButton.getWidth() + padding;
		m_addButton.setLocation(buttonXPosition, elementYPosition);
		buttonXPosition += m_addButton.getWidth() + padding;
		m_clearButton.setLocation(buttonXPosition, elementYPosition);
		buttonXPosition += m_clearButton.getWidth() + padding;
		m_cancelButton.setLocation(buttonXPosition, elementYPosition);
		
		m_mainPanel.setPreferredSize(new Dimension(panelWidth, panelHeight));
		m_mainPanel.setSize(getPreferredSize());
		
		pack();

		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		
		setLocation((d.width / 2) - (getWidth() / 2), (d.height / 2) - (getHeight() / 2));
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
				
				if(formattedPreferredPluginName.isEmpty()) {
					formattedPreferredPluginName = null;
				}
			}
			
			if(formattedPreferredPluginName == null) {
				EditorPluginManager.instance.removePreferredPluginForFileFormat(m_fileFormatLabels.elementAt(i).getText(), FilePlugin.class);
			}
			else {
				EditorPluginManager.instance.setPreferredPluginForFileFormat(m_fileFormatLabels.elementAt(i).getText(), formattedPreferredPluginName, FilePlugin.class);
			}
		}
	}
	
	private void loadPreferredPlugins() {
		reset();
		
		Vector<String> fileFormats = EditorPluginManager.instance.getSupportedAndPreferredFileFormats();
		
		String fileFormat = null;
		JLabel fileFormatLabel = null;
		JComboBox<String> pluginListComboBox = null;
		JTextField customPluginTextField = null;
		Vector<FilePlugin> plugins = null;
		String palettePluginNames[] = null;
		int preferredPluginIndex = -1;
		String preferredPluginName = null;
		for(int i=0;i<fileFormats.size();i++) {
			fileFormat = fileFormats.elementAt(i);
			
			plugins = EditorPluginManager.instance.getPluginsForFileFormat(fileFormat);
			if(plugins == null || plugins.isEmpty()) { continue; }
			
			preferredPluginIndex = -1;
			preferredPluginName = EditorPluginManager.instance.getPreferredPluginForFileFormat(fileFormat, FilePlugin.class);
			
			palettePluginNames = new String[plugins.size() + 2];
			palettePluginNames[0] = "None";
			palettePluginNames[palettePluginNames.length - 1] = "Other";
			for(int j=0;j<plugins.size();j++) {
				palettePluginNames[j + 1] = plugins.elementAt(j).getName();
				
				if(plugins.elementAt(j).getName().equalsIgnoreCase(preferredPluginName)) {
					preferredPluginIndex = j;
				}
			}
			
			fileFormatLabel = new JLabel(fileFormat);
			m_mainPanel.add(fileFormatLabel);
			m_fileFormatLabels.addElement(fileFormatLabel);
			
			pluginListComboBox = new JComboBox<String>(palettePluginNames);
			pluginListComboBox.setSelectedIndex(preferredPluginName == null ? 0 : preferredPluginIndex < 0 ? palettePluginNames.length - 1 : preferredPluginIndex + 1);
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
