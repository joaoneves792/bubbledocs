package pt.ulisboa.tecnico.bubbledocs.service.integration.component;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import pt.ulisboa.tecnico.bubbledocs.domain.Bubbledocs;
import pt.ulisboa.tecnico.bubbledocs.domain.Spreadsheet;
import pt.ulisboa.tecnico.bubbledocs.domain.User;
import pt.ulisboa.tecnico.bubbledocs.exceptions.BubbledocsException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidCellException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidFunctionException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.ProtectedCellException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.SpreadsheetNotFoundException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UnauthorizedUserException;
import pt.ulisboa.tecnico.bubbledocs.service.BubbledocsServiceTest;
import pt.ulisboa.tecnico.bubbledocs.service.integrator.AssignBinaryFunctionToCellIntegrator;

public class AssignBinaryFunctionToCellIntegratorTest extends BubbledocsServiceTest {

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
    private static final int INEXISTANT_SPREADSHEET_ID = 100;
    private static final int INVALID_SPREADSHEET_ID = -1;
    
    private static final String LITERAL_VALUE = "3";
    private static final int LITERAL_ROW = 5;
    private static final int LITERAL_COL = 5;
    
    private static final String PROTECTED_ID = "6;6";
    private static final Integer PROTECTED_ROW = 6;
    private static final Integer PROTECTED_COLUMN = 6;
    
    private static final String INVALID_ID = "abc";
    private static final String OUTBOUND_CELL_ID_ROW = "100;1";
    private static final String OUTBOUND_CELL_ID_COL = "1;100";
    
    private static final String VALID_CELL_ID = "1;1";
    private static final int VALID_CELL_ROW = 1;
    private static final int VALID_CELL_COL = 1;

    private static final String EMPTY_STRING = "";
    
    private static final String INVALID_FUNCTION = "=AVG(7,5;5)";
    private static final String VALID_ADD_FUNCTION = "=ADD(7,5;5)";
    private static final int CORRECT_ADD_RESULT = 10;
    private static final String VALID_SUB_FUNCTION = "=SUB(7,5;5)";
    private static final int CORRECT_SUB_RESULT = 4;
    private static final String VALID_MUL_FUNCTION = "=MUL(7,5;5)";
    private static final int CORRECT_MUL_RESULT = 21;
    private static final String VALID_DIV_FUNCTION = "=DIV(7,5;5)";
    private static final int CORRECT_DIV_RESULT = 2;
    private static final String INVALID_LITERAL_FUNCTION = "=ADD(B,5;5)";
    private static final String INVALID_REFERENCE_FUNCTION = "=ADD(7,5;B)";
    private static final String OUTBOUND_REFERENCE_FUNCTION = "=ADD(7,100;1)";
    
    
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
      
    	   tokenAuthor = addUserToSession(AUTHOR_USERNAME);
     	   tokenRo = addUserToSession(USERNAME_RO);
     	   tokenWrite = addUserToSession(USERNAME_WRITE);
    	   
    	   Spreadsheet ss = createSpreadSheet(user, SPREADHEET_NAME, SPREADHEET_ROWS, SPREADHEET_COLUMNS);
     	   spreadsheetID = ss.getId();

     	   //Add a literal to 5;5
     	   bubble.AssignLiteralCell(tokenAuthor, spreadsheetID, LITERAL_ROW, LITERAL_COL, Integer.parseInt(LITERAL_VALUE));
     	   
    	   //Protect Cell 6;6
    	   bubble.protectSpreadsheetCell(AUTHOR_USERNAME, spreadsheetID, PROTECTED_ROW, PROTECTED_COLUMN);

    	   //Give RO user read permission
    	   bubble.addReadPermission(AUTHOR_USERNAME, USERNAME_RO, spreadsheetID);
    	   //Give Write user write permission
    	   bubble.addWritePermission(AUTHOR_USERNAME, USERNAME_WRITE, spreadsheetID);
     		   

    	   
       } catch (BubbledocsException e) {
    	   assertTrue("Failed to populate for AssignBinaryFunctionToCellTest", false);
       }
    }

    //Test case 1
    @Test(expected = InvalidCellException.class)
    public void assignToInvalidId() throws BubbledocsException{
    	AssignBinaryFunctionToCellIntegrator service = new AssignBinaryFunctionToCellIntegrator(tokenAuthor, spreadsheetID, INVALID_ID, VALID_ADD_FUNCTION);
    	service.execute();
    }
    
    //Test case 2
    @Test(expected = InvalidCellException.class)
    public void assignToOutboundRow() throws BubbledocsException{
    	AssignBinaryFunctionToCellIntegrator service = new AssignBinaryFunctionToCellIntegrator(tokenAuthor, spreadsheetID, OUTBOUND_CELL_ID_ROW, VALID_ADD_FUNCTION);
    	service.execute();
    }
    
    //Test case 3
    @Test(expected = InvalidCellException.class)
    public void assignToOutboundCol() throws BubbledocsException{
    	AssignBinaryFunctionToCellIntegrator service = new AssignBinaryFunctionToCellIntegrator(tokenAuthor, spreadsheetID, OUTBOUND_CELL_ID_COL, VALID_ADD_FUNCTION);
    	service.execute();
    }
    
    //Test case 4
    @Test(expected = InvalidFunctionException.class)
    public void assignInvalidLiteralFunction() throws BubbledocsException{
    	AssignBinaryFunctionToCellIntegrator service = new AssignBinaryFunctionToCellIntegrator(tokenAuthor, spreadsheetID, VALID_CELL_ID, INVALID_LITERAL_FUNCTION);
    	service.execute();
    }
    
    //Test case 5
    @Test(expected = InvalidFunctionException.class)
    public void assignInvalidReferenceFunction() throws BubbledocsException{
    	AssignBinaryFunctionToCellIntegrator service = new AssignBinaryFunctionToCellIntegrator(tokenAuthor, spreadsheetID, VALID_CELL_ID, INVALID_REFERENCE_FUNCTION);
    	service.execute();
    }
    
    //Test case 6
    @Test(expected = InvalidCellException.class)
    public void assignOutboundReferenceFunction() throws BubbledocsException{
    	AssignBinaryFunctionToCellIntegrator service = new AssignBinaryFunctionToCellIntegrator(tokenAuthor, spreadsheetID, VALID_CELL_ID, OUTBOUND_REFERENCE_FUNCTION);
    	service.execute();
    }
    
    //Test case 7
    @Test(expected = UnauthorizedUserException.class)
    public void assignFunctionROUser() throws BubbledocsException{
    	AssignBinaryFunctionToCellIntegrator service = new AssignBinaryFunctionToCellIntegrator(tokenRo, spreadsheetID, VALID_CELL_ID, VALID_ADD_FUNCTION);
    	service.execute();
    }
    
    //Test case 8
    @Test(expected = SpreadsheetNotFoundException.class)
    public void assignFunctionInvalidSpreadsheetID() throws BubbledocsException{
    	AssignBinaryFunctionToCellIntegrator service = new AssignBinaryFunctionToCellIntegrator(tokenAuthor, INVALID_SPREADSHEET_ID, VALID_CELL_ID, VALID_ADD_FUNCTION);
    	service.execute();
    }
    
    //Test case 9
    @Test(expected = SpreadsheetNotFoundException.class)
    public void assignFunctionInexistantSpreadsheetID() throws BubbledocsException{
    	AssignBinaryFunctionToCellIntegrator service = new AssignBinaryFunctionToCellIntegrator(tokenAuthor, INEXISTANT_SPREADSHEET_ID, VALID_CELL_ID, VALID_ADD_FUNCTION);
    	service.execute();
    }
    
    //Test case 10
    @Test(expected = ProtectedCellException.class)
    public void assignFunctionToProtectedCell() throws BubbledocsException{
    	AssignBinaryFunctionToCellIntegrator service = new AssignBinaryFunctionToCellIntegrator(tokenAuthor, spreadsheetID, PROTECTED_ID, VALID_ADD_FUNCTION);
    	service.execute();
    }
    
    //Test case 11
    @Test(expected = InvalidFunctionException.class)
    public void assignInvalidFunction() throws BubbledocsException{
    	AssignBinaryFunctionToCellIntegrator service = new AssignBinaryFunctionToCellIntegrator(tokenAuthor, spreadsheetID, VALID_CELL_ID, INVALID_FUNCTION);
    	service.execute();
    }
    
    //Test case 12
    @Test
    public void successWritePermissionUser() throws BubbledocsException{
    	Bubbledocs bubble = Bubbledocs.getBubbledocs();
    	AssignBinaryFunctionToCellIntegrator service = new AssignBinaryFunctionToCellIntegrator(tokenWrite, spreadsheetID, VALID_CELL_ID, VALID_ADD_FUNCTION);
    	service.execute();
    	//Can you think of an alternative way to check if the function was assigned to the cell??
    	assertTrue("Function not assigned to cell", bubble.getSpreadsheetById(spreadsheetID).getCell(VALID_CELL_ROW, VALID_CELL_COL).calculate() == CORRECT_ADD_RESULT);
    }
    
    //Test case 13
    @Test
    public void successAuthor() throws BubbledocsException{
    	Bubbledocs bubble = Bubbledocs.getBubbledocs();
    	AssignBinaryFunctionToCellIntegrator service = new AssignBinaryFunctionToCellIntegrator(tokenAuthor, spreadsheetID, VALID_CELL_ID, VALID_ADD_FUNCTION);
    	service.execute();
    	//Can you think of an alternative way to check if the function was assigned to the cell??
    	assertTrue("Function not assigned to cell", bubble.getSpreadsheetById(spreadsheetID).getCell(VALID_CELL_ROW, VALID_CELL_COL).calculate() == CORRECT_ADD_RESULT);
    }
    
    //Test case 14
    @Test
    public void successAdd() throws BubbledocsException{
    	AssignBinaryFunctionToCellIntegrator service = new AssignBinaryFunctionToCellIntegrator(tokenAuthor, spreadsheetID, VALID_CELL_ID, VALID_ADD_FUNCTION);
    	service.execute();
    	assertTrue("Function not calculating the correct result", service.getResult() == CORRECT_ADD_RESULT);
    }
    
    //Test case 15
    @Test
    public void successSub() throws BubbledocsException{
    	AssignBinaryFunctionToCellIntegrator service = new AssignBinaryFunctionToCellIntegrator(tokenAuthor, spreadsheetID, VALID_CELL_ID, VALID_SUB_FUNCTION);
    	service.execute();
    	assertTrue("Function not calculating the correct result", service.getResult() == CORRECT_SUB_RESULT);
    }
    
    //Test case 16
    @Test
    public void successMul() throws BubbledocsException{
    	AssignBinaryFunctionToCellIntegrator service = new AssignBinaryFunctionToCellIntegrator(tokenAuthor, spreadsheetID, VALID_CELL_ID, VALID_MUL_FUNCTION);
    	service.execute();
    	assertTrue("Function not calculating the correct result", service.getResult() == CORRECT_MUL_RESULT);
    }
    
    //Test case 17
    @Test
    public void successDiv() throws BubbledocsException{
    	AssignBinaryFunctionToCellIntegrator service = new AssignBinaryFunctionToCellIntegrator(tokenAuthor, spreadsheetID, VALID_CELL_ID, VALID_DIV_FUNCTION);
    	service.execute();
    	assertTrue("Function not calculating the correct result", service.getResult() == CORRECT_DIV_RESULT);
    }
    
    //Test case 18
    @Test(expected = InvalidFunctionException.class)
    public void assignEmptyString() throws BubbledocsException{
    	AssignBinaryFunctionToCellIntegrator service = new AssignBinaryFunctionToCellIntegrator(tokenAuthor, spreadsheetID, VALID_CELL_ID, EMPTY_STRING);
    	service.execute();
    }
           
}
