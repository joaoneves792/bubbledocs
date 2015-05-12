package pt.ulisboa.tecnico.bubbledocs.domain;

import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidCellException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidImportException;

public abstract class SimpleContent extends SimpleContent_Base {

    protected SimpleContent() {
        super();
    }
    
    /**
     * Defines XML element for this class
     * @throws InvalidCellException 
     */
    protected abstract org.jdom2.Element export() throws InvalidCellException;
    
    /**
     * pseudo-constructor for initializing a content from an XML element
     * @param XML JDOM element for this content
     * @throws InvalidCellException 
     */
    protected abstract void init(org.jdom2.Element el, Spreadsheet sheet) throws InvalidImportException, InvalidCellException;
    
}
