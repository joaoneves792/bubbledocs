    package pt.ulisboa.tecnico.bubbledocs.domain;

    import java.io.IOException;
    import java.util.ArrayList;
    import java.util.Collections;
    import java.util.List;
    import java.util.Random;
    import java.util.Set;

    import org.jdom2.JDOMException;

    import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidExportException;
    import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidImportException;
    import pt.ulisboa.tecnico.bubbledocs.exceptions.PermissionNotFoundException;
    import pt.ulisboa.tecnico.bubbledocs.exceptions.SpreadsheetNotFoundException;
    import pt.ulisboa.tecnico.bubbledocs.exceptions.UnauthorizedUserException;
    import pt.ulisboa.tecnico.bubbledocs.exceptions.UserNotFoundException;
    import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidCellException;
    import pt.ulisboa.tecnico.bubbledocs.exceptions.WrongPasswordException;
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
    public Integer loginUser(String username, String password)throws UserNotFoundException, WrongPasswordException{
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
        
        //If a session for this user is already set then delete it and create a new one
        session = getUsernameSession(username);
        if(null != session){
        	removeSession(session);
        	session.clean();
        }
        	        	
        session = new Session(username, tokenInt, new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(new java.util.Date()));
        addSession(session);
        

        return tokenInt;
    }

    /**
     * Method to get the session belonging to username
     * @param username
     * @return Session or null
     */
    private Session getUsernameSession(String username){
    	Set<Session> sessions;
    	
    	sessions = getSessionSet();
    	for(Session s : sessions)
    		if(s.get_username().equals(username))
    			return s;
    	return null;    		
    }
    
    /**
     * Method to get the session identified by a Service layer token
     * @param token
     * @return Session or null
     */
    public Session getTokenSession(String token){
    	Set<Session> sessions;
    	
    	sessions = getSessionSet();
    	for(Session s : sessions)
    		if(token.equals(s.get_username() + s.get_tokenInt()))
    			return s;
    	return null;
    }
    
    /**
     * Everytime a user logs in we have to delete all other sessions that have expired
     */
    private void performSessionClean(){
        Set<Session> sessions;

        sessions = this.getSessionSet();
        for(Session s : sessions)
            if(checkSessionExpired(s)){
               removeSession(s); 
               s.clean();
            }
    }

    /**
     * Private method to check if a session was last accessed over 2 hours ago
     * @param Session
     */
    private boolean checkSessionExpired(Session s){
        java.util.Date date;
        java.util.Date sessionDate;
        java.text.SimpleDateFormat dateFormat;
        long differenceMilliseconds;
        final long TWO_HOURS = 7200000; //Two hours in miliseconds

        dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm");
        try{
            sessionDate = dateFormat.parse(s.get_date());
            date = new java.util.Date();
            
            differenceMilliseconds = date.getTime() - sessionDate.getTime();

            if(TWO_HOURS < differenceMilliseconds)
                return true;
        }catch(java.text.ParseException e){
            //Should we really print this , throw it up or mark the session as invalid and return true?
            System.out.println("ERROR Failed to parse a date for session from user: " + s.get_username());
        }
        return false;
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
 
    //TODO REFACTOR THESE METHODS!!!!! 
    
    public void addReadPermission(User requestUser, String granted, int spreadsheetId) 
    		throws UnauthorizedUserException, SpreadsheetNotFoundException, UserNotFoundException {
    	User user = __getUserByUsername__(granted);
    	Spreadsheet spreadsheet = __getSpreadsheetById__(spreadsheetId);
    	if(null == spreadsheet) {
    		throw new SpreadsheetNotFoundException("Spreadsheet with ID " + spreadsheetId + " not found.");
    	} else if(null == user) {
    		throw new UserNotFoundException("User " + granted + " not found.");
    	} else {
    		Permission permission = __getPermission__(requestUser.get_username(), spreadsheetId);
    		if(null == permission) {
    			throw new UnauthorizedUserException
    			("User " + requestUser.get_username() + " is not authorized to change spreadsheet " + spreadsheetId + ".");
    		} else {
    			if(permission.get_writePermission()) {
    				if (null == __getPermission__(granted, spreadsheetId)) {
    					addPermission(new Permission(spreadsheetId, granted, false));
    				}    			
    			} else {
    				throw new UnauthorizedUserException
    				("User " + requestUser.get_username() + " is not authorized to change spreadsheet " + spreadsheetId + ".");
    			}    				
    		}
    	}
    }
    
    public void addWritePermission(User requestUser, String granted, int spreadsheetId)
    		throws UnauthorizedUserException, SpreadsheetNotFoundException, UserNotFoundException {
    	User user = __getUserByUsername__(granted);
    	Spreadsheet spreadsheet = __getSpreadsheetById__(spreadsheetId);
    	if(null == spreadsheet) {
    		throw new SpreadsheetNotFoundException("Spreadsheet with ID " + spreadsheetId + " not found.");
    	} else if(null == user) {
    		throw new UserNotFoundException("User " + granted + " not found.");
    	} else {
    		Permission permission = __getPermission__(requestUser.get_username(), spreadsheetId);
    		if(null == permission) {
    			throw new UnauthorizedUserException
    			("User " + requestUser.get_username() + " is not authorized to change spreadsheet " + spreadsheetId + ".");
    		} else {
    			if(permission.get_writePermission()) {
    				Permission grantedPermission = __getPermission__(granted, spreadsheetId);
    				if (null == grantedPermission) {
    					addPermission(new Permission(spreadsheetId, granted, true));
    				} else {
    					grantedPermission.set_writePermission(true);
    				}
    			} else {
    				throw new UnauthorizedUserException
    				("User " + requestUser.get_username() + " is not authorized to change spreadsheet " + spreadsheetId + ".");
    			}    				
    		}
    	}
    }
    
    public void revokeReadPermission(User requestUser, String revoked, int spreadsheetId) 
    	throws UnauthorizedUserException, SpreadsheetNotFoundException, UserNotFoundException {
    	User user = __getUserByUsername__(revoked);
    	Spreadsheet spreadsheet = __getSpreadsheetById__(spreadsheetId);
    	if(null == spreadsheet) {
    		throw new SpreadsheetNotFoundException("Spreadsheet with ID " + spreadsheetId + " not found.");
    	} else if(null == user) {
    		throw new UserNotFoundException("User " + revoked + " not found.");
    	} else {
    		Permission permission = __getPermission__(requestUser.get_username(), spreadsheetId);
    		if(null == permission) {
    			throw new UnauthorizedUserException
    			("User " + requestUser.get_username() + " is not authorized to change spreadsheet " + spreadsheetId + ".");
    		} else {
    			if(permission.get_writePermission()) {
    				Permission grantedPermission = __getPermission__(revoked, spreadsheetId);
    				if (null != grantedPermission)
    					removePermission(grantedPermission);
    			} else {
    				throw new UnauthorizedUserException
    				("User " + requestUser.get_username() + " is not authorized to change spreadsheet " + spreadsheetId + ".");
    			}    				
    		}
    	}    	
    }
    
    public void revokeWritePermission(User requestUser, String revoked, int spreadsheetId)
    		throws UnauthorizedUserException, SpreadsheetNotFoundException, UserNotFoundException {
    	User user = __getUserByUsername__(revoked);
    	Spreadsheet spreadsheet = __getSpreadsheetById__(spreadsheetId);
    	if(null == spreadsheet) {
    		throw new SpreadsheetNotFoundException("Spreadsheet with ID " + spreadsheetId + " not found.");
    	} else if(null == user) {
    		throw new UserNotFoundException("User " + revoked + " not found.");
    	} else {
    		Permission permission = __getPermission__(requestUser.get_username(), spreadsheetId);
    		if(null == permission) {
    			throw new UnauthorizedUserException
    			("User " + requestUser.get_username() + " is not authorized to change spreadsheet " + spreadsheetId + ".");
    		} else {
    			if(permission.get_writePermission()) {
    				Permission grantedPermission = __getPermission__(revoked, spreadsheetId);
    				if (null != grantedPermission && grantedPermission.get_writePermission())
    					grantedPermission.set_writePermission(false);
    			} else {
    				throw new UnauthorizedUserException
    				("User " + requestUser.get_username() + " is not authorized to change spreadsheet " + spreadsheetId + ".");
    			}    				
    		}
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
     *  @param JDOM Document
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
    
    public void deleteSpreadsheet(User requestUser, int spreadsheetId)
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
        
    public void protectSpreadsheetCell(User requestUser, int spreadsheetId, int line, int column)
    		throws UnauthorizedUserException, SpreadsheetNotFoundException, InvalidCellException{
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
    				spreadsheet.getCell(line, column).set_protected(true);
    			} else {
    				throw new UnauthorizedUserException
    				("User " + requestUser.get_username() + " is not authorized to change spreadsheet " + spreadsheetId + ".");
    			}    				
    		}
    	}
    }
    
    public void unProtectSpreadsheetCell(User requestUser, int spreadsheetId, int line, int column) 
    		throws UnauthorizedUserException, SpreadsheetNotFoundException, InvalidCellException{
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
    				spreadsheet.getCell(line, column).set_protected(false);
    			} else {
    				throw new UnauthorizedUserException
    				("User " + requestUser.get_username() + " is not authorized to change spreadsheet " + spreadsheetId + ".");
    			}    				
    		}
    	}
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
    
    
}
