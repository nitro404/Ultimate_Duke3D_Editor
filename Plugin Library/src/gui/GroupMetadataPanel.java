package gui;

import java.awt.*;
import javax.swing.*;
import item.*;
import group.*;
import utilities.*;

public class GroupMetadataPanel extends JPanel implements ItemChangeListener, Updatable {
	
	protected Group m_group;
	protected JLabel m_fileTypeLabel;
	protected JLabel m_numberOfFilesLabel;
	protected JLabel m_groupFileSizeLabel;
	protected JLabel m_fileExtensionsLabel;

	protected JLabel m_labels[];
	
	private static final long serialVersionUID = 3942229742673801875L;
	
	public GroupMetadataPanel() {
		this(null);
	}
	
	public GroupMetadataPanel(Group group) {
		setLayout(new WrapLayout(WrapLayout.CENTER, 15, 10));
		setOpaque(false);

		initComponents();

		setGroup(group);
	}
	
	private void initComponents() {
		m_fileTypeLabel = new JLabel();
		m_numberOfFilesLabel = new JLabel();
		m_groupFileSizeLabel = new JLabel();
		m_fileExtensionsLabel = new JLabel();
		
		m_labels = new JLabel[] {
			m_fileTypeLabel,
			m_numberOfFilesLabel,
			m_groupFileSizeLabel,
			m_fileExtensionsLabel
		};

		add(m_fileTypeLabel);
		add(m_numberOfFilesLabel);
		add(m_groupFileSizeLabel);
		add(m_fileExtensionsLabel);
	}
	
	public void setGroup(Group group) {
		if(m_group == group) {
			return;
		}
		
		if(m_group != null) {
			m_group.removeItemChangeListener(this);
		}
		
		m_group = group;
		
		if(m_group != null) {
			m_group.addItemChangeListener(this);
		}
		
		update();
	}
	
	public void update() {
		if(m_group == null) {
			m_fileTypeLabel.setText("File Type: Unknown");
			m_numberOfFilesLabel.setText("Number of Files: N/A");
			m_groupFileSizeLabel.setText("Group File Size: N/A");
			m_fileExtensionsLabel.setText("File Extensions: N/A");
		}
		else {
			String fileExtensions = m_group.getFileExtensionsAsString();

			m_fileTypeLabel.setText("File Type: " + m_group.getFileType());
			m_numberOfFilesLabel.setText("Number of Files: " + m_group.numberOfFiles());
			m_groupFileSizeLabel.setText("Group File Size: " + m_group.getGroupFileSizeString());
			m_fileExtensionsLabel.setText("File Extensions: " + (fileExtensions.isEmpty() ? "None" : fileExtensions));
		}

		Component parent = getParent();

		if(parent != null) {
			Color textColour = Utilities.getHighContrastYIQColor(parent.getBackground());

			for(int i = 0; i < m_labels.length; i++) {
				m_labels[i].setForeground(textColour);
			}
		}
	}

	public void handleItemChange(Item item) {
		if(m_group != item) {
			return;
		}
		
		update();
	}
}
