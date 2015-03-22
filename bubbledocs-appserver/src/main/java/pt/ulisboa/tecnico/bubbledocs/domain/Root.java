package pt.ulisboa.tecnico.bubbledocs.domain;

import java.util.Collections;
import java.util.Set;

import pt.ulisboa.tecnico.bubbledocs.exceptions.EmptyPasswordException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.EmptyUsernameException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.RootRemoveException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UserAlreadyExistsException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UserNotFoundException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UserNotInSessionException;

public class Root extends Root_Base {
    
	/** package protected constructor - only to be used by Bubbledocs
	 */
    Root() {
        super();
        init("Super User", "root", "root");
        //Bubbledocs.getBubbledocs().addUser(this);
        //setBubbledocs(Bubbledocs.getBubbledocs());
    }    
   
    public void addUser(String name, String username, String passwd) throws UserAlreadyExistsException, UserNotInSessionException, EmptyPasswordException, EmptyUsernameException {
    	Bubbledocs.getBubbledocs().createUser(this, new User(name, username, passwd));
    }
    
    public void removeUser(String username) throws UserNotFoundException, UserNotInSessionException, RootRemoveException {
    	Bubbledocs.getBubbledocs().destroyUser(this, username);
    }
    
    public Set<User> getUsers() {
    	return Collections.unmodifiableSet(Bubbledocs.getBubbledocs().getUserSet());
    }
    
}
