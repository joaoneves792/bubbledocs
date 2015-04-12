package pt.ulisboa.tecnico.bubbledocs.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import mockit.Expectations;
import mockit.Mocked;

import org.junit.Test;

import pt.ulisboa.tecnico.bubbledocs.domain.User;
import pt.ulisboa.tecnico.bubbledocs.exceptions.BubbledocsException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.LoginBubbleDocsException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.RemoteInvocationException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UnavailableServiceException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UserNotFoundException;
import pt.ulisboa.tecnico.bubbledocs.service.LoginUser;
import pt.ulisboa.tecnico.bubbledocs.service.remote.IDRemoteServices;


public class LoginUserTest extends BubbledocsServiceTest {

    private static final String USERNAME = "mehrunes";
    private static final String PASSWORD = "dagon";
    private static final String OLD_PASSWORD = "mehrunes";
    private static final String NAME = "Mehrunes Dagon";
    private static final String EMAIL = "mehrunes@dagon.com";
    private static final String INVALID_USERNAME = "hermaeus";
    private static final String INVALID_PASSWORD = "mora";
    private static final String EMPTY_PASSWORD = "";
    
	@Mocked
	private IDRemoteServices sdId;

    
    @Override
    public void initializeDomain() {
        createUser(USERNAME, EMAIL, NAME);
        try {
			setLocalPassword(USERNAME, PASSWORD);
		} catch (UserNotFoundException e) {
			assertTrue("Fail to initialize Domain for LoginUserTest", false);
		}
        
    }

    //Test Case 1 
    @Test
    public void success() throws BubbledocsException {
    	LoginUser service = new LoginUser(USERNAME, PASSWORD);
    
        new Expectations() {
            {
                sdId.loginUser(USERNAME, PASSWORD);
                result = null;
            }
        };
     	service.execute();
        String token = service.getUserToken();

        User user = getUserFromSession(token);
        assertEquals(USERNAME, user.getUsername());
        assertEquals(PASSWORD, user.getPasswd());
        assertEquals(NAME, user.getName());
        assertTrue("Session was not updated", hasSessionUpdated(token));
   }
    
   //Test Case 2 
   @Test
   public void successLocalLogin() throws BubbledocsException {
    	LoginUser service = new LoginUser(USERNAME, PASSWORD);
    
        new Expectations() {
            {
                sdId.loginUser(USERNAME, PASSWORD);
                result = new RemoteInvocationException("SD-ID offline");
            }
        };
     	service.execute();
        String token = service.getUserToken();

        User user = getUserFromSession(token);
        assertEquals(USERNAME, user.getUsername());
        assertEquals(PASSWORD, user.getPasswd());
        assertEquals(NAME, user.getName());
        assertTrue("Session was not updated", hasSessionUpdated(token));
   }

    //Test Case 3
    @Test
    public void successLoginTwice() throws BubbledocsException {
        LoginUser service = new LoginUser(USERNAME, PASSWORD);
        new Expectations() {
            {
                sdId.loginUser(USERNAME, PASSWORD);
                result = null;
            }
        };
        service.execute();
        String token1 = service.getUserToken();
        service.execute();
        String token2 = service.getUserToken();
        User user = getUserFromSession(token1);

        assertEquals(token1, token2);
        assertEquals(USERNAME, user.getUsername());
        assertEquals(PASSWORD, user.getPasswd());
        assertEquals(NAME, user.getName());
        assertTrue("Session was not updated", hasSessionUpdated(token2));
    }
    
    //Test Case 4
    @Test
    public void successLocalLoginTwice() throws BubbledocsException {
        LoginUser service = new LoginUser(USERNAME, PASSWORD);
        new Expectations() {
            {
                sdId.loginUser(USERNAME, PASSWORD);
                result = null; //The first login has to be successfull so the local password gets updated
            }
        };
        service.execute();
        String token1 = service.getUserToken();
        new Expectations() {
            {
                sdId.loginUser(USERNAME, PASSWORD);
                result = new RemoteInvocationException("SD-ID offline");
            }
        };
        service.execute();
        String token2 = service.getUserToken();
        User user = getUserFromSession(token1);

        assertEquals(token1, token2);
        assertEquals(USERNAME, user.getUsername());
        assertEquals(PASSWORD, user.getPasswd());
        assertEquals(NAME, user.getName());
        assertTrue("Session was not updated", hasSessionUpdated(token2));
    }



    //Test Case 5
    @Test(expected = LoginBubbleDocsException.class)
    public void loginUserWithWrongPassword() throws BubbledocsException {
        LoginUser service = new LoginUser(USERNAME, INVALID_PASSWORD);
        new Expectations() {
            {
                sdId.loginUser(USERNAME, INVALID_PASSWORD);
                result = new LoginBubbleDocsException("Wrong password");
            }
        };
        service.execute();
    }
    
    //Test Case 6
    @Test(expected = UnavailableServiceException.class)
    public void localLoginUserWithWrongPassword() throws BubbledocsException {
        LoginUser service = new LoginUser(USERNAME, INVALID_PASSWORD);
        new Expectations() {
            {
                sdId.loginUser(USERNAME, INVALID_PASSWORD);
                result = new RemoteInvocationException("SD-ID offline");
            }
        };
        service.execute();
    }
    
    //Test Case 7
    @Test(expected = LoginBubbleDocsException.class)
    public void loginUnknownUser() throws BubbledocsException {
        LoginUser service = new LoginUser(INVALID_USERNAME, PASSWORD);
        new Expectations() {
            {
                sdId.loginUser(INVALID_USERNAME, PASSWORD);
                result = new LoginBubbleDocsException("Unknown username");
            }
        };
        service.execute();
    }
    
    //Test Case 8
    @Test(expected = UnavailableServiceException.class)
    public void localLoginUnknownUser() throws BubbledocsException {
        LoginUser service = new LoginUser(INVALID_USERNAME, PASSWORD);
        new Expectations() {
            {
                sdId.loginUser(INVALID_USERNAME, PASSWORD);
                result = new RemoteInvocationException("SD-ID offline");
            }
        };
        service.execute();
    }
    
    //Test Case 9
    @Test
    public void successUpdateLocalPassword() throws BubbledocsException {
    	setLocalPassword(USERNAME, OLD_PASSWORD);
        LoginUser service = new LoginUser(USERNAME, PASSWORD);
        new Expectations() {
            {
                sdId.loginUser(USERNAME, PASSWORD);
                result = null;
            }
        };
        service.execute();
        String token = service.getUserToken();

        User user = getUserFromSession(token);
        assertEquals(PASSWORD, user.getPasswd());
        assertTrue("Session was not updated", hasSessionUpdated(token));
    }
    
    //Test Case 10
    //FIXME Should we be testing with an empty password or a null one?
    @Test
    public void successUpdateEmptyLocalPassword() throws BubbledocsException {
    	setLocalPassword(USERNAME, EMPTY_PASSWORD);
        LoginUser service = new LoginUser(USERNAME, PASSWORD);
        new Expectations() {
            {
                sdId.loginUser(USERNAME, PASSWORD);
                result = null;
            }
        };
        service.execute();
        String token = service.getUserToken();

        User user = getUserFromSession(token);
        assertEquals(PASSWORD, user.getPasswd());
        assertTrue("Session was not updated", hasSessionUpdated(token));
    }
    
}
