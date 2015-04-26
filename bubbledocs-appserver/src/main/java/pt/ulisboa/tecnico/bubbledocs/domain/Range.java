package pt.ulisboa.tecnico.bubbledocs.domain;

import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidCellException;
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
     * @throws InvalidCellException 
     */
    void init(Reference ref1, Reference ref2) throws InvalidCellException {
    	if(ref1.getCell().getSpreadsheet().getId() != ref2.getCell().getSpreadsheet().getId())
    		throw new InvalidCellException("Range references point to different spreadsheets.");
    	
    	setReferenceOne(ref1);
    	setReferenceOne(ref2);
    }
    
    /**
     * To be defined by the concrete subclasses
     */
    protected abstract int myValue() throws InvalidCellException;
    
    
    /**
     * pseudo-constructor for initializing a content from an XML element
     * @param XML JDOM element for this content
     * @throws InvalidCellException 
     */
    protected final void init(org.jdom2.Element rangeElement, Spreadsheet sheet) throws InvalidImportException, InvalidCellException {
    	org.jdom2.Element el = rangeElement.getChild("ReferenceOne").getChildren().get(0);
    	String contentName = el.getName();
    	if(contentName.equals("Reference")) {
			Reference ref = new Reference();
			ref.init(el, sheet);
			setReferenceOne(ref);
		}
		el = rangeElement.getChild("ReferenceTwo").getChildren().get(0);
	    contentName = el.getName();
		if(contentName.equals("Reference")) {
			Reference ref = new Reference();
			ref.init(el, sheet);
			setReferenceTwo(ref);
		}
    }
    
	/**
	 * @return JDOM element for this class
	 * @throws InvalidCellException 
	 */
	protected final org.jdom2.Element export() throws InvalidCellException {
		org.jdom2.Element binElement = new org.jdom2.Element(this.getClass().getSimpleName()),
    			referenceOneElement  = new org.jdom2.Element("ReferenceOne"),
    			referenceTwoElement  = new org.jdom2.Element("ReferenceTwo");
    	
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
    	/*SimpleContent ref1 = getReferenceOne(),
			          ref2 = getReferenceTwo();*/
	
    	setReferenceOne(null); //ref1.clean();
    	setReferenceTwo(null); //ref2.clean();   	
	
		super.deleteDomainObject();
    }
}
