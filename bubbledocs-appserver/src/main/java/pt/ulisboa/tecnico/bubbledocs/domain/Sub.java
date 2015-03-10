package pt.ulisboa.tecnico.bubbledocs.domain;

public class Sub extends Sub_Base {

	public Sub() {
		super();
	}
	
	@Override
	protected final int __getValue__() {
		return getArgumentOne().getValue() - getArgumentTwo().getValue();		
	}
	
}
