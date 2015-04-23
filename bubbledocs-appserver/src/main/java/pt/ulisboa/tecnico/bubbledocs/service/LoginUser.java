package pt.ulisboa.tecnico.bubbledocs.service;

import pt.ulisboa.tecnico.bubbledocs.domain.Bubbledocs;
import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidLoginException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.LoginBubbleDocsException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UnavailableServiceException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UserNotFoundException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UserNotInSessionException;


public class LoginUser extends BubbledocsService {

    private String username;
    private String password;

    public LoginUser(String usrname, String passwd) {
        username = usrname;
        password = passwd;
    }

    @Override
	protected void checkUserInSession() throws UserNotInSessionException{
    	//EMPTY ON PURPOSE
    }
    
    @Override
    protected void dispatch() throws UnavailableServiceException, LoginBubbleDocsException{
        Bubbledocs bubble = Bubbledocs.getBubbledocs();
        
        try{
        	bubble.localLogin(username, password);
        }catch(UserNotFoundException | InvalidLoginException lle){
        	throw new UnavailableServiceException("The service is unavailable.");
        }
        createSession();
    }

    public void createSession() throws LoginBubbleDocsException{
        Bubbledocs bubble = Bubbledocs.getBubbledocs();

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
