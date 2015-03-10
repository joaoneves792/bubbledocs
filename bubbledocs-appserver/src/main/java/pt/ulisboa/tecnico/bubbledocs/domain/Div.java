package pt.ulisboa.tecnico.bubbledocs.domain;

public class Div extends Div_Base {
    
	public Div() {
		super();
	}
	
    public Div(SimpleContent arg1, SimpleContent arg2) {
    	super();
    	init(arg1, arg2);
    }
	
	@Override
	protected int __getValue__() {
		return arg1.getValue() / arg2.getValue();	
	}
	
	/**
	 * Defines XML element for this class
	 */
	@Override
	protected org.jdom2.Element export() {
		org.jdom2.Element el = new org.jdom2.Element("Div");
		el.addContent(arg1.export());
		el.addContent(arg2.export());
		return el;
	}


}
