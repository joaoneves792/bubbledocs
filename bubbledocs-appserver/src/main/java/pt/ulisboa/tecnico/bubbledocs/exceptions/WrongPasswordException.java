package pt.ulisboa.tecnico.bubbledocs.exceptions;

public class WrongPasswordException extends BubbledocsException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public WrongPasswordException(String m) {
		super(m);
	}
}
