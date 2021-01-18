package exception;

public class MapReadException extends ItemReadException {

	private static final long serialVersionUID = 5624164158814914898L;

	public MapReadException() {
		super();
	}

	public MapReadException(String message) {
		super(message);
	}

	public MapReadException(String message, Throwable cause) {
		super(message, cause);
	}

	public MapReadException(Throwable cause) {
		super(cause);
	}

}
