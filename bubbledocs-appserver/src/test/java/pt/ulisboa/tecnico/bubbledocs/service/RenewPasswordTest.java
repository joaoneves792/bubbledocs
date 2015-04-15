package pt.ulisboa.tecnico.bubbledocs.service;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import mockit.Expectations;
import mockit.Mocked;

import org.junit.Test;

import pt.ulisboa.tecnico.bubbledocs.domain.Bubbledocs;
import pt.ulisboa.tecnico.bubbledocs.exceptions.BubbledocsException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.RemoteInvocationException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UnavailableServiceException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UserNotInSessionException;
import pt.ulisboa.tecnico.bubbledocs.service.remote.IDRemoteServices;

public class RenewPasswordTest extends BubbledocsServiceTest {

    private static final String USER_NAME = "Molag Bal";
    private static final String USER_EMAIL = "molag@cold-harbour.oblivion";
    private static final String USERNAME = "molag";
    
    private String userToken;
    
	@Mocked
	private IDRemoteServices sdId;
    
    @Override
    public void initializeDomain() {
    	createUser(USERNAME, USER_EMAIL, USER_NAME);
    	
    	try {
			userToken = addUserToSession(USERNAME);
		} catch (BubbledocsException e) {
			assertTrue("Failed to populate domain for RenewPasswordTest", false);
		}
    }

    //Test case 1
    @Test
    public void success() throws BubbledocsException {
        RenewPassword service = new RenewPassword(userToken);
        
        new Expectations() {
        	{
        		sdId.renewPassword(USERNAME);
        	}
        };
        
        service.execute();

        assertNull(Bubbledocs.getBubbledocs().getUserByUsername(USERNAME).getPasswd());
        assertTrue("Session was not updated", hasSessionUpdated(userToken));
    }
    
    //Test case 2
    @Test(expected = UnavailableServiceException.class)
    public void renewPasswordRemoteServiceOffline() throws BubbledocsException {
        RenewPassword service = new RenewPassword(userToken);
        
        new Expectations() {
        	{
        		sdId.renewPassword(USERNAME);
                result = new RemoteInvocationException("SD-ID offline");
        	}
        };
        
        service.execute();
        assertTrue("Session was not updated", hasSessionUpdated(userToken));
    }
    
  //Test case 3
    @Test(expected = UserNotInSessionException.class)
    public void renewPasswordUserNotInSession() throws BubbledocsException {
    	removeUserFromSession(userToken);
        new RenewPassword(userToken).execute();
    }
}
