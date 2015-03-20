package pt.ulisboa.tecnico.bubbledocs.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import pt.ulisboa.tecnico.bubbledocs.domain.Bubbledocs;
import pt.ulisboa.tecnico.bubbledocs.domain.Root;
import pt.ulisboa.tecnico.bubbledocs.domain.User;
import pt.ulisboa.tecnico.bubbledocs.exceptions.BubbledocsException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.CreateRootException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.EmptyNameException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.EmptyPasswordException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.EmptyUsernameException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidSessionTimeException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UnauthorizedUserException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UserAlreadyExistsException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UserNotInSessionException;
import pt.ulisboa.tecnico.bubbledocs.service.CreateUser;

public class CreateUserTest extends BubbledocsServiceTest {

    // the tokens
    private static final int ROOT_TOKEN_INT     = 8;
    private static final String ROOT_TOKEN      = "root8";
    @SuppressWarnings("unused")
	private static final String EXISTING_TOKEN  = "md4";
    private static final int EXISTING_TOKEN_INT = 4;
    private static final String UNAUTHORIZED_TOKEN = "cv3";
    private static final int UNAUTHORIZED_TOKEN_INT = 3;

    private static final String ROOT_USERNAME = "root";
    private static final String UNAUTHORIZED_USERNAME       = "cv";
    private static final String EXISTING_USERNAME    = "md";
    private static final String NON_EXSTING_USERNAME = "mb";

    private static final String UNAUTHORIZED_PASSWORD        = "vile";
    private static final String EXISTING_PASSWORD     = "dagon";
    private static final String NON_EXISTING_PASSWORD = "bal";

    private static final String UNAUTHORIZED_NAME        = "Clavicus Vile";
    private static final String EXISTING_NAME     = "Mehrunes Dagon";
    private static final String NON_EXISTING_NAME = "Molag Bal";

    private static final String EMPTY_NAME     = "";
    private static final String EMPTY_USERNAME = "";
    private static final String EMPTY_PASSWORD = "";
    
    
    @Override
    public void initializeDomain() {
    	Bubbledocs bubble = Bubbledocs.getBubbledocs();
    	Root root = bubble.getSuperUser();    	
    	User existingUser = createUser(EXISTING_USERNAME, EXISTING_PASSWORD, EXISTING_NAME),
    	     unauthorizedUser = createUser(UNAUTHORIZED_USERNAME, UNAUTHORIZED_PASSWORD, UNAUTHORIZED_NAME);
    	
    	addUserToSession(ROOT_TOKEN_INT, root);
    	addUserToSession(EXISTING_TOKEN_INT, existingUser);
    	addUserToSession(UNAUTHORIZED_TOKEN_INT, unauthorizedUser);
    }

    @Test
    public void success() throws BubbledocsException {
    	addUserToSession(ROOT_TOKEN_INT, Bubbledocs.getBubbledocs().getSuperUser());
        CreateUser service = new CreateUser(ROOT_TOKEN, NON_EXSTING_USERNAME, NON_EXISTING_PASSWORD, NON_EXISTING_NAME);
        service.execute();

        User user = getUserFromUsername(NON_EXSTING_USERNAME);

        assertEquals(NON_EXSTING_USERNAME, user.getUsername());
        assertEquals(NON_EXISTING_PASSWORD, user.getPasswd());
        assertEquals(NON_EXISTING_NAME, user.getName());
        assertTrue("Root session was not updated", hasSessionUpdated(ROOT_TOKEN));
    }

    @Test(expected = UserAlreadyExistsException.class)
    public void usernameExists() throws BubbledocsException {
    	new CreateUser(ROOT_TOKEN, EXISTING_USERNAME, UNAUTHORIZED_PASSWORD, UNAUTHORIZED_NAME).execute();
    }
    
    @Test(expected = CreateRootException.class)
    public void createRoot() throws BubbledocsException {
    	new CreateUser(ROOT_TOKEN, ROOT_USERNAME, EXISTING_PASSWORD, EXISTING_NAME).execute();
    }
    
    @Test
    public void rootFailSessionUpdate() throws UserNotInSessionException, InvalidSessionTimeException {
    	try {
			new CreateUser(ROOT_TOKEN, EXISTING_USERNAME, UNAUTHORIZED_PASSWORD, UNAUTHORIZED_NAME).execute();
		} catch (BubbledocsException e) {
			boolean isSessionUpdated = hasSessionUpdated(ROOT_TOKEN);
			assertTrue("Root Session was not updated", isSessionUpdated);
			return;
		}	
    	assertTrue("Root Session was not Updated", false);
    }

    @Test(expected = EmptyUsernameException.class)
    public void emptyUsername() throws BubbledocsException {
        new CreateUser(ROOT_TOKEN, EMPTY_USERNAME, UNAUTHORIZED_PASSWORD, UNAUTHORIZED_NAME).execute();
    }
    
    @Test(expected = EmptyPasswordException.class)
    public void emptyPassword() throws BubbledocsException {
        new CreateUser(ROOT_TOKEN, UNAUTHORIZED_USERNAME, EMPTY_PASSWORD, UNAUTHORIZED_NAME).execute();
    }
    
    @Test(expected = EmptyNameException.class)
    public void emptyName() throws BubbledocsException {
        new CreateUser(ROOT_TOKEN, UNAUTHORIZED_USERNAME, UNAUTHORIZED_PASSWORD, EMPTY_NAME).execute();
    }

    @Test(expected = UnauthorizedUserException.class)
    public void unauthorizedUserCreation() throws BubbledocsException {
        new CreateUser(UNAUTHORIZED_TOKEN, EXISTING_USERNAME, EMPTY_PASSWORD, EMPTY_NAME).execute();
    }
    
    @Test
    public void unauthorizedFailSessionUpdate() throws BubbledocsException {
    	try {
			new CreateUser(UNAUTHORIZED_TOKEN, EXISTING_USERNAME, UNAUTHORIZED_PASSWORD, UNAUTHORIZED_NAME).execute();
		} catch (BubbledocsException e) {
			boolean isSessionUpdated = hasSessionUpdated(UNAUTHORIZED_TOKEN);
			assertTrue("Unauthorized Session was not updated", isSessionUpdated);
			return;
		}	
    	assertTrue("Unauthorized Session was not Updated", false);
    }


    @Test(expected = UserNotInSessionException.class)
    public void accessUsernameNotExist() throws BubbledocsException {
    	removeUserFromSession(ROOT_TOKEN);
        new CreateUser(ROOT_TOKEN, EMPTY_USERNAME, EMPTY_PASSWORD, EMPTY_NAME).execute();
    }
}
