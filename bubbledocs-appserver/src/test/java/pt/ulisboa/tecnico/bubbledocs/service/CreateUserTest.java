package pt.ulisboa.tecnico.bubbledocs.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import pt.ulisboa.tecnico.bubbledocs.domain.User;
import pt.ulisboa.tecnico.bubbledocs.exceptions.BubbledocsException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.DuplicateUsernameException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.EmptyUsernameException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UnauthorizedOperationException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UserNotInSessionException;
import pt.ulisboa.tecnico.bubbledocs.service.CreateUser;

// add needed import declarations

public class CreateUserTest extends BubbledocsServiceTest {

    // the tokens
    private String root;
    private String ars;

    private static final String USERNAME = "ars";
    private static final String PASSWORD = "ars";
    @SuppressWarnings("unused")
	private static final String ROOT_USERNAME = "root";
    private static final String USERNAME_DOES_NOT_EXIST = "no-one";

    @Override
    public void initializeDomain() {
        createUser(USERNAME, PASSWORD, "António Rito Silva");
        try {
			root = addUserToSession("root", "root");
	        ars = addUserToSession("ars", "ars");
        } catch (BubbledocsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }

    @Test
    public void success() throws BubbledocsException {
        CreateUser service = new CreateUser(root, USERNAME_DOES_NOT_EXIST, "jose",
                "José Ferreira");
        service.execute();

	// User is the domain class that represents a User
        User user = getUserFromUsername(USERNAME_DOES_NOT_EXIST);

        assertEquals(USERNAME_DOES_NOT_EXIST, user.getUsername());
        assertEquals("jose", user.getPasswd());
        assertEquals("José Ferreira", user.getName());
    }

    @Test(expected = DuplicateUsernameException.class)
    public void usernameExists() throws BubbledocsException {
        CreateUser service = new CreateUser(root, USERNAME, "jose",
                "José Ferreira");
        service.execute();
    }

    @Test(expected = EmptyUsernameException.class)
    public void emptyUsername() throws BubbledocsException {
        CreateUser service = new CreateUser(root, "", "jose", "José Ferreira");
        service.execute();
    }

    @Test(expected = UnauthorizedOperationException.class)
    public void unauthorizedUserCreation() throws BubbledocsException {
        CreateUser service = new CreateUser(ars, USERNAME_DOES_NOT_EXIST, "jose",
                "José Ferreira");
        service.execute();
    }

    @Test(expected = UserNotInSessionException.class)
    public void accessUsernameNotExist() throws BubbledocsException {
        removeUserFromSession(root);
        CreateUser service = new CreateUser(root, USERNAME_DOES_NOT_EXIST, "jose",
                "José Ferreira");
        service.execute();
    }

}
