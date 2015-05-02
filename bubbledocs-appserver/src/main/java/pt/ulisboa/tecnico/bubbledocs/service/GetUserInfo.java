package pt.ulisboa.tecnico.bubbledocs.service;

import pt.ulisboa.tecnico.bubbledocs.domain.Bubbledocs;
import pt.ulisboa.tecnico.bubbledocs.domain.User;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UserNotInSessionException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UserNotFoundException;


public class GetUserInfo extends BubbledocsService {

    private String username;
    private String name;
    private String email;

    public GetUserInfo(String usrname) {
        username = usrname;
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
        User user = bubble.getUserByUsername(username);
        name = user.getName();
        email = user.getEmail();
    }
    
    public final String getUsername() {
    	return username;
    }
    
    public final String getName() {
    	return name;
    }
    
    public final String getEmail() {
    	return email;
    }
}
