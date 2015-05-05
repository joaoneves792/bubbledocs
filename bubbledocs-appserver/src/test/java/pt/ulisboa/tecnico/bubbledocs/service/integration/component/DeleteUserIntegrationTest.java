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
import pt.ulisboa.tecnico.bubbledocs.exceptions.LoginBubbleDocsException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.RemoteInvocationException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.RootRemoveException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UnauthorizedUserException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UnavailableServiceException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UserNotFoundException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UserNotInSessionException;
import pt.ulisboa.tecnico.bubbledocs.service.BubbledocsServiceTest;
import pt.ulisboa.tecnico.bubbledocs.service.integrator.DeleteUserIntegrator;
import pt.ulisboa.tecnico.bubbledocs.service.remote.IDRemoteServices;

public class DeleteUserIntegrationTest extends BubbledocsServiceTest {

	private static final String EXISTING_USERNAME = "joao69";
	private static final String NAME ="joao";
	private static final String EMAIL = "joao69@live.com";
	private static final String NON_EXISTING_USERNAME = "sbr2014";
	private static final String EMPTY_USERNAME = "";
	private static final String ROOT_USERNAME = "root";
	
	private String rootToken;
	private String userToken;
	private String unauthorizedToken;
	
	@Mocked
	private IDRemoteServices sdId;
	
	@Override
	protected void initializeDomain() {
		createUser(EXISTING_USERNAME, EMAIL, NAME);
		Bubbledocs.getBubbledocs().getSuperUser(); 
		try{
			 rootToken = addUserToSession(ROOT_USERNAME);
			 userToken = addUserToSession(EXISTING_USERNAME);
			 unauthorizedToken = addUserToSession(NON_EXISTING_USERNAME);
		 }catch (BubbledocsException e) {
				assertTrue("Failed to populate domain for DeleteUserTest", false);
			}
	}
	//nao é bubble docs exception?
	@Test(expected = LoginBubbleDocsException.class)
    public void userToDeleteDoesNotExist() throws BubbledocsException {
    	DeleteUserIntegrator integrator = new DeleteUserIntegrator(rootToken, NON_EXISTING_USERNAME);
    	
    	new Expectations() {
            {
                sdId.removeUser(NON_EXISTING_USERNAME);
                result = new LoginBubbleDocsException("Invalid Username");
            }
        };
    	
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
    }
    
    @Test
    public void successUserNotInSession() throws BubbledocsException {
    	removeUserFromSession(userToken);
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
    }
    
    
    @Test(expected = EmptyUsernameException.class)
    public void emptyUsername() throws BubbledocsException {
    	
    	DeleteUserIntegrator integrator = new DeleteUserIntegrator(rootToken, EMPTY_USERNAME);
    	
    	new Expectations() {
            {
                sdId.removeUser(EMPTY_USERNAME);
                result = new LoginBubbleDocsException("Empty Username");
            }
        };
    	
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

    
    @Test(expected = UnavailableServiceException.class)
    public void removeUserWithUnavailableSDID() throws BubbledocsException {
    	DeleteUserIntegrator integrator = new DeleteUserIntegrator(rootToken, EXISTING_USERNAME);
    	
        new Expectations() {
            {
                sdId.removeUser(EXISTING_USERNAME);
                result = new RemoteInvocationException("SD-ID offline");
            }
        };
        
        integrator.execute();
        
        User user = Bubbledocs.getBubbledocs().getUserByUsername(EXISTING_USERNAME);

        assertEquals(EXISTING_USERNAME, user.getUsername());
        assertNull(user.getPasswd());
        assertEquals(NAME, user.getName());
        assertEquals(EMAIL, user.getEmail());

    }
}

