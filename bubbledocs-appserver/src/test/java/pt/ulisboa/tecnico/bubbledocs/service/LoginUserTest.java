package pt.ulisboa.tecnico.bubbledocs.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import pt.ulisboa.tecnico.bubbledocs.domain.User;
import pt.ulisboa.tecnico.bubbledocs.exceptions.BubbledocsException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UserNotFoundException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.WrongPasswordException;
import pt.ulisboa.tecnico.bubbledocs.service.LoginUser;

// add needed import declarations

public class LoginUserTest extends BubbledocsServiceTest {

    private static final String USERNAME = "jp";
    private static final String PASSWORD = "jp#";
    private static final String NAME = "João Pereira";
    private static final String INVALID_USERNAME = "jp2";
    private static final String INVALID_PASSWORD = "jp#2";
    
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

        assertTrue("Session was not updated", hasSessionUpdated(token2));
        
        User user1 = getUserFromSession(token1);
        User user2 = getUserFromSession(token2);
     
        //FIXME What happens if the random generator hits the same number??
        assertNull(user1);
        user2 = getUserFromSession(token2);
        assertEquals(USERNAME, user2.getUsername());
    }

    //Test Case 3
    @Test(expected = UserNotFoundException.class)
    public void loginUnknownUser() throws BubbledocsException {
        LoginUser service = new LoginUser(INVALID_USERNAME, INVALID_PASSWORD);
        service.execute();
    }

    //Test Case 4
    @Test(expected = WrongPasswordException.class)
    public void loginUserWithWrongPassword() throws BubbledocsException {
        LoginUser service = new LoginUser(USERNAME, INVALID_PASSWORD);
        service.execute();
    }
}
