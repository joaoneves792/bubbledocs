package pt.ulisboa.tecnico.bubbledocs.domain;

import pt.ulisboa.tecnico.bubbledocs.exceptions.CellDivisionByZeroException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidCellException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidReferenceException;

public class Mul extends Mul_Base {
 
	public Mul() {
		super();
	}
	
    public Mul(SimpleContent arg1, SimpleContent arg2) {
    	super();
    	init(arg1, arg2);
    }
	
	@Override
	protected Integer calculate() throws InvalidCellException, InvalidReferenceException, CellDivisionByZeroException {
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
