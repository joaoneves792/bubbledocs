package pt.ulisboa.tecnico.bubbledocs.service.integrator;

import pt.ulisboa.tecnico.bubbledocs.exceptions.BubbledocsException;
import pt.ulisboa.tecnico.bubbledocs.service.RenewPassword;
import pt.ulisboa.tecnico.bubbledocs.service.remote.IDRemoteServices;
import pt.ulisboa.tecnico.bubbledocs.exceptions.RemoteInvocationException;

public class RenewPasswordIntegrator extends BubbledocsIntegrator {

	public RenewPasswordIntegrator(String token) {
		this.userToken = token;
	}
	
    @Override
    protected void dispatch() throws BubbledocsException {
    	IDRemoteServices sdId = new IDRemoteServices();
    	RenewPassword service = new RenewPassword(userToken);
		String username = userToken.split("\\d")[0];
		
        try{
        	sdId.renewPassword(username);
        	} catch(RemoteInvocationException e) {
        		service.execute();
        	}
    }

}
