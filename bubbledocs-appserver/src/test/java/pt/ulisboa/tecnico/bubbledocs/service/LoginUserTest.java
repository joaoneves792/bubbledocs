package pt.ulisboa.tecnico.bubbledocs.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import pt.ulisboa.tecnico.bubbledocs.domain.User;
import pt.ulisboa.tecnico.bubbledocs.exceptions.BubbledocsException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UserNotFoundException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.WrongPasswordException;
import pt.ulisboa.tecnico.bubbledocs.service.LoginUser;


public class LoginUserTest extends BubbledocsServiceTest {

	private static final int TOKEN_INT = 1;
    private static final String USERNAME = "md";
    private static final String PASSWORD = "dagon";
    private static final String NAME = "Mehrunes Dagon";
    
    private static final String INVALID_USERNAME = "hm";
    private static final String INVALID_PASSWORD = "mora";
    
    @Override
    public void initializeDomain() {
        createUser(USERNAME, PASSWORD, NAME);
    }

    //Test Case 1 
    @Test
    public void success() throws BubbledocsException {
        LoginUser service = new LoginUser(USERNAME, PASSWORD);
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
    public void successLoginTwice() throws BubbledocsException {
        LoginUser service = new LoginUser(USERNAME, PASSWORD);
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

    //Test Case 3
    @Test(expected = UserNotFoundException.class)
    public void loginUnknownUser() throws BubbledocsException {
        LoginUser service = new LoginUser(INVALID_USERNAME, PASSWORD);
        service.execute();
    }

    //Test Case 4
    @Test(expected = WrongPasswordException.class)
    public void loginUserWithWrongPassword() throws BubbledocsException {
        LoginUser service = new LoginUser(USERNAME, INVALID_PASSWORD);
        service.execute();
    }
    
}
