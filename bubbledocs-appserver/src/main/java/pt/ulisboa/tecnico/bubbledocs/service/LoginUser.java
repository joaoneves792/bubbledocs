package pt.ulisboa.tecnico.bubbledocs.service;

import pt.ulisboa.tecnico.bubbledocs.domain.Bubbledocs;
import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidLoginException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.LoginBubbleDocsException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.RemoteInvocationException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UnavailableServiceException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UserNotFoundException;
import pt.ulisboa.tecnico.bubbledocs.service.remote.IDRemoteServices;

// add needed import declarations

public class LoginUser extends BubbledocsService {

    private String username;
    private String password;

    /**
     * Perform a user login
     * @param string with a username
     * @param string with the users password
     */
    public LoginUser(String usrname, String passwd) {
        username = usrname;
        password = passwd;
    }

    @Override
    private void checkUserInSession() throws UserNotInSessionException{
    	//EMPTY ON PURPOSE
    }
    
    @Override
    protected void dispatch() throws UnavailableServiceException, LoginBubbleDocsException {
        Bubbledocs bubble;
        IDRemoteServices sdId;
        
        bubble = Bubbledocs.getBubbledocs();
        sdId = new IDRemoteServices();
        
        try{
        	sdId.loginUser(username, password);
        }catch(RemoteInvocationException e){
        	try{
        		bubble.localLogin(username, password);
        	}catch(UserNotFoundException | InvalidLoginException lle){
        		throw new UnavailableServiceException("The service is unavailable.");
        	}
        }
        //If we reach here then login was successful
        
        try{
        	bubble.updateLocalPassword(username, password);
    	
        	userToken = bubble.createSession(username);
        }catch(UserNotFoundException e){
        	//This should only happen if the user was deleted on the domain but still managed to login remotely!
        	throw new LoginBubbleDocsException("Login failed");
        }
    }

    public final String getUserToken() {
    	return userToken;
    }
}
