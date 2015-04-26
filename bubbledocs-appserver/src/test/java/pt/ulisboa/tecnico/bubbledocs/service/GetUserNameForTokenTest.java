package pt.ulisboa.tecnico.bubbledocs.service;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import pt.ulisboa.tecnico.bubbledocs.exceptions.BubbledocsException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UserNotFoundException;
import pt.ulisboa.tecnico.bubbledocs.service.GetUserNameForToken;

public class GetUserNameForTokenTest extends BubbledocsServiceTest {

    private static final String USER_NAME = "Molag Bal";
    private static final String USER_EMAIL = "molag@cold-harbour.oblivion";
    private static final String USERNAME = "molag";
    private static final String INVALID_TOKEN = "hermaeus7";

    private String userToken;
    
    @Override
    public void initializeDomain() {
    	createUser(USERNAME, USER_EMAIL, USER_NAME);
    	
    	try {
			userToken = addUserToSession(USERNAME);
		} catch (BubbledocsException e) {
			assertTrue("Failed to populate domain for GetUserNameForTokenTest", false);
		}
    }

    //Test case 1
    @Test
    public void success() throws BubbledocsException {
        GetUserNameForToken service  = new GetUserNameForToken(userToken);
        String username; 

        service.execute();
        username = service.getUsername();

        assertTrue("Empty string returned", null != username);
        assertTrue("Returned username is not the expected one", username.equals(USERNAME));
    }
    
    //Test case 2
    @Test(expected = UserNotFoundException.class)
    public void userDoesNotExist() throws BubbledocsException {
        GetUserNameForToken service  = new GetUserNameForToken(INVALID_TOKEN);
        service.execute();
    }
} 
