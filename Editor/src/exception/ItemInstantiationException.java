package exception;

public class ItemInstantiationException extends Exception {
	
	private static final long serialVersionUID = 7201980351019097876L;

	public ItemInstantiationException() {
		super();
	}
	
	public ItemInstantiationException(String message) {
		super(message);
	}
	
	public ItemInstantiationException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public ItemInstantiationException(Throwable cause) {
		super(cause);
	}
	
}
