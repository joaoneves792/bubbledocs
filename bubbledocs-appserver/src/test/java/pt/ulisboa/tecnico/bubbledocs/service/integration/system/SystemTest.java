package pt.ulisboa.tecnico.bubbledocs.service.integration.system;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;

import org.junit.After;
import org.junit.Before;

import pt.ist.fenixframework.FenixFramework;
import pt.ist.fenixframework.TransactionManager;
import pt.ulisboa.tecnico.bubbledocs.domain.Bubbledocs;
import pt.ulisboa.tecnico.bubbledocs.exceptions.BubbledocsException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidUsernameException;

public abstract class SystemTest {
	
    private TransactionManager tm = FenixFramework.getTransactionManager();
	
    @Before
    public final void setUp() throws BubbledocsException, NotSupportedException, SystemException, SecurityException, IllegalStateException, RollbackException, HeuristicMixedException, HeuristicRollbackException {
    	try {
    		
    		//FIXME GOT TO ROLLBACK IF IT GOES WRONG!!!!
        	tm.begin();
			Bubbledocs.getBubbledocs().getSuperUser();
        	tm.commit();
        	
		} catch (InvalidUsernameException e) {
			throw new BubbledocsException("Unable to create the super user!");
		}
    }

    @After
    public final void tearDown() throws BubbledocsException, NotSupportedException, SystemException, SecurityException, IllegalStateException, RollbackException, HeuristicMixedException, HeuristicRollbackException {
    	tm.begin();
    	Bubbledocs.getBubbledocs().clean();
    	tm.commit();
    }

}
