package pt.ulisboa.tecnico.bubbledocs.domain;

import pt.ulisboa.tecnico.bubbledocs.exceptions.CellDivisionByZeroException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidCellException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidReferenceException;

public class Sub extends Sub_Base {

	public Sub() {
		super();
	}
	
    public Sub(SimpleContent arg1, SimpleContent arg2) {
    	super();
    	init(arg1, arg2);
    }
	
	@Override
	protected final Integer calculate() throws InvalidCellException, InvalidReferenceException, CellDivisionByZeroException {
		return getArgumentOne().calculate() - getArgumentTwo().calculate();		
	}
	
    @Override
    public boolean equals(Content other) {
    	if(!(other instanceof Sub))
    		return false;
    	else return getArgumentOne().equals(((Sub)other).getArgumentOne()) &&
    		    	getArgumentTwo().equals(((Sub)other).getArgumentTwo());
    }
	
}
