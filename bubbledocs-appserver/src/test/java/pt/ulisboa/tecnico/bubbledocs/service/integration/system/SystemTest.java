package pt.ulisboa.tecnico.bubbledocs.service.integration.system;

import org.junit.After;
import org.junit.Before;
import pt.ulisboa.tecnico.bubbledocs.domain.Bubbledocs;
import pt.ulisboa.tecnico.bubbledocs.exceptions.BubbledocsException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidUsernameException;

public abstract class SystemTest {
	
    @Before
    protected final void setUp() throws BubbledocsException {
    	try {
			Bubbledocs.getBubbledocs().getSuperUser();
		} catch (InvalidUsernameException e) {
			throw new BubbledocsException("Unable to create the super user!");
		}
    }

    @After
    protected final void tearDown() throws BubbledocsException {
    	Bubbledocs.getBubbledocs().clean();
    }

}
