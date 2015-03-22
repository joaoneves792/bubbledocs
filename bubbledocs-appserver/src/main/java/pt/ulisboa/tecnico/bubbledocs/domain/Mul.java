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
	protected int myValue() {
		return getArgumentOne().calculate() * getArgumentTwo().calculate();	
	}	
	
    @Override
    public boolean equals(Content other) {
    	if(!(other instanceof Mul))
    		return false;
    	else return getArgumentOne().equals(((Mul)other).getArgumentOne()) &&
    		    	getArgumentTwo().equals(((Mul)other).getArgumentTwo());
    }
	
}
