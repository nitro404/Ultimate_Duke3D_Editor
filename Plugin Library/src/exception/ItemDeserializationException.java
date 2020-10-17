package exception;

public class ItemDeserializationException extends DeserializationException {
	
	private static final long serialVersionUID = -5132873938682543459L;

	public ItemDeserializationException() {
		super();
	}
	
	public ItemDeserializationException(String message) {
		super(message);
	}
	
	public ItemDeserializationException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public ItemDeserializationException(Throwable cause) {
		super(cause);
	}
	
}
