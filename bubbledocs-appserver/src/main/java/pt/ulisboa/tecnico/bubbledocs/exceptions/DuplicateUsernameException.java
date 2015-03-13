package pt.ulisboa.tecnico.bubbledocs.exceptions.;

public class DuplicateUsernameException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public DuplicateUsernameException (String m) {
		super(m);
	}

}
