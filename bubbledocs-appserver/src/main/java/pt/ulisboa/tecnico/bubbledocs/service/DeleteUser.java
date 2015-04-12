package pt.ulisboa.tecnico.bubbledocs.service;

import pt.ulisboa.tecnico.bubbledocs.domain.Bubbledocs;
import pt.ulisboa.tecnico.bubbledocs.domain.Root;
import pt.ulisboa.tecnico.bubbledocs.exceptions.BubbledocsException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.EmptyUsernameException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UnauthorizedUserException;

// add needed import declarations

public class DeleteUser extends BubbledocsService {
	private String deadUsername;

    public DeleteUser(String userToken, String deadUsername) throws EmptyUsernameException {
    	
    	if(deadUsername == null || deadUsername.isEmpty())
    		throw new EmptyUsernameException("Attempted to delete user with no username");
    	
    	this.setUserToken(userToken);
    	this.setDeadUsername(deadUsername);
    }

    @Override
    protected void dispatch() throws BubbledocsException {
    	if(!userToken.matches("root\\d"))
    		throw new UnauthorizedUserException("The user in session ["+ userToken + "] is not authorized to create new users.");
    	((Root)Bubbledocs.getBubbledocs().getUserByUsername("root")).removeUser(deadUsername);
    	//Bubbledocs.getBubbledocs().destroyUser(userToken, deadUsername);
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