package exception;

public class GroupWriteException extends Exception {
	
	private static final long serialVersionUID = -8002235844467408805L;

	public GroupWriteException() {
		super();
	}
	
	public GroupWriteException(String message) {
		super(message);
	}
	
	public GroupWriteException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public GroupWriteException(Throwable cause) {
		super(cause);
	}
	
}
