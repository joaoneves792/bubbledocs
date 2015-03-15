package pt.ulisboa.tecnico.bubbledocs.service;

import pt.ulisboa.tecnico.bubbledocs.domain.Root;
import pt.ulisboa.tecnico.bubbledocs.exceptions.BubbledocsException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UnauthorizedUserException;

// add needed import declarations

public class DeleteUser extends BubbledocsService {
	private String userToken;
	private String deadUsername;

    public DeleteUser(String userToken, String deadUsername) {
    	this.setUserToken(userToken);
    	this.setDeadUsername(deadUsername);
    }

    @Override
    protected void dispatch() throws BubbledocsException {
    	if(!userToken.matches("root\\d"))
    		throw new UnauthorizedUserException("The user in session ["+ userToken + "] is not authorized to create new users.");
    	Root.getRoot().removeUser(deadUsername);
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