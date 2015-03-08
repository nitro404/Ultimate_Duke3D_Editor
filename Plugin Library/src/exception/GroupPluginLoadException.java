package exception;

public class GroupPluginLoadException extends PluginLoadException {
	
	private static final long serialVersionUID = 6188736673414933680L;

	public GroupPluginLoadException() {
		super();
	}
	
	public GroupPluginLoadException(String message) {
		super(message);
	}
	
	public GroupPluginLoadException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public GroupPluginLoadException(Throwable cause) {
		super(cause);
	}
	
}
