package exception;

public class MaxWallsExceededException extends Exception {

	private static final long serialVersionUID = -3218152227622699262L;

	public MaxWallsExceededException() {
		super();
	}
	
	public MaxWallsExceededException(String message) {
		super(message);
	}
	
	public MaxWallsExceededException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public MaxWallsExceededException(Throwable cause) {
		super(cause);
	}
	
}
