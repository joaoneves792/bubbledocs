package pt.ulisboa.tecnico.bubbledocs.service.integration.component;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import mockit.Expectations;
import mockit.Mocked;

import org.junit.Test;

import pt.ulisboa.tecnico.bubbledocs.domain.Bubbledocs;
import pt.ulisboa.tecnico.bubbledocs.domain.User;
import pt.ulisboa.tecnico.bubbledocs.exceptions.BubbledocsException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.DuplicateEmailException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.DuplicateUsernameException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.EmptyNameException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidEmailException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidUsernameException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.RemoteInvocationException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UnauthorizedUserException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UnavailableServiceException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UserAlreadyExistsException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UserNotFoundException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UserNotInSessionException;
import pt.ulisboa.tecnico.bubbledocs.service.BubbledocsServiceTest;
import pt.ulisboa.tecnico.bubbledocs.service.integrator.CreateUserIntegrator;
import pt.ulisboa.tecnico.bubbledocs.service.remote.IDRemoteServices;

public class CreateUserIntegratorTest extends BubbledocsServiceTest {

	private static final String ROOT_USERNAME = "root";
	private static final String ROOT_NAME     = "Super User";
    private static final String ROOT_EMAIL    = "root@bubbledocs.tecnico.ulisboa.pt";


    private static final String UNAUTHORIZED_USERNAME  = "hermaeus";
    private static final String EXISTING_USERNAME      = "mehrunes";
    private static final String NON_EXISTING_USERNAME  = "molag";
    private static final String USERNAME_TOO_SHORT     = "md";
    private static final String USERNAME_TOO_LONG      = "mehrunesdagon";
    
    private static final String UNAUTHORIZED_EMAIL = "hermaeus@apocrypha.oblivion";
    private static final String EXISTING_EMAIL     = "mehrunes@deadlands.oblivion";
    private static final String NON_EXISTING_EMAIL = "molag@cold-harbour.oblivion";

    private static final String UNAUTHORIZED_NAME = "Hermaeus Mora";
    private static final String EXISTING_NAME     = "Mehrunes Dagon";
    private static final String NON_EXISTING_NAME = "Molag Bal";

    private static final String EMPTY_NAME     = "";
    private static final String EMPTY_USERNAME = "";
	private static final String EMPTY_EMAIL    = "";
        
	private static final String INVALID_EMAIL = "dovahkin@helgen.skyrim";
	
    private String rootToken;
	private String unauthorizedUserToken;
	
	@Mocked
	private IDRemoteServices sdId;
    
    @Override
    public void initializeDomain() {

    	try {
    	Bubbledocs.getBubbledocs().getSuperUser();
    	createUser(EXISTING_USERNAME, EXISTING_EMAIL, EXISTING_NAME);
    	createUser(UNAUTHORIZED_USERNAME, UNAUTHORIZED_EMAIL, UNAUTHORIZED_NAME);
    	
    	
			rootToken = addUserToSession(ROOT_USERNAME);
	    	unauthorizedUserToken = addUserToSession(UNAUTHORIZED_USERNAME);
		} catch (BubbledocsException e) {
			assertTrue("Failed to populate domain for CreateUseTest", false);
		}
    }

    //Test Case 1
    @Test
    public void success() throws BubbledocsException {
        CreateUserIntegrator service = new CreateUserIntegrator(rootToken, NON_EXISTING_USERNAME, NON_EXISTING_EMAIL, NON_EXISTING_NAME);
        
        new Expectations() {
        	{
        		sdId.createUser(NON_EXISTING_USERNAME, NON_EXISTING_EMAIL);
        	}
        };
        
        service.execute();

        User user = Bubbledocs.getBubbledocs().getUserByUsername(NON_EXISTING_USERNAME);

        assertEquals(NON_EXISTING_USERNAME, user.getUsername());
        assertNull(user.getPasswd());
        assertEquals(NON_EXISTING_NAME, user.getName());
        assertEquals(NON_EXISTING_EMAIL, user.getEmail());
        assertTrue("Root session was not updated", hasSessionUpdated(rootToken));
    }
    
    //Test Case 2.1
    @Test(expected = InvalidUsernameException.class)
    public void usernameTooShort() throws BubbledocsException {

    	new CreateUserIntegrator(rootToken, USERNAME_TOO_SHORT, NON_EXISTING_EMAIL, NON_EXISTING_NAME).execute();    	
    }
    
    //Test Case 2.2
    @Test(expected = InvalidUsernameException.class)
    public void usernameTooLong() throws BubbledocsException {

    	new CreateUserIntegrator(rootToken, USERNAME_TOO_LONG, NON_EXISTING_EMAIL, NON_EXISTING_NAME).execute();
    }

    //Test Case 3.1
    @Test
    public void usernameExists() throws BubbledocsException {
    	try {
			new CreateUserIntegrator(rootToken, EXISTING_USERNAME, EXISTING_EMAIL, EXISTING_NAME).execute();
		} catch (UserAlreadyExistsException e) {
			boolean isSessionUpdated = hasSessionUpdated(rootToken);
			assertTrue("Root Session was not updated", isSessionUpdated);
			return;
		}	
    	assertTrue("Expected UserAlreadyExistsException but got none", false);
    }
    
    //Test Case 3.1.1
    @Test(expected = UserAlreadyExistsException.class)
    public void createRoot() throws BubbledocsException {
    	
    	new CreateUserIntegrator(rootToken, ROOT_USERNAME, NON_EXISTING_EMAIL, ROOT_NAME).execute();
    }

    //Test Case 3.2
    @Test(expected = DuplicateEmailException.class)
    public void emailExists() throws BubbledocsException {
    	new Expectations() {
    		{
    			sdId.createUser(NON_EXISTING_USERNAME, EXISTING_EMAIL);
    			result = new DuplicateEmailException("");
    		}
    	};
    	new CreateUserIntegrator(rootToken, NON_EXISTING_USERNAME, EXISTING_EMAIL, NON_EXISTING_NAME).execute();
    }
    
    //Test Case 3.2.1
    @Test(expected = DuplicateEmailException.class)
    public void createRootEmail() throws BubbledocsException {
    	
    	new Expectations() {
    		{
    			sdId.createUser(NON_EXISTING_USERNAME, ROOT_EMAIL);
    			result = new DuplicateEmailException("");
    		}
    	};
    	
    	new CreateUserIntegrator(rootToken, NON_EXISTING_USERNAME, ROOT_EMAIL, NON_EXISTING_NAME).execute();
    }
    
    //Test Case 4
    @Test(expected = UserNotFoundException.class)
    public void remoteIdUnavailable() throws BubbledocsException {
    	
    	new Expectations() {
    		{
    			sdId.createUser(NON_EXISTING_USERNAME, NON_EXISTING_EMAIL);
    			result = new RemoteInvocationException("");
    		}
    	};
    	
    	try{
    		new CreateUserIntegrator(rootToken, NON_EXISTING_USERNAME, NON_EXISTING_EMAIL, NON_EXISTING_NAME).execute();
    	}catch(UnavailableServiceException e){
    		Bubbledocs.getBubbledocs().getUserByUsername(NON_EXISTING_USERNAME); //We want this to fail
    	}
    	
    }
    //Test Case 5
    @Test(expected = InvalidUsernameException.class)
    public void emptyUsername() throws BubbledocsException {
    	   	
        new CreateUserIntegrator(rootToken, EMPTY_USERNAME, NON_EXISTING_EMAIL, NON_EXISTING_NAME).execute();
    }
    
    //Test Case 6
    @Test(expected = EmptyNameException.class)
    public void emptyName() throws BubbledocsException {
        new CreateUserIntegrator(rootToken, NON_EXISTING_USERNAME, NON_EXISTING_EMAIL, EMPTY_NAME).execute();
    }
    
    //Test Case 7
    @Test(expected = InvalidEmailException.class)
    public void emptyEmail() throws BubbledocsException {
    	
        new CreateUserIntegrator(rootToken, NON_EXISTING_USERNAME, EMPTY_EMAIL, NON_EXISTING_NAME).execute();
    }

    //Test Case 8
    @Test(expected = UserNotInSessionException.class)
    public void rootNotInSession() throws BubbledocsException {
    	removeUserFromSession(rootToken);
        new CreateUserIntegrator(rootToken, NON_EXISTING_USERNAME, NON_EXISTING_EMAIL, NON_EXISTING_NAME).execute();
    }
    
    //Test Case 9
    @Test
    public void unauthorizedFailSessionUpdate() throws BubbledocsException {
    	try {
            new CreateUserIntegrator(unauthorizedUserToken, NON_EXISTING_USERNAME, NON_EXISTING_EMAIL, NON_EXISTING_NAME).execute();
		} catch (UnauthorizedUserException e) {
			boolean isSessionUpdated = hasSessionUpdated(unauthorizedUserToken);
			assertTrue("Unauthorized Session was not updated", isSessionUpdated);
			return;
		}	
    	assertTrue("Expected UnauthorizedUserException but got none", false);
    }
    
    //Test Case 10
    @Test(expected = InvalidEmailException.class)
    public void invalidEmail() throws BubbledocsException {
    	
    	new Expectations() {
    		{
    			sdId.createUser(NON_EXISTING_USERNAME, INVALID_EMAIL);
    			result = new InvalidEmailException("");
    		}
    	};
    	
    	new CreateUserIntegrator(rootToken, NON_EXISTING_USERNAME, INVALID_EMAIL, NON_EXISTING_NAME).execute();
    }
    
    //Test Case 11
    @Test(expected = DuplicateUsernameException.class)
    public void usernameExistsButOnlyRemotely() throws BubbledocsException {
    	new Expectations() {
    		{
    			sdId.createUser(NON_EXISTING_USERNAME, NON_EXISTING_EMAIL);
    			result = new DuplicateUsernameException("");
    		}
    	};
    	new CreateUserIntegrator(rootToken, NON_EXISTING_USERNAME, NON_EXISTING_EMAIL, NON_EXISTING_NAME).execute();

    }
}
