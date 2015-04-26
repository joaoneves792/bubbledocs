package pt.ulisboa.tecnico.bubbledocs.service.integrator;

import pt.ulisboa.tecnico.bubbledocs.exceptions.BubbledocsException;
import pt.ulisboa.tecnico.bubbledocs.service.RenewPassword;

public class RenewPasswordIntegrator extends BubbledocsIntegrator {
		
	public RenewPasswordIntegrator(String token) {
		userToken=token;
	}
	
    @Override
    protected void dispatch() throws BubbledocsException {
    	
    	RenewPassword localService = new RenewPassword(userToken);
    	localService.execute();
    	result = localService.getResult();

    }

	public final Integer getResult() {
		return result;
	}
	
}
