package exception;

public class MaxSpritesExceededException extends Exception {

	private static final long serialVersionUID = -8289141569906681123L;

	public MaxSpritesExceededException() {
		super();
	}
	
	public MaxSpritesExceededException(String message) {
		super(message);
	}
	
	public MaxSpritesExceededException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public MaxSpritesExceededException(Throwable cause) {
		super(cause);
	}
	
}
