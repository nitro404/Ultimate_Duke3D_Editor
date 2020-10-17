package exception;

public class GroupReadException extends ItemReadException {
	
	private static final long serialVersionUID = -6975368443455104963L;

	public GroupReadException() {
		super();
	}
	
	public GroupReadException(String message) {
		super(message);
	}
	
	public GroupReadException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public GroupReadException(Throwable cause) {
		super(cause);
	}
	
}
