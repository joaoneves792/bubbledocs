package pt.ulisboa.tecnico.bubbledocs.domain;

public class Mul extends Mul_Base {
 
	public Mul() {
		super();
	}
	
	@Override
	protected int __getValue__() {
		return arg1.getValue() * arg2.getValue();	
	}	
	
	/**
	 * Defines XML element for this class
	 */
	@Override
	protected org.jdom2.Element export() {
		org.jdom2.Element el = new org.jdom2.Element("Mul");
		el.addContent(arg1.export());
		el.addContent(arg2.export());
		return el;
	}

}
