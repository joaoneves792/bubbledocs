package pt.ulisboa.tecnico.bubbledocs.service;

import pt.ulisboa.tecnico.bubbledocs.domain.Bubbledocs;
import pt.ulisboa.tecnico.bubbledocs.domain.Root;
import pt.ulisboa.tecnico.bubbledocs.exceptions.EmptyNameException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.EmptyPasswordException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.EmptyUsernameException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UnauthorizedUserException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UserAlreadyExistsException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UserNotFoundException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UserNotInSessionException;

// add needed import declarations

public class CreateUser extends BubbledocsService {
	private String rootToken;
	private String newUsername;
	private String passwd;
	private String name;

    public CreateUser(String rootTok, String username, String passwd, String name) throws EmptyPasswordException, EmptyUsernameException, EmptyNameException {

    	if(username == null || username.isEmpty())
    		throw new EmptyUsernameException("Attempted to create user with no username");
    	else if(passwd == null || passwd.isEmpty()) {
    		throw new EmptyPasswordException("Attempted to create a user with no password.");
    	} else if(name == null || name.isEmpty()) {
    		throw new EmptyNameException("Attempted to create a user with no name.");
    	} 
    	
    	rootToken = rootTok;
    	newUsername = username;
    	this.passwd = passwd;
    	this.name = name;
    }

    @Override
    protected void dispatch() throws UnauthorizedUserException, UserAlreadyExistsException, UserNotInSessionException, EmptyPasswordException, EmptyUsernameException, UserNotFoundException {
    	if(!rootToken.matches("root\\d"))
    		throw new UnauthorizedUserException("The user in session ["+ rootToken + "] is not authorized to create new users.");
    	((Root)Bubbledocs.getBubbledocs().getUserByUsername("root")).addUser(name, newUsername, passwd);
    }

	public String getRootToken() {
		return rootToken;
	}
	
}
