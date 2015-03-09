package pt.ulisboa.tecnico.bubbledocs.domain;

//abstract class
//this should not be instanced
public abstract class Binary extends Binary_Base {
    
	/**
	 * This constructor should never be called
	 */
    Binary() {
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
    protected final void init(SimpleContent arg1, SimpleContent arg2) {
    	addArgument(arg1); this.arg1 = arg1;
    	addArgument(arg2); this.arg2 = arg2;
    }
    
    /**
     * To be defined by the concrete subclasses
     */
    protected abstract int __getValue__();
}
