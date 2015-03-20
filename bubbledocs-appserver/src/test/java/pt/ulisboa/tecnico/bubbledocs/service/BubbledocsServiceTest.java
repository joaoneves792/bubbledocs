package pt.ulisboa.tecnico.bubbledocs.service;

//import java.util.List;
import java.util.Set;

import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;

import org.joda.time.LocalDateTime;
import org.joda.time.Seconds;
import org.junit.After;
import org.junit.Before;

import pt.ist.fenixframework.FenixFramework;
import pt.ist.fenixframework.core.WriteOnReadError;
import pt.ulisboa.tecnico.bubbledocs.domain.Bubbledocs;
import pt.ulisboa.tecnico.bubbledocs.domain.Permission;
import pt.ulisboa.tecnico.bubbledocs.domain.Session;
import pt.ulisboa.tecnico.bubbledocs.domain.Spreadsheet;
import pt.ulisboa.tecnico.bubbledocs.domain.User;
import pt.ulisboa.tecnico.bubbledocs.exceptions.BubbledocsException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidSessionTimeException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.SpreadsheetNotFoundException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UserNotFoundException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UserNotInSessionException;

// add needed import declarations

public abstract class BubbledocsServiceTest {

    @Before
    public void setUp() throws Exception {

        try {
            FenixFramework.getTransactionManager().begin(false);
            initializeDomain();
        } catch (WriteOnReadError | NotSupportedException | SystemException e1) {
            e1.printStackTrace();
        }
    }

    @After
    public void tearDown() {
        try {
            FenixFramework.getTransactionManager().rollback();
        } catch (IllegalStateException | SecurityException | SystemException e) {
        	System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    // should redefine this method in the subclasses if it is needed to specify
    // some initial state
    protected abstract void initializeDomain() ;

    // auxiliary methods that access the domain layer and are needed in the test classes
    // for defining the initial state and checking that the service has the expected behavior
    /** create user if not exists */
    User createUser(String username, String password, String name) {
    	Bubbledocs bubble = Bubbledocs.getBubbledocs();
    	User user = null;
		user = new User(name, username, password);
		bubble.addUser(user);
    	return user;
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
    		if(s.getName().equals(name))
    			return s;
    	return null;    	
    }

    public Spreadsheet getSpreadSheetById(Integer id) throws SpreadsheetNotFoundException {
    	Bubbledocs bubble = Bubbledocs.getBubbledocs();
    	return bubble.getSpreadsheetById(id);    	
    }
    
    /* FIXME suggestion for your hack... */    
    public java.util.List<Spreadsheet> getSpreadsheetsByName(String name) {
    	java.util.List<Spreadsheet> sheets = new java.util.ArrayList<Spreadsheet>();
    	for(Spreadsheet sheet : Bubbledocs.getBubbledocs().getSpreadsheetSet()) {
    		if(sheet.getName().equals(name)) {
    			sheets.add(sheet);
    		}
    	}
    	return sheets;
    }
    
    public Spreadsheet getSpreadsheetById(Integer id) {
    	for(Spreadsheet sheet : Bubbledocs.getBubbledocs().getSpreadsheetSet()) {
    		if(sheet.getId().equals(id))
    			return sheet;
    	}
    	return null;
    }
    
    // returns the user registered in the application whose username is equal to username
    User getUserFromUsername(String username) {
    	Bubbledocs bubble = Bubbledocs.getBubbledocs();
    	try {
			return bubble.getUserByUsername(username);
		} catch (UserNotFoundException e) {
			return null;
		}
    }

    /** put a user into session  and returns the token associated to it   
     */
    String addUserToSession(String username, String password) throws BubbledocsException {
    	Bubbledocs bubble = Bubbledocs.getBubbledocs();
    	//Session session;
    	
    	/*try {
    		session = bubble.getSessionByUsername(username);
    		session.update();
        	return username + session.getTokenInt();
    	} catch (UserNotInSessionException e) {*/
    		int tok = new java.util.Random().nextInt(10);
    		bubble.addSession(new Session(bubble.getUserByUsername(username), tok, org.joda.time.LocalDate.now()));
    		return username + tok;
    	/*}*/    	   	
    }
    
    /**put a user in session (if not already)
     * required because JUnit does not guarantee order of the tests */
    void addUserToSession(Integer tokInt, User user) {
    	Bubbledocs bubble = Bubbledocs.getBubbledocs();
    	//Session session = null;
    	/*try {
    		session = bubble.getSessionByUsername(user.getUsername());  
        	session.update();
    	} catch (UserNotInSessionException e) {*/
    		bubble.addSession(new Session(user, tokInt, org.joda.time.LocalDate.now()));
    	//}
    }

    // remove a user from session given its token
    void removeUserFromSession(String token) throws UserNotInSessionException {
    	Bubbledocs bubble = Bubbledocs.getBubbledocs();
    	Session session = bubble.getSessionByToken(token);
    	bubble.clearSession(session);
    }

    // return the user registered in session whose token is equal to token
    User getUserFromSession(String token) {
       	Bubbledocs bubble = Bubbledocs.getBubbledocs();
        try {
			return bubble.getUserByUsername(bubble.getSessionByToken(token).getUser().getUsername());
		} catch (UserNotFoundException e) {
			return null;
		} catch (UserNotInSessionException e) {
			return null;
		}
    }
    
    java.util.List<Permission> getPermissionsByUser(String username) {
    	Bubbledocs bubble = Bubbledocs.getBubbledocs();
    	java.util.List<Permission> permissions = new java.util.ArrayList<Permission>();
    	for(Permission p : bubble.getPermissionsByUser(username)) {
    		permissions.add(p);
    	}
    	return permissions;
    }
   
    boolean hasSessionUpdated(String token) throws UserNotInSessionException, InvalidSessionTimeException {
    	final int MAXIMUM_ACCEPTABLE_VALUE = 2; //in seconds, plenty of time to execute a service
    	org.joda.time.LocalDateTime time = getLastAccessTimeInSession(token);
    	int difference = Seconds.secondsBetween(time, new org.joda.time.LocalDateTime()).getSeconds();
    	if(difference <= 0)
    		throw new InvalidSessionTimeException("Session Time is incorrectly set.");
    	return difference < MAXIMUM_ACCEPTABLE_VALUE;
    }

    // returns the time of the last access for the user with token userToken.
    // It must get this data from the session object of the application
    protected LocalDateTime getLastAccessTimeInSession(String userToken) throws UserNotInSessionException {
    	Bubbledocs bubble = Bubbledocs.getBubbledocs();
    	Session session = bubble.getSessionByToken(userToken);
    	
    	//Not 100% sure the next line works!!
    	return new LocalDateTime(session.getDate());
    }
}
