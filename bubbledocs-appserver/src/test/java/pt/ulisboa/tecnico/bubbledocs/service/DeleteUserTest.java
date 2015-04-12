package pt.ulisboa.tecnico.bubbledocs.service;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import mockit.Expectations;
import mockit.Mocked;

import org.junit.Test;

import pt.ulisboa.tecnico.bubbledocs.domain.Bubbledocs;
import pt.ulisboa.tecnico.bubbledocs.domain.User;
import pt.ulisboa.tecnico.bubbledocs.exceptions.BubbledocsException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.EmptyUsernameException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.LoginBubbleDocsException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.RemoteInvocationException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.RootRemoveException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UnauthorizedUserException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UserNotFoundException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UserNotInSessionException;
import pt.ulisboa.tecnico.bubbledocs.service.DeleteUser;
import pt.ulisboa.tecnico.bubbledocs.service.remote.IDRemoteServices;

// add needed import declarations

public class DeleteUserTest extends BubbledocsServiceTest {

    private String rootToken          = "";
    private String existingToken      = "";
    private String unauthorizedToken  = "";
    @SuppressWarnings("unused")
	private static final String NON_EXISTING_TOKEN  = "hm2";
 
    private static final String ROOT_USERNAME          = "root";
    private static final String EXISTING_USERNAME      = "md";
    private static final String NON_EXISTING_USERNAME  = "mb";
    private static final String UNAUTHORIZED_USERNAME  = "cv";

    private static final String UNAUTHORIZED_EMAIL = "hermaeus@apocrypha.oblivion";
    private static final String EXISTING_EMAIL     = "mehrunes@deadlands.oblivion";

    private static final String EXISTING_NAME      = "Mehrunes Dagon";
    private static final String UNAUTHORIZED_NAME  = "Clavicus Vile";

    private static final String EMPTY_USERNAME = "";
    
    private static final String SPREADSHEET_NAME = "Argonian Account Book";
    private static final Integer SPREADSHEET_ROWS = 42;
    private static final Integer SPREADSHEET_COLUMNS = 42;

    @Mocked
	private IDRemoteServices sdId;
    
    @Override
    public void initializeDomain() {
    	
    	User userToDelete       = createUser(EXISTING_USERNAME, EXISTING_EMAIL, EXISTING_NAME);
        createUser(UNAUTHORIZED_USERNAME, UNAUTHORIZED_EMAIL, UNAUTHORIZED_NAME);
        
        createSpreadSheet(userToDelete, SPREADSHEET_NAME, SPREADSHEET_ROWS, SPREADSHEET_COLUMNS);
        
        try {
			rootToken          = addUserToSession(ROOT_USERNAME);
			existingToken      = addUserToSession(EXISTING_USERNAME);
	        unauthorizedToken  = addUserToSession(UNAUTHORIZED_USERNAME);
		} catch (BubbledocsException e) {
			e.printStackTrace();
		}
    }

    
    //Test case 1 
    @Test(expected = LoginBubbleDocsException.class)
    public void userToDeleteDoesNotExist() throws BubbledocsException {
    	DeleteUser service = new DeleteUser(rootToken, NON_EXISTING_USERNAME);
    	
    	new Expectations() {
            {
                sdId.removeUser(NON_EXISTING_USERNAME);
                result = new LoginBubbleDocsException("Invalid Username");
            }
        };
    	
    	service.execute();
    }
    
    //Test case 2 
    @Test
    public void success() throws BubbledocsException {
    	
    	DeleteUser service = new DeleteUser(rootToken, EXISTING_USERNAME);
    	
    	new Expectations() {
            {
                sdId.removeUser(EXISTING_USERNAME);
            }
        };
    	
        service.execute();
        
        boolean isUserDeleted = false;
        try {
        	Bubbledocs.getBubbledocs().getUserByUsername(EXISTING_USERNAME);
        } catch (UserNotFoundException e) {
        	isUserDeleted = true;
        }
        
        assertTrue("User was not Deleted", isUserDeleted);
        assertNull("Spreadsheet was not Deleted", getSpreadSheet(SPREADSHEET_NAME));
        assertNull("Session was not Deleted", getUserFromSession(existingToken));
        assertTrue("Permissions were not Deleted", getPermissionsByUser(EXISTING_USERNAME).isEmpty());
        assertTrue("Root session was not updated", hasSessionUpdated(rootToken));     
    }
    
    //Test case 3
    @Test(expected = EmptyUsernameException.class)
    public void emptyUsername() throws BubbledocsException {
    	
    	DeleteUser service = new DeleteUser(rootToken, EMPTY_USERNAME);
    	
    	new Expectations() {
            {
                sdId.removeUser(EMPTY_USERNAME);
                result = new LoginBubbleDocsException("Empty Username");
            }
        };
    	
    	service.execute();
    }
    
    //Test case 4
    @Test(expected = UserNotInSessionException.class)
    public void rootNotInSession() throws BubbledocsException {
        removeUserFromSession(rootToken);
        
        DeleteUser service = new DeleteUser(rootToken, EXISTING_USERNAME);
        
        service.execute();
    }
    
    //Test case 5
    @Test(expected = RootRemoveException.class)
    public void rootUsername() throws BubbledocsException {
    	
    	DeleteUser service = new DeleteUser(rootToken, ROOT_USERNAME);
    	
    	service.execute();
    }

    //Test case 6
    @Test(expected = UnauthorizedUserException.class)
    public void notRootUser() throws BubbledocsException {
        new DeleteUser(unauthorizedToken, EXISTING_USERNAME).execute();    	
    }
    
    //Test Case 7
    @Test(expected = RemoteInvocationException.class)
    public void removeUserWithUnavailableSDID() throws BubbledocsException {
    	DeleteUser service = new DeleteUser(rootToken, EXISTING_USERNAME);
    	
        new Expectations() {
            {
                sdId.removeUser(EXISTING_USERNAME);
                result = new RemoteInvocationException("SD-ID offline");
            }
        };
        
        service.execute();
    }
    
    @Test
    public void successToDeleteIsNotInSession() throws BubbledocsException {
    	removeUserFromSession(existingToken);
        success();
    }
    
    @Test
    public void rootFailSessionUpdate() throws BubbledocsException {
    	
    	new Expectations() {
            {
                sdId.removeUser(NON_EXISTING_USERNAME);
                result = new LoginBubbleDocsException("Username does not exist");
            }
        };
    	
    	try {
    		new DeleteUser(rootToken, NON_EXISTING_USERNAME).execute();
    	} catch (BubbledocsException e) {
    		boolean isSessionUpdated = hasSessionUpdated(rootToken);
    		assertTrue("Root Session was not updated", isSessionUpdated);
    		return;    	
    	}
    	assertTrue("Root Session was not Updated", false);
    }
    
    @Test
    public void unauthorizedFailSessionUpdate() throws BubbledocsException {
    	try {
    		new DeleteUser(unauthorizedToken, EXISTING_USERNAME).execute();
    	} catch (BubbledocsException e) {
    		boolean isSessionUpdated = hasSessionUpdated(unauthorizedToken);
    		assertTrue("Unauthorized Session was not updated", isSessionUpdated);
    		return;
    	}
    	assertTrue("Unauthorized Session was not Updated", false);
    }
}
