package pt.ulisboa.tecnico.bubbledocs.service;

import pt.ulisboa.tecnico.bubbledocs.domain.Bubbledocs;
import pt.ulisboa.tecnico.bubbledocs.exceptions.BubbledocsException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UnknownBubbledocsUserException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UserNotFoundException;

// add needed import declarations

public class LoginUser extends BubbledocsService {

    private String _userToken;
    private String _username;
    private String _password;

    /**
     * Perform a user login
     * @param string with a username
     * @param string with the users password
     */
    public LoginUser(String username, String password) {
        _username = username;
        _password = password;
    }

    @Override
    protected void dispatch() throws BubbledocsException {
        Bubbledocs bubble;
        Integer tokenInt;
        
        bubble = Bubbledocs.getBubbledocs();

        try{
            tokenInt = bubble.loginUser(_username, _password);
            _userToken = _username + tokenInt;
        }catch(UserNotFoundException e){
        	throw new UnknownBubbledocsUserException("Unknown username");
        }
    }

    public final String getUserToken() {
    	return _userToken;
    }
}
