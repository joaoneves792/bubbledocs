package pt.ulisboa.tecnico.bubbledocs.domain;

public class Mul extends Mul_Base {
 
	public Mul() {
		super();
	}
	
	@Override
	protected int __getValue__() {
		return getArgumentOne().getValue() * getArgumentTwo().getValue();	
	}	
	
}
