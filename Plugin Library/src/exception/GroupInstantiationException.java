package exception;

public class GroupInstantiationException extends Exception {
	
	private static final long serialVersionUID = 7555311198642639278L;

	public GroupInstantiationException() {
		super();
	}
	
	public GroupInstantiationException(String message) {
		super(message);
	}
	
	public GroupInstantiationException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public GroupInstantiationException(Throwable cause) {
		super(cause);
	}
	
}
