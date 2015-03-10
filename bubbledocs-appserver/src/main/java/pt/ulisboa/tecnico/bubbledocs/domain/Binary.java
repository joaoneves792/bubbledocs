package pt.ulisboa.tecnico.bubbledocs.domain;

import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidCellException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidImportException;

//abstract class
//this should not be instanced
public abstract class Binary extends Binary_Base {
    
	/**
	 * This constructor should never be called
	 */
    public Binary() {
        super();
    }
        
    /**
     * This method is only to be inherited by the Reference or Literal Subclasses
     * @param Instanceof Literal or Reference
     * @param Instanceof Literal or Reference
     */
    public final void init(SimpleContent arg1, SimpleContent arg2) {
    	setArgumentOne(arg1); 
    	setArgumentTwo(arg2);
    }    
    
    /**
     * To be defined by the concrete subclasses
     */
    protected abstract int __getValue__();
    
    /**
     * @return JDOM element for this class
     * @throws InvalidCellException 
     */
    protected final org.jdom2.Element export() throws InvalidCellException {
    	org.jdom2.Element binElement = new org.jdom2.Element(this.getClass().getSimpleName()),
    			argumentOneElement   = new org.jdom2.Element("ArgumentOne"),
    			argumentTwoElement   = new org.jdom2.Element("ArgumentTwo");
    	
    	argumentOneElement.addContent(getArgumentOne().export());
    	argumentTwoElement.addContent(getArgumentTwo().export());
    	
    	binElement.addContent(argumentOneElement);
    	binElement.addContent(argumentTwoElement);
    	
    	return binElement;
    }
    
    /**
     * pseudo-constructor for initializing a content from an JDOM element
     * @param XML JDOM element for this content
     */
    protected final void init(org.jdom2.Element binElement) throws InvalidImportException {
    	org.jdom2.Element el = binElement.getChild("ArgumentOne").getChildren().get(0);
    	String contentName = el.getName();
		if(contentName.equals("Reference")) {
			Reference ref = new Reference();
			ref.init(el);
			setArgumentOne(ref);
		} else if(contentName.equals("Literal")) {
			Literal lit = new Literal();
			lit.init(el);
			setArgumentOne(lit);
		} else {
			throw new InvalidImportException("Attempted to Import Invalid Cell Content: " + contentName);
		}
		
		el = binElement.getChild("ArgumentTwo").getChildren().get(0);
		contentName = el.getName();
		if(contentName.equals("Reference")) {
			Reference ref = new Reference();
			ref.init(el);
			setArgumentTwo(ref);
		} else if(contentName.equals("Literal")) {
			Literal lit = new Literal();
			lit.init(el);
			setArgumentTwo(lit);
		} else {
			throw new InvalidImportException("Attempted to Import Invalid Cell Content: " + contentName);
		}		
    }
    
    /**
      * Method to erase this Binary Function (from persistence)
      */
    public final void clean(){
    	SimpleContent arg1 = getArgumentOne(),
    			      arg2 = getArgumentTwo();
    	
    	setArgumentOne(null); arg1.clean();
    	setArgumentTwo(null); arg2.clean();   	
    	
        super.deleteDomainObject();   
    }
}
