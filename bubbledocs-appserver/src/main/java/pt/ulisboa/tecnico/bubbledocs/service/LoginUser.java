package pt.ulisboa.tecnico.bubbledocs.service;

import pt.ulisboa.tecnico.bubbledocs.domain.Bubbledocs;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UserNotFoundException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.WrongPasswordException;

// add needed import declarations

public class LoginUser extends BubbledocsService {

    private String userToken;
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
    protected void dispatch() throws UserNotFoundException, WrongPasswordException {
        Bubbledocs bubble;
        Integer tokenInt;
        
        bubble = Bubbledocs.getBubbledocs();

        tokenInt = bubble.loginUser(username, password);
        userToken = username + tokenInt;
    }

    public final String getUserToken() {
    	return userToken;
    }
}
