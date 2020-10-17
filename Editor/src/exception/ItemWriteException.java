package exception;

public class ItemWriteException extends Exception {
	
	private static final long serialVersionUID = -9145180291650046342L;

	public ItemWriteException() {
		super();
	}
	
	public ItemWriteException(String message) {
		super(message);
	}
	
	public ItemWriteException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public ItemWriteException(Throwable cause) {
		super(cause);
	}
	
}
