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
	protected void updateSession() throws UserNotInSessionException{
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
        
    }
    
}
