package pt.ulisboa.tecnico.bubbledocs.service;

import pt.ulisboa.tecnico.bubbledocs.domain.Bubbledocs;
import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidTokenException;
//import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidTokenException;
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
    protected void dispatch() throws UserNotFoundException, InvalidTokenException{
        Bubbledocs bubble = Bubbledocs.getBubbledocs();
        String[] splittedToken;
        
        //The "-1" is so that it will apply the pattern as many times as necessary and
        //create a trailing empty string so that we can check for invalid tokens
        //(as oposed to "0" which is the same as no second argument) 
        splittedToken = userToken.split("\\d$", -1); 
        if(splittedToken.length < 2)  
        	throw new InvalidTokenException("The token is invalid");
        
        username = splittedToken[0];
        bubble.getUserByUsername(username);  //Check if the user exists     
    }
    
    public final String getUsername() {
    	return username;
    }
}
