package pt.ulisboa.tecnico.bubbledocs.service;

import pt.ist.fenixframework.Atomic;
import pt.ulisboa.tecnico.bubbledocs.domain.Bubbledocs;
import pt.ulisboa.tecnico.bubbledocs.exceptions.BubbledocsException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UserNotInSessionException;

// add needed import declarations

public abstract class BubbledocsService {
	
	protected String userToken;

    @Atomic
    public final void execute() throws BubbledocsException {
    	checkUserInSession();
        dispatch();
    	updateSession();
    }

    protected abstract void dispatch() throws BubbledocsException;
    
    private void checkUserInSession() throws UserNotInSessionException{
    	Bubbledocs bubble = Bubbledocs.getBubbledocs();
    	Session session = bubble.getSessionByUsername(userToken.split("\\d")[0]);
        if(session.hasExpired()){
            bubble.clearSession(session);
            throw new UserNotInSessionException(userToken + "'s Session expired!");
        }
    }
    
    private final void updateSession() throws UserNotInSessionException{
    	Bubbledocs.getBubbledocs().getSessionByUsername(userToken.split("\\d")[0]).update();
    }
}
