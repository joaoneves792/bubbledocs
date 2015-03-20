package pt.ulisboa.tecnico.bubbledocs.service;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import pt.ulisboa.tecnico.bubbledocs.domain.Bubbledocs;
import pt.ulisboa.tecnico.bubbledocs.domain.Root;
import pt.ulisboa.tecnico.bubbledocs.domain.User;
import pt.ulisboa.tecnico.bubbledocs.exceptions.BubbledocsException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UnauthorizedUserException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UnknownBubbledocsUserException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UserNotFoundException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UserNotInSessionException;
import pt.ulisboa.tecnico.bubbledocs.service.DeleteUser;

// add needed import declarations

public class DeleteUserTest extends BubbledocsServiceTest {

    private static final String ROOT_TOKEN          = "root8";
    private static final int ROOT_TOKEN_INT         = 8;
    private static final String EXISTING_TOKEN      = "md4";
    private static final int EXISTING_TOKEN_INT     = 4;
    private static final String UNAUTHORIZED_TOKEN  = "cv3";
    private static final int UNAUTHORIZED_TOKEN_INT = 3;
 
    private static final String EXISTING_USERNAME     = "md";
    private static final String NON_EXSTING_USERNAME  = "mb";
    private static final String UNAUTHORIZED_USERNAME = "cv";

    private static final String EXISTING_PASSWORD      = "dagon";
    private static final String UNAUTHORIZED_PASSWORD  = "vile";

    private static final String EXISTING_NAME      = "Mehrunes Dagon";
    private static final String UNAUTHORIZED_NAME  = "Clavicus Vile";

    private static final String EMPTY_USERNAME = "";
    
    private static final String SPREADSHEET_NAME = "Argonian Account Book";
    private static final Integer SPREADSHEET_ROWS = 42;
    private static final Integer SPREADSHEET_COLUMNS = 42;

    private static Integer SPREADSHEET_ID;    

    @Override
    public void initializeDomain() {
        Bubbledocs bubble = Bubbledocs.getBubbledocs();
    	User userToDelete       = createUser(EXISTING_USERNAME, EXISTING_PASSWORD, EXISTING_NAME),
        	 unauthorizedUser   = createUser(UNAUTHORIZED_USERNAME, UNAUTHORIZED_PASSWORD, UNAUTHORIZED_NAME);
        Root root = bubble.getSuperUser();
        
        createSpreadSheetIfNotExists(userToDelete, SPREADSHEET_NAME, SPREADSHEET_ROWS, SPREADSHEET_COLUMNS);
        
        addUserToSession(ROOT_TOKEN_INT, root);
        addUserToSession(EXISTING_TOKEN_INT, userToDelete);
        addUserToSession(UNAUTHORIZED_TOKEN_INT, unauthorizedUser);        
    }

    @Test
    public void success() throws BubbledocsException {
    	createUser(EXISTING_USERNAME, EXISTING_PASSWORD, EXISTING_NAME);
    	Bubbledocs bubble = Bubbledocs.getBubbledocs();
    	User userToDelete = bubble.getUserByUsername(EXISTING_USERNAME);
    	createSpreadSheetIfNotExists(userToDelete, SPREADSHEET_NAME, SPREADSHEET_ROWS, SPREADSHEET_COLUMNS);
    	
        new DeleteUser(ROOT_TOKEN, EXISTING_USERNAME).execute();
        
        boolean isUserDeleted = false;

        try {
        	getUserFromUsername(EXISTING_USERNAME);
        } catch(UserNotFoundException e) {
        	isUserDeleted = true;
        } 
        
        assertTrue("user was not deleted", isUserDeleted);
        assertNull("Spreadsheet was not deleted", getSpreadsheetsByName(SPREADSHEET_NAME).isEmpty());
    }

    /*
     * accessUsername exists, is in session and is root toDeleteUsername exists
     * and is not in session
     */
    @Test
    public void successToDeleteIsNotInSession() throws BubbledocsException {
        success();
    }

    /*
     * accessUsername exists, is in session and is root toDeleteUsername exists
     * and is in session Test if user and session are both deleted
     */
    @Test
    public void successToDeleteIsInSession() throws BubbledocsException {
        String token = addUserToSession(USERNAME_TO_DELETE, "smf");
        success();
	assertNull("Removed user but not removed from session", getUserFromSession(token));
    }

    @Test(expected = UnknownBubbledocsUserException.class)
    public void userToDeleteDoesNotExist() throws BubbledocsException {
        new DeleteUser(root, USERNAME_DOES_NOT_EXIST).execute();
    }

    @Test(expected = UnauthorizedUserException.class)
    public void notRootUser() throws BubbledocsException {
        String ars = addUserToSession(USERNAME, PASSWORD);
        new DeleteUser(ars, USERNAME_TO_DELETE).execute();
    }

    @Test(expected = UserNotInSessionException.class)
    public void rootNotInSession() throws BubbledocsException {
        removeUserFromSession(root);

        new DeleteUser(root, USERNAME_TO_DELETE).execute();
    }

    @Test(expected = UserNotInSessionException.class)
    public void notInSessionAndNotRoot() throws BubbledocsException {
        String ars = addUserToSession(USERNAME, PASSWORD);
        removeUserFromSession(ars);

        new DeleteUser(ars, USERNAME_TO_DELETE).execute();
    }

    @Test(expected = UserNotInSessionException.class)
    public void accessUserDoesNotExist() throws BubbledocsException {
        new DeleteUser(USERNAME_DOES_NOT_EXIST, USERNAME_TO_DELETE).execute();
    }
}
