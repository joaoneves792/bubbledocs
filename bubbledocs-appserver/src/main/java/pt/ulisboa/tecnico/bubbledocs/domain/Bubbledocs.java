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
             set_idGenerator(new Integer(0));
             //addUser(Root.getRoot()); FIXME WTF???
        }
        
        //private static Bubbledocs theBubbledocs = new Bubbledocs();	
            
    public static Bubbledocs getBubbledocs() {
        Bubbledocs bubble = FenixFramework.getDomainRoot().getBubbledocs();
        if( null == bubble )
            bubble = new Bubbledocs();

        return bubble;
    }
    
    private int generateId() {
    	int currentId = get_idGenerator();
    	set_idGenerator(currentId + 1);
    	return currentId;
    }

    private User __getUserByUsername__(String username) {
    	for(User user : getUserSet()) {
    		if(user.get_username().equals(username))
    			return user;
    	}
		return null;
    }
    
    public User getUserByUsername(String username) throws UserNotFoundException {
    	User user = __getUserByUsername__(username);
    	if(null == user) {
    		throw new UserNotFoundException("User was not found");
    	} else {
    		return user;
    	}
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
        if(!password.equals(user.get_passwd()))
            throw new WrongPasswordException("Failed to login user " + username + "due to password mismatch.");
        tokenInt = rand.nextInt(10);
        
        try{
            session = getUsernameSession(username);
        } catch(UserNotInSessionException e) {
            //Some code duplication... (but its better than an empty catch block)
            session = new Session(username, tokenInt, new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(new java.util.Date()));
            addSession(session);
            return tokenInt;
        }
        
        //If a session for this user was already set then delete it and create a new one
        clearSession(session);
        session = new Session(username, tokenInt, new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(new java.util.Date()));
        addSession(session);

        return tokenInt;
    }

    /**
     * Method to get the session belonging to username
     * @param username
     * @return Session or null
     */
    private Session getUsernameSession(String username) throws UserNotInSessionException{
    	Set<Session> sessions;
    	
    	sessions = getSessionSet();
    	for(Session s : sessions)
    		if(s.get_username().equals(username))
    			return s;
        throw new UserNotInSessionException("No existing session for " + username + " found!");
    }
    
    /**
     * Method to get the session identified by a Service layer token
     * @param token
     * @return Session
     */
    public Session getSessionByToken(String token)throws UserNotInSessionException {
    	Set<Session> sessions;
    	
    	sessions = getSessionSet();
    	for(Session s : sessions)
    		if(token.equals(s.get_username() + s.get_tokenInt()))
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

    private Spreadsheet __getSpreadsheetById__(int spreadsheetId) {
    	for(Spreadsheet spreadsheet : getSpreadsheetSet()) {
    		if(spreadsheet.get_id() == spreadsheetId)
    			return spreadsheet;
    	}
		return null;
    }
    
    public Spreadsheet getSpreadsheetById(int spreadsheetId) throws SpreadsheetNotFoundException {
    	Spreadsheet spreadsheet = __getSpreadsheetById__(spreadsheetId);
    	if(null == spreadsheet) {
    		throw new SpreadsheetNotFoundException("Spreadsheet was not found");
    	} else {
    		return spreadsheet;
    	}
    }
    
    private Permission __getPermission__(String username, int spreadsheetId) {
    	for(Permission permission : getPermissionSet()) {
    		if(permission.get_username().equals(username) &&
    				permission.get_spreadsheetId() == spreadsheetId)
    			return permission;
    	}
		return null;
    }
    
    public Permission getPermission(String username, int spreadsheetId) throws PermissionNotFoundException {
    	Permission permission = __getPermission__(username, spreadsheetId);
    	if(null == permission) {
    		throw new PermissionNotFoundException("Permission was not found");
    	} else {
    		return permission;
    	}
    }
    
    public List<Permission> getPermissionsByUser(String username) {
    	List<Permission>	permissions = new ArrayList<Permission>();
    	for(Permission permission : getPermissionSet()) {
    		if(permission.get_username().equals(username))
    			permissions.add(permission);
    	}
    	return Collections.unmodifiableList(permissions);
    }
    
    public List<Permission> getPermissionsBySpreadsheet(int spreadsheetId) {
    	List<Permission>	permissions = new ArrayList<Permission>();
    	for(Permission permission : getPermissionSet()) {
    		if(permission.get_spreadsheetId() == spreadsheetId)
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

            author = getUserByUsername(session.get_username());
            spreadsheet = createSpreadsheet(author, name, rows, columns);
            return spreadsheet.get_id();
        }catch(UserNotFoundException e){
            throw new UserNotInSessionException("FATAL: there is a session for " + userToken + " but there isnt a user by that name!");
        }
    }  

    public Spreadsheet createSpreadsheet(User author, String name, int lines, int columns) {
    	Spreadsheet spreadsheet = new Spreadsheet(name, author.get_username(), generateId(), lines, columns);
    	addSpreadsheet(spreadsheet);
        addPermission(new Permission(spreadsheet.get_id(), author.get_username(), true));
    	return spreadsheet;
    }
    
    /**
     *  This is the XML import 
     *  @param XMLString
     *  @return the new spreadsheet
     * @throws IOException 
     * @throws JDOMException 
     */    
    public Spreadsheet createSpreadsheet(User requestUser, String XMLString) throws InvalidImportException, JDOMException, IOException {
    	Spreadsheet spreadsheet = new Spreadsheet(requestUser.get_username(), XMLString);
    	spreadsheet.set_id(generateId());
    	addSpreadsheet(spreadsheet);
        addPermission(new Permission(spreadsheet.get_id(), spreadsheet.get_author(), true));
    	return spreadsheet;
    }
    
    
    /*
    public void deleteSpreadsheet(String requestUsername, int spreadsheetId)
    		throws UnauthorizedUserException, SpreadsheetNotFoundException {
    	Spreadsheet spreadsheet = __getSpreadsheetById__(spreadsheetId);
    	if(null == spreadsheet) {
    		throw new SpreadsheetNotFoundException("Spreadsheet with ID " + spreadsheetId + " not found.");
    	} else {
    		Permission permission = __getPermission__(requestUser.get_username(), spreadsheetId);
    		if(null == permission) {
    			throw new UnauthorizedUserException
    			("User " + requestUser.get_username() + " is not authorized to change spreadsheet " + spreadsheetId + ".");
    		} else {
    			if(permission.get_writePermission()) {
    				removeSpreadsheet(spreadsheet);
                    spreadsheet.clean();
    				//remove all permissions for this spreadsheet
    				for(Permission permission_it : getPermissionsBySpreadsheet(spreadsheetId)) {
    					removePermission(permission_it);
                        permission_it.clean();
    				}
    			} else {
    				throw new UnauthorizedUserException
    				("User " + requestUser.get_username() + " is not authorized to change spreadsheet " + spreadsheetId + ".");
    			}    				
    		}
    	}
    }
    */
    
    
    public void deleteSpreadsheet(String requestUsername, int spreadsheetId) throws SpreadsheetNotFoundException, UnauthorizedUserException {
    	Spreadsheet spreadsheet = getSpreadsheetById(spreadsheetId);
    	if(requestUsername.equals(spreadsheet.get_author())) {
    		removeSpreadsheet(spreadsheet);
            spreadsheet.clean();
			//remove all permissions for this spreadsheet
			for(Permission permission_it : getPermissionsBySpreadsheet(spreadsheetId)) {
				removePermission(permission_it);
                permission_it.clean();
			}
    	} else {	
    		throw new UnauthorizedUserException
    					("User " + requestUsername + " is not authorized to delete spreadsheet " + spreadsheetId + ".");
    	}
    }
    
    public void protectSpreadsheetCell(String requestUsername, int spreadsheetId, int line, int column)
    		throws SpreadsheetNotFoundException, PermissionNotFoundException, UnauthorizedUserException, InvalidCellException {
    	new ProtectRoutine().execute(requestUsername, spreadsheetId, line, column);
    }
    
    public void unProtectSpreadsheetCell(String requestUsername, int spreadsheetId, int line, int column)
    		throws SpreadsheetNotFoundException, PermissionNotFoundException, UnauthorizedUserException, InvalidCellException {
    	new UnprotectRoutine().execute(requestUsername, spreadsheetId, line, column);
    }
       
    public List<Spreadsheet> getSpreadsheetsByAuthor(String author) {
    	List<Spreadsheet> spreadsheets = new ArrayList<Spreadsheet>();
    	for(Spreadsheet sheet : getSpreadsheetSet()) {
    		System.out.println(sheet.toString());
    		if(author.equals(sheet.get_author())) {    			
    			spreadsheets.add(sheet);
    		}    			
    	}
    	return spreadsheets;
    }
    
    public String export(Spreadsheet spreadsheet) throws InvalidExportException {
    	return spreadsheet.export();    	
    }

	public void createUser(Root root, User newUser) throws UserAlreadyExistsException, UserNotInSessionException {
		Session session = getUsernameSession("root");
		if(session.hasExpired()) {
			clearSession(session);
			throw new UserNotInSessionException("Root is not logged in.");
		}
		
		session.update();
    	
		try {
			User user = getUserByUsername(newUser.get_username());
    		if(null != user) {
    			throw new UserAlreadyExistsException("User with usaname " + newUser.get_username() + " already exists.");
    		}    		
    	} catch (UserNotFoundException e) {
    		addUser(newUser);
    	} 
	}
    
    public void destroyUser(Root root, String deadUserUsername) throws UserNotFoundException, UserNotInSessionException {
    	Session session = getUsernameSession("root");
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
    
    private void assertSessionAndWritePermission(String userToken, Integer spreadsheetId, int line, int column) throws BubbledocsException {
    	Session session = getSessionByToken(userToken);
    	
    	if(session.hasExpired()){
    		clearSession(session);
    		throw new UserNotInSessionException("User session has expired.");
    	}
    	
    	session.update();

    	Permission userPermission = getPermission(session.get_username(), spreadsheetId);
    	if(!userPermission.get_writePermission()) {
    		throw new UnauthorizedUserException("Assign reference to Cell: User doesn't have permission to write in Spreadsheet");
    	}
    	
    	if(getSpreadsheetById(spreadsheetId).getCell(line, column).get_protected()) {
    		throw new ProtectedCellException("Cannot write to protected cell at coordinates [" + line + ", " + column + "].");
    	}
    }
    
    public Integer AssignReferenceCell(String _userToken, Integer _spreadsheetId, Integer _cellIdLine, Integer _cellIdColumn, Integer _cellReferenceLine, Integer _cellReferenceColumn) throws BubbledocsException{
    	assertSessionAndWritePermission(_userToken,_spreadsheetId, _cellIdLine, _cellIdColumn);
    	
    	Spreadsheet spreadsheet = getSpreadsheetById(_spreadsheetId);
    	Cell myCell = spreadsheet.getCell(_cellIdLine, _cellIdColumn);
    	
        Reference ref = new Reference(_cellReferenceLine, _cellReferenceColumn);
        ref.setCell(myCell);
        myCell.setContent(ref);
        return myCell.getValue();
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

    public Integer AssignLiteralCell(String _userToken, Integer _spreadsheetId, Integer _cellIdLine, Integer _cellIdColumn, Integer _literal) throws BubbledocsException{
    	assertSessionAndWritePermission(_userToken,_spreadsheetId, _cellIdLine, _cellIdColumn);
        Spreadsheet spreadsheet = getSpreadsheetById(_spreadsheetId);
        spreadsheet.getCell(_cellIdLine, _cellIdColumn).setContent(new Literal(_literal));        
        return spreadsheet.getCell(_cellIdLine, _cellIdColumn).getValue();
    }
    
    
    private abstract class ReadWriteRoutine {
    	protected final void execute(String requestUsername, String targetUsername, int spreadsheetId) 
    			throws UserNotFoundException, SpreadsheetNotFoundException, PermissionNotFoundException, UnauthorizedUserException {
    		getUserByUsername(targetUsername);
    		Spreadsheet spreadsheet = getSpreadsheetById(spreadsheetId);
    		if(requestUsername.equals(spreadsheet.get_author()) ||
    				getPermission(requestUsername, spreadsheetId).get_writePermission()) {
    	    			Permission permission = __getPermission__(targetUsername, spreadsheetId);
    	    			dispatch(targetUsername, spreadsheetId, permission);
    		} else {
    			throw new UnauthorizedUserException
    			("User " + requestUsername + " is not authorized to change spreadsheet " + spreadsheetId + ".");
    		}
    	}
    	
    	protected abstract void dispatch(String targetUsername, int spreadsheetId, Permission permission);	    
    }
    
    private class AddReadRoutine extends ReadWriteRoutine {
    	protected void dispatch(String targetUsername, int spreadsheetId, Permission permission) {
    		if(null == permission) {
    			addPermission(new Permission(spreadsheetId, targetUsername, false));
    		} else return;
    	}
    }
    
    private class AddWriteRoutine extends ReadWriteRoutine {
    	protected void dispatch(String targetUsername, int spreadsheetId, Permission permission) {
    		if(null == permission) {
    			addPermission(new Permission(spreadsheetId, targetUsername, true));
    		} else {
    			permission.set_writePermission(true);
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
    			permission.set_writePermission(false);
    		} else return;
    	}
    }
    
    private abstract class ProtectionRoutine {
    	protected final void execute(String requestUsername, int spreadsheetId, int line, int column)
    			throws SpreadsheetNotFoundException, InvalidCellException, PermissionNotFoundException, UnauthorizedUserException {
    		Spreadsheet spreadsheet = getSpreadsheetById(spreadsheetId);
        	if(requestUsername.equals(spreadsheet.get_author()) ||
        			getPermission(requestUsername, spreadsheetId).get_writePermission()) {
        		dispatch(spreadsheet, line, column);
        	} else {
        		throw new UnauthorizedUserException
        			("User " + requestUsername + " is not authorized to change spreadsheet " + spreadsheetId + ".");
        	}
    	}
    	
    	protected abstract void dispatch(Spreadsheet spreadsheet, int line, int column) throws InvalidCellException ;
    }
    
    private class ProtectRoutine extends ProtectionRoutine{
    	protected void dispatch(Spreadsheet spreadsheet, int line, int column) throws InvalidCellException {
    		spreadsheet.getCell(line, column).set_protected(true);
    	}
    }
    
    private class UnprotectRoutine extends ProtectionRoutine {
    	protected void dispatch(Spreadsheet spreadsheet, int line, int column) throws InvalidCellException {
    		spreadsheet.getCell(line, column).set_protected(false);
    	}
    }

}