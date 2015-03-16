package pt.ulisboa.tecnico.bubbledocs.domain;

import java.text.ParseException;
import java.util.Collections;
import java.util.Set;

import pt.ulisboa.tecnico.bubbledocs.exceptions.UserAlreadyExistsException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UserNotFoundException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UserNotInSessionException;

public class Root extends Root_Base {
    
    private Root(String name, String username, String passwd) {
        super();
        init(name, username, passwd);
        Bubbledocs.getBubbledocs().addUser(this);
    }
    
    private static Root theRoot = null;
    
    public static Root getRoot() {
    	if(theRoot == null){
    		theRoot = new Root("Super User", "root", "root");
    	}
    	return theRoot;
    }
    
    public void addUser(String name, String username, String passwd) throws UserAlreadyExistsException, UserNotInSessionException {
    	Bubbledocs.getBubbledocs().createUser(this, new User(name, username, passwd));
    }
    
    public void removeUser(String username) throws UserNotFoundException, UserNotInSessionException, ParseException {
    	Bubbledocs.getBubbledocs().destroyUser(this, username);
    }
    
    public Set<User> getUsers() {
    	return Collections.unmodifiableSet(Bubbledocs.getBubbledocs().getUserSet());
    }
}
