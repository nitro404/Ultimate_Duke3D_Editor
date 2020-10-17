package exception;

public class GroupProcessorPluginLoadException extends PluginLoadException {
	
	private static final long serialVersionUID = 2214655260618601246L;
	
	public GroupProcessorPluginLoadException() {
		super();
	}
	
	public GroupProcessorPluginLoadException(String message) {
		super(message);
	}
	
	public GroupProcessorPluginLoadException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public GroupProcessorPluginLoadException(Throwable cause) {
		super(cause);
	}
	
}
