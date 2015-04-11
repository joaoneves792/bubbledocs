package pt.ulisboa.tecnico.bubbledocs.service;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import mockit.Expectations;
import mockit.Mocked;

import org.junit.Test;

import pt.ulisboa.tecnico.bubbledocs.domain.Bubbledocs;
import pt.ulisboa.tecnico.bubbledocs.domain.Root;
import pt.ulisboa.tecnico.bubbledocs.domain.User;
import pt.ulisboa.tecnico.bubbledocs.exceptions.BubbledocsException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.EmptyUsernameException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.RemoteInvocationException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.RootRemoveException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UnauthorizedUserException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UnavailableServiceException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UserNotFoundException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UserNotInSessionException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidUsernameException;
import pt.ulisboa.tecnico.bubbledocs.service.DeleteUser;
import pt.ulisboa.tecnico.bubbledocs.service.remote.IDRemoteServices;

// add needed import declarations

public class DeleteUserTest extends BubbledocsServiceTest {

    private static final String ROOT_TOKEN          = "root8";
    private static final int ROOT_TOKEN_INT         = 8;
    private static final String EXISTING_TOKEN      = "md4";
    private static final int EXISTING_TOKEN_INT     = 4;
    private static final String UNAUTHORIZED_TOKEN  = "cv3";
    private static final int UNAUTHORIZED_TOKEN_INT = 3;
    @SuppressWarnings("unused")
	private static final String NON_EXISTING_TOKEN  = "hm2";
 
    private static final String ROOT_USERNAME          = "root";
    private static final String EXISTING_USERNAME      = "mehrunes";
    private static final String NON_EXISTING_USERNAME  = "molag";
    private static final String UNAUTHORIZED_USERNAME  = "hermaeus";
    private static final String USERNAME_TOO_SHORT     = "md";
    private static final String USERNAME_TOO_LONG      = "mehrunesdagon";
    
    private static final String UNAUTHORIZED_EMAIL = "hermaeus@apocrypha.oblivion";
    private static final String EXISTING_EMAIL     = "mehrunes@deadlands.oblivion";

    private static final String EXISTING_NAME      = "Mehrunes Dagon";
    private static final String UNAUTHORIZED_NAME  = "Hermaeus Mora";

    private static final String EMPTY_USERNAME = "";
    
    private static final String SPREADSHEET_NAME = "Argonian Account Book";
    private static final Integer SPREADSHEET_ROWS = 42;
    private static final Integer SPREADSHEET_COLUMNS = 42;

    
    @Mocked
    private IDRemoteServices sdId;
    
    @Override
    public void initializeDomain() {
        Bubbledocs bubble = Bubbledocs.getBubbledocs();
    	User userToDelete       = createUser(EXISTING_USERNAME, EXISTING_EMAIL, EXISTING_NAME),
        	 unauthorizedUser   = createUser(UNAUTHORIZED_USERNAME, UNAUTHORIZED_EMAIL, UNAUTHORIZED_NAME);
        Root root = bubble.getSuperUser();
        
        createSpreadSheet(userToDelete, SPREADSHEET_NAME, SPREADSHEET_ROWS, SPREADSHEET_COLUMNS);
        
        addUserToSession(ROOT_TOKEN_INT, root);
        addUserToSession(EXISTING_TOKEN_INT, userToDelete);
        addUserToSession(UNAUTHORIZED_TOKEN_INT, unauthorizedUser);        
    }

    @Test
    public void success() throws BubbledocsException {    	
        DeleteUser service = new DeleteUser(ROOT_TOKEN, EXISTING_USERNAME);
        
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
        assertNull("Session was not Deleted", getUserFromSession(EXISTING_TOKEN));
        assertTrue("Permissions were not Deleted", getPermissionsByUser(EXISTING_USERNAME).isEmpty());
        assertTrue("Root session was not updated", hasSessionUpdated(ROOT_TOKEN));     
    }


    @Test(expected = InvalidUsernameException.class)
    public void usernameTooShort() throws BubbledocsException {
    	
    	new Expectations() {
    		{
    			sdId.removeUser(USERNAME_TOO_SHORT);
    		}
    	};
    	
    	new DeleteUser(ROOT_TOKEN, USERNAME_TOO_SHORT).execute();    	
    }
    
    @Test(expected = InvalidUsernameException.class)
    public void usernameTooLong() throws BubbledocsException {
    	
    	new Expectations() {
    		{
    			sdId.removeUser(USERNAME_TOO_LONG);
    		}
    	};
    	
    	new DeleteUser(ROOT_TOKEN, USERNAME_TOO_LONG).execute();
    }
    
    
    @Test
    public void successToDeleteIsNotInSession() throws BubbledocsException {
    	removeUserFromSession(EXISTING_TOKEN);
        success();
    }

    @Test(expected = UserNotFoundException.class)
    public void userToDeleteDoesNotExist() throws BubbledocsException {
        new DeleteUser(ROOT_TOKEN, NON_EXISTING_USERNAME).execute();
    }
    
    @Test(expected = UnavailableServiceException.class)
    public void remoteIdUnavailable() throws BubbledocsException {
    	DeleteUser service = new DeleteUser(ROOT_TOKEN, NON_EXISTING_USERNAME);
    	
    	new Expectations() {
    		{
    			sdId.removeUser(NON_EXISTING_USERNAME);
    			result = new RemoteInvocationException("SD-ID offline");
    		}
    	};
    	
    	service.execute();
    }
    
    @Test
    public void rootFailSessionUpdate() throws BubbledocsException {
    	
    	new Expectations() {
    	    		{
    	    			sdId.removeUser(EXISTING_USERNAME);
    	    			result = new RemoteInvocationException("SD-ID Offline");
    	    		}
    	};
    	
    	try {
    		new DeleteUser(ROOT_TOKEN, EXISTING_USERNAME).execute();
    	} catch (BubbledocsException e) {
    		boolean isSessionUpdated = hasSessionUpdated(ROOT_TOKEN);
    		assertTrue("Root Session was not updated", isSessionUpdated);
    		return;    	
    	}
    	assertTrue("Root Session was not Updated", false);
    }
    
    @Test
    public void unauthorizedFailSessionUpdate() throws BubbledocsException {
    	try {
    		new DeleteUser(UNAUTHORIZED_TOKEN, EXISTING_USERNAME).execute();
    	} catch (BubbledocsException e) {
    		boolean isSessionUpdated = hasSessionUpdated(UNAUTHORIZED_TOKEN);
    		assertTrue("Unauthorized Session was not updated", isSessionUpdated);
    		return;
    	}
    	assertTrue("Unauthorized Session was not Updated", false);
    }

    @Test(expected = UnauthorizedUserException.class)
    public void notRootUser() throws BubbledocsException {
        new DeleteUser(UNAUTHORIZED_TOKEN, EXISTING_USERNAME).execute();    	
    }

    @Test(expected = UserNotInSessionException.class)
    public void rootNotInSession() throws BubbledocsException {
        removeUserFromSession(ROOT_TOKEN);
        new DeleteUser(ROOT_TOKEN, EXISTING_USERNAME).execute();
    }
    
    @Test(expected = EmptyUsernameException.class)
    public void emptyUsername() throws BubbledocsException {
    	new DeleteUser(ROOT_TOKEN, EMPTY_USERNAME).execute();
    }
    
    @Test(expected = RootRemoveException.class)
    public void rootUsername() throws BubbledocsException {
    	new DeleteUser(ROOT_TOKEN, ROOT_USERNAME).execute();
    }
}
