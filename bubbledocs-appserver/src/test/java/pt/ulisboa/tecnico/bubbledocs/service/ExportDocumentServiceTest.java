package pt.ulisboa.tecnico.bubbledocs.service;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import pt.ulisboa.tecnico.bubbledocs.domain.Bubbledocs;
import pt.ulisboa.tecnico.bubbledocs.domain.Literal;
import pt.ulisboa.tecnico.bubbledocs.domain.Reference;
import pt.ulisboa.tecnico.bubbledocs.domain.Spreadsheet;
import pt.ulisboa.tecnico.bubbledocs.domain.User;
import pt.ulisboa.tecnico.bubbledocs.exceptions.BubbledocsException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.PermissionNotFoundException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.SpreadsheetNotFoundException;
import pt.ulisboa.tecnico.bubbledocs.service.ExportDocument;


public class ExportDocumentServiceTest extends BubbledocsServiceTest {

    private static final String USERNAME = "jp";
    private static final String NAME = "João Pereira";
    private static final String PASSWORD = "jp#";
    private static final String USERNAME_RO = "jn";
    private static final String NAME_RO = "João Neves";
    private static final String PASSWORD_RO = "jn#";
    private static final String SPREADHEET_NAME = "My Spreadsheet";
    private static final int SPREADHEET_ROWS = 10;
    private static final int SPREADHEET_COLUMNS = 15;
    private static final int REFERENCE_ROW = 1;
    private static final int REFERENCE_COLUMN = 1;
    private static final int LITERAL_ROW = 1;
    private static final int LITERAL_COLUMN = 1;

    private static final int LITERAL = 3;

    private static final int DOCID_INVALID = -5;
      
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
     	   
     	   //Assign a reference to cell
     	   ss.getCell(REFERENCE_ROW, REFERENCE_COLUMN).setContent(new Reference(ss.getCell(LITERAL_ROW, LITERAL_COLUMN)));     	        	   
     	   
     	   //Give RO user read permissions
     	   bubble.addReadPermission(USERNAME, USERNAME_RO, _spreadsheetID);

        }catch (BubbledocsException e) {
     	   System.out.println("FAILED TO POPULATE FOR ExportDocumentTest");
     	   //FIXME At this point we should probably abort!
        }
		
    }
    
    //Test case 1
    @Test
    public void successUserReadPermission() throws BubbledocsException {
  	    String token = addUserToSession(USERNAME_RO, PASSWORD_RO);
      	ExportDocument service = new ExportDocument(token, _spreadsheetID);
        service.execute();
        assertTrue("Returning empty XML string!", !service.getDocXML().isEmpty());
    }

    //Test case 2
    @Test
    public void successUserWritePermission() throws BubbledocsException {
  	    String token = addUserToSession(USERNAME, PASSWORD);
      	ExportDocument service = new ExportDocument(token, _spreadsheetID);
        service.execute();
        assertTrue("Returning empty XML string!", !service.getDocXML().isEmpty());
    }

    //Test case 3
    @Test(expected = SpreadsheetNotFoundException.class)
    public void invalidDocumentExport() throws BubbledocsException {
	    String token = addUserToSession(USERNAME, PASSWORD);
	    ExportDocument service = new ExportDocument(token, DOCID_INVALID);
        service.execute();
    }

    //Test case 4
    @Test(expected = PermissionNotFoundException.class)
    public void noPermissionsUserExport() throws BubbledocsException {
	    Bubbledocs bubble = Bubbledocs.getBubbledocs();
    	addUserToSession(USERNAME, PASSWORD);
    	//Temporarily revoke RO user permissions
    	bubble.revokeReadPermission(USERNAME, USERNAME_RO, _spreadsheetID);
       	
    	String token = addUserToSession(USERNAME_RO, PASSWORD_RO);    	
	    ExportDocument service = new ExportDocument(token, _spreadsheetID);
	    
        try{
        	service.execute();
        }catch(PermissionNotFoundException e){
        	//Give the RO user his read-only permission back
        	bubble.addReadPermission(USERNAME, USERNAME_RO, _spreadsheetID);
        	throw e;
        }        
        
    }    
}
