package pt.ulisboa.tecnico.bubbledocs.exceptions;

public class UserAlreadyExistsException extends BubbledocsException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UserAlreadyExistsException(String message) {
		super(message);
	}
}
