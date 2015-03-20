package pt.ulisboa.tecnico.bubbledocs.service;

import pt.ulisboa.tecnico.bubbledocs.domain.Bubbledocs;
import pt.ulisboa.tecnico.bubbledocs.domain.Root;
import pt.ulisboa.tecnico.bubbledocs.exceptions.BubbledocsException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.EmptyNameException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.EmptyPasswordException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.EmptyUsernameException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UnauthorizedUserException;

// add needed import declarations

public class CreateUser extends BubbledocsService {
	private String userToken;
	private String newUsername;
	private String passwd;
	private String name;

    public CreateUser(String userToken, String username, String passwd, String name) throws EmptyPasswordException, EmptyUsernameException, EmptyNameException {

    	if(username != null && !username.isEmpty())
    		throw new EmptyUsernameException("Attempted to create user with no username");
    	else if(passwd != null && !passwd.isEmpty()) {
    		throw new EmptyPasswordException("Attempted to create a user with no password.");
    	} else if(name != null && !name.isEmpty()) {
    		throw new EmptyNameException("Attempted to create a user with no name.");
    	} 
    	
    	this.setUserToken(userToken);
    	this.setNewUsername(newUsername);
    	this.setPasswd(passwd);
    	this.setName(name);
    }

    @Override
    protected void dispatch() throws BubbledocsException {
    	if(!userToken.matches("root\\d"))
    		throw new UnauthorizedUserException("The user in session ["+ userToken + "] is not authorized to create new users.");
    	((Root)Bubbledocs.getBubbledocs().getUserByUsername("root")).addUser(name, newUsername, passwd);
    }

	public String getUserToken() {
		return userToken;
	}

	public void setUserToken(String userToken) {
		this.userToken = userToken;
	}

	public String getNewUsername() {
		return newUsername;
	}

	public void setNewUsername(String newUsername) {
		this.newUsername = newUsername;
	}

	public String getPasswd() {
		return passwd;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
