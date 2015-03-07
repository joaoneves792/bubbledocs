package pt.ulisboa.tecnico.bubbledocs.domain;

import java.util.Collections;
import java.util.Set;

import pt.ulisboa.tecnico.bubbledocs.exceptions.UserAlreadyExistsException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UserNotFoundException;

public class Root extends Root_Base {
    
    private Root(String name, String username, String passwd) {
        super();
        init(name, username, passwd);
    }
    
    private static Root theRoot = null;
    
    public static Root getRoot() {
    	if(theRoot == null){
    		theRoot = new Root("Super User", "root", "root");
    	}
    	return theRoot;
    }
    
    public void addUser(String name, String username, String passwd) throws UserAlreadyExistsException {
    	try {
    		User user = Bubbledocs.getBubbledocs().getUserByUsername(username);
    		if(null != user) {
    			throw new UserAlreadyExistsException("User with usaname " + username + " already exists.");
    		}
    	} catch (UserNotFoundException e) {
    		Bubbledocs.getBubbledocs().addUser(new User(name, username, passwd));
    	}
    }
    
    public void removeUser(String username) throws UserNotFoundException {
    	try {
    		User user = Bubbledocs.getBubbledocs().getUserByUsername(username);
    		Bubbledocs.getBubbledocs().removeUser(user);
    	} catch (UserNotFoundException e) {
    		throw e;
    	}
    }
    
    public Set<User> getUsers() {
    	return Collections.unmodifiableSet(Bubbledocs.getBubbledocs().getUserSet());
    }
}
