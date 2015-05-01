package pt.ulisboa.tecnico.bubbledocs.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import pt.ulisboa.tecnico.bubbledocs.domain.Add;
import pt.ulisboa.tecnico.bubbledocs.domain.Bubbledocs;
import pt.ulisboa.tecnico.bubbledocs.domain.Div;
import pt.ulisboa.tecnico.bubbledocs.domain.Literal;
import pt.ulisboa.tecnico.bubbledocs.domain.Mul;
import pt.ulisboa.tecnico.bubbledocs.domain.Prd;
import pt.ulisboa.tecnico.bubbledocs.domain.Avg;
import pt.ulisboa.tecnico.bubbledocs.domain.Reference;
import pt.ulisboa.tecnico.bubbledocs.domain.Spreadsheet;
import pt.ulisboa.tecnico.bubbledocs.domain.Sub;
import pt.ulisboa.tecnico.bubbledocs.domain.User;
import pt.ulisboa.tecnico.bubbledocs.exceptions.BubbledocsException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.PermissionNotFoundException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.SpreadsheetNotFoundException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UserNotInSessionException;
import pt.ulisboa.tecnico.bubbledocs.service.BubbledocsServiceTest;

public class GetSpreadsheetContentTest extends BubbledocsServiceTest {

	private static final String AUTHOR_USERNAME = "mehrunes";
    private static final String AUTHOR_NAME = "Mehrunes Dagon";
    private static final String AUTHOR_EMAIL = "mehrunes@deadlands.oblivion";
    
    private static final String USERNAME_RO = "molag";
    private static final String NAME_RO = "Molag Bal";
    private static final String EMAIL_RO = "molag@coldharbor.oblivion";

    private static final String USERNAME_WRITE = "hermaeus";
    private static final String NAME_WRITE = "Hermaeus Mora";
    private static final String EMAIL_WRITE = "mora";
    
    private static final String SPREADHEET_NAME = "Argonian Account Book";
    private static final Integer SPREADHEET_ROWS = 4;
    private static final Integer SPREADHEET_COLUMNS = 3;
    
    private static final String EXTERNAL_SPREADSHEET_REPRESENTATION =
    		"[ 2 3 12 ]\n" + 
    		"[ 1 6 6 ]\n"  + 
    		"[ 5 2 6 ]\n"; 
    	   /*[ 4 0 0 ]";*/

    private static final Integer DOCID_INVALID = -5;
    private static final Integer NON_EXISTING_ID = 100;
      
    //This is needed throughout the tests
    private Integer spreadsheetID;
    private String tokenAuthor;
    private String tokenRo;
	private String tokenWrite;
	
	private User author;
	private Spreadsheet ss; 
	
	
	@Override
	protected final void initializeDomain() {
		Bubbledocs bubble = Bubbledocs.getBubbledocs();
  	    author = createUser(AUTHOR_USERNAME, AUTHOR_EMAIL, AUTHOR_NAME);
        createUser(USERNAME_RO, EMAIL_RO, NAME_RO);
        createUser(USERNAME_WRITE, EMAIL_WRITE, NAME_WRITE);
	
        try{
    	   ss = createSpreadSheet(author, SPREADHEET_NAME, SPREADHEET_ROWS, SPREADHEET_COLUMNS);
     	   spreadsheetID = ss.getId();

     	   //Row 1
     	   ss.getCell(1, 1).setContent(new Literal(2));
     	   ss.getCell(1, 2).setContent(new Add(new Reference(ss.getCell(1, 1)), new Literal(1)));
     	   ss.getCell(1, 3).setContent(new Mul(new Reference(ss.getCell(2, 3)), new Reference(ss.getCell(1, 1))));
     	   
     	   //Row 2
     	   ss.getCell(2, 1).setContent(new Literal(1));
     	   ss.getCell(2, 2).setContent(new Mul(new Reference(ss.getCell(1, 1)), new Reference(ss.getCell(1, 2))));
     	   ss.getCell(2, 3).setContent(new Reference(ss.getCell(2, 2)));
     	   
     	   //Row 3
     	   ss.getCell(3, 1).setContent(new Add(new Reference(ss.getCell(1, 1)), new Reference(ss.getCell(1, 2))));
     	   ss.getCell(3, 2).setContent(new Sub(new Reference(ss.getCell(1, 2)), new Reference(ss.getCell(2, 1))));
     	   ss.getCell(3, 3).setContent(new Div(new Reference(ss.getCell(1, 3)), new Reference(ss.getCell(1, 1))));
     
     	   /*
     	   //Row 4
     	   ss.getCell(4, 1).setContent(new Avg(new Reference(ss.getCell(1, 1)), new Reference(ss.getCell(3, 3))));
    	   ss.getCell(4, 2).setContent(new Prd(new Reference(ss.getCell(1, 3)), new Reference(ss.getCell(4, 3))));
    	   ss.getCell(4, 3).setContent(new Literal(0));
    	     */   	   
     	   
     	   //Give RO user read permissions
     	   bubble.addReadPermission(AUTHOR_USERNAME, USERNAME_RO, spreadsheetID);
     	   //Give WRITE user write permissions
     	   bubble.addWritePermission(AUTHOR_USERNAME, USERNAME_WRITE, spreadsheetID);     	   

           tokenAuthor = addUserToSession(AUTHOR_USERNAME);
       	   tokenRo = addUserToSession(USERNAME_RO);
       	   tokenWrite = addUserToSession(USERNAME_WRITE);
       	   
        } catch (BubbledocsException e) {
     	   assertTrue(false);
        }	
               
	}
	
	@Test
	public void successAuthor() throws BubbledocsException {
		GetSpreadsheetContent service = new GetSpreadsheetContent(tokenAuthor, spreadsheetID);
		service.execute();
				
		assertTrue("Strings don't match", service.getResult().equals(EXTERNAL_SPREADSHEET_REPRESENTATION));
        assertTrue("Session was not updated", hasSessionUpdated(tokenAuthor));
	}
	
	
	@Test
	public void successReadOnly() throws BubbledocsException {
		GetSpreadsheetContent service = new GetSpreadsheetContent(tokenRo, spreadsheetID);
		service.execute();
		
		assertEquals(EXTERNAL_SPREADSHEET_REPRESENTATION, service.getResult());
        assertTrue("Session was not updated", hasSessionUpdated(tokenRo));
	}
	
	
	@Test
	public void successWrite() throws BubbledocsException {
		GetSpreadsheetContent service = new GetSpreadsheetContent(tokenWrite, spreadsheetID);
		service.execute();
		
		assertEquals(EXTERNAL_SPREADSHEET_REPRESENTATION, service.getResult());
        assertTrue("Session was not updated", hasSessionUpdated(tokenWrite));
	}
	
	
	@Test(expected = PermissionNotFoundException.class)
	public void failNoPermissionAndSessionUpdated() throws BubbledocsException {
    	Bubbledocs.getBubbledocs().revokeReadPermission(AUTHOR_USERNAME, USERNAME_RO, spreadsheetID);
		successReadOnly();
	}
	
	@Test(expected = UserNotInSessionException.class)
	public void failNotInSession() throws BubbledocsException {
		removeUserFromSession(tokenAuthor);
		new GetSpreadsheetContent(tokenAuthor, spreadsheetID).execute();
	}
	
	@Test(expected = SpreadsheetNotFoundException.class)
	public void failInvalidID() throws BubbledocsException {
		new GetSpreadsheetContent(tokenAuthor, DOCID_INVALID).execute();
	}
	
	@Test(expected = SpreadsheetNotFoundException.class)
	public void failNoID() throws BubbledocsException {
		new GetSpreadsheetContent(tokenAuthor, NON_EXISTING_ID).execute();
	}
	
	
}
