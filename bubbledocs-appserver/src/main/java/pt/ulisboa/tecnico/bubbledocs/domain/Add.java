package pt.ulisboa.tecnico.bubbledocs.domain;

public class Add extends Add_Base {
    
    public Add() {
        super();
    }

    public Add(SimpleContent arg1, SimpleContent arg2) {
    	super();
    	init(arg1, arg2);
    }
    
    @Override
	protected final int __getValue__() {
		return getArgumentOne().getValue() + getArgumentTwo().getValue();		
	}

}
