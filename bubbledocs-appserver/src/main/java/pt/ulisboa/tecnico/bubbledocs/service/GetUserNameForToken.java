package pt.ulisboa.tecnico.bubbledocs.service;

import pt.ulisboa.tecnico.bubbledocs.domain.Bubbledocs;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UserNotInSessionException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UserNotFoundException;


public class GetUserNameForToken extends BubbledocsService {

    private String username;

    public GetUserNameForToken(String token) {
        userToken = token;
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
    protected void dispatch() throws UserNotFoundException{
        Bubbledocs bubble = Bubbledocs.getBubbledocs();
       
        username=userToken.split("\\d")[0];
        bubble.getUserByUsername(username);       
    }
    
    public final String getUsername() {
    	return username;
    }
}
