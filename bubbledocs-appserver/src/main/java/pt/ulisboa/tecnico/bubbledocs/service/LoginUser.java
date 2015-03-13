package pt.ulisboa.tecnico.bubbledocs.service;

// add needed import declarations

public class LoginUser extends BubbledocsService {

    private String userToken;

    public LoginUser(String username, String password) {
	// add code here
    }

    @Override
    protected void dispatch() throws BubbledocsException {
	// add code here
    }

    public final String getUserToken() {
	return userToken;
    }
}
