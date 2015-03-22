package pt.ulisboa.tecnico.bubbledocs.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import pt.ulisboa.tecnico.bubbledocs.domain.Bubbledocs;
import pt.ulisboa.tecnico.bubbledocs.domain.User;
import pt.ulisboa.tecnico.bubbledocs.exceptions.BubbledocsException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.EmptyNameException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.EmptyPasswordException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.EmptyUsernameException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidSessionTimeException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UnauthorizedUserException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UserAlreadyExistsException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UserNotInSessionException;
import pt.ulisboa.tecnico.bubbledocs.service.CreateUser;

public class CreateUserTest extends BubbledocsServiceTest {

	private static final String ROOT_PASSWORD = "root";
    private static final String ROOT_USERNAME = "root";
    @SuppressWarnings("unused")
	private static final String ROOT_NAME = "Super User";

    private static final String UNAUTHORIZED_USERNAME = "cv";
    private static final String EXISTING_USERNAME     = "md";
    private static final String NON_EXISTING_USERNAME  = "mb";

    private static final String UNAUTHORIZED_PASSWORD = "vile";
    private static final String EXISTING_PASSWORD     = "dagon";
    private static final String NON_EXISTING_PASSWORD = "bal";

    private static final String UNAUTHORIZED_NAME = "Clavicus Vile";
    private static final String EXISTING_NAME     = "Mehrunes Dagon";
    private static final String NON_EXISTING_NAME = "Molag Bal";

    private static final String EMPTY_NAME     = "";
    private static final String EMPTY_USERNAME = "";
    private static final String EMPTY_PASSWORD = "";
    
    private String rootToken;
	private String unauthorizedUserToken;
    
    @Override
    public void initializeDomain() {
    	//createUser(ROOT_USERNAME, ROOT_PASSWORD, ROOT_NAME);
    	Bubbledocs.getBubbledocs().getSuperUser();
    	createUser(EXISTING_USERNAME, EXISTING_PASSWORD, EXISTING_NAME);
    	createUser(UNAUTHORIZED_USERNAME, UNAUTHORIZED_PASSWORD, UNAUTHORIZED_NAME);
    	
    	try {
			rootToken = addUserToSession(ROOT_USERNAME, ROOT_PASSWORD);
	    	unauthorizedUserToken = addUserToSession(UNAUTHORIZED_USERNAME, UNAUTHORIZED_PASSWORD);
		} catch (BubbledocsException e) {
			assertTrue("Failed to populate domain for CreateUseTest", false);
		}
    }

    @Test
    public void success() throws BubbledocsException {
        CreateUser service = new CreateUser(rootToken, NON_EXISTING_USERNAME, NON_EXISTING_PASSWORD, NON_EXISTING_NAME);
        service.execute();

        User user = Bubbledocs.getBubbledocs().getUserByUsername(NON_EXISTING_USERNAME);

        assertEquals(NON_EXISTING_USERNAME, user.getUsername());
        assertEquals(NON_EXISTING_PASSWORD, user.getPasswd());
        assertEquals(NON_EXISTING_NAME, user.getName());
        assertTrue("Root session was not updated", hasSessionUpdated(rootToken));
    }

    @Test(expected = UserAlreadyExistsException.class)
    public void usernameExists() throws BubbledocsException {
    	new CreateUser(rootToken, EXISTING_USERNAME, UNAUTHORIZED_PASSWORD, UNAUTHORIZED_NAME).execute();
    }
    
    @Test(expected = UserAlreadyExistsException.class)
    public void createRoot() throws BubbledocsException {
    	new CreateUser(rootToken, ROOT_USERNAME, EXISTING_PASSWORD, EXISTING_NAME).execute();
    }
    
    @Test
    public void rootFailSessionUpdate() throws UserNotInSessionException, InvalidSessionTimeException {
    	try {
			new CreateUser(rootToken, EXISTING_USERNAME, UNAUTHORIZED_PASSWORD, UNAUTHORIZED_NAME).execute();
		} catch (BubbledocsException e) {
			boolean isSessionUpdated = hasSessionUpdated(rootToken);
			assertTrue("Root Session was not updated", isSessionUpdated);
			return;
		}	
    	assertTrue("Root Session was not Updated", false);
    }

    @Test(expected = EmptyUsernameException.class)
    public void emptyUsername() throws BubbledocsException {
        new CreateUser(rootToken, EMPTY_USERNAME, NON_EXISTING_PASSWORD, NON_EXISTING_NAME).execute();
    }
    
    @Test(expected = EmptyPasswordException.class)
    public void emptyPassword() throws BubbledocsException {
        new CreateUser(rootToken, NON_EXISTING_USERNAME, EMPTY_PASSWORD, NON_EXISTING_NAME).execute();
    }
    
    @Test(expected = EmptyNameException.class)
    public void emptyName() throws BubbledocsException {
        new CreateUser(rootToken, NON_EXISTING_USERNAME, NON_EXISTING_PASSWORD, EMPTY_NAME).execute();
    }

    @Test(expected = UnauthorizedUserException.class)
    public void unauthorizedUserCreation() throws BubbledocsException {
        new CreateUser(unauthorizedUserToken, NON_EXISTING_USERNAME, NON_EXISTING_PASSWORD, NON_EXISTING_NAME).execute();
    }
    
    @Test
    public void unauthorizedFailSessionUpdate() throws BubbledocsException {
    	try {
            new CreateUser(unauthorizedUserToken, NON_EXISTING_USERNAME, NON_EXISTING_PASSWORD, NON_EXISTING_NAME).execute();
		} catch (BubbledocsException e) {
			boolean isSessionUpdated = hasSessionUpdated(unauthorizedUserToken);
			assertTrue("Unauthorized Session was not updated", isSessionUpdated);
			return;
		}	
    	assertTrue("Unauthorized Session was not Updated", false);
    }


    @Test(expected = UserNotInSessionException.class)
    public void accessUsernameNotExist() throws BubbledocsException {
    	removeUserFromSession(rootToken);
        new CreateUser(rootToken, NON_EXISTING_USERNAME, NON_EXISTING_PASSWORD, NON_EXISTING_NAME).execute();
    }
}
