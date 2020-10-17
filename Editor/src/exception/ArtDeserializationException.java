package exception;

public class ArtDeserializationException extends ItemDeserializationException {
	
	private static final long serialVersionUID = -7506963311470781503L;

	public ArtDeserializationException() {
		super();
	}
	
	public ArtDeserializationException(String message) {
		super(message);
	}
	
	public ArtDeserializationException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public ArtDeserializationException(Throwable cause) {
		super(cause);
	}
	
}
