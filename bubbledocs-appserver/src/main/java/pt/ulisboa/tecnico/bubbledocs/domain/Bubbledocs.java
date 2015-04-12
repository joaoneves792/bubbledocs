package pt.ulisboa.tecnico.bubbledocs.domain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;

    import org.jdom2.JDOMException;

import pt.ulisboa.tecnico.bubbledocs.exceptions.BubbledocsException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidExportException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidImportException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidLoginException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.PermissionNotFoundException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.ProtectedCellException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.RootRemoveException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.SpreadsheetNotFoundException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UnauthorizedUserException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UserAlreadyExistsException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UserNotFoundException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidCellException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UserNotInSessionException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.EmptySpreadsheetNameException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.OutOfBoundsSpreadsheetException;
import pt.ist.fenixframework.FenixFramework;

    public class Bubbledocs extends Bubbledocs_Base {
        private Bubbledocs() {
             FenixFramework.getDomainRoot().setBubbledocs(this);
             setIdGenerator(new Integer(0));
             //addUser(Root.getRoot());
             //Root.getRoot().setBubbledocs(this);
        }
        
    //private static Bubbledocs theBubbledocs = new Bubbledocs();	
            
    public static Bubbledocs getBubbledocs() {
        Bubbledocs bubble = FenixFramework.getDomainRoot().getBubbledocs();
        if( null == bubble ) {
          bubble = new Bubbledocs();
          //Root.getRoot();
          //bubble.addUser(Root.getRoot());
          //Root.getRoot().setBubbledocs(bubble);
        }
        return bubble;
    }
    
    private int generateId() {
    	int currentId = getIdGenerator();
    	setIdGenerator(currentId + 1);
    	return currentId;
    }

    public User getUserByUsername(String username) throws UserNotFoundException {
    	for(User user : getUserSet()) {
    		if(user.getUsername().equals(username))
    			return user;
    	}
    	throw new UserNotFoundException("User with username " + username + " was not found.");
    }
    
	public Root getSuperUser() {
		Root root;
		try {
			root = (Root) getUserByUsername("root");
		} catch (UserNotFoundException e) {
			root = new Root();
			addUser((User)root);
		}
		return root;
	}


    /**
     * Perform a local login (to be used only if the remote service is unavailable)
     * @param username
     * @param password
     * @throws UserNotFoundException
     * @throws InvalidLoginException
     */
    public void localLogin(String username, String password) throws UserNotFoundException, InvalidLoginException{
    	User user = getUserByUsername(username);
    	
    	if(!password.equals(user.getPasswd()))
    		throw new InvalidLoginException("Unable to login localy: Password mismatch");	
    }
    
    /**
     * Update the local password of a user
     * @param username
     * @param password
     * @throws UserNotFoundException
     */
    public void updateLocalPassword(String username, String password) throws UserNotFoundException{
    	User user = getUserByUsername(username);
    	
    	if(!password.equals(user.getPasswd()))
    		user.setPasswd(password);    	
    }
    
    /**
     * Method to create a session for a user that successfully logged in
     * @param username
     * @return the session token
     * @throws UserNotFoundException
     */
    public String createSession(String username)throws UserNotFoundException{
        int tokenInt;
        User user;
        Session session;
    	
    	//Before anything else perform a check on all Sessions
        performSessionClean();
        
        user = getUserByUsername(username);
        try { 
            session = getSessionByUsername(username);
        } catch(UserNotInSessionException e) {
            //Some code duplication... (but its better than an empty catch block)
        	tokenInt = (new Random()).nextInt(10);
			session = new Session(user, tokenInt, org.joda.time.LocalDate.now());
            addSession(session);
            return username + tokenInt;
        }
        
        return username + session.getTokenInt();
    }
    
    /**
     * Method to get the session belonging to username
     * @param username
     * @return Session or null
     */
    public Session getSessionByUsername(String username) throws UserNotInSessionException {
    	Set<Session> sessions;
    	
     	sessions = getSessionSet();
    	for(Session s : sessions) {
    		if(s.getUser().getUsername().equals(username))
    			return s;
    	}
        throw new UserNotInSessionException("No existing session for " + username + " found!");
    }
    
    /**
     * Method to get the session identified by a Service layer token
     * @param token
     * @return Session
     */
    public Session getSessionByToken(String token) throws UserNotInSessionException {
    	Set<Session> sessions;
    	
    	sessions = getSessionSet();
    	for(Session s : sessions) {
    		if(token.equals(s.getUser().getUsername() + s.getTokenInt()))
    			return s;
    	}
        throw new UserNotInSessionException("No existing session for " + token + " found!");
    }
    
    /**
     * Every time a user logs in we have to delete all other sessions that have expired
     */
    private void performSessionClean()  {
        Set<Session> sessions;

        sessions = getSessionSet();
        for(Session s : sessions)
            if(s.hasExpired())
                clearSession(s);
    }

    /**
     * Clear a session (from persistence)
     * @param Session
     */
    //I really don't think this should be public but its required in BubbledocsServiceTest
    public void clearSession(Session s){
        removeSession(s); 
        s.clean();
    }

    public Spreadsheet getSpreadsheetById(int spreadsheetId) throws SpreadsheetNotFoundException {
    	for(Spreadsheet sheet : getSpreadsheetSet()) {
    		if(sheet.getId() == spreadsheetId)
    			return sheet;
    	}
    	throw new SpreadsheetNotFoundException("No spreadsheet found for ID: " + spreadsheetId);
    }
    
    public Permission getPermission(String username, int spreadsheetId) throws PermissionNotFoundException {
    	for(Permission permission : getPermissionSet()) {
    		if(permission.getUser().getUsername().equals(username) && 
    				permission.getSpreadsheet().getId() == spreadsheetId) {
    			return permission;
    		}
    	}
    	throw new PermissionNotFoundException("Permission for user " + username + " on spreadsheet with ID " + spreadsheetId + " was not found.");
    }
    
    public List<Permission> getPermissionsByUser(String username) {
    	List<Permission>	permissions = new ArrayList<Permission>();
    	for(Permission permission : getPermissionSet()) {
    		if(permission.getUser().getUsername().equals(username))
    			permissions.add(permission);
    	}
    	return Collections.unmodifiableList(permissions);
    }
    
    public List<Permission> getPermissionsBySpreadsheet(int spreadsheetId) {
    	List<Permission>	permissions = new ArrayList<Permission>();
    	for(Permission permission : getPermissionSet()) {
    		if(permission.getSpreadsheet().getId() == spreadsheetId)
    			permissions.add(permission);
    	}
    	return Collections.unmodifiableList(permissions);
    }

    public void addReadPermission(String requestUsername, String granted, int spreadsheetId)
    		throws UserNotFoundException, SpreadsheetNotFoundException, PermissionNotFoundException, UnauthorizedUserException {
    	new AddReadRoutine().execute(requestUsername, granted, spreadsheetId);
    }   

    public void addWritePermission(String requestUsername, String granted, int spreadsheetId)
    		throws UserNotFoundException, SpreadsheetNotFoundException, PermissionNotFoundException, UnauthorizedUserException {
    	new AddWriteRoutine().execute(requestUsername, granted, spreadsheetId);
    }

    public void revokeReadPermission(String requestUsername, String revoked, int spreadsheetId)
    		throws UserNotFoundException, SpreadsheetNotFoundException, PermissionNotFoundException, UnauthorizedUserException {
    	new RevokeReadRoutine().execute(requestUsername, revoked, spreadsheetId);
    }

    public void revokeWritePermission(String requestUsername, String revoked, int spreadsheetId)
    		throws UserNotFoundException, SpreadsheetNotFoundException, PermissionNotFoundException, UnauthorizedUserException {
    	new RevokeWriteRoutine().execute(requestUsername, revoked, spreadsheetId);
    }
        
    /**
     * Method to create a spreadsheet (overload to be called from the service layer)
     * @param userToken
     * @param spreadsheetname
     * @param rows
     * @param comulns
     * @return SpreadsheetID
     */
    public Integer createSpreadsheet(String userToken, String name, int rows, int columns)
        throws UserNotInSessionException, EmptySpreadsheetNameException, OutOfBoundsSpreadsheetException {
        Session session;
        User author;
        Spreadsheet spreadsheet;
        try{
        	session = getSessionByToken(userToken);


        	//Sanity checks 
        	if(name.isEmpty())
        		throw new EmptySpreadsheetNameException("Operation not permited: create a Spreadsheet with an empty name!");
        	if(1 > rows || 1 > columns)
        		throw new OutOfBoundsSpreadsheetException("Operation not permited: create a spreadheet with 0 or less rows/columns!");

            author = getUserByUsername(session.getUser().getUsername());
            spreadsheet = createSpreadsheet(author, name, rows, columns);
            return spreadsheet.getId();
        }catch(UserNotFoundException e){
            throw new UserNotInSessionException("FATAL: there is a session for " + userToken + " but there isnt a user by that name!");
        }
    }  

    public Spreadsheet createSpreadsheet(User author, String name, int rows, int columns) {
    	Spreadsheet spreadsheet = new Spreadsheet(name, author.getUsername(), generateId(), rows, columns);
    	addSpreadsheet(spreadsheet);
    	Permission permission = new Permission(spreadsheet, author, true);
        addPermission(permission);
        //spreadsheet.addPermission(permission);
    	return spreadsheet;
    }
    
    /**
     *  This is the XML import 
     *  @param XMLString
     *  @return the new spreadsheet
     * @throws IOException 
     * @throws JDOMException 
     * @throws UserNotFoundException 
     * @throws InvalidCellException 
     */    
    public Spreadsheet createSpreadsheet(User requestUser, String XMLString) throws InvalidImportException, JDOMException, IOException, UserNotFoundException, InvalidCellException {
    	Spreadsheet spreadsheet = new Spreadsheet(requestUser.getUsername(), XMLString);
    	spreadsheet.setId(generateId());
    	addSpreadsheet(spreadsheet);
        addPermission(new Permission(spreadsheet, getUserByUsername(spreadsheet.getAuthor()), true));
    	return spreadsheet;
    }

    public void deleteSpreadsheet(String requestUsername, int spreadsheetId) throws SpreadsheetNotFoundException, UnauthorizedUserException {
    	Spreadsheet spreadsheet = getSpreadsheetById(spreadsheetId);
    	if(requestUsername.equals(spreadsheet.getAuthor())) {
    		removeSpreadsheet(spreadsheet);
    		//remove all permissions for this spreadsheet
			for(Permission permission : getPermissionsBySpreadsheet(spreadsheetId)) {
				removePermission(permission);
                permission.clean();
			}
            spreadsheet.clean();			
    	} else {	
    		throw new UnauthorizedUserException
    					("User " + requestUsername + " is not authorized to delete spreadsheet " + spreadsheetId + ".");
    	}
    }
    
    public void protectSpreadsheetCell(String requestUsername, int spreadsheetId, int row, int column)
    		throws SpreadsheetNotFoundException, PermissionNotFoundException, UnauthorizedUserException, InvalidCellException {
    	new ProtectRoutine().execute(requestUsername, spreadsheetId, row, column);
    }
    
    public void unProtectSpreadsheetCell(String requestUsername, int spreadsheetId, int row, int column)
    		throws SpreadsheetNotFoundException, PermissionNotFoundException, UnauthorizedUserException, InvalidCellException {
    	new UnprotectRoutine().execute(requestUsername, spreadsheetId, row, column);
    }
       
    public List<Spreadsheet> getSpreadsheetsByAuthor(String author) {
    	List<Spreadsheet> spreadsheets = new ArrayList<Spreadsheet>();
    	for(Spreadsheet sheet : getSpreadsheetSet()) {
    		if(author.equals(sheet.getAuthor())) {    			
    			spreadsheets.add(sheet);
    		}    			
    	}
    	return spreadsheets;
    }
    
    public String export(Spreadsheet spreadsheet) throws InvalidExportException {
    	return spreadsheet.export();    	
    }
    
    
	public void createUser(Root root, User newUser) throws UserAlreadyExistsException, UserNotInSessionException {		
		Session session = getSessionByUsername("root");
		
		try {
			User user = getUserByUsername(newUser.getUsername());
    		if(null != user) {
    			throw new UserAlreadyExistsException("User with username " + newUser.getUsername() + " already exists.");
    		}		
    	} catch (UserNotFoundException e) {
    		addUser(newUser);
    	} 
	}

    public void destroyUser(Root root, String deadUserUsername) throws UserNotFoundException, UserNotInSessionException, RootRemoveException {
    	if(deadUserUsername.equals("root"))
    		throw new RootRemoveException("Super User, you may not delete yourself...");
    	
    	Session session = getSessionByUsername("root");
    		
    	User user = getUserByUsername(deadUserUsername);
    	removeUser(user);
    	
    	for(Session s : getSessionSet()) {
    		if(s.getUser().getUsername().equals(deadUserUsername)) {
    			removeSession(s);
    			s.clean();
    		}
    	}
    	
    	for(Permission p : getPermissionSet()) {
    		if(p.getUser().getUsername().equals(deadUserUsername)){
        		removePermission(p);
        		p.clean();
    		}
    	}
    	
    	for(Spreadsheet spreadsheet : getSpreadsheetsByAuthor(deadUserUsername)) {
    		removeSpreadsheet(spreadsheet);
    		spreadsheet.clean();
    	}    	
    }
    
       
    private void assertSessionAndWritePermission(String userToken, Integer spreadsheetId, int row, int column) throws BubbledocsException {
    	Session session = getSessionByToken(userToken);
    	
    	//Make sure the spreadsheet exists
    	getSpreadsheetById(spreadsheetId);

    	Permission userPermission = getPermission(session.getUser().getUsername(), spreadsheetId);
    	if(!userPermission.getWritePermission()) {
    		throw new UnauthorizedUserException("Assign reference to Cell: User doesn't have permission to write in Spreadsheet");
    	}
    	
    	if(getSpreadsheetById(spreadsheetId).getCell(row, column).getProtectd()) {
    		throw new ProtectedCellException("Cannot write to protected cell at coordinates [" + row + ", " + column + "].");
    	}
    }
    
    public Integer assignReferenceCell(String userToken, Integer spreadsheetId, Integer cellIdrow, Integer cellIdColumn, Integer cellReferenceRow, Integer cellReferenceColumn) throws BubbledocsException {
    	assertSessionAndWritePermission(userToken,spreadsheetId, cellIdrow, cellIdColumn);
    	
    	Spreadsheet spreadsheet = getSpreadsheetById(spreadsheetId);
    	Cell modifiedCell = spreadsheet.getCell(cellIdrow, cellIdColumn);
    	Cell referencedCell = spreadsheet.getCell(cellReferenceRow, cellReferenceColumn);
    	
        modifiedCell.setContent(new Reference(referencedCell));
        return modifiedCell.calculate();
    }

    public String exportDocument(String userToken, int docId) throws UserNotInSessionException, PermissionNotFoundException, InvalidExportException, SpreadsheetNotFoundException {
    	if(docId < 0) throw new SpreadsheetNotFoundException("Invalid Document ID");
    	String username = userToken.split("\\d")[0];
    	getPermission(username, docId);	     
    	return getSpreadsheetById(docId).export();
    }

    public Integer AssignLiteralCell(String userToken, Integer spreadsheetId, Integer cellIdrow, Integer cellIdColumn, Integer literal) throws BubbledocsException{
    	assertSessionAndWritePermission(userToken,spreadsheetId, cellIdrow, cellIdColumn);
        Spreadsheet spreadsheet = getSpreadsheetById(spreadsheetId);
        spreadsheet.getCell(cellIdrow, cellIdColumn).setContent(new Literal(literal));        
        return spreadsheet.getCell(cellIdrow, cellIdColumn).calculate();
    }
        
    private abstract class ReadWriteRoutine {
    	protected final void execute(String requestUsername, String targetUsername, int spreadsheetId) 
    			throws UserNotFoundException, SpreadsheetNotFoundException, PermissionNotFoundException, UnauthorizedUserException {
    		Spreadsheet spreadsheet = getSpreadsheetById(spreadsheetId);
    		if(targetUsername.equals(spreadsheet.getAuthor()))
    			throw new UnauthorizedUserException("Attemped to change permissions of author of spreadsheet with ID = " + spreadsheetId + ".");
    		User targetUser = getUserByUsername(targetUsername);
    		if(getPermission(requestUsername, spreadsheetId).getWritePermission()) {
    					Permission permission = null;
    					try{
    	    				permission = getPermission(targetUsername, spreadsheetId);
    	    			} catch(PermissionNotFoundException e) {
    	    				permission = null;
    	    			} finally {
    	    				dispatch(targetUser, spreadsheet, permission);
    	    			}
    		} else {
    			throw new UnauthorizedUserException
    			("User " + requestUsername + " is not authorized to change spreadsheet " + spreadsheetId + ".");
    		}
    	}
    	
    	protected abstract void dispatch(User targetUser, Spreadsheet spreadsheet, Permission permission) throws SpreadsheetNotFoundException, UserNotFoundException;	    
    }
    
    private class AddReadRoutine extends ReadWriteRoutine {
    	protected void dispatch(User targetUser, Spreadsheet spreadsheet, Permission permission) throws SpreadsheetNotFoundException, UserNotFoundException {
    		if(null == permission) {
    			addPermission(new Permission(spreadsheet, targetUser, false));
    		} else return;
    	}
    }
    
    private class AddWriteRoutine extends ReadWriteRoutine {
    	protected void dispatch(User targetUser, Spreadsheet spreadsheet, Permission permission) throws SpreadsheetNotFoundException, UserNotFoundException {
    		if(null == permission) {
    			addPermission(new Permission(spreadsheet, targetUser, true));
    		} else {
    			permission.setWritePermission(true);
    		}
    	}
    }
    
    private class RevokeReadRoutine extends ReadWriteRoutine {
    	protected void dispatch(User targetUser, Spreadsheet spreadsheet, Permission permission) {
    		if(null != permission) {
    			removePermission(permission);
    			permission.clean();
    		} else return;
    	}
    }
    
    private class RevokeWriteRoutine extends ReadWriteRoutine {
    	protected void dispatch(User targetUser, Spreadsheet spreadsheet, Permission permission) {
    		if(null != permission) {
    			permission.setWritePermission(false);
    		} else return;
    	}
    }
    
    private abstract class ProtectionRoutine {
    	protected final void execute(String requestUsername, int spreadsheetId, int row, int column)
    			throws SpreadsheetNotFoundException, InvalidCellException, PermissionNotFoundException, UnauthorizedUserException {
    		Spreadsheet spreadsheet = getSpreadsheetById(spreadsheetId);
        	if(getPermission(requestUsername, spreadsheetId).getWritePermission()) {
        		dispatch(spreadsheet, row, column);
        	} else {
        		throw new UnauthorizedUserException
        			("User " + requestUsername + " is not authorized to change spreadsheet " + spreadsheetId + ".");
        	}
    	}
    	
    	protected abstract void dispatch(Spreadsheet spreadsheet, int row, int column) throws InvalidCellException ;
    }
    
    private class ProtectRoutine extends ProtectionRoutine{
    	protected void dispatch(Spreadsheet spreadsheet, int row, int column) throws InvalidCellException {
    		spreadsheet.getCell(row, column).setProtectd(true);
    	}
    }
    
    private class UnprotectRoutine extends ProtectionRoutine {
    	protected void dispatch(Spreadsheet spreadsheet, int row, int column) throws InvalidCellException {
    		spreadsheet.getCell(row, column).setProtectd(false);
    	}
    }

}
