package pt.ulisboa.tecnico.bubbledocs.domain;

public class Add extends Add_Base {
    
    public Add() {
        super();
    }

    
    public Add(SimpleContent arg1, SimpleContent arg2) {
    	super();
    	init(arg1, arg2);
    }
    
    
	@Override
	protected final int __getValue__() {
		return arg1.getValue() + arg2.getValue();		
	}

	/**
	 * Defines XML element for this class
	 */
	//FIXME REVIEW THIS FOR SERIALIZATION
	@Override
	protected org.jdom2.Element export() {
		org.jdom2.Element el = new org.jdom2.Element("Add");
		el.addContent(arg1.export());
		el.addContent(arg2.export());
		return el;
	}

}
