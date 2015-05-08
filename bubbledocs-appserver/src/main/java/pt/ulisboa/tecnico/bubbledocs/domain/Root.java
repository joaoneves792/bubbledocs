package pt.ulisboa.tecnico.bubbledocs.domain;

import java.util.Collections;
import java.util.Set;

import pt.ulisboa.tecnico.bubbledocs.exceptions.EmptyPasswordException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.EmptyUsernameException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidUsernameException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.RootRemoveException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UserAlreadyExistsException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UserNotFoundException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UserNotInSessionException;

public class Root extends Root_Base {
    
	/** package protected constructor - only to be used by Bubbledocs
	 * @throws InvalidUsernameException 
	 */
    Root() throws InvalidUsernameException {
        super();
        init("Super User", "root", "root@bubbledocs.tecnico.ulisboa.pt");
        setPasswd("root");
        //Bubbledocs.getBubbledocs().addUser(this);
        //setBubbledocs(Bubbledocs.getBubbledocs());
    }    
   
    public void addUser(String name, String username, String email) throws UserAlreadyExistsException, UserNotInSessionException, EmptyPasswordException, EmptyUsernameException, InvalidUsernameException {
    	Bubbledocs.getBubbledocs().createUser(this, new User(name, username, email));
    }
    
    public void removeUser(String username) throws UserNotFoundException, UserNotInSessionException, RootRemoveException {
    	Bubbledocs.getBubbledocs().destroyUser(this, username);
    }
    
    public Set<User> getUsers() {
    	return Collections.unmodifiableSet(Bubbledocs.getBubbledocs().getUserSet());
    }
    
	public void clean(){
		setBubbledocs(null);
		super.deleteDomainObject();
	}
    
}
