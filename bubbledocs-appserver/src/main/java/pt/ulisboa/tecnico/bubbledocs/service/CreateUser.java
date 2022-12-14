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
import pt.ulisboa.tecnico.bubbledocs.exceptions.UnauthorizedUserException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UnavailableServiceException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UserAlreadyExistsException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UserNotFoundException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UserNotInSessionException;

// add needed import declarations

public class CreateUser extends BubbledocsService {
	private String newUsername;
	private String email;
	private String name;

    public CreateUser(String rootTok, String username, String email, String name) throws EmptyNameException, InvalidUsernameException, InvalidEmailException {
 
    	if(username == null || username.isEmpty())
    		throw new InvalidUsernameException("Attempted to create user with no username");
    	else if(email == null || email.isEmpty()) {
    		throw new InvalidEmailException("Attempted to create a user with no email.");
    	} else if(name == null || name.isEmpty()) {
    		throw new EmptyNameException("Attempted to create a user with no name.");
    	}
    	
    	userToken = rootTok;
    	newUsername = username;
    	this.email = email;
    	this.name = name;
    }

    @Override
    protected void dispatch() throws UnauthorizedUserException, UserAlreadyExistsException, UserNotInSessionException, EmptyPasswordException, EmptyUsernameException, UserNotFoundException, InvalidUsernameException, DuplicateUsernameException, DuplicateEmailException, InvalidEmailException, UnavailableServiceException {
    	if(!userToken.matches("root\\d"))
    		throw new UnauthorizedUserException("The user in session ["+ userToken + "] is not authorized to create new users.");

    	((Root)Bubbledocs.getBubbledocs().getUserByUsername("root")).addUser(name, newUsername, email);
    }

	public String getuserToken() {
		return userToken;
	}
	
}
