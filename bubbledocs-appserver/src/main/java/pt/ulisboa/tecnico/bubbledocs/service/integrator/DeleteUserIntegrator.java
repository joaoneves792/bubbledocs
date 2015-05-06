package pt.ulisboa.tecnico.bubbledocs.service.integrator;

import pt.ulisboa.tecnico.bubbledocs.exceptions.BubbledocsException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.RemoteInvocationException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UnavailableServiceException;
import pt.ulisboa.tecnico.bubbledocs.service.CreateUser;
import pt.ulisboa.tecnico.bubbledocs.service.DeleteUser;
import pt.ulisboa.tecnico.bubbledocs.service.GetUserInfo;
import pt.ulisboa.tecnico.bubbledocs.service.remote.IDRemoteServices;

public class DeleteUserIntegrator extends BubbledocsIntegrator {

	private GetUserInfo localUser;
	
	
	public DeleteUserIntegrator(String userToken, String deadUsername) {
		this.userToken=userToken;
		localUser = new GetUserInfo(deadUsername);
	}
	
	@Override
	protected void dispatch() throws BubbledocsException {
		localUser.execute();
		
		DeleteUser localService = new DeleteUser(userToken, localUser.getUsername());
		localService.execute();
		
		IDRemoteServices sdId = new IDRemoteServices();
		try{
			sdId.removeUser(localUser.getUsername());
		}catch(RemoteInvocationException  e){
	    		CreateUser compensation = new CreateUser(userToken, localUser.getUsername(), localUser.getEmail(), localUser.getName());
	    		compensation.execute();
	    		throw new UnavailableServiceException("SD ID unavailable");
		}
	}

}
