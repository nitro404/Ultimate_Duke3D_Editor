package gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import utilities.*;

public class TileResizeDialog extends JDialog implements Updatable, ActionListener {

	private static final long serialVersionUID = -2783476810493228354L;

	protected Frame m_parent;
	protected JPanel m_mainPanel;
	protected Dimension m_dimensions;
	protected Dimension m_previousDimensions;
	protected ResizeAnchorPanel m_resizeAnchorPanel;
	protected JLabel m_dimensionsLabel;
	protected JTextField m_widthTextField;
	protected JTextField m_heightTextField;
	protected JCheckBox m_relativeCheckbox;
	protected JCheckBox m_maintainAspectRatioCheckbox;
	protected JComboBox<String> m_resizeAlgorithmComboBox;
	protected JButton m_resizeButton;
	protected JButton m_cancelButton;

	public TileResizeDialog(Frame parent) {
		super(parent, true);

		setResizable(false);
// TODO: disable default layout manager
//		setLayout(null);
		setTitle("Resize Tile");
		setDefaultCloseOperation(HIDE_ON_CLOSE);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				close();
			}
		});

		getRootPane().registerKeyboardAction(
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					close();
				}
			},
			KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
			JComponent.WHEN_IN_FOCUSED_WINDOW
		);
		
		m_parent = parent;
		m_dimensions = new Dimension(0, 0);
		
		initComponents();
		update();
	}

	private void initComponents() {
		m_mainPanel = new JPanel();
		
		m_resizeAnchorPanel = new ResizeAnchorPanel();

		m_dimensionsLabel = new JLabel("Dimensions:");

		m_widthTextField = new JTextField();
// TODO: add listener

		m_heightTextField = new JTextField();
// TODO: add listener

		m_relativeCheckbox = new JCheckBox("Relative");

		m_maintainAspectRatioCheckbox = new JCheckBox("Maintain Aspect Ratio");

		m_resizeAlgorithmComboBox = new JComboBox<String>();
		m_resizeAlgorithmComboBox = new JComboBox<String>(ResizeAlgorithm.getValidDisplayNames());
		m_resizeAlgorithmComboBox.setSelectedIndex(ResizeAlgorithm.defaultResizeAlgorithm.ordinal());
		m_resizeAlgorithmComboBox.addActionListener(this);

		m_resizeButton = new JButton("Resize");
		m_resizeButton.addActionListener(this);

		m_cancelButton = new JButton("Cancel");
		m_cancelButton.addActionListener(this);

		m_mainPanel.add(m_resizeAnchorPanel);
		m_mainPanel.add(m_dimensionsLabel);
		m_mainPanel.add(m_widthTextField);
		m_mainPanel.add(m_heightTextField);
		m_mainPanel.add(m_relativeCheckbox);
		m_mainPanel.add(m_resizeAlgorithmComboBox);
		m_mainPanel.add(m_resizeButton);
		m_mainPanel.add(m_cancelButton);

		setContentPane(m_mainPanel);
	}

	public void update() {
// TODO: store local state and use it here:
boolean resizeCanvas = true;
boolean sizeChanged = false;

		m_resizeAnchorPanel.setEnabled(resizeCanvas);
		m_relativeCheckbox.setEnabled(resizeCanvas);
		m_resizeAlgorithmComboBox.setEnabled(!resizeCanvas);
		m_maintainAspectRatioCheckbox.setEnabled(!resizeCanvas);
		m_resizeButton.setEnabled(sizeChanged);

		updateLayout();
	}

	public void close() {
		clear();

		setVisible(false);
	}
	
	public void clear() {
		// TODO: clear resize tile dialog
	}
	
	public void display() {
		setVisible(true);
	}
	
	public void updateLayout() {
// TODO: update layout

		m_previousDimensions = m_dimensions;
		
		m_dimensions = new Dimension(320, 240);

		setSize(m_dimensions);
		
		if(!m_dimensions.equals(m_previousDimensions)) {
			Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
			
			setLocation((d.width / 2) - (m_dimensions.width / 2), (d.height / 2) - (m_dimensions.height / 2));
		}
	}

	public Dimension getPreferredSize() {
		return m_dimensions;
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == m_cancelButton) {
			close();
		}
	}

}
