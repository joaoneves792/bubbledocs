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
import pt.ulisboa.tecnico.bubbledocs.service.integrator.AssignRangeFunctionToCellIntegrator;

public class AssignRangeFunctionToCellIntegratorTest extends BubbledocsServiceTest {

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

    private static final String INVALID_RANGE_FUNCTION = "=ADD(-1;-1:a;b)";
    
    private static final String OUTBOUND_RANGE_START_FUNCTION = "=AVG(100;100:150;150)";
    private static final String OUTBOUND_RANGE_END_FUNCTION = "=AVG(5;5:100;100)";

        
    private static final String INVALID_FUNCTION = "=ADD(1;1:2;2)";
    private static final String VALID_AVG_FUNCTION = "=AVG(7;7:8;8)";
    private static final int CORRECT_AVG_RESULT = 2;
    private static final String VALID_AVG_LITERALS_AND_REFERENCES_FUNCTION = "=AVG(7;9:8;10)";
    private static final int CORRECT_AVG_LITERALS_AND_REFERENCES_RESULT = 2;
    private static final String VALID_AVG_LITERALS_AND_EMPTY_FUNCTION = "=AVG(7;11:8;12)";
    private static final int CORRECT_AVG_LITERALS_AND_EMPTY_RESULT = 1;
    private static final String VALID_AVG_WITH_ZEROS_FUNCTION = "=AVG(7;13:8;14)";
    private static final int CORRECT_AVG_WITH_ZEROS_RESULT = 1;
    private static final String VALID_AVG_SINGLE_CELL_FUNCTION  = "=AVG(5;5:5;5)";
    private static final int CORRECT_AVG_SINGLE_CELL_RESULT = 3;
    
    private static final String VALID_PRD_FUNCTION = "=PRD(7;7:8;8)";
    private static final int CORRECT_PRD_RESULT = 24;
    private static final String VALID_PRD_LITERALS_AND_REFERENCES_FUNCTION = "=PRD(7;9:8;10)";
    private static final int CORRECT_PRD_LITERALS_AND_REFERENCES_RESULT = 24;
    private static final String VALID_PRD_LITERALS_AND_EMPTY_FUNCTION = "=PRD(7;11:8;12)";
    private static final int CORRECT_PRD_LITERALS_AND_EMPTY_RESULT = 0;
    private static final String VALID_PRD_WITH_ZEROS_FUNCTION = "=PRD(7;13:8;14)";
    private static final int CORRECT_PRD_WITH_ZEROS_RESULT = 0;
    private static final String VALID_PRD_SINGLE_CELL_FUNCTION  = "=PRD(5;5:5;5)";
    private static final int CORRECT_PRD_SINGLE_CELL_RESULT = 3;
    
    
    
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
     	   
     	   //Create a matrix of literals (7;7:8;8)
     	   bubble.AssignLiteralCell(tokenAuthor, spreadsheetID, 7, 7, 1);
     	   bubble.AssignLiteralCell(tokenAuthor, spreadsheetID, 7, 8, 2);
     	   bubble.AssignLiteralCell(tokenAuthor, spreadsheetID, 8, 7, 3);
     	   bubble.AssignLiteralCell(tokenAuthor, spreadsheetID, 8, 8, 4);
     	   
     	   //Create a matrix of literals and references to literals (7;9:8;10)
     	   bubble.AssignLiteralCell(tokenAuthor, spreadsheetID, 7, 9, 1);
     	   bubble.assignReferenceCell(tokenAuthor, spreadsheetID, 7, 10, 7, 8);     	   
     	   bubble.AssignLiteralCell(tokenAuthor, spreadsheetID, 8, 9, 3);
     	   bubble.assignReferenceCell(tokenAuthor, spreadsheetID, 8, 10, 8, 8);     	   
     	   
     	   //Create a matrix of literals and empty cells (7;11:8;12)
     	   bubble.AssignLiteralCell(tokenAuthor, spreadsheetID, 7, 11, 1);
     	   		//cell (7;12) is left empty
     	   bubble.AssignLiteralCell(tokenAuthor, spreadsheetID, 8, 12, 3);
     	   		//cell (8;11) is left empty
     	   
     	   //Create a matrix of literals with some 0's (7;13:8;14)
     	   bubble.AssignLiteralCell(tokenAuthor, spreadsheetID, 7, 13, 1);
    	   bubble.AssignLiteralCell(tokenAuthor, spreadsheetID, 7, 14, 0);
    	   bubble.AssignLiteralCell(tokenAuthor, spreadsheetID, 8, 13, 3);
    	   bubble.AssignLiteralCell(tokenAuthor, spreadsheetID, 8, 14, 0);
    	   
     	   
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
    	AssignRangeFunctionToCellIntegrator service = new AssignRangeFunctionToCellIntegrator(tokenAuthor, spreadsheetID, INVALID_ID, VALID_AVG_FUNCTION);
    	service.execute();
    }
    
    //Test case 2
    @Test(expected = InvalidCellException.class)
    public void assignToOutboundRow() throws BubbledocsException{
    	AssignRangeFunctionToCellIntegrator service = new AssignRangeFunctionToCellIntegrator(tokenAuthor, spreadsheetID, OUTBOUND_CELL_ID_ROW, VALID_AVG_FUNCTION);
    	service.execute();
    }
    
    //Test case 3
    @Test(expected = InvalidCellException.class)
    public void assignToOutboundCol() throws BubbledocsException{
    	AssignRangeFunctionToCellIntegrator service = new AssignRangeFunctionToCellIntegrator(tokenAuthor, spreadsheetID, OUTBOUND_CELL_ID_COL, VALID_AVG_FUNCTION);
    	service.execute();
    }
    
    //Test case 4
    @Test(expected = InvalidFunctionException.class)
    public void assignInvalidLiteralFunction() throws BubbledocsException{
    	AssignRangeFunctionToCellIntegrator service = new AssignRangeFunctionToCellIntegrator(tokenAuthor, spreadsheetID, VALID_CELL_ID, INVALID_RANGE_FUNCTION);
    	service.execute();
    }
    
    //Test case 5
    @Test(expected = InvalidCellException.class)
    public void assignOutboundRangeStartFunction() throws BubbledocsException{
    	AssignRangeFunctionToCellIntegrator service = new AssignRangeFunctionToCellIntegrator(tokenAuthor, spreadsheetID, VALID_CELL_ID, OUTBOUND_RANGE_START_FUNCTION);
    	service.execute();
    }
    
    //Test case 6
    @Test(expected = InvalidCellException.class)
    public void assignOutboundRangeEndFunction() throws BubbledocsException{
    	AssignRangeFunctionToCellIntegrator service = new AssignRangeFunctionToCellIntegrator(tokenAuthor, spreadsheetID, VALID_CELL_ID, OUTBOUND_RANGE_END_FUNCTION);
    	service.execute();
    }
    
    //Test case 7
    @Test(expected = UnauthorizedUserException.class)
    public void assignFunctionROUser() throws BubbledocsException{
    	AssignRangeFunctionToCellIntegrator service = new AssignRangeFunctionToCellIntegrator(tokenRo, spreadsheetID, VALID_CELL_ID, VALID_AVG_FUNCTION);
    	service.execute();
    }
    
    //Test case 8
    @Test(expected = SpreadsheetNotFoundException.class)
    public void assignFunctionInvalidSpreadsheetID() throws BubbledocsException{
    	AssignRangeFunctionToCellIntegrator service = new AssignRangeFunctionToCellIntegrator(tokenAuthor, INVALID_SPREADSHEET_ID, VALID_CELL_ID, VALID_AVG_FUNCTION);
    	service.execute();
    }
    
    //Test case 9
    @Test(expected = SpreadsheetNotFoundException.class)
    public void assignFunctionInexistantSpreadsheetID() throws BubbledocsException{
    	AssignRangeFunctionToCellIntegrator service = new AssignRangeFunctionToCellIntegrator(tokenAuthor, INEXISTANT_SPREADSHEET_ID, VALID_CELL_ID, VALID_AVG_FUNCTION);
    	service.execute();
    }
    
    //Test case 10
    @Test(expected = ProtectedCellException.class)
    public void assignFunctionToProtectedCell() throws BubbledocsException{
    	AssignRangeFunctionToCellIntegrator service = new AssignRangeFunctionToCellIntegrator(tokenAuthor, spreadsheetID, PROTECTED_ID, VALID_AVG_FUNCTION);
    	service.execute();
    }
    
    //Test case 11
    @Test(expected = InvalidFunctionException.class)
    public void assignInvalidFunction() throws BubbledocsException{
    	AssignRangeFunctionToCellIntegrator service = new AssignRangeFunctionToCellIntegrator(tokenAuthor, spreadsheetID, VALID_CELL_ID, INVALID_FUNCTION);
    	service.execute();
    }
    
    //Test case 12
    @Test
    public void successWritePermissionUser() throws BubbledocsException{
    	Bubbledocs bubble = Bubbledocs.getBubbledocs();
    	AssignRangeFunctionToCellIntegrator service = new AssignRangeFunctionToCellIntegrator(tokenWrite, spreadsheetID, VALID_CELL_ID, VALID_AVG_FUNCTION);
    	service.execute();
    	//Can you think of an alternative way to check if the function was assigned to the cell??
    	assertTrue("Function not assigned to cell", bubble.getSpreadsheetById(spreadsheetID).getCell(VALID_CELL_ROW, VALID_CELL_COL).calculate() == CORRECT_AVG_RESULT);
    }
    
    //Test case 13
    @Test
    public void successAuthor() throws BubbledocsException{
    	Bubbledocs bubble = Bubbledocs.getBubbledocs();
    	AssignRangeFunctionToCellIntegrator service = new AssignRangeFunctionToCellIntegrator(tokenAuthor, spreadsheetID, VALID_CELL_ID, VALID_AVG_FUNCTION);
    	service.execute();
    	//Can you think of an alternative way to check if the function was assigned to the cell??
    	assertTrue("Function not assigned to cell", bubble.getSpreadsheetById(spreadsheetID).getCell(VALID_CELL_ROW, VALID_CELL_COL).calculate() == CORRECT_AVG_RESULT);
    }
    
    //Test case 14
    @Test
    public void successAvgOnlyLiterals() throws BubbledocsException{
    	AssignRangeFunctionToCellIntegrator service = new AssignRangeFunctionToCellIntegrator(tokenAuthor, spreadsheetID, VALID_CELL_ID, VALID_AVG_FUNCTION);
    	service.execute();
    	assertTrue("Function not calculating the correct result", service.getResult() == CORRECT_AVG_RESULT);
    }
    
    //Test case 15
    @Test
    public void successAvgLiteralsAndReferences() throws BubbledocsException{
    	AssignRangeFunctionToCellIntegrator service = new AssignRangeFunctionToCellIntegrator(tokenAuthor, spreadsheetID, VALID_CELL_ID, VALID_AVG_LITERALS_AND_REFERENCES_FUNCTION);
    	service.execute();
    	assertTrue("Function not calculating the correct result", service.getResult() == CORRECT_AVG_LITERALS_AND_REFERENCES_RESULT);
    }

    //Test case 16
    @Test
    public void successAvgLiteralsAndEmptyCells() throws BubbledocsException{
    	AssignRangeFunctionToCellIntegrator service = new AssignRangeFunctionToCellIntegrator(tokenAuthor, spreadsheetID, VALID_CELL_ID, VALID_AVG_LITERALS_AND_EMPTY_FUNCTION);
    	service.execute();
    	assertTrue("Function not calculating the correct result", service.getResult() == CORRECT_AVG_LITERALS_AND_EMPTY_RESULT);
    }
    
    //Test case 17
    @Test
    public void successAvgLiteralsWithZeros() throws BubbledocsException{
    	AssignRangeFunctionToCellIntegrator service = new AssignRangeFunctionToCellIntegrator(tokenAuthor, spreadsheetID, VALID_CELL_ID, VALID_AVG_WITH_ZEROS_FUNCTION);
    	service.execute();
    	assertTrue("Function not calculating the correct result", service.getResult() == CORRECT_AVG_WITH_ZEROS_RESULT);
    }
    
    //Test case 18
    @Test
    public void successAvgSingleCell() throws BubbledocsException{
    	AssignRangeFunctionToCellIntegrator service = new AssignRangeFunctionToCellIntegrator(tokenAuthor, spreadsheetID, VALID_CELL_ID, VALID_AVG_SINGLE_CELL_FUNCTION);
    	service.execute();
    	assertTrue("Function not calculating the correct result", service.getResult() == CORRECT_AVG_SINGLE_CELL_RESULT);
    }
    
  //Test case 19
    @Test
    public void successPrdOnlyLiterals() throws BubbledocsException{
    	AssignRangeFunctionToCellIntegrator service = new AssignRangeFunctionToCellIntegrator(tokenAuthor, spreadsheetID, VALID_CELL_ID, VALID_PRD_FUNCTION);
    	service.execute();
    	assertTrue("Function not calculating the correct result", service.getResult() == CORRECT_PRD_RESULT);
    }
    
    //Test case 20
    @Test
    public void successPrdLiteralsAndReferences() throws BubbledocsException{
    	AssignRangeFunctionToCellIntegrator service = new AssignRangeFunctionToCellIntegrator(tokenAuthor, spreadsheetID, VALID_CELL_ID, VALID_PRD_LITERALS_AND_REFERENCES_FUNCTION);
    	service.execute();
    	assertTrue("Function not calculating the correct result", service.getResult() == CORRECT_PRD_LITERALS_AND_REFERENCES_RESULT);
    }

    //Test case 21
    @Test
    public void successPrdLiteralsAndEmptyCells() throws BubbledocsException{
    	AssignRangeFunctionToCellIntegrator service = new AssignRangeFunctionToCellIntegrator(tokenAuthor, spreadsheetID, VALID_CELL_ID, VALID_PRD_LITERALS_AND_EMPTY_FUNCTION);
    	service.execute();
    	assertTrue("Function not calculating the correct result", service.getResult() == CORRECT_PRD_LITERALS_AND_EMPTY_RESULT);
    }
    
    //Test case 22
    @Test
    public void successPrdLiteralsWithZeros() throws BubbledocsException{
    	AssignRangeFunctionToCellIntegrator service = new AssignRangeFunctionToCellIntegrator(tokenAuthor, spreadsheetID, VALID_CELL_ID, VALID_PRD_WITH_ZEROS_FUNCTION);
    	service.execute();
    	assertTrue("Function not calculating the correct result", service.getResult() == CORRECT_PRD_WITH_ZEROS_RESULT);
    }
    
    //Test case 23
    @Test
    public void successPrdSingleCell() throws BubbledocsException{
    	AssignRangeFunctionToCellIntegrator service = new AssignRangeFunctionToCellIntegrator(tokenAuthor, spreadsheetID, VALID_CELL_ID, VALID_PRD_SINGLE_CELL_FUNCTION);
    	service.execute();
    	assertTrue("Function not calculating the correct result", service.getResult() == CORRECT_PRD_SINGLE_CELL_RESULT);
    }
       
}
