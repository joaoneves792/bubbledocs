package pt.ulisboa.tecnico.bubbledocs.service;

import pt.ulisboa.tecnico.bubbledocs.domain.Bubbledocs;
import pt.ulisboa.tecnico.bubbledocs.exceptions.BubbledocsException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.LoginBubbleDocsException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.RemoteInvocationException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UnavailableServiceException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UserNotFoundException;
import pt.ulisboa.tecnico.bubbledocs.service.remote.IDRemoteServices;

public class RenewPassword extends BubbledocsService {
		
	public RenewPassword(String token) {
		userToken=token;
	}
	
    @Override
    protected void dispatch() throws BubbledocsException {
    	   Bubbledocs bubble;
           IDRemoteServices sdId;
           String username = userToken.split("\\d")[0];
       
           bubble = Bubbledocs.getBubbledocs();
           sdId = new IDRemoteServices();
           
           try{
        	   sdId.renewPassword(username);
           	   bubble.updateLocalPassword(username, null);
           }catch(RemoteInvocationException e) {
        	   throw new UnavailableServiceException("The renew service is unavailable");
           }catch(UserNotFoundException e){
        	   throw new LoginBubbleDocsException("Login failed");
           }
    }
   
}
