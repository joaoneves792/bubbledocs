package pt.ulisboa.tecnico.bubbledocs.domain;

import pt.ulisboa.tecnico.bubbledocs.exceptions.CellDivisionByZeroException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidCellException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidImportException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidReferenceException;

//an abstract class
public abstract class Content extends Content_Base {
    
	protected static final String INVALID = "#VALUE";
	
	/**
	 * This constructor should never be called
	 */
    Content() { 
        super();
    }
  
    /**
     * @return the Value of the content, null if a Invalid Reference was found
     * @throws CellDivisionByZeroException 
     * @throws InvalidReferenceException 
     * @throws InvalidCellException 
     */
    protected abstract Integer calculate() throws CellDivisionByZeroException, InvalidCellException, InvalidReferenceException ;
    
    /**
     * Defines XML element for this class
     * @throws InvalidCellException 
     */
    protected abstract org.jdom2.Element export() throws InvalidCellException;
    
    /**
     * pseudo-constructor for initializing a content from an XML element
     * @param XML JDOM element for this content
     * @throws InvalidImportException 
     * @throws InvalidCellException 
     */
    protected abstract void init(org.jdom2.Element el, Spreadsheet sheet) throws InvalidImportException, InvalidCellException;

    /**
     * Method to erase this Content (from persistence)
     */
    public final void clean() {
    	myClean();
    	super.deleteDomainObject();
    }
    
    protected abstract void myClean();
    
    /**
     * compare two contents
     * @param the content to compare against
     * @return true if and only if the contents have the same properties
     */
    public abstract boolean equals(Content other);
    
    @Override
    public final String toString() {
    	try {
			return calculate().toString();
		} catch (InvalidCellException | InvalidReferenceException | CellDivisionByZeroException e) {
			return INVALID;
		}
    }
}
