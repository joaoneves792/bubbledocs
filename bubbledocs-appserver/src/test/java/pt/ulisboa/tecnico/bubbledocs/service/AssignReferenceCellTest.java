package pt.ulisboa.tecnico.bubbledocs.service;

import org.junit.Test;

import pt.ulisboa.tecnico.bubbledocs.domain.Bubbledocs;
import pt.ulisboa.tecnico.bubbledocs.domain.Literal;
import pt.ulisboa.tecnico.bubbledocs.domain.Spreadsheet;
import pt.ulisboa.tecnico.bubbledocs.domain.User;
import pt.ulisboa.tecnico.bubbledocs.exceptions.BubbledocsException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidCellException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.ProtectedCellException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.SpreadsheetNotFoundException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UnauthorizedUserException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UserNotInSessionException;
import pt.ulisboa.tecnico.bubbledocs.service.AssignReferenceCell;

// add needed import declarations

public class AssignReferenceCellTest extends BubbledocsServiceTest {

    private static final String USERNAME = "jp";
    private static final String NAME = "João Pereira";
    private static final String PASSWORD = "jp#";
    private static final String USERNAME_RO = "jn";
    private static final String NAME_RO = "João Neves";
    private static final String PASSWORD_RO = "jn#";
    private static final String SPREADHEET_NAME = "My Spreadsheet";
    private static final int SPREADHEET_ROWS = 10;
    private static final int SPREADHEET_COLUMNS = 15;
    private static final String REFERENCE_ID = "1;1";
    private static final String PROTECTED_ID = "6;6";
    private static final String LITERAL_ID = "5;5";
    private static final int LITERAL_ROW = 5;
    private static final int LITERAL_COLUMN = 5;
    private static final int LITERAL = 3;
    private static final String INVALID_ID = "abc";
    private static final String INVALID_CELL_ID = "100;100";
    
    //This is needed throughout the tests
    private Integer _spreadsheetID;
    
    
    @Override
    public void initializeDomain() {
 	   Bubbledocs bubble = Bubbledocs.getBubbledocs();
 	   User user = createUser(USERNAME, PASSWORD, NAME);
       createUser(USERNAME_RO, PASSWORD_RO, NAME_RO);
       try{
    	   Spreadsheet ss = createSpreadSheet(user, SPREADHEET_NAME, SPREADHEET_ROWS, SPREADHEET_COLUMNS);
     	   _spreadsheetID = ss.getId();

     	   //Assign a literal to cell
     	   ss.getCell(LITERAL_ROW ,LITERAL_COLUMN).setContent(new Literal(LITERAL));
     	   
    	   //Protect Cell 6;6
    	   bubble.protectSpreadsheetCell(USERNAME, _spreadsheetID, 6, 6);
    	   
    	   //Give RO user read permissions
    	   bubble.addReadPermission(USERNAME, USERNAME_RO, _spreadsheetID);

       }catch (BubbledocsException e) {
    	   System.out.println("FAILED TO POPULATE FOR AssignReferenceCellTest");
    	   //FIXME At this point we should probably abort!
       }
    }

    //Test case 1
    @Test(expected = NumberFormatException.class)
    public void assignReferenceToInvalidId() throws BubbledocsException, NumberFormatException {
 	    String token = addUserToSession(USERNAME, PASSWORD);
    	AssignReferenceCell service = new AssignReferenceCell(token, _spreadsheetID, INVALID_ID, LITERAL_ID);
        service.execute();   
    }
    
    //Test case 2
    @Test(expected = InvalidCellException.class)
    public void assignReferenceToInvalidCell() throws BubbledocsException, NumberFormatException {
 	    String token = addUserToSession(USERNAME, PASSWORD);
    	AssignReferenceCell service = new AssignReferenceCell(token, _spreadsheetID, INVALID_CELL_ID, LITERAL_ID);
        service.execute();   
    }
    
    //Test case 3
    @Test(expected = NumberFormatException.class)
    public void assignReferenceWithInvalidReferenceId() throws BubbledocsException, NumberFormatException {
 	    String token = addUserToSession(USERNAME, PASSWORD);
    	AssignReferenceCell service = new AssignReferenceCell(token, _spreadsheetID, REFERENCE_ID, INVALID_ID);
        service.execute();   
    }
    
    //Test case 4
    //FIXME VERY IMPORTANT CREATING THIS REFERENCE IS NOT THROWING THE EXCEPTION
    @Test(expected = InvalidCellException.class)
    public void assignReferenceWithInvalidReferenceCell() throws BubbledocsException, NumberFormatException {
 	    String token = addUserToSession(USERNAME, PASSWORD);
    	AssignReferenceCell service = new AssignReferenceCell(token, _spreadsheetID, REFERENCE_ID, INVALID_CELL_ID);
        service.execute();   
    }
    
    //Test case 5
    @Test(expected = SpreadsheetNotFoundException.class)
    public void assignReferenceOnNonExistingSpreadsheet() throws BubbledocsException {
 	    String token = addUserToSession(USERNAME, PASSWORD);
    	AssignReferenceCell service = new AssignReferenceCell(token, _spreadsheetID+5, REFERENCE_ID, LITERAL_ID);
        service.execute();   
    }
    
    //Test case 6
    @Test(expected = ProtectedCellException.class)
    public void assignReferenceOnProtectedCell() throws BubbledocsException {
 	    String token = addUserToSession(USERNAME, PASSWORD);
    	AssignReferenceCell service = new AssignReferenceCell(token, _spreadsheetID, PROTECTED_ID, LITERAL_ID);
        service.execute();   
    }
    
    //Test case 7
    @Test(expected = UserNotInSessionException.class)
    public void assignReferenceUserNotInSession() throws BubbledocsException {
 	    String token = addUserToSession(USERNAME, PASSWORD);
 	    removeUserFromSession(token);
 	    AssignReferenceCell service = new AssignReferenceCell(token, _spreadsheetID, REFERENCE_ID, LITERAL_ID);
        service.execute();   
    }
    
    //Test case 8
    @Test(expected = UnauthorizedUserException.class)
    public void assignReferenceUserReadOnlyPermission() throws BubbledocsException {
 	    String token = addUserToSession(USERNAME_RO, PASSWORD_RO);
 	    AssignReferenceCell service = new AssignReferenceCell(token, _spreadsheetID, REFERENCE_ID, LITERAL_ID);
        service.execute();   
    }
    
    
    //Test case 9
    @Test
    public void success() throws BubbledocsException {
 	    String token = addUserToSession(USERNAME, PASSWORD);
 	    AssignReferenceCell service = new AssignReferenceCell(token, _spreadsheetID, REFERENCE_ID, LITERAL_ID);
        service.execute();   
    }
    
}
