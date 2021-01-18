package exception;

public class MapDeserializationException extends ItemDeserializationException {

	private static final long serialVersionUID = -2102502283244741981L;
	
	public MapDeserializationException() {
		super();
	}
	
	public MapDeserializationException(String message) {
		super(message);
	}
	
	public MapDeserializationException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public MapDeserializationException(Throwable cause) {
		super(cause);
	}

}
