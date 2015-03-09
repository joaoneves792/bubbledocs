package pt.ulisboa.tecnico.bubbledocs.domain;

import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidCellException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidImportException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidReferenceException;

//an abstract class
public abstract class Content extends Content_Base {
    
	
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
    protected final Integer getValue() {
    	Integer value;
    	try {
    		value = __getValue__();
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
