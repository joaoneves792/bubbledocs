package pt.ulisboa.tecnico.bubbledocs.domain;

import java.util.Collections;
import java.util.Set;

import pt.ulisboa.tecnico.bubbledocs.exceptions.CreateRootException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.RootRemoveException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UserAlreadyExistsException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UserNotFoundException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UserNotInSessionException;

public class Root extends Root_Base {
    
    public Root() {
        super();
        init("Super User", "root", "root");
        //Bubbledocs.getBubbledocs().addUser(this);
        //setBubbledocs(Bubbledocs.getBubbledocs());
    }    
   
    public void addUser(String name, String username, String passwd) throws UserAlreadyExistsException, UserNotInSessionException, CreateRootException {
    	Bubbledocs.getBubbledocs().createUser(this, new User(name, username, passwd));
    }
    
    public void removeUser(String username) throws UserNotFoundException, UserNotInSessionException, RootRemoveException {
    	Bubbledocs.getBubbledocs().destroyUser(this, username);
    }
    
    public Set<User> getUsers() {
    	return Collections.unmodifiableSet(Bubbledocs.getBubbledocs().getUserSet());
    }
}
