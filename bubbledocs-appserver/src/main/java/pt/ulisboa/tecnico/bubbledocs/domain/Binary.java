package pt.ulisboa.tecnico.bubbledocs.domain;

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
    
    /** These references guarantee the ordering of arguments */
    protected SimpleContent arg1;
    protected SimpleContent arg2;
    
    /**
     * This method is only to be inherited by the Reference or Literal Subclasses
     * @param Instanceof Literal or Reference
     * @param Instanceof Literal or Reference
     */
    public final void init(SimpleContent arg1, SimpleContent arg2) {
    	addArgument(arg1); this.arg1 = arg1;
    	addArgument(arg2); this.arg2 = arg2;
    }
    
    public Binary(SimpleContent arg1, SimpleContent arg2) {
    	super();
    	init(arg1, arg2);
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
	//FIXME REVIEW THIS FOR ARGUMENT ORDER
    protected final void init(org.jdom2.Element binElement) throws InvalidImportException {
    	for(org.jdom2.Element el : binElement.getChildren()) {
    		String contentName = el.getName();
    		if(contentName.equals("Reference")) {
    			Reference ref = new Reference();
    			ref.init(el);
    			addArgument(ref);
    		} else if(contentName.equals("Literal")) {
    			Literal lit = new Literal();
    			lit.init(el);
    			addArgument(lit);
    		} else {
    			throw new InvalidImportException("Attempted to Import Invalid Cell Content.");
    		}
    	}
    }
}
