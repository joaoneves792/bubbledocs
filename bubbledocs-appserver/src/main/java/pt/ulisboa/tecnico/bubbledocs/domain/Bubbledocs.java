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
import pt.ulisboa.tecnico.bubbledocs.exceptions.PermissionNotFoundException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.ProtectedCellException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.RootRemoveException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.SpreadsheetNotFoundException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UnauthorizedUserException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UserAlreadyExistsException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UserNotFoundException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidCellException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UserNotInSessionException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.WrongPasswordException;
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

    /**
     * Method to create a session for a user
     * @param username
     * @param password
     * @return the session token int
     * @throws UserNotFoundException
     * @throws WrongPasswordException
     */
    public Integer loginUser(String username, String password)throws UserNotFoundException, WrongPasswordException {
        User user;
        Session session;
        Random rand = new Random();
        Integer tokenInt;

        //Before anything else perform a check on all Sessions
        performSessionClean();
        
        user = getUserByUsername(username);
        if(!password.equals(user.getPasswd()))
            throw new WrongPasswordException("Failed to login user " + username + "due to password mismatch.");
        tokenInt = rand.nextInt(10);
        
        try { 
            session = getSessionByUsername(username);
        } catch(UserNotInSessionException e) {
            //Some code duplication... (but its better than an empty catch block)
            session = new Session(user, tokenInt, org.joda.time.LocalDate.now());
            addSession(session);
            return tokenInt;
        }
        
        //If a session for this user was already set then delete it and create a new one
        clearSession(session);
        session = new Session(user, tokenInt, org.joda.time.LocalDate.now());
        addSession(session);

        return tokenInt;
    }

    /**
     * Method to get the session belonging to username
     * @param username
     * @return Session or null
     */
    private Session getSessionByUsername(String username) throws UserNotInSessionException {
    	Set<Session> sessions;
    	
    	sessions = getSessionSet();
    	for(Session s : sessions)
    		if(s.getUser().getUsername().equals(username))
    			return s;
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
    	for(Session s : sessions)
    		if(token.equals(s.getUser().getUsername() + s.getTokenInt()))
    			return s;
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
    
    private Permission __getPermission__(String username, int spreadsheetId) {
    	for(Permission permission : getPermissionSet()) {
    		if(permission.getUser().getUsername().equals(username) &&
    				permission.getSpreadsheet().getId() == spreadsheetId)
    			return permission;
    	}
		return null;
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
       
        //Sanity checks 
        if(name.isEmpty())
            throw new EmptySpreadsheetNameException("Operation not permited: create a Spreadsheet with an empty name!");
        if(1 > rows || 1 > columns)
            throw new OutOfBoundsSpreadsheetException("Operation not permited: create a spreadheet with 0 or less rows/columns!");

        try{
            session = getSessionByToken(userToken);

            //This if block should be in everyones functions called from the service layer!!
            if(session.hasExpired()){
                clearSession(session);
                throw new UserNotInSessionException(userToken + "'s Session expired!");
            }else
                session.update();
            //-----------------------------

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
        addPermission(new Permission(spreadsheet, author, true));
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
			for(Permission permission_it : getPermissionsBySpreadsheet(spreadsheetId)) {
				removePermission(permission_it);
                permission_it.clean();
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
    		System.out.println(sheet.toString());
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
		if(session.hasExpired()) {
			clearSession(session);
			throw new UserNotInSessionException("Root is not logged in.");
		}
		
		session.update();
    	
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
    		throw new RootRemoveException("Don't delete yourself!");
    	
    	Session session = getSessionByUsername("root");
    	if(session.hasExpired()) {
    		clearSession(session);
    		throw new UserNotInSessionException("Root is not logged in.");
    	}
    		
    	session.update();
    	
    	User user = getUserByUsername(deadUserUsername);
    	removeUser(user);
    	
    	for(Spreadsheet spreadsheet : getSpreadsheetsByAuthor(deadUserUsername)) {
    		removeSpreadsheet(spreadsheet);
    		spreadsheet.clean();
    	}    	
    }
    
    private void assertSessionAndWritePermission(String userToken, Integer spreadsheetId, int row, int column) throws BubbledocsException {
    	Session session = getSessionByToken(userToken);
    	
    	if(session.hasExpired()){
    		clearSession(session);
    		throw new UserNotInSessionException("User session has expired.");
    	}
    	
    	session.update();

    	Permission userPermission = getPermission(session.getUser().getUsername(), spreadsheetId);
    	if(!userPermission.getWritePermission()) {
    		throw new UnauthorizedUserException("Assign reference to Cell: User doesn't have permission to write in Spreadsheet");
    	}
    	
    	if(getSpreadsheetById(spreadsheetId).getCell(row, column).getProtectd()) {
    		throw new ProtectedCellException("Cannot write to protected cell at coordinates [" + row + ", " + column + "].");
    	}
    }
    
    public Integer AssignReferenceCell(String userToken, Integer spreadsheetId, Integer cellIdrow, Integer cellIdColumn, Integer cellReferencerow, Integer cellReferenceColumn) throws BubbledocsException {
    	assertSessionAndWritePermission(userToken,spreadsheetId, cellIdrow, cellIdColumn);
    	
    	Spreadsheet spreadsheet = getSpreadsheetById(spreadsheetId);
    	Cell myCell = spreadsheet.getCell(cellIdrow, cellIdColumn);
    	
        Reference ref = new Reference(myCell);
        myCell.setContent(ref);
        return myCell.calculate();
    }

    public String exportDocument(String userToken, int docId) throws UserNotInSessionException, PermissionNotFoundException, InvalidExportException, SpreadsheetNotFoundException {
    	Session session = getSessionByToken(userToken);
    	if(session.hasExpired()){
    		clearSession(session);
    		throw new UserNotInSessionException(userToken + "'s Session expired!");
    	}
    	session.update();
    	getPermission(userToken, docId);	     
    	return getSpreadsheetById(docId).export();
    }

    public Integer AssignLiteralCell(String _userToken, Integer _spreadsheetId, Integer _cellIdrow, Integer _cellIdColumn, Integer _literal) throws BubbledocsException{
    	assertSessionAndWritePermission(_userToken,_spreadsheetId, _cellIdrow, _cellIdColumn);
        Spreadsheet spreadsheet = getSpreadsheetById(_spreadsheetId);
        spreadsheet.getCell(_cellIdrow, _cellIdColumn).setContent(new Literal(_literal));        
        return spreadsheet.getCell(_cellIdrow, _cellIdColumn).calculate();
    }
        
    private abstract class ReadWriteRoutine {
    	protected final void execute(String requestUsername, String targetUsername, int spreadsheetId) 
    			throws UserNotFoundException, SpreadsheetNotFoundException, PermissionNotFoundException, UnauthorizedUserException {
    		getUserByUsername(targetUsername);
    		Spreadsheet spreadsheet = getSpreadsheetById(spreadsheetId);
    		if(requestUsername.equals(spreadsheet.getAuthor()) ||
    				getPermission(requestUsername, spreadsheetId).getWritePermission()) {
    	    			Permission permission = __getPermission__(targetUsername, spreadsheetId);
    	    			dispatch(targetUsername, spreadsheetId, permission);
    		} else {
    			throw new UnauthorizedUserException
    			("User " + requestUsername + " is not authorized to change spreadsheet " + spreadsheetId + ".");
    		}
    	}
    	
    	protected abstract void dispatch(String targetUsername, int spreadsheetId, Permission permission) throws SpreadsheetNotFoundException, UserNotFoundException;	    
    }
    
    private class AddReadRoutine extends ReadWriteRoutine {
    	protected void dispatch(String targetUsername, int spreadsheetId, Permission permission) throws SpreadsheetNotFoundException, UserNotFoundException {
    		if(null == permission) {
    			addPermission(new Permission(getSpreadsheetById(spreadsheetId), getUserByUsername(targetUsername), false));
    		} else return;
    	}
    }
    
    private class AddWriteRoutine extends ReadWriteRoutine {
    	protected void dispatch(String targetUsername, int spreadsheetId, Permission permission) throws SpreadsheetNotFoundException, UserNotFoundException {
    		if(null == permission) {
    			addPermission(new Permission(getSpreadsheetById(spreadsheetId), getUserByUsername(targetUsername), true));
    		} else {
    			permission.setWritePermission(true);
    		}
    	}
    }
    
    private class RevokeReadRoutine extends ReadWriteRoutine {
    	protected void dispatch(String targetUsername, int spreadsheetId, Permission permission) {
    		if(null != permission) {
    			removePermission(permission);
    			permission.clean();
    		} else return;
    	}
    }
    
    private class RevokeWriteRoutine extends ReadWriteRoutine {
    	protected void dispatch(String targetUsername, int spreadsheetId, Permission permission) {
    		if(null != permission) {
    			permission.setWritePermission(false);
    		} else return;
    	}
    }
    
    private abstract class ProtectionRoutine {
    	protected final void execute(String requestUsername, int spreadsheetId, int row, int column)
    			throws SpreadsheetNotFoundException, InvalidCellException, PermissionNotFoundException, UnauthorizedUserException {
    		Spreadsheet spreadsheet = getSpreadsheetById(spreadsheetId);
        	if(requestUsername.equals(spreadsheet.getAuthor()) ||
        			getPermission(requestUsername, spreadsheetId).getWritePermission()) {
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
