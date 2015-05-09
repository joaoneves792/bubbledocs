package pt.ulisboa.tecnico.bubbledocs.service.integration.component;

import java.io.UnsupportedEncodingException;

import org.junit.Test;

import mockit.Expectations;
import mockit.Mocked;
import pt.ulisboa.tecnico.bubbledocs.domain.Bubbledocs;
import pt.ulisboa.tecnico.bubbledocs.domain.Literal;
import pt.ulisboa.tecnico.bubbledocs.domain.Reference;
import pt.ulisboa.tecnico.bubbledocs.domain.Spreadsheet;
import pt.ulisboa.tecnico.bubbledocs.domain.User;
import pt.ulisboa.tecnico.bubbledocs.exceptions.BubbledocsException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.CannotLoadDocumentException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidImportException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.PermissionNotFoundException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.RemoteInvocationException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UnavailableServiceException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UserNotInSessionException;
import pt.ulisboa.tecnico.bubbledocs.service.BubbledocsServiceTest;
import pt.ulisboa.tecnico.bubbledocs.service.integrator.ImportDocumentIntegrator;
import pt.ulisboa.tecnico.bubbledocs.service.remote.StoreRemoteServices;


public class ImportDocumentIntegratorTest extends BubbledocsServiceTest {
	
    private static final String AUTHOR_USERNAME = "mehrunes";
    private static final String AUTHOR_NAME     = "Mehrunes Dagon";
    private static final String AUTHOR_EMAIL    = "mehrunes@deadlands.oblivion";
    
    private static final String USERNAME_RO     = "molag";
    private static final String NAME_RO         = "Molag Bal";
    private static final String EMAIL_RO        = "molag@coldharbor.oblivion";

    private static final String USERNAME_WRITE  = "hermaeus";
    private static final String NAME_WRITE      = "Hermaeus Mora";
    private static final String EMAIL_WRITE     = "mora";
    
    private static final String  SPREADSHEET_NAME   = "Argonian Account Book";
    private static final Integer SPREADSHEET_ROWS    = 10;
    private static final Integer SPREADSHEET_COLUMNS = 15;
    
    private static final Integer REFERENCE_ROW      = 1;
    private static final Integer REFERENCE_COLUMN   = 2;
    
    private static final Integer LITERAL_ROW        = 3;
    private static final Integer LITERAL_COLUMN     = 4;
    private static final Integer LITERAL_VALUE      = 3;

	
    //This is needed throughout the tests
    private Integer spreadsheetID;
    private String tokenRo;
	
	private User author;
	private Spreadsheet ss; 
	private String docXML;
	
	@Mocked
	StoreRemoteServices sdStore;
	
	@Override
	protected void initializeDomain() {
		Bubbledocs bubble = Bubbledocs.getBubbledocs();
		try {
			bubble.getSuperUser();
		
			author = createUser(AUTHOR_USERNAME, AUTHOR_EMAIL, AUTHOR_NAME);
			createUser(USERNAME_RO, EMAIL_RO, NAME_RO);
			createUser(USERNAME_WRITE, EMAIL_WRITE, NAME_WRITE);
			
			ss = createSpreadSheet(author, SPREADSHEET_NAME, SPREADSHEET_ROWS, SPREADSHEET_COLUMNS);
			spreadsheetID = ss.getId();
			
			//Assign a literal to cell
			ss.getCell(LITERAL_ROW ,LITERAL_COLUMN).setContent(new Literal(LITERAL_VALUE));
	     	   	
			//Assign a reference to cell
	     	ss.getCell(REFERENCE_ROW, REFERENCE_COLUMN).setContent(new Reference(ss.getCell(LITERAL_ROW, LITERAL_COLUMN)));     	        	   
	     	   
	     	//Give RO user read permissions
	     	bubble.addReadPermission(AUTHOR_USERNAME, USERNAME_RO, spreadsheetID);
	     	//Give WRITE user write permissions
	     	bubble.addWritePermission(AUTHOR_USERNAME, USERNAME_WRITE, spreadsheetID);     	   

	     	tokenRo = addUserToSession(USERNAME_RO);

	     	docXML = ss.export();
	     				
			} catch (Exception e) {
				org.junit.Assert.assertTrue(false);
			}
		}	

	@Test
	public void success() throws BubbledocsException, UnsupportedEncodingException {
		
		new Expectations() {
			{
				sdStore.loadDocument(USERNAME_RO, spreadsheetID.toString());
				result = docXML.getBytes("UTF-8");
			}
		};
				
		ImportDocumentIntegrator idi = new ImportDocumentIntegrator(tokenRo, spreadsheetID);
		idi.execute();
		
		Spreadsheet theNewSpreadsheet = getSpreadSheet(SPREADSHEET_NAME);

		org.junit.Assert.assertTrue(hasSessionUpdated(tokenRo));
		org.junit.Assert.assertTrue(theNewSpreadsheet.equal(ss));
		org.junit.Assert.assertTrue(theNewSpreadsheet.getAuthor().equals(USERNAME_RO));
		org.junit.Assert.assertTrue(theNewSpreadsheet.getId().equals(ss.getId()+1));
		
		try {
			Bubbledocs.getBubbledocs().getPermission(AUTHOR_USERNAME, theNewSpreadsheet.getId());
		} catch(PermissionNotFoundException e) {
			return; //permissions are correct
		}
		
		org.junit.Assert.assertTrue("Permission created for wrong user", false);
	}

	
	@Test(expected = InvalidImportException.class)
	public void failEmptySS() throws BubbledocsException {
		
		new Expectations() {
			{
				sdStore.loadDocument(USERNAME_RO, spreadsheetID.toString());
				result = null;
			}
		};
		
		new ImportDocumentIntegrator(tokenRo, spreadsheetID).execute();
	}

	@Test
	public void remoteUnavailableAndSessionUpdated() throws BubbledocsException {
		new Expectations() {
			{
				sdStore.loadDocument(USERNAME_RO, spreadsheetID.toString());
				result = new RemoteInvocationException("");
			}
		};
		
		try {
			new ImportDocumentIntegrator(tokenRo, spreadsheetID).execute();	
		} catch(UnavailableServiceException e) {
			org.junit.Assert.assertTrue("Session was not updated", hasSessionUpdated(tokenRo));
			return;
		}
		
		org.junit.Assert.assertTrue(false);
	}
	
	@Test(expected = CannotLoadDocumentException.class)
	public void fail() throws BubbledocsException {
		new Expectations() {
			{
				sdStore.loadDocument(USERNAME_RO, spreadsheetID.toString());
				result = new CannotLoadDocumentException("");
			}
		};
		new ImportDocumentIntegrator(tokenRo, spreadsheetID).execute();	
	}
	
	
	@Test(expected = UserNotInSessionException.class)
	public void failNotInSession() throws UnsupportedEncodingException, BubbledocsException {
		removeUserFromSession(tokenRo);
		success();
	}
}