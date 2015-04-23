package pt.ulisboa.tecnico.bubbledocs.service.integrator;

import pt.ulisboa.tecnico.bubbledocs.exceptions.BubbledocsException;


// add needed import declarations

public abstract class BubbledocsIntegrator {
	
	protected String userToken;

    public final void execute() throws BubbledocsException {
        dispatch();
    }

    protected abstract void dispatch() throws BubbledocsException;
    
}
