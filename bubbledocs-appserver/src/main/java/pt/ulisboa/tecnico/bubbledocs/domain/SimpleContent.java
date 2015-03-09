package pt.ulisboa.tecnico.bubbledocs.domain;

import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidCellException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidReferenceException;

//an abstract class
public abstract class SimpleContent extends SimpleContent_Base {

	/**
	 * This constructor should never be called
	 */
    SimpleContent() {
        super();
    }
    
    /**
     * To be defined by the concrete subclasses
     * @throws InvalidCellException 
     * @throws InvalidReferenceException 
     */
    protected abstract int __getValue__() throws InvalidCellException, InvalidReferenceException;
        
}
