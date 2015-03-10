package pt.ulisboa.tecnico.bubbledocs.domain;

public class Mul extends Mul_Base {
 
	public Mul() {
		super();
	}
	
    public Mul(SimpleContent arg1, SimpleContent arg2) {
    	super();
    	init(arg1, arg2);
    }
	
	@Override
	protected int __getValue__() {
		return getArgumentOne().getValue() * getArgumentTwo().getValue();	
	}	
	
}
