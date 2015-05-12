package pt.ulisboa.tecnico.bubbledocs.domain;

import pt.ulisboa.tecnico.bubbledocs.exceptions.CellDivisionByZeroException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidCellException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidReferenceException;

public class Add extends Add_Base {
    
    public Add() {
        super();
    }

    public Add(SimpleContent arg1, SimpleContent arg2) {
    	super();
    	init(arg1, arg2);
    }
    
    @Override
	protected final Integer calculate() throws InvalidCellException, InvalidReferenceException, CellDivisionByZeroException {
		return getArgumentOne().calculate() + getArgumentTwo().calculate();		
	}
    
    @Override
    public boolean equals(Content other) {
    	if(!(other instanceof Add))
    		return false;
    	else return getArgumentOne().equals(((Add)other).getArgumentOne()) &&
    		    	getArgumentTwo().equals(((Add)other).getArgumentTwo());
    }
}
