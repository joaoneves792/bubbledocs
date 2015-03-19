package pt.ulisboa.tecnico.bubbledocs.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;


import pt.ulisboa.tecnico.bubbledocs.domain.Bubbledocs;
import pt.ulisboa.tecnico.bubbledocs.domain.Spreadsheet;
import pt.ulisboa.tecnico.bubbledocs.exceptions.BubbledocsException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.EmptySpreadsheetNameException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.OutOfBoundsSpreadsheetException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UserNotInSessionException;

import pt.ulisboa.tecnico.bubbledocs.service.CreateSpreadSheet;

// add needed import declarations

public class CreateSpreadSheetTest extends BubbledocsServiceTest {

    private static final String USERNAME = "jp";
    private static final String NAME = "João Pereira";
    private static final String PASSWORD = "jp#";
    private static final String SPREADHEET_NAME = "My Spreadsheet";
    private static final String EMPTY_STRING = "";
    private static final int SPREADHEET_ROWS = 5;
    private static final int SPREADHEET_COLUMNS = 7;
    private static final int INVALID_SPREADHEET_ROWS = -1;
    private static final int INVALID_SPREADHEET_COLUMNS = -3;

    @Override
    public void populate4Test() {
       createUser(USERNAME, PASSWORD, NAME);
    }

    //Test case 1
    @Test
    public void success() throws BubbledocsException {
        Bubbledocs bubble = Bubbledocs.getBubbledocs();
        Spreadsheet ss;
        String token = addUserToSession(USERNAME, PASSWORD);
    	
        CreateSpreadSheet service = new CreateSpreadSheet(token, SPREADHEET_NAME, SPREADHEET_ROWS, SPREADHEET_COLUMNS);
        service.execute();
        
        int ssId = service.getSheetId();
        ss = bubble.getSpreadsheetById(ssId);
     
        //Some checking to make absolutely sure it does what is expected   
        assertTrue("Generating bad IDs!",0 < ssId);
        assertEquals(ssId, ss.get_id().intValue());
        assertEquals(SPREADHEET_NAME, ss.get_name());
        assertEquals(SPREADHEET_ROWS, ss.get_lines().intValue());
        assertEquals(SPREADHEET_COLUMNS, ss.get_columns().intValue());
        assertEquals(NAME, ss.get_author());
        
    }

    //Test case 2
    @Test
    public void successSameName() throws BubbledocsException{
        String token = addUserToSession(USERNAME, PASSWORD);
        
        CreateSpreadSheet service = new CreateSpreadSheet(token, SPREADHEET_NAME, SPREADHEET_ROWS, SPREADHEET_COLUMNS);
        service.execute();
    }
     
    //Test case 3
    @Test(expected = EmptySpreadsheetNameException.class)
    public void createEmptyNameSpreadsheet() throws BubbledocsException {
        String token = addUserToSession(USERNAME, PASSWORD);

        CreateSpreadSheet service = new CreateSpreadSheet(token, EMPTY_STRING, SPREADHEET_ROWS, SPREADHEET_COLUMNS);
        service.execute();
    }
   
    //Test case 4
    @Test(expected = UserNotInSessionException.class)
    public void createSpreadsheetUserNotInSession() throws BubbledocsException {
        String token = addUserToSession(USERNAME, PASSWORD);
        removeUserFromSession(token);

        CreateSpreadSheet service = new CreateSpreadSheet(token, SPREADHEET_NAME, SPREADHEET_ROWS, SPREADHEET_COLUMNS);
        service.execute();
    }
    

    //Test case 5
    @Test(expected = OutOfBoundsSpreadsheetException.class)
    public void createInvalidSizeSpreadsheetNegativeRows() throws BubbledocsException {
        String token = addUserToSession(USERNAME, PASSWORD);

        CreateSpreadSheet service = new CreateSpreadSheet(token, SPREADHEET_NAME, INVALID_SPREADHEET_ROWS, SPREADHEET_COLUMNS);
        service.execute();
    }
   
    //Test case 6 
    @Test(expected = OutOfBoundsSpreadsheetException.class)
    public void createInvalidSizeSpreadsheetNegativeColumns() throws BubbledocsException {
        String token = addUserToSession(USERNAME, PASSWORD);

        CreateSpreadSheet service = new CreateSpreadSheet(token, SPREADHEET_NAME, SPREADHEET_ROWS, INVALID_SPREADHEET_COLUMNS);
        service.execute();
    }
    
}
