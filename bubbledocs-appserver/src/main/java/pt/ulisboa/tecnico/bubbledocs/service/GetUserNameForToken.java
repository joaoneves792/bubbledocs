package pt.ulisboa.tecnico.bubbledocs.service;

import pt.ulisboa.tecnico.bubbledocs.exceptions.UserNotInSessionException;


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
    protected void dispatch() {
        username=userToken.split("\\d")[0];   
    }
    
    public final String getUsername() {
    	return username;
    }
}
