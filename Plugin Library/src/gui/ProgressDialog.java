package gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import utilities.*;

public class ProgressDialog extends JDialog implements TaskListener, ActionListener {
	
	private boolean m_cancelled;
	private JPanel m_mainPanel;
	private JLabel m_messageLabel;
	private JProgressBar m_progressBar;
	private JButton m_cancelButton;
	private static final long serialVersionUID = -6769263380656386615L;
	
	public ProgressDialog(Frame parent) {
		super(parent, true);
		setTitle("Progress");
		setSize(260, 130);
		setResizable(false);
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		
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

		m_cancelled = false;
		
		initComponents();
	}
	
	private void initComponents() {
		m_mainPanel = new JPanel();
		
		m_messageLabel = new JLabel(" ");
		
		m_progressBar = new JProgressBar(0, 1);
		m_progressBar.setStringPainted(true);
		
		m_cancelButton = new JButton("Cancel");
		m_cancelButton.addActionListener(this);
		
		m_mainPanel.add(m_messageLabel);
		m_mainPanel.add(m_progressBar);
		m_mainPanel.add(m_cancelButton);
		
		setContentPane(m_mainPanel);
		
		initLayout();
	}
	
	private void initLayout() {
		GridBagLayout layout = new GridBagLayout();
		m_mainPanel.setLayout(layout);
		GridBagConstraints c;
		Insets insets = new Insets(6, 6, 6, 6);
		
		c = new GridBagConstraints();
		c.insets = insets;
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 5;
		c.gridheight = 1;
		c.weightx = 1;
		c.weighty = 0;
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		layout.setConstraints(m_messageLabel, c);
		
		c = new GridBagConstraints();
		c.insets = insets;
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 5;
		c.gridheight = 1;
		c.weightx = 1;
		c.weighty = 0;
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.HORIZONTAL;
		layout.setConstraints(m_progressBar, c);
		
		c = new GridBagConstraints();
		c.insets = insets;
		c.gridx = 4;
		c.gridy = 2;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.weightx = 0;
		c.weighty = 0;
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.NONE;
		layout.setConstraints(m_cancelButton, c);
	}
	
	public boolean userCancelled() {
		return m_cancelled;
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
		setTitle("Progress");
		m_messageLabel.setText("");
		m_progressBar.setMinimum(0);
		m_progressBar.setMaximum(1);
		m_progressBar.setValue(0);
	}
	
	public void display(String title, String message, int minimum, int maximum, Task task, Thread thread) {
		if(title == null || title.trim().isEmpty() || minimum < 0 || maximum <= minimum || task == null || task.isCancelled() || task.isCompleted()) { return; }
		
		setTitle(title.trim());
		
		m_messageLabel.setText(message == null ? " " : message.trim());
		
		m_progressBar.setMinimum(minimum);
		m_progressBar.setMaximum(maximum);
		m_progressBar.setValue(minimum);
		
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		
		setLocation((d.width / 2) - (getWidth() / 2), (d.height / 2) - (getHeight() / 2));
		
		if(task.isCompleted()) {
			close();
			return;
		}
		
		if(task.isCancelled()) {
			cancel();
			return;
		}
		
		if(thread != null && !thread.isAlive()) {
			thread.start();
		}
		
		setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == m_cancelButton) {
			cancel();
		}
	}
	
	public void taskProgressChanged(Task t) {
		m_progressBar.setValue(t.getProgress());
		
		if(t.isCompleted()) {
			close();
		}
	}
	
	public void taskCancelled(Task t) {
		cancel();
	}
	
}
