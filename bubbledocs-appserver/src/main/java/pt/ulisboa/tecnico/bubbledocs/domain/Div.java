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
	protected final int myValue() {
		return getArgumentOne().calculate() / getArgumentTwo().calculate();		
	}
	
    @Override
    public boolean equals(Content other) {
    	if(!(other instanceof Div))
    		return false;
    	else return getArgumentOne().equals(((Div)other).getArgumentOne()) &&
    		    	getArgumentTwo().equals(((Div)other).getArgumentTwo());
    }

}
