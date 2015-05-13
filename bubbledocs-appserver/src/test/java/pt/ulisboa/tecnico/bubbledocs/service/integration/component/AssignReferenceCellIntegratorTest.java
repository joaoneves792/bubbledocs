package pt.ulisboa.tecnico.bubbledocs.service.integration.component;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import pt.ulisboa.tecnico.bubbledocs.domain.Add;
import pt.ulisboa.tecnico.bubbledocs.domain.Bubbledocs;
import pt.ulisboa.tecnico.bubbledocs.domain.Div;
import pt.ulisboa.tecnico.bubbledocs.domain.Literal;
import pt.ulisboa.tecnico.bubbledocs.domain.Spreadsheet;
import pt.ulisboa.tecnico.bubbledocs.domain.User;
import pt.ulisboa.tecnico.bubbledocs.exceptions.BubbledocsException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidCellException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.ProtectedCellException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.SpreadsheetNotFoundException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UnauthorizedUserException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UserNotInSessionException;
import pt.ulisboa.tecnico.bubbledocs.service.BubbledocsServiceTest;
import pt.ulisboa.tecnico.bubbledocs.service.integrator.AssignReferenceCellIntegrator;

public class AssignReferenceCellIntegratorTest extends BubbledocsServiceTest {

    private static final String AUTHOR_USERNAME = "mehrunes";
    private static final String AUTHOR_NAME = "Mehrunes Dagon";
    private static final String AUTHOR_EMAIL = "mehrunes@deadlands.oblivion";
    
    private static final String USERNAME_RO = "molagb";
    private static final String NAME_RO = "Molag Bal";
    private static final String EMAIL_RO = "molag@cold-harbor.oblivion";
    
    private static final String USERNAME_WRITE = "hermaeus";
    private static final String NAME_WRITE = "Hermaeus Mora";
    private static final String EMAIL_WRITE = "hermaeus@apocrypha.oblivion";
    
    private static final int SPREADHEET_ROWS = 10;
    private static final int SPREADHEET_COLUMNS = 15;
    private static final String SPREADHEET_NAME = "Argonian Account Book";
    
    private static final String REFERENCE_ID = "1;1";
    private static final String LITERAL_ID = "5;5";
    private static final String PROTECTED_ID = "6;6";
    
    private static final Integer PROTECTED_ROW = 6;
    private static final Integer PROTECTED_COLUMN = 6;

    private static final int LITERAL_ROW = 5;
    private static final int LITERAL_COLUMN = 5;
    private static final String LITERAL_VALUE = "3";
    
    private static final String INVALID_ID = "abc";
    private static final String OUTBOUND_CELL_ID_ROW = "100;1";
    private static final String OUTBOUND_CELL_ID_COL = "1;100";
    
	private static final int DIV_ROW = 10;
	private static final int DIV_COLUMN = 10;
	
	private static final String DIV_ID = "10;10";
	private static final String DIV_REF_ID = "9;12";
	
	private static final String INVALID = "#VALUE";
	
	private static final String ADD_REF_ID = "4;4";
	private static final String ADD_ID = "9;9";
	private static final String ADD_VALUE = "10";
	private static final int ADD_ROW = 9;
	private static final int ADD_COLUMN = 9;
	
	
    
    //This is needed throughout the tests
    private Integer spreadsheetID;
    private Integer invalidSSID;
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
     	   invalidSSID = spreadsheetID + 100;

     	   //Assign a literal to a cell
     	   ss.getCell(LITERAL_ROW ,LITERAL_COLUMN).setContent(new Literal(Integer.parseInt(LITERAL_VALUE)));
     	   
     	   //assign div to  cell
     	   ss.getCell(DIV_ROW, DIV_COLUMN).setContent(new Div(new Literal(5), new Literal(0)));
     	        	   
     	   //assign add to  cell
     	   ss.getCell(ADD_ROW, ADD_COLUMN).setContent(new Add(new Literal(7), new Literal(3)));
     	   
    	   //Protect another Cell
    	   bubble.protectSpreadsheetCell(AUTHOR_USERNAME, spreadsheetID, PROTECTED_ROW, PROTECTED_COLUMN);
    	   
    	   //Give RO user read permissions
    	   bubble.addReadPermission(AUTHOR_USERNAME, USERNAME_RO, spreadsheetID);
    	   //Give Write user read permissions
    	   bubble.addWritePermission(AUTHOR_USERNAME, USERNAME_WRITE, spreadsheetID);
    	   
    	   tokenAuthor = addUserToSession(AUTHOR_USERNAME);
     	   tokenRo = addUserToSession(USERNAME_RO);
     	   tokenWrite = addUserToSession(USERNAME_WRITE);

       } catch (BubbledocsException e) {
    	   assertTrue("Failed to populate for AssignReferenceCellIntegratorTest", false);
       }
    }

    //Test case 1
    @Test(expected = NumberFormatException.class)
    public void assignReferenceInvalidId() throws BubbledocsException, NumberFormatException {
    	AssignReferenceCellIntegrator service = new AssignReferenceCellIntegrator(tokenAuthor, spreadsheetID, INVALID_ID, LITERAL_ID);
        service.execute();   
    }
    
    //Test case 2
    @Test(expected = InvalidCellException.class)
    public void assignReferenceToOutOfBoundsCellRow() throws BubbledocsException, NumberFormatException {
    	try{
    		AssignReferenceCellIntegrator service = new AssignReferenceCellIntegrator(tokenAuthor, spreadsheetID, OUTBOUND_CELL_ID_ROW, LITERAL_ID);
    		service.execute(); 
        //This test case also checks if in case of failure the session is still updated
		}catch(BubbledocsException e){
			assertTrue("Session was not updated", hasSessionUpdated(tokenAuthor));
			throw e;
		}
    }
    
    //Test case 2
    @Test(expected = InvalidCellException.class)
    public void assignReferenceToOutOfBoundsCellCol() throws BubbledocsException, NumberFormatException {
    	try{
    		AssignReferenceCellIntegrator service = new AssignReferenceCellIntegrator(tokenAuthor, spreadsheetID, OUTBOUND_CELL_ID_COL, LITERAL_ID);
    		service.execute(); 
        //This test case also checks if in case of failure the session is still updated
		}catch(BubbledocsException e){
			assertTrue("Session was not updated", hasSessionUpdated(tokenAuthor));
			throw e;
		}
    }
    
    //Test case 3
    @Test(expected = NumberFormatException.class)
    public void assignReferenceWithInvalidReferenceId() throws BubbledocsException, NumberFormatException {
    	AssignReferenceCellIntegrator service = new AssignReferenceCellIntegrator(tokenAuthor, spreadsheetID, REFERENCE_ID, INVALID_ID);
        service.execute();   
    }
    
    //Test case 4
    @Test(expected = InvalidCellException.class)
    public void assignReferenceWithInvalidReferenceCell() throws BubbledocsException, NumberFormatException {
    	AssignReferenceCellIntegrator service = new AssignReferenceCellIntegrator(tokenAuthor, spreadsheetID, REFERENCE_ID, OUTBOUND_CELL_ID_ROW);
        service.execute();   
    }
    
    //Test case 5
    @Test(expected = SpreadsheetNotFoundException.class)
    public void assignReferenceOnNonExistingSpreadsheet() throws BubbledocsException {
    	AssignReferenceCellIntegrator service = new AssignReferenceCellIntegrator(tokenAuthor, invalidSSID, REFERENCE_ID, LITERAL_ID);
        service.execute();   
    }
    
    //Test case 6
    @Test(expected = ProtectedCellException.class)
    public void assignReferenceOnProtectedCell() throws BubbledocsException {
    	AssignReferenceCellIntegrator service = new AssignReferenceCellIntegrator(tokenAuthor, spreadsheetID, PROTECTED_ID, LITERAL_ID);
        service.execute();   
    }
    
    //Test case 7
    @Test(expected = UserNotInSessionException.class)
    public void assignReferenceUserNotInSession() throws BubbledocsException {
 	    removeUserFromSession(tokenAuthor);
 	    AssignReferenceCellIntegrator service = new AssignReferenceCellIntegrator(tokenAuthor, spreadsheetID, REFERENCE_ID, LITERAL_ID);
        service.execute();   
    }
    
    //Test case 8
    @Test(expected = UnauthorizedUserException.class)
    public void assignReferenceUserReadOnlyPermission() throws BubbledocsException {
 	    AssignReferenceCellIntegrator service = new AssignReferenceCellIntegrator(tokenRo, spreadsheetID, REFERENCE_ID, LITERAL_ID);
        service.execute();   
    }
    
    @Test
    public void assignReferenceNotAuthorWritePermission() throws BubbledocsException {
 	    AssignReferenceCellIntegrator service = new AssignReferenceCellIntegrator(tokenWrite, spreadsheetID, REFERENCE_ID, LITERAL_ID);
        service.execute();   
    }
        
    //Test case 9
    @Test
    public void success() throws BubbledocsException {
 	    AssignReferenceCellIntegrator service = new AssignReferenceCellIntegrator(tokenAuthor, spreadsheetID, REFERENCE_ID, LITERAL_ID);
        service.execute();
        assertEquals("Not returning the expected value for the cell!", LITERAL_VALUE, service.getResult());
        assertTrue("Session was not updated", hasSessionUpdated(tokenAuthor));
        
    }
    
    @Test
    public void successInvalidCell() throws BubbledocsException {
    	AssignReferenceCellIntegrator service = new AssignReferenceCellIntegrator(tokenAuthor, spreadsheetID, DIV_REF_ID, DIV_ID);
    	service.execute();
    	assertTrue(service.getResult().equals(INVALID));
    }
    
    @Test
    public void successValidBinary() throws BubbledocsException {
    	AssignReferenceCellIntegrator service = new AssignReferenceCellIntegrator(tokenAuthor, spreadsheetID, ADD_REF_ID, ADD_ID);
    	service.execute();
    	assertTrue(service.getResult().equals(ADD_VALUE));
    }
    
}
