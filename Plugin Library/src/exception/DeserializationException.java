package exception;

public class DeserializationException extends Exception {
	
	private static final long serialVersionUID = -5132873938682543459L;

	public DeserializationException() {
		super();
	}
	
	public DeserializationException(String message) {
		super(message);
	}
	
	public DeserializationException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public DeserializationException(Throwable cause) {
		super(cause);
	}
	
}
