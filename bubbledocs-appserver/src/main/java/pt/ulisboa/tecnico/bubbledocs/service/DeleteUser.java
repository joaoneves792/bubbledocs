package pt.ulisboa.tecnico.bubbledocs.service;

import pt.ulisboa.tecnico.bubbledocs.domain.Bubbledocs;
import pt.ulisboa.tecnico.bubbledocs.domain.Root;
import pt.ulisboa.tecnico.bubbledocs.exceptions.BubbledocsException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.EmptyUsernameException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UnauthorizedUserException;
import pt.ulisboa.tecnico.bubbledocs.service.remote.IDRemoteServices;

// add needed import declarations

public class DeleteUser extends BubbledocsService {
	private String deadUsername;
	
	IDRemoteServices sdId;
	 
    public DeleteUser(String userToken, String deadUsername) throws EmptyUsernameException {
    	
    	if(deadUsername == null || deadUsername.isEmpty())
    		throw new EmptyUsernameException("Attempted to delete user with no username");
    	
    	this.setUserToken(userToken);
    	this.setDeadUsername(deadUsername);
    }

    @Override
    protected void dispatch() throws BubbledocsException {
    	GetUserNameForToken usernameDTO = new GetUserNameForToken(userToken);
    	usernameDTO.execute();
    	if(!usernameDTO.getUsername().equals("root"))
    		throw new UnauthorizedUserException("The user in session ["+ userToken + "] is not authorized to create new users.");
   	
    	((Root)Bubbledocs.getBubbledocs().getUserByUsername("root")).removeUser(deadUsername);
    }

	public String getUserToken() {
		return userToken;
	}

	public void setUserToken(String userToken) {
		this.userToken = userToken;
	}

	public String getNewUsername() {
		return deadUsername;
	}

	public void setDeadUsername(String deadUsername) {
		this.deadUsername = deadUsername;
	}
	
}