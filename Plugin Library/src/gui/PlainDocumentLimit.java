package gui;

import javax.swing.text.*;

public class PlainDocumentLimit extends PlainDocument {
	
	protected int m_limit;
	
	private static final long serialVersionUID = 2507481891492018424L;
	
	public PlainDocumentLimit(int limit) {
		setLimit(limit);
	}
	
	public void setLimit(int limit) {
		m_limit = limit < 0 ? 0 : limit;
	}
	
	public void insertString(int offset, String data, AttributeSet attributes) throws BadLocationException {
		if(data == null || m_limit == 0) { return; }
		
		if(getLength() + data.length() <= m_limit) {
			super.insertString(offset, data, attributes);
		}
	}
	
}
