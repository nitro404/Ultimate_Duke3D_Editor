package exception;

public class ItemReadException extends Exception {

	private static final long serialVersionUID = -4301731966658621363L;

	public ItemReadException() {
		super();
	}
	
	public ItemReadException(String message) {
		super(message);
	}
	
	public ItemReadException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public ItemReadException(Throwable cause) {
		super(cause);
	}
	
}
