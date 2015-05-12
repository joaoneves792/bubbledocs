package pt.ulisboa.tecnico.bubbledocs.domain;

import pt.ulisboa.tecnico.bubbledocs.exceptions.CellDivisionByZeroException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidCellException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidReferenceException;

public class Div extends Div_Base {
    
	public Div() {
		super();
	}
	
    public Div(SimpleContent arg1, SimpleContent arg2) {
    	super();
    	init(arg1, arg2);
    }
	
	@Override
	protected final Integer calculate() throws CellDivisionByZeroException, InvalidCellException, InvalidReferenceException {
		if(getArgumentTwo().calculate() == 0) throw new CellDivisionByZeroException("Division By Zero");
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
