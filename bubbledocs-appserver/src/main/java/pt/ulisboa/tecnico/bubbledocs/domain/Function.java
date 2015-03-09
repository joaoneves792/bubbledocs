package pt.ulisboa.tecnico.bubbledocs.domain;

import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidCellException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidImportException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidReferenceException;

//an abstract class
public abstract class Function extends Function_Base {
    
	/**
	 * This constructor should never be called
	 */
    Function() {
        super();
    }
    
    /**
     * To be defined by the concrete subclasses
     */
    protected abstract int __getValue__() throws InvalidCellException, InvalidReferenceException;    
    
    /**
     * Defines XML element for this class
     */    
    protected abstract org.jdom2.Element export();
    
    /**
     * pseudo-constructor for initializing a content from an XML element
     * @param XML JDOM element for this content
     * @throws InvalidImportException 
     */
    protected abstract void init(org.jdom2.Element el) throws InvalidImportException;
}
