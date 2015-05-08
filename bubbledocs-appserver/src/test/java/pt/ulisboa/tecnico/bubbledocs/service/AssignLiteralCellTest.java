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

    private static final String AUTHOR_USERNAME = "mehrunes";
    private static final String AUTHOR_NAME = "Mehrunes Dagon";
    private static final String AUTHOR_EMAIL = "mehrunes@deadlands.oblivion";
    
    private static final String USERNAME_RO = "molag";
    private static final String NAME_RO = "Molag Bal";
    private static final String EMAIL_RO = "molag@cold-harbor.oblivion";
    
    private static final String USERNAME_WRITE = "hermaeus";
    private static final String NAME_WRITE = "Hermaeus Mora";
    private static final String EMAIL_WRITE = "hermaeus@apocrypha.oblivion";
    
    private static final String SPREADHEET_NAME = "My Spreadsheet";
    private static final int SPREADHEET_ROWS = 10;
    private static final int SPREADHEET_COLUMNS = 15;
    
    private static final String LITERAL_VALUE = "3";
    private static final String INVALID_LITERAL_VALUE = "10mil";
    
    private static final String LITERAL_ID = "5;5";
    private static final String PROTECTED_ID = "6;6";
    
    private static final Integer PROTECTED_ROW = 6;
    private static final Integer PROTECTED_COLUMN = 6;
    
    private static final String INVALID_ID = "abc";
    private static final String OUTBOUND_CELL_ID_ROW = "100;1";
    private static final String OUTBOUND_CELL_ID_COL = "1;100";
    
    //This is needed throughout the tests
    private Integer spreadsheetID;
    private String tokenAuthor;
    private String tokenRo;
    private String tokenWrite;
    
    
    @Override
    public void initializeDomain() {
 	   Bubbledocs bubble = Bubbledocs.getBubbledocs();
 	  try{
 	   User user = createUser(AUTHOR_USERNAME, AUTHOR_EMAIL, AUTHOR_NAME);
       createUser(USERNAME_RO, EMAIL_RO, NAME_RO);
       createUser(USERNAME_WRITE, EMAIL_WRITE, NAME_WRITE);
       
    	   Spreadsheet ss = createSpreadSheet(user, SPREADHEET_NAME, SPREADHEET_ROWS, SPREADHEET_COLUMNS);
     	   spreadsheetID = ss.getId();

    	   //Protect Cell 6;6
    	   bubble.protectSpreadsheetCell(AUTHOR_USERNAME, spreadsheetID, PROTECTED_ROW, PROTECTED_COLUMN);

    	   //Give RO user read permission
    	   bubble.addReadPermission(AUTHOR_USERNAME, USERNAME_RO, spreadsheetID);
    	   //Give Write user write permission
    	   bubble.addWritePermission(AUTHOR_USERNAME, USERNAME_WRITE, spreadsheetID);
     		   
    	   tokenAuthor = addUserToSession(AUTHOR_USERNAME);
     	   tokenRo = addUserToSession(USERNAME_RO);
     	   tokenWrite = addUserToSession(USERNAME_WRITE);
    	   
       } catch (BubbledocsException e) {
    	   assertTrue("Failed to populate for AssignReferenceCellTest", false);
       }
    }

    //Test case 1
    @Test(expected = NumberFormatException.class)
    public void assignLiteralToInvalidId() throws BubbledocsException, NumberFormatException {
    	AssignLiteralCell service = new AssignLiteralCell(tokenAuthor, spreadsheetID, INVALID_ID, LITERAL_VALUE);
        service.execute();
    }
    
    //Test case 2
    @Test(expected = InvalidCellException.class)
    public void assignLiteralToInvalidCellRow() throws BubbledocsException, NumberFormatException {
    	try{
    		AssignLiteralCell service = new AssignLiteralCell(tokenAuthor, spreadsheetID, OUTBOUND_CELL_ID_ROW, LITERAL_VALUE);
    		service.execute();
        //This test case also checks if in case of failure the session is still updated
		}catch(BubbledocsException e){
			assertTrue("Session was not updated", hasSessionUpdated(tokenAuthor));
			throw e;
		}
    }
    
    
    //Test case 2
    @Test(expected = InvalidCellException.class)
    public void assignLiteralToInvalidCellCol() throws BubbledocsException, NumberFormatException {
    	try{
    		AssignLiteralCell service = new AssignLiteralCell(tokenAuthor, spreadsheetID, OUTBOUND_CELL_ID_COL, LITERAL_VALUE);
    		service.execute();
        //This test case also checks if in case of failure the session is still updated
		}catch(BubbledocsException e){
			assertTrue("Session was not updated", hasSessionUpdated(tokenAuthor));
			throw e;
		}
    }
    
    //Test case 3
    @Test(expected = NumberFormatException.class)
    public void assignLiteralWithInvalidLiteral() throws BubbledocsException, NumberFormatException {
    	AssignLiteralCell service = new AssignLiteralCell(tokenAuthor, spreadsheetID, LITERAL_ID, INVALID_LITERAL_VALUE);
        service.execute();
    }
    
    //Test case 4
    @Test(expected = SpreadsheetNotFoundException.class)
    public void assignLiteralOnNonExistingSpreadsheet() throws BubbledocsException {
    	AssignLiteralCell service = new AssignLiteralCell(tokenAuthor, spreadsheetID+5, LITERAL_ID, LITERAL_VALUE);
        service.execute();
    }
    
    //Test case 5
    @Test(expected = ProtectedCellException.class)
    public void assignLiteralOnProtectedCell() throws BubbledocsException {
    	AssignLiteralCell service = new AssignLiteralCell(tokenAuthor, spreadsheetID, PROTECTED_ID, LITERAL_VALUE);
        service.execute();
    }
    
    //Test case 6
    @Test(expected = UserNotInSessionException.class)
    public void assignLiteralUserNotInSession() throws BubbledocsException {
 	    removeUserFromSession(tokenAuthor);
 	    AssignLiteralCell service = new AssignLiteralCell(tokenAuthor, spreadsheetID, LITERAL_ID, LITERAL_VALUE);
        service.execute();
    }
    
    //Test case 7
    @Test(expected = UnauthorizedUserException.class)
    public void assignLiteralUserReadOnlyPermission() throws BubbledocsException {
 	    AssignLiteralCell service = new AssignLiteralCell(tokenRo, spreadsheetID, LITERAL_ID, LITERAL_VALUE);
        service.execute();   
    }
    
    @Test
    public void assignLiteralNotAuthorWritePermission() throws BubbledocsException {
 	    AssignLiteralCell service = new AssignLiteralCell(tokenWrite, spreadsheetID, LITERAL_ID, LITERAL_VALUE);
        service.execute();   
    }
    
    //Test case 8
    @Test
    public void success() throws BubbledocsException {
 	    AssignLiteralCell service = new AssignLiteralCell(tokenAuthor, spreadsheetID, LITERAL_ID, LITERAL_VALUE);
        service.execute();
        assertEquals("Not returning the expected value for the cell!", LITERAL_VALUE.toString(), service.getResult().toString());
        assertTrue("Session was not updated", hasSessionUpdated(tokenAuthor));
    }
    
}
