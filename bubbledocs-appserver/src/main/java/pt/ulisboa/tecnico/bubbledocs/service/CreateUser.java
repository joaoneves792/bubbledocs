package pt.ulisboa.tecnico.bubbledocs.service;

import pt.ulisboa.tecnico.bubbledocs.domain.Bubbledocs;
import pt.ulisboa.tecnico.bubbledocs.domain.Root;
import pt.ulisboa.tecnico.bubbledocs.exceptions.DuplicateEmailException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.DuplicateUsernameException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.EmptyNameException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.EmptyPasswordException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.EmptyUsernameException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidEmailException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidUsernameException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.RemoteInvocationException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UnauthorizedUserException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UnavailableServiceException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UserAlreadyExistsException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UserNotFoundException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UserNotInSessionException;
import pt.ulisboa.tecnico.bubbledocs.service.remote.IDRemoteServices;

// add needed import declarations

public class CreateUser extends BubbledocsService {
	private String rootToken;
	private String newUsername;
	private String email;
	private String name;

    public CreateUser(String rootTok, String username, String email, String name) throws EmptyPasswordException, EmptyUsernameException, EmptyNameException, InvalidUsernameException, InvalidEmailException {

    	if(username == null || username.isEmpty())
    		throw new InvalidUsernameException("Attempted to create user with no username");
    	else if(email == null || email.isEmpty()) {
    		throw new InvalidEmailException("Attempted to create a user with no email.");
    	} else if(name == null || name.isEmpty()) {
    		throw new EmptyNameException("Attempted to create a user with no name.");
    	} else if(username.length() < 3 || username.length() > 8) {
    		throw new InvalidUsernameException("User usernames must have 3 to 8 characters.");
    	}
    	
    	rootToken = rootTok;
    	newUsername = username;
    	this.email = email;
    	this.name = name;
    }

    @Override
    protected void dispatch() throws UnauthorizedUserException, UserAlreadyExistsException, UserNotInSessionException, EmptyPasswordException, EmptyUsernameException, UserNotFoundException, InvalidUsernameException, DuplicateUsernameException, DuplicateEmailException, InvalidEmailException, UnavailableServiceException {
    	if(!rootToken.matches("root\\d"))
    		throw new UnauthorizedUserException("The user in session ["+ rootToken + "] is not authorized to create new users.");

    	IDRemoteServices sdId = new IDRemoteServices();
    	
    	try {
    		sdId.createUser(newUsername, email);
    	} catch (RemoteInvocationException e) {
    		throw new UnavailableServiceException("SD-ID is offline.");
    	}
    	
    	((Root)Bubbledocs.getBubbledocs().getUserByUsername("root")).addUser(name, newUsername, email);
    }

	public String getRootToken() {
		return rootToken;
	}
	
}
