package gui;

import java.awt.*;
import javax.swing.*;
import item.*;

public abstract class ScrollableItemPanel extends ItemPanel implements Scrollable {
	
	private static final long serialVersionUID = -3971375740157707468L;

	public ScrollableItemPanel() {
		super(null);
	}
	
	public ScrollableItemPanel(Item item) {
		super(item);
	}
	
	public abstract Dimension getPreferredSize();

	public Dimension getPreferredScrollableViewportSize() {
		return getPreferredSize();
	}
	
	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
		int currentPosition = 0;
		if(orientation == SwingConstants.HORIZONTAL) {
			currentPosition = visibleRect.x;
		}
		else {
			currentPosition = visibleRect.y;
		}

		int maxUnitIncrement = 40;
		if(direction < 0) {
			int newPosition = currentPosition -
							  (currentPosition / maxUnitIncrement)
							  * maxUnitIncrement;
			return (newPosition == 0) ? maxUnitIncrement : newPosition;
		}
		else {
			return ((currentPosition / maxUnitIncrement) + 1)
				   * maxUnitIncrement
				   - currentPosition;
		}
	}
	
	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
		if(orientation == SwingConstants.HORIZONTAL) {
			return visibleRect.width - 5;
		}
		else {
			return visibleRect.height - 5;
		}
	}
	
	public boolean getScrollableTracksViewportWidth() {
		return false;
	}
	
	public boolean getScrollableTracksViewportHeight() {
		return false;
	}
	
}
