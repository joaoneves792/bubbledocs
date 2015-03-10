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
    	setReferenceOne(ref1);
    	setReferenceOne(ref2);
    }
    
    /**
     * To be defined by the concrete subclasses
     */
    protected abstract int __getValue__();
    
    
    /**
     * pseudo-constructor for initializing a content from an XML element
     * @param XML JDOM element for this content
     */
    protected final void init(org.jdom2.Element rangeElement) throws InvalidImportException {
    	//FIXME LOLOLOLOL
    	org.jdom2.Element el = binElement.getChild("ReferenceOne").getChildren().get(0);
    	String contentName = el.getName();
		if(contentName.equals("Reference")) {
			Reference ref = new Reference();
			ref.init(el);
			setReferenceOne(ref);
		}
		el = binElement.getChild("ReferenceTwo").getChildren().get(0);
	    String contentName = el.getName();
		if(contentName.equals("Reference")) {
			Reference ref = new Reference();
			ref.init(el);
			setReferenceTwo(ref);
		}
    }
    
	/**
	 * @return JDOM element for this class
	 */
	protected final org.jdom2.Element export() {
		org.jdom2.Element binElement = new org.jdom2.Element(this.getClass().getName()),
    			referenceOneElement   = new org.jdom2.Element("ReferenceOne"),
    			referenceTwoElement   = new org.jdom2.Element("ReferenceTwo");
    	
		referenceOneElement.addContent(getReferenceOne().export());
		referenceTwoElement.addContent(getReferenceTwo().export());
    	
    	binElement.addContent(referenceOneElement);
    	binElement.addContent(referenceTwoElement);
    	
    	return binElement;
	}
 
    /**
     * Method to clean a range (from persistence)
     */
    public void clean(){
        //TODO IMPLEMENT ME
    }
}
