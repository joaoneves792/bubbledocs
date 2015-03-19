package pt.ulisboa.tecnico.bubbledocs.service;

import org.junit.Test;

import pt.ulisboa.tecnico.bubbledocs.domain.Bubbledocs;
import pt.ulisboa.tecnico.bubbledocs.domain.Spreadsheet;
import pt.ulisboa.tecnico.bubbledocs.domain.User;
import pt.ulisboa.tecnico.bubbledocs.exceptions.BubbledocsException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidCellException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.ProtectedCellException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.SpreadsheetNotFoundException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UnauthorizedUserException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UserNotInSessionException;
import pt.ulisboa.tecnico.bubbledocs.service.AssignLiteralCell;

// add needed import declarations

public class AssignLiteralCellTest extends BubbledocsServiceTest {

    private static final String USERNAME = "jp";
    private static final String NAME = "João Pereira";
    private static final String PASSWORD = "jp#";
    private static final String USERNAME_RO = "jn";
    private static final String NAME_RO = "João Neves";
    private static final String PASSWORD_RO = "jn#";
    private static final String SPREADHEET_NAME = "My Spreadsheet";
    private static final int SPREADHEET_ROWS = 10;
    private static final int SPREADHEET_COLUMNS = 15;
    private static final String INVALID_LITERAL = "10mil";
    private static final String LITERAL_ID = "5;5";
    private static final String PROTECTED_ID = "6;6";
    private static final String LITERAL = "3";
    private static final String INVALID_ID = "abc";
    private static final String INVALID_CELL_ID = "100;100";
    
    //This is needed throughout the tests
    private Integer _spreadsheetID;
    
    
    @Override
    public void initializeDomain() {
 	   Bubbledocs bubble = Bubbledocs.getBubbledocs();
 	   User user =createUser(USERNAME, PASSWORD, NAME);
       createUser(USERNAME_RO, PASSWORD_RO, NAME_RO);
       try{
    	   Spreadsheet ss = createSpreadSheet(user, SPREADHEET_NAME, SPREADHEET_ROWS, SPREADHEET_COLUMNS);
     	   _spreadsheetID = ss.getId();

    	   //Protect Cell 6;6
    	   bubble.protectSpreadsheetCell(USERNAME, _spreadsheetID, 6, 6);
    	   
    	   //Give RO user read permissions
    	   bubble.addReadPermission(USERNAME, USERNAME_RO, _spreadsheetID);

       } catch (BubbledocsException e) {
    	   System.out.println("FAILED TO POPULATE FOR AssignLiteralCellTest");
    	   //FIXME At this point we should probably abort!
       }
    }

    //Test case 1
    @Test(expected = NumberFormatException.class)
    public void assignLiteralToInvalidId() throws BubbledocsException, NumberFormatException {
 	    String token = addUserToSession(USERNAME, PASSWORD);
    	AssignLiteralCell service = new AssignLiteralCell(token, _spreadsheetID, INVALID_ID, LITERAL);
        service.execute();
    }
    
    //Test case 2
    @Test(expected = InvalidCellException.class)
    public void assignLiteralToInvalidCell() throws BubbledocsException, NumberFormatException {
 	    String token = addUserToSession(USERNAME, PASSWORD);
    	AssignLiteralCell service = new AssignLiteralCell(token, _spreadsheetID, INVALID_CELL_ID, LITERAL);
        service.execute();
    }
    
    //Test case 3
    @Test(expected = NumberFormatException.class)
    public void assignLiteralWithInvalidLiteral() throws BubbledocsException, NumberFormatException {
 	    String token = addUserToSession(USERNAME, PASSWORD);
    	AssignLiteralCell service = new AssignLiteralCell(token, _spreadsheetID, LITERAL_ID, INVALID_LITERAL);
        service.execute();
    }
    
    //Test case 4
    @Test(expected = SpreadsheetNotFoundException.class)
    public void assignLiteralOnNonExistingSpreadsheet() throws BubbledocsException {
 	    String token = addUserToSession(USERNAME, PASSWORD);
    	AssignLiteralCell service = new AssignLiteralCell(token, _spreadsheetID+5, LITERAL_ID, LITERAL);
        service.execute();
    }
    
    //Test case 5
    @Test(expected = ProtectedCellException.class)
    public void assignLiteralOnProtectedCell() throws BubbledocsException {
 	    String token = addUserToSession(USERNAME, PASSWORD);
    	AssignLiteralCell service = new AssignLiteralCell(token, _spreadsheetID, PROTECTED_ID, LITERAL);
        service.execute();
    }
    
    //Test case 6
    @Test(expected = UserNotInSessionException.class)
    public void assignLiteralUserNotInSession() throws BubbledocsException {
 	    String token = addUserToSession(USERNAME, PASSWORD);
 	    removeUserFromSession(token);
 	    AssignLiteralCell service = new AssignLiteralCell(token, _spreadsheetID, LITERAL_ID, LITERAL);
        service.execute();
    }
    
    //Test case 7
    @Test(expected = UnauthorizedUserException.class)
    public void assignLiteralUserReadOnlyPermission() throws BubbledocsException {
 	    String token = addUserToSession(USERNAME_RO, PASSWORD_RO);
 	    AssignLiteralCell service = new AssignLiteralCell(token, _spreadsheetID, LITERAL_ID, LITERAL);
        service.execute();   
    }
    
    
    //Test case 8
    @Test
    public void success() throws BubbledocsException {
 	    String token = addUserToSession(USERNAME, PASSWORD);
 	    AssignLiteralCell service = new AssignLiteralCell(token, _spreadsheetID, LITERAL_ID, LITERAL);
        service.execute();
    }
    
}
