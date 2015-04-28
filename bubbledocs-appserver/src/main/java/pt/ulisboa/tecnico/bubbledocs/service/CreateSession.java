package pt.ulisboa.tecnico.bubbledocs.service;

import pt.ulisboa.tecnico.bubbledocs.domain.Bubbledocs;
import pt.ulisboa.tecnico.bubbledocs.exceptions.LoginBubbleDocsException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UserNotInSessionException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UserNotFoundException;


public class CreateSession extends BubbledocsService {

    private String username;
    private String password;

    public CreateSession(String usrname, String passwrd) {
        username = usrname;
        password = passwrd;
    }

    @Override
	protected void checkUserInSession() throws UserNotInSessionException{
    	//EMPTY ON PURPOSE
    }
    
    @Override
    protected void dispatch() throws LoginBubbleDocsException{
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
