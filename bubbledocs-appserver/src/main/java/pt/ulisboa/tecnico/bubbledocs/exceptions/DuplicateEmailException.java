package pt.ulisboa.tecnico.bubbledocs.exceptions;

public class DuplicateEmailException extends BubbledocsException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DuplicateEmailException(String m) {
		super(m);
	}
}
