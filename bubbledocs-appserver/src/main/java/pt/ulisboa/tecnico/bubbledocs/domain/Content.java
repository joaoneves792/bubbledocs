package pt.ulisboa.tecnico.bubbledocs.domain;

import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidCellException;
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
    
	org.jdom2.Element export() {
		return new org.jdom2.Element("Content");
	}
}
