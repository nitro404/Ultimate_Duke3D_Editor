package exception;

public class GroupPanelInstantiationException extends Exception {
	
	private static final long serialVersionUID = -2369602257337920741L;
	
	public GroupPanelInstantiationException() {
		super();
	}
	
	public GroupPanelInstantiationException(String message) {
		super(message);
	}
	
	public GroupPanelInstantiationException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public GroupPanelInstantiationException(Throwable cause) {
		super(cause);
	}
	
}
