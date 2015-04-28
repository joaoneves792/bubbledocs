package pt.ulisboa.tecnico.bubbledocs.service;

import pt.ulisboa.tecnico.bubbledocs.domain.Bubbledocs;
import pt.ulisboa.tecnico.bubbledocs.exceptions.BubbledocsException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UnavailableServiceException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UserNotFoundException;

public class RenewPassword extends BubbledocsService {
	
	public RenewPassword(String userToken) {
		this.userToken = userToken;
	}
	
    @Override
    protected void dispatch() throws BubbledocsException {
    	   Bubbledocs bubble = Bubbledocs.getBubbledocs();
           String username = userToken.split("\\d")[0];
           
           try{
           	   bubble.updateLocalPassword(username, null);
           } catch(UserNotFoundException e){
        	   throw new UnavailableServiceException("The service is unavailable.");
           }
    }

}
