package pt.ulisboa.tecnico.bubbledocs.domain;

import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidCellException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidImportException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidReferenceException;

//an abstract class
public abstract class Content extends Content_Base {
    
	private final String INVALID = "#VALUE";
	
	/**
	 * This constructor should never be called
	 */
    Content() { 
        super();
    }

  
    /**
     * Template Method for getting a value of a Content
     * @return the Value of the content, null if a Invalid Reference was found
     */
    protected final Integer calculate() {
    	Integer value;
    	try {
    		value = myValue();
    	} catch (InvalidCellException e) {
    		return null;
    	} catch (InvalidReferenceException e) {
			return null;
		}
    	return value;
    }
    
    /**
     * To be defined by the concrete subclasses
     * @throws InvalidCellException 
     * @throws InvalidReferenceException 
     */
    protected abstract int myValue() throws InvalidCellException, InvalidReferenceException;
    
    
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
    	Integer val = calculate();
    	if(val == null) {
    		return INVALID;
    	} return val.toString();
    }
}
