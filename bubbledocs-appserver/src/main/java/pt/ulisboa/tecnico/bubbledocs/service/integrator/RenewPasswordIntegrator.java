package pt.ulisboa.tecnico.bubbledocs.service.integrator;

import pt.ulisboa.tecnico.bubbledocs.exceptions.BubbledocsException;
import pt.ulisboa.tecnico.bubbledocs.service.GetUserNameForToken;
import pt.ulisboa.tecnico.bubbledocs.service.RenewPassword;
import pt.ulisboa.tecnico.bubbledocs.service.remote.IDRemoteServices;
import pt.ulisboa.tecnico.bubbledocs.exceptions.RemoteInvocationException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UnavailableServiceException;

public class RenewPasswordIntegrator extends BubbledocsIntegrator {

	public RenewPasswordIntegrator(String token) {
		this.userToken = token;
	}
	
    @Override
    protected void dispatch() throws BubbledocsException {
    	IDRemoteServices sdId = new IDRemoteServices();
    	RenewPassword service = new RenewPassword(userToken);
		GetUserNameForToken usernameService = new GetUserNameForToken(userToken);
		usernameService.execute();
    	String username = usernameService.getUsername();
		
        try{
        	sdId.renewPassword(username);
        } catch(RemoteInvocationException e) {
        	throw new UnavailableServiceException("SD-Store offline.");
        }

        service.execute();
    }

}
