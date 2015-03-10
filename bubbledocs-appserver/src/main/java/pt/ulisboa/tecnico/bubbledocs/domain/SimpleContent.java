package pt.ulisboa.tecnico.bubbledocs.domain;

import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidCellException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidImportException;
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
     * @throws InvalidCellException 
     */
    protected abstract org.jdom2.Element export() throws InvalidCellException;
    
    /**
     * pseudo-constructor for initializing a content from an XML element
     * @param XML JDOM element for this content
     */
    protected abstract void init(org.jdom2.Element el) throws InvalidImportException;
    
}
