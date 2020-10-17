package exception;

public class ItemPanelInstantiationException extends Exception {
	
	private static final long serialVersionUID = 2400187426995924592L;

	public ItemPanelInstantiationException() {
		super();
	}
	
	public ItemPanelInstantiationException(String message) {
		super(message);
	}
	
	public ItemPanelInstantiationException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public ItemPanelInstantiationException(Throwable cause) {
		super(cause);
	}
	
}
