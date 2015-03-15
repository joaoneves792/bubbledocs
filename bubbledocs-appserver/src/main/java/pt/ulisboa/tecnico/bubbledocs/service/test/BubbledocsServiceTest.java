package pt.ulisboa.tecnico.bubbledocs.service.test;

import java.util.Set;

import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;

import org.junit.After;
import org.junit.Before;

import pt.ist.fenixframework.FenixFramework;
import pt.ist.fenixframework.core.WriteOnReadError;
import pt.ulisboa.tecnico.bubbledocs.domain.Bubbledocs;
import pt.ulisboa.tecnico.bubbledocs.domain.Session;
import pt.ulisboa.tecnico.bubbledocs.domain.Spreadsheet;
import pt.ulisboa.tecnico.bubbledocs.domain.User;
import pt.ulisboa.tecnico.bubbledocs.exceptions.BubbledocsException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UserNotFoundException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UserNotInSessionException;
import pt.ulisboa.tecnico.bubbledocs.service.LoginUser;

// add needed import declarations

public class BubbledocsServiceTest {

    @Before
    public void setUp() throws Exception {

        try {
            FenixFramework.getTransactionManager().begin(false);
            populate4Test();
        } catch (WriteOnReadError | NotSupportedException | SystemException e1) {
            e1.printStackTrace();
        }
    }

    @After
    public void tearDown() {
        try {
            FenixFramework.getTransactionManager().rollback();
        } catch (IllegalStateException | SecurityException | SystemException e) {
            e.printStackTrace();
        }
    }

    // should redefine this method in the subclasses if it is needed to specify
    // some initial state
    public void populate4Test() {
    }

    // auxiliary methods that access the domain layer and are needed in the test classes
    // for defining the initial state and checking that the service has the expected behavior
    User createUser(String username, String password, String name) {
	// add code here
    	return null; 
    }

    public Spreadsheet createSpreadSheet(User user, String name, int row, int column) {
        Bubbledocs bubble = Bubbledocs.getBubbledocs();
        return bubble.createSpreadsheet(user, name, row, column);
    }

    // returns a spreadsheet whose name is equal to name
    //THIS IS BAD since there can be more than one spreadsheet with the same name...
    //FIXME This is just a quick hack
    public Spreadsheet getSpreadSheet(String name) {
    	Bubbledocs bubble = Bubbledocs.getBubbledocs();
    	Set<Spreadsheet> spreadsheets = bubble.getSpreadsheetSet();
    	for(Spreadsheet s : spreadsheets)
    		if(s.get_name().equals(name))
    			return s;
    	return null;    	
    }

    // returns the user registered in the application whose username is equal to username
    User getUserFromUsername(String username) throws UserNotFoundException {
    	Bubbledocs bubble = Bubbledocs.getBubbledocs();
    	return bubble.getUserByUsername(username);
    }

    // put a user into session and returns the token associated to it
    String addUserToSession(String username, String password) throws BubbledocsException {
        LoginUser service = new LoginUser(username, password);
        service.execute();
    	return service.getUserToken();
    }

    // remove a user from session given its token
    void removeUserFromSession(String token) throws UserNotInSessionException {
    	Bubbledocs bubble = Bubbledocs.getBubbledocs();
    	Session session = bubble.getSessionByToken(token);
    	bubble.clearSession(session);    	
    }

    // return the user registered in session whose token is equal to token
    User getUserFromSession(String token) throws UserNotInSessionException, UserNotFoundException {
       	Bubbledocs bubble = Bubbledocs.getBubbledocs();
        return bubble.getUserByUsername(bubble.getSessionByToken(token).get_username());
    }

}
