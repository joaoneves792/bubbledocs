package pt.ulisboa.tecnico.bubbledocs.domain;

import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidCellException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidReferenceException;

public abstract class SimpleContent extends SimpleContent_Base {

    protected SimpleContent() {
        super();
    }
    
    /**
     * To be defined by the concrete subclasses
     * @throws InvalidCellException 
     * @throws InvalidReferenceException 
     */
    protected abstract int __getValue__() throws InvalidCellException, InvalidReferenceException;
 
    /**
     * Defines XML element for this class
     */
    protected abstract org.jdom2.Element export();
}
