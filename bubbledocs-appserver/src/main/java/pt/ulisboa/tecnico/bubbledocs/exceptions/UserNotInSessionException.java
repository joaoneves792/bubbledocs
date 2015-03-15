package pt.ulisboa.tecnico.bubbledocs.exceptions;

public class UserNotInSessionException extends BubbledocsException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UserNotInSessionException(String m) {
		super(m);
	}
}
