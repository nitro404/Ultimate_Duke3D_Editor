package gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.text.*;
import map.*;
import utilities.*;

public class OffsetMapSoundRangeDialog extends JDialog implements ActionListener, DocumentListener, Updatable {

	protected boolean m_submitted;
	protected boolean m_cancelled;
	protected boolean m_updatingUsingDocument;
	protected short m_soundRangeStart;
	protected short m_soundRangeEnd;
	protected short m_soundRangeOffset;
	protected JPanel m_mainPanel;
	protected JButton m_updateButton;
	protected JButton m_defaultsButton;
	protected JButton m_cancelButton;
	protected JLabel m_soundRangeStartLabel;
	protected JLabel m_soundRangeEndLabel;
	protected JLabel m_soundRangeOffsetLabel;
	protected JTextField m_soundRangeStartTextField;
	protected JTextField m_soundRangeEndTextField;
	protected JTextField m_soundRangeOffsetTextField;
	protected JLabel m_labels[];
	protected JTextField m_textFields[];
	protected Border m_defaultTextFieldBorder;

	protected static final short DEFAULT_SOUND_RANGE_START = 309;
	protected static final short DEFAULT_SOUND_RANGE_END = 397;
	protected static final short DEFAULT_SOUND_RANGE_OFFSET = 89;

	protected static final Border invalidTextFieldBorder = BorderFactory.createLineBorder(Color.RED);

	private static final long serialVersionUID = -1579065484467173734L;

	public OffsetMapSoundRangeDialog(Frame parent) {
		super(parent, true);

		setTitle("Offset Map Sound Range");
		setResizable(false);
		setDefaultCloseOperation(HIDE_ON_CLOSE);

		m_submitted = false;
		m_cancelled = false;
		m_updatingUsingDocument = false;
		m_defaultTextFieldBorder = null;

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
		reset();
	}
	
	protected void initComponents() {
		m_mainPanel = new JPanel();
		m_mainPanel.setLayout(null);

		m_updateButton = new JButton("Offset Sounds");
		m_updateButton.setSize(m_updateButton.getPreferredSize());
		m_updateButton.addActionListener(this);

		m_defaultsButton = new JButton("Defaults");
		m_defaultsButton.setSize(m_defaultsButton.getPreferredSize());
		m_defaultsButton.addActionListener(this);

		m_cancelButton = new JButton("Cancel");
		m_cancelButton.setSize(m_cancelButton.getPreferredSize());
		m_cancelButton.addActionListener(this);

		m_soundRangeStartLabel = new JLabel("Sound Range Start:");
		m_soundRangeEndLabel = new JLabel("Sound Range End:");
		m_soundRangeOffsetLabel = new JLabel("Sound Range Offset:");

		m_soundRangeStartTextField = new JTextField();
		m_soundRangeEndTextField = new JTextField();
		m_soundRangeOffsetTextField = new JTextField();

		m_labels = new JLabel[] {
			m_soundRangeStartLabel,
			m_soundRangeEndLabel,
			m_soundRangeOffsetLabel
		};

		m_textFields = new JTextField[] {
			m_soundRangeStartTextField,
			m_soundRangeEndTextField,
			m_soundRangeOffsetTextField
		};

		m_mainPanel.add(m_updateButton);
		m_mainPanel.add(m_defaultsButton);
		m_mainPanel.add(m_cancelButton);

		JLabel label = null;

		for(int i = 0; i < m_labels.length; i++) {
			label = m_labels[i];

			m_mainPanel.add(label);
		}

		JTextField textField = null;

		for(int i = 0; i < m_textFields.length; i++) {
			textField = m_textFields[i];

			textField.getDocument().addDocumentListener(this);
			m_mainPanel.add(textField);
		}
		
		setContentPane(m_mainPanel);
	}
	
	public boolean userSubmitted() {
		return m_submitted;
	}

	public boolean userCancelled() {
		return m_cancelled;
	}

	public short getSoundRangeStart() {
		return m_soundRangeStart;
	}

	protected static boolean isValidSoundRangeStart(short soundRangeStart) {
		return soundRangeStart >= 0 &&
			   soundRangeStart <= BuildConstants.MAX_NUMBER_OF_SOUNDS;
	}

	public void setSoundRangeStart(short soundRangeStart) throws IllegalArgumentException {
		if(!isValidSoundRangeStart(soundRangeStart)) {
			throw new IllegalArgumentException("Invalid sound range start, expected number between 0 and " + BuildConstants.MAX_NUMBER_OF_SOUNDS + ", inclusively.");
		}

		if(m_soundRangeStart == soundRangeStart) {
			return;
		}

		m_soundRangeStart = soundRangeStart;

		m_soundRangeStartTextField.setText(Short.toString(m_soundRangeStart));

		update();
	}

	public short getSoundRangeEnd() {
		return m_soundRangeEnd;
	}

	protected static boolean isValidSoundRangeEnd(short soundRangeEnd) {
		return soundRangeEnd >= 0 &&
			   soundRangeEnd <= BuildConstants.MAX_NUMBER_OF_SOUNDS;
	}

	public void setSoundRangeEnd(short soundRangeEnd) throws IllegalArgumentException {
		if(!isValidSoundRangeEnd(soundRangeEnd)) {
			throw new IllegalArgumentException("Invalid sound range end, expected number between 0 and " + BuildConstants.MAX_NUMBER_OF_SOUNDS + ", inclusively.");
		}

		if(m_soundRangeEnd == soundRangeEnd) {
			return;
		}

		m_soundRangeEnd = soundRangeEnd;

		m_soundRangeEndTextField.setText(Short.toString(m_soundRangeEnd));

		update();
	}

	protected static boolean isValidSoundRangeOffset(short soundRangeOffset) {
		return soundRangeOffset >= -BuildConstants.MAX_NUMBER_OF_SOUNDS &&
			   soundRangeOffset <= BuildConstants.MAX_NUMBER_OF_SOUNDS;
	}

	public short getSoundRangeOffset() {
		return m_soundRangeOffset;
	}

	public void setSoundRangeOffset(short soundRangeOffset) throws IllegalArgumentException {
		if(!isValidSoundRangeOffset(soundRangeOffset)) {
			throw new IllegalArgumentException("Invalid sound range offset, expected number between " + (-BuildConstants.MAX_NUMBER_OF_SOUNDS) + " and " + BuildConstants.MAX_NUMBER_OF_SOUNDS + ", inclusively.");
		}

		if(m_soundRangeOffset == soundRangeOffset) {
			return;
		}

		m_soundRangeOffset = soundRangeOffset;

		m_soundRangeOffsetTextField.setText(Short.toString(m_soundRangeOffset));

		update();
	}

	public void submit() {
		if(!isValidInput()) {
			return;
		}

		if(m_soundRangeStart > m_soundRangeEnd) {
			JOptionPane.showMessageDialog(SwingUtilities.getRoot(this), "Invalid sound range.", "Invalid Sound Range", JOptionPane.ERROR_MESSAGE);
			return;
		}

		if(m_soundRangeStart + m_soundRangeOffset < 0) {
			if(JOptionPane.showConfirmDialog(SwingUtilities.getRoot(this), "Sound offset is too low, may result in negative sound values.\nAre you sure you wish to continue?", "Low Sound Offset", JOptionPane.YES_NO_CANCEL_OPTION) != JOptionPane.YES_OPTION) {
				return;
			}
		}

		m_submitted = true;
		
		setVisible(false);
	}

	public void close() {
		m_cancelled = false;
		
		setVisible(false);
	}
	
	protected void cancel() {
		m_cancelled = true;
		
		setVisible(false);
	}

	public void reset() {
		setSoundRangeStart(DEFAULT_SOUND_RANGE_START);
		setSoundRangeEnd(DEFAULT_SOUND_RANGE_END);
		setSoundRangeOffset(DEFAULT_SOUND_RANGE_OFFSET);
	}

	public void clear() {
		setSoundRangeStart((short) 0);
		setSoundRangeEnd((short) 0);
		setSoundRangeOffset((short) 0);
	}
	
	public void display() {
		m_cancelled = false;
		m_submitted = false;

		setVisible(true);
	}

	public boolean isValidInput() {
		return isValidSoundRangeStart(m_soundRangeStart) &&
			   isValidSoundRangeEnd(m_soundRangeEnd) &&
			   isValidSoundRangeOffset(m_soundRangeOffset);
	}

	public void update() {
		m_updateButton.setEnabled(isValidInput());
	}

	protected void updateUsingDocument(Document document) {
		if(m_updatingUsingDocument || document == null) {
			return;
		}

		m_updatingUsingDocument = true;

		boolean validValue = false;
		boolean valueChanged = false;
		JTextField textField = null;

		if(m_soundRangeStartTextField.getDocument() == document) {
			textField = m_soundRangeStartTextField;

			try {
				short soundRangeStart = Short.parseShort(document.getText(0, document.getLength()));

				if(isValidSoundRangeStart(soundRangeStart)) {
					validValue = true;
					valueChanged = m_soundRangeStart != soundRangeStart;

					m_soundRangeStart = soundRangeStart;
				}
			}
			catch(NumberFormatException e) { }
			catch(BadLocationException e) { }
		}
		else if(m_soundRangeEndTextField.getDocument() == document) {
			textField = m_soundRangeEndTextField;

			try {
				short soundRangeEnd = Short.parseShort(document.getText(0, document.getLength()));

				if(isValidSoundRangeEnd(soundRangeEnd)) {
					validValue = true;
					valueChanged = m_soundRangeEnd != soundRangeEnd;

					m_soundRangeEnd = soundRangeEnd;
				}
			}
			catch(NumberFormatException e) { }
			catch(BadLocationException e) { }
		}
		else if(m_soundRangeOffsetTextField.getDocument() == document) {
			textField = m_soundRangeOffsetTextField;

			try {
				short soundRangeOffset = Short.parseShort(document.getText(0, document.getLength()));

				if(isValidSoundRangeOffset(soundRangeOffset)) {
					validValue = true;
					valueChanged = m_soundRangeOffset != soundRangeOffset;

					m_soundRangeOffset = soundRangeOffset;
				}
			}
			catch(NumberFormatException e) { }
			catch(BadLocationException e) { }
		}

		if(textField != null) {
			if(m_defaultTextFieldBorder == null) {
				m_defaultTextFieldBorder = textField.getBorder();
			}

			if(validValue) {
				textField.setBorder(m_defaultTextFieldBorder);
			}
			else {
				textField.setBorder(invalidTextFieldBorder);
			}
		}

		if(valueChanged) {
			update();
		}

		m_updatingUsingDocument = false;
	}

	public void updateLayout() {
		int padding = 4;
		Dimension preferredSize = null;

		int maxLabelWidth = 0;
		int maxLabelHeight = 0;
		for(int i = 0; i < m_labels.length; i++) {
			preferredSize = m_labels[i].getPreferredSize();

			if(preferredSize.width  > maxLabelWidth)  { maxLabelWidth  = preferredSize.width;  }
			if(preferredSize.height > maxLabelHeight) { maxLabelHeight = preferredSize.height; }
		}

		int maxTextFieldWidth = 152;
		int maxTextFieldHeight = 0;

		for(int i = 0; i < m_textFields.length; i++) {
			preferredSize = m_textFields[i].getPreferredSize();

			if(preferredSize.width  > maxTextFieldWidth)  { maxTextFieldWidth  = preferredSize.width;  }
			if(preferredSize.height > maxTextFieldHeight) { maxTextFieldHeight = preferredSize.height; }
		}

		int elementHeight = 0;
		if(maxLabelHeight > elementHeight)     { elementHeight = maxLabelHeight;     }
		if(maxTextFieldHeight > elementHeight) { elementHeight = maxTextFieldHeight; }

		int elementYPosition = padding;
		JLabel label = null;
		JTextField textField = null;

		for(int i = 0; i < m_labels.length; i++) {
			label = m_labels[i];
			textField = m_textFields[i];

			label.setPreferredSize(new Dimension(maxLabelWidth, elementHeight));
			label.setSize(label.getPreferredSize());
			label.setLocation(padding, elementYPosition);

			textField.setPreferredSize(new Dimension(maxTextFieldWidth, maxTextFieldHeight));
			textField.setSize(textField.getPreferredSize());
			textField.setLocation(maxLabelWidth + (padding * 3), elementYPosition);

			elementYPosition += elementHeight + padding;
		}

		int buttonHeight = 0;
		if(m_updateButton.getHeight() > buttonHeight) { buttonHeight = m_updateButton.getHeight(); }
		if(m_defaultsButton.getHeight() > buttonHeight) { buttonHeight = m_defaultsButton.getHeight(); }
		if(m_cancelButton.getHeight() > buttonHeight) { buttonHeight = m_cancelButton.getHeight(); }

		int buttonAreaWidth = m_updateButton.getWidth() + m_defaultsButton.getWidth() + m_cancelButton.getWidth() + (padding * 3);

		int panelWidth = maxLabelWidth + maxTextFieldWidth + (padding * 4);
		int panelHeight = (m_labels.length * (elementHeight + padding)) + buttonHeight + (padding * 2);

		int buttonXPosition = (panelWidth / 2) - (buttonAreaWidth / 2);
		m_updateButton.setLocation(buttonXPosition, elementYPosition);
		buttonXPosition += m_updateButton.getWidth() + padding;
		m_defaultsButton.setLocation(buttonXPosition, elementYPosition);
		buttonXPosition += m_defaultsButton.getWidth() + padding;
		m_cancelButton.setLocation(buttonXPosition, elementYPosition);

		m_mainPanel.setPreferredSize(new Dimension(panelWidth, panelHeight));
		m_mainPanel.setSize(getPreferredSize());
		
		pack();

		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();

		setLocation((d.width / 2) - (getWidth() / 2), (d.height / 2) - (getHeight() / 2));
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == m_updateButton) {
			submit();
		}
		else if(e.getSource() == m_defaultsButton) {
			reset();
		}
		else if(e.getSource() == m_cancelButton) {
			cancel();
		}
	}

	public void insertUpdate(final DocumentEvent e) {
		if(e == null || e.getDocument() == null) { return; }
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				updateUsingDocument(e.getDocument());
			}
		});
	}

	public void removeUpdate(final DocumentEvent e) {
		if(e == null || e.getDocument() == null) { return; }
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				updateUsingDocument(e.getDocument());
			}
		});
	}

	public void changedUpdate(final DocumentEvent e) {
		if(e == null || e.getDocument() == null) { return; }
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				updateUsingDocument(e.getDocument());
			}
		});
	}

}
