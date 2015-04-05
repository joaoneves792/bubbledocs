package pt.ulisboa.tecnico.bubbledocs.service.remote;

import pt.ulisboa.tecnico.bubbledocs.exceptions.DuplicateEmailException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.DuplicateUsernameException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidEmailException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidUsernameException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.LoginBubbleDocsException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.RemoteInvocationException;


public class IDRemoteServices {
	
	public void createUser(String username, String email) 
		throws InvalidUsernameException, DuplicateUsernameException,
		DuplicateEmailException, InvalidEmailException, RemoteInvocationException {
		// TODO : the connection and invocation of the remote service
	}
	
	public void loginUser(String username, String password)
		throws LoginBubbleDocsException, RemoteInvocationException {
		// TODO : the connection and invocation of the remote service
	}

	public void removeUser(String username) 
		throws LoginBubbleDocsException, RemoteInvocationException {
		// TODO : the connection and invocation of the remote service
	}
	
	public void renewPassword(String username) 
		throws LoginBubbleDocsException, RemoteInvocationException {
		// TODO : the connection and invocation of the remote service
	}

}