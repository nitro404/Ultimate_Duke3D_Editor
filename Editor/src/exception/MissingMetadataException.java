package exception;

public class MissingMetadataException extends PluginLoadException {
	
	private static final long serialVersionUID = -3700865567263384665L;

	public MissingMetadataException() {
		super();
	}
	
	public MissingMetadataException(String message) {
		super(message);
	}
	
	public MissingMetadataException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public MissingMetadataException(Throwable cause) {
		super(cause);
	}
	
}
