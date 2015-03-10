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
	protected final int __getValue__() {
		return getArgumentOne().getValue() / getArgumentTwo().getValue();		
	}

}
