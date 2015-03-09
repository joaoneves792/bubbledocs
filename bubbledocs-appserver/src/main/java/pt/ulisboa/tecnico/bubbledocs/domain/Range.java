package pt.ulisboa.tecnico.bubbledocs.domain;

import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidImportException;

//an abstract class
public abstract class Range extends Range_Base {
    
	/**
	 * This constructor should never be called
	 */
    Range() {
        super();
    }
    
    /**
     * This method is only to be inherited by the Avg and Prd Subclasses
     * @param A corner of the Submatrix of this range
     * @param A corner of the Submatrix of this range
     */
    void init(Reference ref1, Reference ref2) {
    	addReference(ref1);
    	addReference(ref2);
    }
    
    /**
     * To be defined by the concrete subclasses
     */
    protected abstract int __getValue__();
    
    /**
     * Defines XML element for this class
     */
    protected abstract org.jdom2.Element export();
    
    /**
     * pseudo-constructor for initializing a content from an XML element
     * @param XML JDOM element for this content
     */
    protected final void init(org.jdom2.Element rangeElement) throws InvalidImportException {
    	for(org.jdom2.Element el : rangeElement.getChildren()) {
    		Reference ref = new Reference();
    		ref.init(el);
    		addReference(ref);
    	}
    }
}
