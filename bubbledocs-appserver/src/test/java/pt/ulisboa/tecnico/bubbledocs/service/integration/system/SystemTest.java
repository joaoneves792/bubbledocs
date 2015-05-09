package pt.ulisboa.tecnico.bubbledocs.service.integration.system;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;

import org.junit.After;
import org.junit.Before;

import pt.ist.fenixframework.Atomic;
import pt.ulisboa.tecnico.bubbledocs.domain.Bubbledocs;
import pt.ulisboa.tecnico.bubbledocs.exceptions.BubbledocsException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidUsernameException;

public abstract class SystemTest {
	

	
    @Before 
    @Atomic
    public final void setUp() throws BubbledocsException, NotSupportedException, SystemException, SecurityException, IllegalStateException, RollbackException, HeuristicMixedException, HeuristicRollbackException {
    	try {

			Bubbledocs.getBubbledocs().getSuperUser();
        	
		} catch (InvalidUsernameException e) {
			throw new BubbledocsException("Unable to create the super user!");
		}
    }

    @After
    @Atomic
    public final void tearDown() throws BubbledocsException, NotSupportedException, SystemException, SecurityException, IllegalStateException, RollbackException, HeuristicMixedException, HeuristicRollbackException {
    	Bubbledocs.getBubbledocs().clean();
    }

}
