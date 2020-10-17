package gui;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import utilities.*;

public class ResizeAnchorPanel extends JPanel implements Updatable, Resettable, ActionListener {

	protected ArrowDirection m_resizeAnchor;
	protected ArrowButton m_arrowButtons[][];
	protected JLabel m_titleLabel;
	protected boolean m_showLabel;
	protected Dimension m_dimensions;
	protected Vector<ResizeAnchorChangeListener> m_resizeAnchorChangeListeners;

	private static final long serialVersionUID = 2017545155575792691L;

	public ResizeAnchorPanel() {
		this(true);
	}

	public ResizeAnchorPanel(boolean showLabel) {
		setLayout(null);
		setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

		m_resizeAnchor = ArrowDirection.Center;
		m_showLabel = showLabel;
		m_dimensions = new Dimension(0, 0);
		m_resizeAnchorChangeListeners = new Vector<ResizeAnchorChangeListener>();

		initComponents();
	}
	
	private void initComponents() {
		if(m_showLabel) {
			m_titleLabel = new JLabel("Resize Anchor");
			m_titleLabel.setHorizontalAlignment(JLabel.CENTER);
			add(m_titleLabel);
		}

		m_arrowButtons = new ArrowButton[][] {
			{
				new ArrowButton(ArrowDirection.NorthWest),
				new ArrowButton(ArrowDirection.North),
				new ArrowButton(ArrowDirection.NorthEast)
			},
			{
				new ArrowButton(ArrowDirection.West),
				new ArrowButton(ArrowDirection.Center),
				new ArrowButton(ArrowDirection.East)
			},
			{
				new ArrowButton(ArrowDirection.SouthWest),
				new ArrowButton(ArrowDirection.South),
				new ArrowButton(ArrowDirection.SouthEast)
			}
		};
		
		ArrowButton arrowButton = null;
		
		for(int i = 0; i < m_arrowButtons.length; i++) {
			for(int j = 0; j < m_arrowButtons[i].length; j++) {
				arrowButton = m_arrowButtons[i][j];
				arrowButton.addActionListener(this);
				
				add(arrowButton);
			}
		}
		
		updateLayout();
	}
	
	public void setEnabled(boolean enabled) {
		boolean wasEnabled = isEnabled();
	
		super.setEnabled(enabled);

		if(enabled != wasEnabled) {
			for(int i = 0; i < m_arrowButtons.length; i++) {
				for(int j = 0; j < m_arrowButtons[i].length; j++) {
					m_arrowButtons[i][j].setEnabled(enabled);
				}
			}
		}
	}

	public void reset() {
		for(int i = 0; i < m_arrowButtons.length; i++) {
			for(int j = 0; j < m_arrowButtons[i].length; j++) {
				m_arrowButtons[i][j].reset();
			}
		}
	}

	public void update() {
		ArrowButton arrowButton = null;

		for(int i = 0; i < m_arrowButtons.length; i++) {
			for(int j = 0; j < m_arrowButtons[i].length; j++) {
				arrowButton = m_arrowButtons[i][j];

				if(arrowButton.getOriginalDirection() == m_resizeAnchor) {
					arrowButton.setDirection(ArrowDirection.Center);
			
					if(j == 0) {
						for(int k = 0; k < 3; k++) {
							m_arrowButtons[k][2].setDirection(ArrowDirection.None);
						}
					}
					else {
						m_arrowButtons[i][j - 1].setDirection(ArrowDirection.West);
					}
			
					if(j == 2) {
						for(int k = 0; k < 3; k++) {
							m_arrowButtons[k][0].setDirection(ArrowDirection.None);
						}
					}
					else {
						m_arrowButtons[i][j + 1].setDirection(ArrowDirection.East);
					}
			
					if(i == 0) {
						for(int k = 0; k < 3; k++) {
							m_arrowButtons[2][k].setDirection(ArrowDirection.None);
						}
					}
					else {
						m_arrowButtons[i - 1][j].setDirection(ArrowDirection.North);
						
						if(j != 0) {
							m_arrowButtons[i - 1][j - 1].setDirection(ArrowDirection.NorthWest);
						}
			
						if(j != 2) {
							m_arrowButtons[i - 1][j + 1].setDirection(ArrowDirection.NorthEast);
						}
					}
			
					if(i == 2) {
						for(int k = 0; k < 3; k++) {
							m_arrowButtons[0][k].setDirection(ArrowDirection.None);
						}
					}
					else {
						m_arrowButtons[i + 1][j].setDirection(ArrowDirection.South);
			
						if(j != 0) {
							m_arrowButtons[i + 1][j - 1].setDirection(ArrowDirection.SouthWest);
						}
						
						if(j != 2) {
							m_arrowButtons[i + 1][j + 1].setDirection(ArrowDirection.SouthEast);
						}
					}
					
					break;
				}
			}
		}
	}

	public void updateLayout() {
		int padding = 4;
		int spacing = 2;
		int x = padding;
		int y = padding;
		int totalWidth = 0;
		int totalHeight = 0;
		ArrowButton arrowButton = null;
		
		if(m_showLabel) {
			m_titleLabel.setLocation(x, y);

			y += m_titleLabel.getPreferredSize().height + spacing;
		}

		for(int i = 0; i < m_arrowButtons.length; i++) {
			for(int j = 0; j < m_arrowButtons[i].length; j++) {
				arrowButton = m_arrowButtons[i][j];
	
				arrowButton.setSize(arrowButton.getPreferredSize());
				arrowButton.setLocation(x, y);
	
				x += arrowButton.getWidth() + spacing;
			}
			
			totalWidth = x + padding - spacing;
			x = padding;
			y += arrowButton.getHeight() + spacing;
			totalHeight = y + padding - spacing;
		}

		if(m_showLabel) {
			m_titleLabel.setSize(totalWidth - (padding * 2), m_titleLabel.getPreferredSize().height);
		}

		m_dimensions = new Dimension(totalWidth, totalHeight);
		setSize(m_dimensions);

	}

	public Dimension getPreferredSize() {
		return m_dimensions;
	}

	public void actionPerformed(ActionEvent e) {
		ArrowButton arrowButton = null;

		for(int i = 0; i < m_arrowButtons.length; i++) {
			for(int j = 0; j < m_arrowButtons[i].length; j++) {
				arrowButton = m_arrowButtons[i][j];

				if(e.getSource() == arrowButton) {
					m_resizeAnchor = arrowButton.getOriginalDirection();
					
					update();
					
					notifyResizeAnchorChanged();

					break;
				}
			}
		}
	}

	public int numberOfResizeAnchorChangeListeners() {
		return m_resizeAnchorChangeListeners.size();
	}
	
	public ResizeAnchorChangeListener getResizeAnchorChangeListener(int index) {
		if(index < 0 || index >= m_resizeAnchorChangeListeners.size()) { return null; }
		
		return m_resizeAnchorChangeListeners.elementAt(index);
	}
	
	public boolean hasResizeAnchorChangeListener(ResizeAnchorChangeListener c) {
		return m_resizeAnchorChangeListeners.contains(c);
	}
	
	public int indexOfResizeAnchorChangeListener(ResizeAnchorChangeListener c) {
		return m_resizeAnchorChangeListeners.indexOf(c);
	}
	
	public boolean addResizeAnchorChangeListener(ResizeAnchorChangeListener c) {
		if(c == null || m_resizeAnchorChangeListeners.contains(c)) { return false; }
		
		m_resizeAnchorChangeListeners.add(c);
		
		return true;
	}
	
	public boolean removeResizeAnchorChangeListener(int index) {
		if(index < 0 || index >= m_resizeAnchorChangeListeners.size()) { return false; }
		
		m_resizeAnchorChangeListeners.remove(index);
		
		return true;
	}
	
	public boolean removeResizeAnchorChangeListener(ResizeAnchorChangeListener c) {
		if(c == null) { return false; }
		
		return m_resizeAnchorChangeListeners.remove(c);
	}
	
	public void clearResizeAnchorChangeListeners() {
		m_resizeAnchorChangeListeners.clear();
	}
	
	public void notifyResizeAnchorChanged() {
		for(int i=0;i<m_resizeAnchorChangeListeners.size();i++) {
			m_resizeAnchorChangeListeners.elementAt(i).handleResizeAnchorChange(m_resizeAnchor);
		}
	}

}
