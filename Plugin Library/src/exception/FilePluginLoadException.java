package exception;

public class FilePluginLoadException extends PluginLoadException {
	
	private static final long serialVersionUID = -1406083065116664866L;

	public FilePluginLoadException() {
		super();
	}
	
	public FilePluginLoadException(String message) {
		super(message);
	}
	
	public FilePluginLoadException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public FilePluginLoadException(Throwable cause) {
		super(cause);
	}
	
}
