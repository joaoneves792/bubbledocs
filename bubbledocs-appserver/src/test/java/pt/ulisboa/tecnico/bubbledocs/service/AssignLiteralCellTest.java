package pt.ulisboa.tecnico.bubbledocs.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
    private Integer spreadsheetID;
    private String token;
    private String token_ro;
    
    
    @Override
    public void initializeDomain() {
 	   Bubbledocs bubble = Bubbledocs.getBubbledocs();
 	   User user =createUser(USERNAME, PASSWORD, NAME);
       createUser(USERNAME_RO, PASSWORD_RO, NAME_RO);
       try{
    	   Spreadsheet ss = createSpreadSheet(user, SPREADHEET_NAME, SPREADHEET_ROWS, SPREADHEET_COLUMNS);
     	   spreadsheetID = ss.getId();

    	   //Protect Cell 6;6
    	   bubble.protectSpreadsheetCell(USERNAME, spreadsheetID, 6, 6);
    	   
    	   //Give RO user read permissions
    	   bubble.addReadPermission(USERNAME, USERNAME_RO, spreadsheetID);
    	   
    	   token = addUserToSession(USERNAME, PASSWORD);
     	   token_ro = addUserToSession(USERNAME_RO, PASSWORD_RO);
    	   
       } catch (BubbledocsException e) {
    	   System.out.println("FAILED TO POPULATE FOR AssignLiteralCellTest");
    	   //FIXME At this point we should probably abort!
       }
    }

    //Test case 1
    @Test(expected = NumberFormatException.class)
    public void assignLiteralToInvalidId() throws BubbledocsException, NumberFormatException {
    	AssignLiteralCell service = new AssignLiteralCell(token, spreadsheetID, INVALID_ID, LITERAL);
        service.execute();
    }
    
    //Test case 2 FAIL
    @Test(expected = InvalidCellException.class)
    public void assignLiteralToInvalidCell() throws BubbledocsException, NumberFormatException {
    	try{
    		AssignLiteralCell service = new AssignLiteralCell(token, spreadsheetID, INVALID_CELL_ID, LITERAL);
    		service.execute();
        //This test case also checks if in case of failure the session is still updated
		}catch(BubbledocsException e){
			assertTrue("Session was not updated", hasSessionUpdated(token));
			throw e;
		}
    }
    
    //Test case 3
    @Test(expected = NumberFormatException.class)
    public void assignLiteralWithInvalidLiteral() throws BubbledocsException, NumberFormatException {
    	AssignLiteralCell service = new AssignLiteralCell(token, spreadsheetID, LITERAL_ID, INVALID_LITERAL);
        service.execute();
    }
    
    //Test case 4
    @Test(expected = SpreadsheetNotFoundException.class)
    public void assignLiteralOnNonExistingSpreadsheet() throws BubbledocsException {
    	AssignLiteralCell service = new AssignLiteralCell(token, spreadsheetID+5, LITERAL_ID, LITERAL);
        service.execute();
    }
    
    //Test case 5 FAIL
    @Test(expected = ProtectedCellException.class)
    public void assignLiteralOnProtectedCell() throws BubbledocsException {
    	AssignLiteralCell service = new AssignLiteralCell(token, spreadsheetID, PROTECTED_ID, LITERAL);
        service.execute();
    }
    
    //Test case 6
    @Test(expected = UserNotInSessionException.class)
    public void assignLiteralUserNotInSession() throws BubbledocsException {
 	    removeUserFromSession(token);
 	    AssignLiteralCell service = new AssignLiteralCell(token, spreadsheetID, LITERAL_ID, LITERAL);
        service.execute();
    }
    
    //Test case 7
    @Test(expected = UnauthorizedUserException.class)
    public void assignLiteralUserReadOnlyPermission() throws BubbledocsException {
 	    AssignLiteralCell service = new AssignLiteralCell(token_ro, spreadsheetID, LITERAL_ID, LITERAL);
        service.execute();   
    }
    
    
    //Test case 8 FAIL
    @Test
    public void success() throws BubbledocsException {
 	    AssignLiteralCell service = new AssignLiteralCell(token, spreadsheetID, LITERAL_ID, LITERAL);
        service.execute();
        assertEquals("Not returning the expected value for the cell!", Integer.parseInt(LITERAL), service.getResult().intValue());
        assertTrue("Session was not updated", hasSessionUpdated(token));
    }
    
}
