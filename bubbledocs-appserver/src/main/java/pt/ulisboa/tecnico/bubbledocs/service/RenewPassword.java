package pt.ulisboa.tecnico.bubbledocs.service;

import pt.ulisboa.tecnico.bubbledocs.domain.Bubbledocs;
import pt.ulisboa.tecnico.bubbledocs.exceptions.BubbledocsException;

public class RenewPassword extends BubbledocsService {
	
	public RenewPassword(String userToken) {
		this.userToken = userToken;
	}
	
    @Override
    protected void dispatch() throws BubbledocsException {
    	Bubbledocs bubble = Bubbledocs.getBubbledocs();
   		GetUserNameForToken usernameService = new GetUserNameForToken(userToken);
   		usernameService.execute();
       	String username = usernameService.getUsername();
           
        bubble.updateLocalPassword(username, null);
    }

}
