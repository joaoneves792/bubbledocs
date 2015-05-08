package pt.ulisboa.tecnico.bubbledocs.service.integration.component;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import mockit.Expectations;
import mockit.Mocked;
import pt.ulisboa.tecnico.bubbledocs.domain.Bubbledocs;
import pt.ulisboa.tecnico.bubbledocs.domain.User;
import pt.ulisboa.tecnico.bubbledocs.exceptions.BubbledocsException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.EmptyUsernameException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.RemoteInvocationException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.RootRemoveException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UnauthorizedUserException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UnavailableServiceException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UserNotFoundException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UserNotInSessionException;
import pt.ulisboa.tecnico.bubbledocs.service.BubbledocsServiceTest;
import pt.ulisboa.tecnico.bubbledocs.service.integrator.DeleteUserIntegrator;
import pt.ulisboa.tecnico.bubbledocs.service.remote.IDRemoteServices;

public class DeleteUserIntegratorTest extends BubbledocsServiceTest {

	private static final String ROOT_USERNAME = "root";
	private static final String UNAUTHORIZED_USERNAME  = "hermaeus";
    private static final String EXISTING_USERNAME      = "mehrunes";
    private static final String NON_EXISTING_USERNAME  = "molag";
    private static final String UNAUTHORIZED_EMAIL = "hermaeus@apocrypha.oblivion";
    private static final String EXISTING_EMAIL     = "mehrunes@deadlands.oblivion";
    private static final String UNAUTHORIZED_NAME = "Hermaeus Mora";
    private static final String EXISTING_NAME     = "Mehrunes Dagon";
    private static final String EMPTY_USERNAME = "";
	
	private static final String SPREADSHEET_NAME = "Argonian Account Book";
	private static final Integer SPREADSHEET_ROWS = 42;
	private static final Integer SPREADSHEET_COLUMNS = 42;
	
	private String rootToken;
	private String userToken;
	private String unauthorizedToken;
	
	@Mocked
	private IDRemoteServices sdId;
	
	@Override
	protected void initializeDomain() {
		try{
		User userToDelete = createUser(EXISTING_USERNAME, EXISTING_EMAIL, EXISTING_NAME);
		createUser(UNAUTHORIZED_USERNAME, UNAUTHORIZED_EMAIL, UNAUTHORIZED_NAME);
		Bubbledocs.getBubbledocs().getSuperUser(); 
		
			 rootToken = addUserToSession(ROOT_USERNAME);
			 userToken = addUserToSession(EXISTING_USERNAME);
			 unauthorizedToken = addUserToSession(UNAUTHORIZED_USERNAME);
			 
			 createSpreadSheet(userToDelete, SPREADSHEET_NAME, SPREADSHEET_ROWS, SPREADSHEET_COLUMNS);
		 }catch (BubbledocsException e) {
				assertTrue("Failed to populate domain for DeleteUserTest", false);
			}
	}

	@Test(expected = UserNotFoundException.class)
    public void userToDeleteDoesNotExist() throws BubbledocsException {
    	DeleteUserIntegrator integrator = new DeleteUserIntegrator(rootToken, NON_EXISTING_USERNAME);
    	
     	integrator.execute();
    }
	
	//Test case 2
    @Test
    public void success() throws BubbledocsException {
    	
    	DeleteUserIntegrator integrator = new DeleteUserIntegrator(rootToken, EXISTING_USERNAME);
    	
    	new Expectations() {
            {
                sdId.removeUser(EXISTING_USERNAME);
            }
        };
    	
        integrator.execute();
        
        boolean isUserDeleted = false;
        try {
        	Bubbledocs.getBubbledocs().getUserByUsername(EXISTING_USERNAME);
        } catch (UserNotFoundException e) {
        	isUserDeleted = true;
        }
        
        assertTrue("User was not Deleted", isUserDeleted);
        assertTrue("Root session was not updated", hasSessionUpdated(rootToken));  
        assertNull("Spreadsheet was not Deleted", getSpreadSheet(SPREADSHEET_NAME));
        assertTrue("Permissions were not Deleted", getPermissionsByUser(EXISTING_USERNAME).isEmpty());
        assertNull("Session was not Deleted", getUserFromSession(userToken));
        
    }
    
    @Test
    public void successUserNotInSession() throws BubbledocsException {
    	removeUserFromSession(userToken);
    	success();     
    }
    
    
    @Test(expected = EmptyUsernameException.class)
    public void emptyUsername() throws BubbledocsException {
    	
    	DeleteUserIntegrator integrator = new DeleteUserIntegrator(rootToken, EMPTY_USERNAME);
    	
    	integrator.execute();
    }
    
    
    @Test(expected = UserNotInSessionException.class)
    public void rootNotInSession() throws BubbledocsException {
        removeUserFromSession(rootToken);
        
        DeleteUserIntegrator integrator = new DeleteUserIntegrator(rootToken, EXISTING_USERNAME);
        
        integrator.execute();
    }
    
    
    @Test(expected = RootRemoveException.class)
    public void rootUsername() throws BubbledocsException {
    	
    	DeleteUserIntegrator integrator = new DeleteUserIntegrator(rootToken, ROOT_USERNAME);
    	
    	integrator.execute();
    }
    
    
    @Test(expected = UnauthorizedUserException.class)
    public void notRootUser() throws BubbledocsException {
        new DeleteUserIntegrator(unauthorizedToken, EXISTING_USERNAME).execute();    	
    }

    
    @Test//(expected = UnavailableServiceException.class)
    public void removeUserWithUnavailableSDID() throws BubbledocsException {
    	DeleteUserIntegrator integrator = new DeleteUserIntegrator(rootToken, EXISTING_USERNAME);
    	
        new Expectations() {
            {
                sdId.removeUser(EXISTING_USERNAME);
                result = new RemoteInvocationException("SD-ID offline");
            }
        };
        try{
        	integrator.execute();
        }catch(UnavailableServiceException e){
        	User user = Bubbledocs.getBubbledocs().getUserByUsername(EXISTING_USERNAME);

        	assertEquals(EXISTING_USERNAME, user.getUsername());
        	assertNull(user.getPasswd());
        	assertEquals(EXISTING_NAME, user.getName());
        	assertEquals(EXISTING_EMAIL, user.getEmail());
        	return;
        }
        assertTrue("removeUserWithUnavailableSDID failed", false);

    }
}

