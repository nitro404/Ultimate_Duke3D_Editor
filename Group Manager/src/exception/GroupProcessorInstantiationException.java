package exception;

public class GroupProcessorInstantiationException extends Exception {
	
	private static final long serialVersionUID = 1465628701453307723L;
	
	public GroupProcessorInstantiationException() {
		super();
	}
	
	public GroupProcessorInstantiationException(String message) {
		super(message);
	}
	
	public GroupProcessorInstantiationException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public GroupProcessorInstantiationException(Throwable cause) {
		super(cause);
	}
	
}
