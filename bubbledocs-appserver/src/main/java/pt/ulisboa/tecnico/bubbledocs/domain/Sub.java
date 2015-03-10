package pt.ulisboa.tecnico.bubbledocs.domain;

public class Sub extends Sub_Base {

	public Sub() {
		super();
	}
	
    public Sub(SimpleContent arg1, SimpleContent arg2) {
    	super();
    	init(arg1, arg2);
    }
	
	@Override
	protected final int __getValue__() {
		return getArgumentOne().getValue() - getArgumentTwo().getValue();		
	}
	
}
