package pt.ulisboa.tecnico.bubbledocs.service.integrator;

import pt.ulisboa.tecnico.bubbledocs.exceptions.BubbledocsException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.EmptyNameException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidEmailException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidUsernameException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.RemoteInvocationException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UnavailableServiceException;
import pt.ulisboa.tecnico.bubbledocs.service.CreateUser;
import pt.ulisboa.tecnico.bubbledocs.service.DeleteUser;
import pt.ulisboa.tecnico.bubbledocs.service.remote.IDRemoteServices;

public class CreateUserIntegrator extends BubbledocsIntegrator{
	
	private String newUsername;
	private String email;
	private String name;
	
	public CreateUserIntegrator(String rootTok, String username, String email, String name) throws EmptyNameException, InvalidUsernameException, InvalidEmailException {
    	
    	this.userToken = rootTok;
    	newUsername = username;
    	this.email = email;
    	this.name = name;
    }

	@Override
	protected void dispatch() throws BubbledocsException {
		IDRemoteServices sdId = new IDRemoteServices();
        CreateUser createUserService = new CreateUser(userToken, newUsername, email, name);
        DeleteUser deleteUserService = new DeleteUser(userToken, newUsername);
        
        createUserService.execute();
        
    	try {
    		sdId.createUser(newUsername, email);
    	} catch (RemoteInvocationException e) {
    		deleteUserService.execute();
    		throw new UnavailableServiceException("SD-ID is offline.");
    	}

    }

    public final String getUserToken() {
    	return userToken;
    }
}
