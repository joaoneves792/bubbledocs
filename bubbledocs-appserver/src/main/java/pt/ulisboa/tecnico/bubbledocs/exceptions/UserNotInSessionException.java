package pt.ulisboa.tecnico.bubbledocs.exceptions;

public class UserNotInSessionException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UserNotInSessionException(String m) {
		super(m);
	}
}
