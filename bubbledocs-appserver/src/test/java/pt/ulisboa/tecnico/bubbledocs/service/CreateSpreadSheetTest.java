package pt.ulisboa.tecnico.bubbledocs.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.joda.time.DateTime;
import org.junit.Test;

import pt.ulisboa.tecnico.bubbledocs.domain.Bubbledocs;
import pt.ulisboa.tecnico.bubbledocs.domain.Spreadsheet;
import pt.ulisboa.tecnico.bubbledocs.exceptions.BubbledocsException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.EmptySpreadsheetNameException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.OutOfBoundsSpreadsheetException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UserNotInSessionException;
import pt.ulisboa.tecnico.bubbledocs.service.CreateSpreadSheet;

public class CreateSpreadSheetTest extends BubbledocsServiceTest {

    private static final String USERNAME = "md";
    private static final String NAME = "Mehrunes Dagon";
    private static final String PASSWORD = "md3";
	
    private static final String ANOTHER_USERNAME = "mb";
    private static final String ANOTHER_NAME = "Molag Bal";
    private static final String ANOTHER_PASSWORD = "mb8";
    
    private static final String EMPTY_STRING = "";
    
    private static final int SPREADSHEET_ROWS = 5;
    private static final int ANOTHER_ROWS = 1;
    private static final int SPREADSHEET_COLUMNS = 7;
    private static final String SPREADSHEET_NAME = "Argonian Account Book";
    private static final String ANOTHER_SPREADSHEET = "The Oblivion Crisis";
    
    private static final int INVALID_SPREADHEET_ROWS = -1;
    private static final int INVALID_SPREADHEET_COLUMNS = 0;  

    private String token;
    private String anotherToken;
    
    @Override
    public void initializeDomain() {
    	createUser(USERNAME, PASSWORD, NAME);
    	createUser(ANOTHER_USERNAME, ANOTHER_PASSWORD, ANOTHER_NAME);
    	try{
    		token = addUserToSession(USERNAME, PASSWORD);
    		anotherToken = addUserToSession(ANOTHER_USERNAME, ANOTHER_PASSWORD);
    	}catch (BubbledocsException e) {
    		assertTrue("FAILED TO POPULATE FOR CreateSpreadsheetTest", false);
    	}
    }

    //Test case 1
    @Test
    public void success() throws BubbledocsException {
        Bubbledocs bubble = Bubbledocs.getBubbledocs();
    	
        CreateSpreadSheet service = new CreateSpreadSheet(token, SPREADSHEET_NAME, SPREADSHEET_ROWS, SPREADSHEET_COLUMNS);
        service.execute();
        
        int ssId = service.getSheetId();
        Spreadsheet ss = bubble.getSpreadsheetById(ssId);        
        boolean writePermission = bubble.getPermission(USERNAME, ssId).getWritePermission();        
        
        assertTrue("Session was not updated", hasSessionUpdated(token));
        
        //Some checking to make absolutely sure it does what is expected   
        assertTrue("Generating bad IDs!", ssId >= 0);
       // assertTrue("IDs not sequential!", ssId < anotherSSID);
        assertEquals(ssId, ss.getId().intValue());
        assertEquals(SPREADSHEET_NAME, ss.getName());
        assertEquals(SPREADSHEET_ROWS, ss.getRows().intValue());
        assertEquals(SPREADSHEET_COLUMNS, ss.getColumns().intValue());
        assertEquals(USERNAME, ss.getAuthor());
        assertTrue("Author does not have write permission", writePermission);
        //Warning: this might not work well at midnight!!
        assertEquals(DateTime.now().getDayOfMonth(), ss.getDate().getDayOfMonth());
        assertEquals(DateTime.now().getMonthOfYear(), ss.getDate().getMonthOfYear());
        assertEquals(DateTime.now().getYear(), ss.getDate().getYear());
    }

    @Test
    public void successSequentialIds() throws BubbledocsException {
    	CreateSpreadSheet service = new CreateSpreadSheet(token, SPREADSHEET_NAME, SPREADSHEET_ROWS, SPREADSHEET_COLUMNS);
        service.execute();
        CreateSpreadSheet service2 = new CreateSpreadSheet(anotherToken, ANOTHER_SPREADSHEET, ANOTHER_ROWS, SPREADSHEET_COLUMNS);
        service2.execute();
        
        int SSID = service.getSheetId(),
        	anotherSSID = service2.getSheetId();
        
        assertTrue("IDs not sequential", SSID < anotherSSID);
    }
    
    //Test case 2
    @Test
    public void successSameName() throws BubbledocsException{
        CreateSpreadSheet service = new CreateSpreadSheet(token, SPREADSHEET_NAME, SPREADSHEET_ROWS, SPREADSHEET_COLUMNS);
        service.execute();
        CreateSpreadSheet service2 = new CreateSpreadSheet(token, SPREADSHEET_NAME, ANOTHER_ROWS, SPREADSHEET_COLUMNS);
        service2.execute();
    }
     
    //Test case 3
    @Test(expected = EmptySpreadsheetNameException.class)
    public void createEmptyNameSpreadsheet() throws BubbledocsException {
        try{
    		CreateSpreadSheet service = new CreateSpreadSheet(token, EMPTY_STRING, SPREADSHEET_ROWS, SPREADSHEET_COLUMNS);
        	service.execute();
        //This test case also checks if in case of failure the session is still updated
    	}catch(BubbledocsException e){
            assertTrue("Session was not updated", hasSessionUpdated(token));
            throw e;
    	}
    }
   
    //Test case 4
    @Test(expected = UserNotInSessionException.class)
    public void createSpreadsheetUserNotInSession() throws BubbledocsException {
        removeUserFromSession(token);
        CreateSpreadSheet service = new CreateSpreadSheet(token, SPREADSHEET_NAME, SPREADSHEET_ROWS, SPREADSHEET_COLUMNS);
        service.execute();
    }
    

    //Test case 5
    @Test(expected = OutOfBoundsSpreadsheetException.class)
    public void createInvalidSizeSpreadsheetNegativeRows() throws BubbledocsException {
        CreateSpreadSheet service = new CreateSpreadSheet(token, SPREADSHEET_NAME, INVALID_SPREADHEET_ROWS, SPREADSHEET_COLUMNS);
        service.execute();
    }
   
    //Test case 6 
    @Test(expected = OutOfBoundsSpreadsheetException.class)
    public void createInvalidSizeSpreadsheetNegativeColumns() throws BubbledocsException {
        CreateSpreadSheet service = new CreateSpreadSheet(token, SPREADSHEET_NAME, SPREADSHEET_ROWS, INVALID_SPREADHEET_COLUMNS);
        service.execute();
    }
    
    
}
