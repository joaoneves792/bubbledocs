package pt.ulisboa.tecnico.bubbledocs.service;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import pt.ulisboa.tecnico.bubbledocs.exceptions.BubbledocsException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UserNotFoundException;

public class GetUserInfoTest extends BubbledocsServiceTest {

    private static final String USER_NAME = "Molag Bal";
    private static final String USER_EMAIL = "molag@cold-harbour.oblivion";
    private static final String USERNAME = "molag";
    private static final String NON_EXISTENT_USERNAME = "hermaeus";

    private String userToken;
    
    @Override
    public void initializeDomain() {
    	try {
    	createUser(USERNAME, USER_EMAIL, USER_NAME);
    	
    	
			userToken = addUserToSession(USERNAME);
		} catch (BubbledocsException e) {
			assertTrue("Failed to populate domain for GetUserNameForTokenTest", false);
		}
    }

    //Test case 1
    @Test
    public void success() throws BubbledocsException {
    	GetUserInfo service = new GetUserInfo(USERNAME);
        service.execute();

        String username = service.getUsername();
        String email = service.getEmail();
        String name = service.getName();

        assertTrue("Returned username is not the expected one", username.equals(USERNAME));
        assertTrue("Returned email is not the expected one", email.equals(USER_EMAIL));
        assertTrue("Returned name is not the expected one", name.equals(USER_NAME));
    }
    
    //Test case 2
    @Test
    public void successNotInSession() throws BubbledocsException {
    	GetUserInfo service = new GetUserInfo(USERNAME);

        removeUserFromSession(userToken);
        service.execute();
        
        String username = service.getUsername();
        String email = service.getEmail();
        String name = service.getName();

        assertTrue("Returned username is not the expected one", username.equals(USERNAME));
        assertTrue("Returned email is not the expected one", email.equals(USER_EMAIL));
        assertTrue("Returned name is not the expected one", name.equals(USER_NAME));
    }
   
    //Test case 3
    @Test(expected = UserNotFoundException.class)
    public void userDoesNotExist() throws BubbledocsException {
    	GetUserInfo service = new GetUserInfo(NON_EXISTENT_USERNAME);
        service.execute();
    }
} 
