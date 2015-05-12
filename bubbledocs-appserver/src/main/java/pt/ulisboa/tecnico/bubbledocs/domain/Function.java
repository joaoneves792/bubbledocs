package pt.ulisboa.tecnico.bubbledocs.domain;

import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidCellException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidImportException;

//an abstract class
public abstract class Function extends Function_Base {
    
	/**
	 * This constructor should never be called
	 */
    Function() {
        super();
    }
    
    /**
     * Defines XML element for this class
     */    
    protected abstract org.jdom2.Element export() throws InvalidCellException;
    
    /**
     * pseudo-constructor for initializing a content from an XML element
     * @param XML JDOM element for this content
     * @throws InvalidImportException 
     * @throws InvalidCellException 
     */
    protected abstract void init(org.jdom2.Element el, Spreadsheet sheet) throws InvalidImportException, InvalidCellException;
}
