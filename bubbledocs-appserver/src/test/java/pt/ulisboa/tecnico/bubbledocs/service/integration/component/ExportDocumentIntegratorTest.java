package pt.ulisboa.tecnico.bubbledocs.service.integration.component;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import mockit.Expectations;
import mockit.Mocked;

import org.jdom2.JDOMException;
import org.junit.Test;

import pt.ulisboa.tecnico.bubbledocs.domain.Bubbledocs;
import pt.ulisboa.tecnico.bubbledocs.domain.Literal;
import pt.ulisboa.tecnico.bubbledocs.domain.Reference;
import pt.ulisboa.tecnico.bubbledocs.domain.Spreadsheet;
import pt.ulisboa.tecnico.bubbledocs.domain.User;
import pt.ulisboa.tecnico.bubbledocs.exceptions.BubbledocsException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.CannotStoreDocumentException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.PermissionNotFoundException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.RemoteInvocationException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.SpreadsheetNotFoundException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UnavailableServiceException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UserNotInSessionException;
import pt.ulisboa.tecnico.bubbledocs.service.BubbledocsServiceTest;
import pt.ulisboa.tecnico.bubbledocs.service.integrator.ExportDocumentIntegrator;
import pt.ulisboa.tecnico.bubbledocs.service.remote.StoreRemoteServices;

public class ExportDocumentIntegratorTest extends BubbledocsServiceTest {
	
    private static final String AUTHOR_USERNAME = "mehrunes";
    private static final String AUTHOR_NAME = "Mehrunes Dagon";
    private static final String AUTHOR_EMAIL = "mehrunes@deadlands.oblivion";
    
    private static final String USERNAME_RO = "molag";
    private static final String NAME_RO = "Molag Bal";
    private static final String EMAIL_RO = "molag@coldharbor.oblivion";

    private static final String USERNAME_WRITE = "hermaeus";
    private static final String NAME_WRITE = "Hermaeus Mora";
    private static final String EMAIL_WRITE = "mora";
    
    private static final String SPREADSHEET_NAME = "Argonian Account Book";
    private static final Integer SPREADSHEET_ROWS = 10;
    private static final Integer SPREADSHEET_COLUMNS = 15;
    private static final Integer REFERENCE_ROW = 1;
    private static final Integer REFERENCE_COLUMN = 1;
    private static final Integer LITERAL_ROW = 1;
    private static final Integer LITERAL_COLUMN = 1;

    private static final Integer LITERAL_VALUE = 3;

    private static final Integer DOCID_INVALID = -5;
      
    //This is needed throughout the tests
    private Integer spreadsheetID;
    private String tokenAuthor;
    private String tokenRo;
	private String tokenWrite;
	
	private User author;
	private Spreadsheet ss; 
	
	@Mocked
	StoreRemoteServices sdStore;
	    
    @Override
    public void initializeDomain() {
    	Bubbledocs bubble = Bubbledocs.getBubbledocs();
    	try {
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

           tokenAuthor = addUserToSession(AUTHOR_USERNAME);
       	   tokenRo = addUserToSession(USERNAME_RO);
       	   tokenWrite = addUserToSession(USERNAME_WRITE);
       	   
        } catch (BubbledocsException e) {
     	   assertTrue(false);
        }		
    }
    
    @Test
    public void successCorrectExport() throws BubbledocsException, UnsupportedEncodingException {
      	ExportDocumentIntegrator service = new ExportDocumentIntegrator(tokenRo, spreadsheetID);
      	
      	new Expectations() {
      		{
      			sdStore.storeDocument(USERNAME_RO, Integer.toString(spreadsheetID), (byte[]) any);
      		}
      	};
      	
        service.execute();
        
        org.jdom2.Document exported = null;
        try {
			exported = new org.jdom2.input.SAXBuilder().build(new java.io.StringReader(service.getDocXML()));
		} catch (IOException | JDOMException e) {
			assertTrue(false);
		} 
        
        assertTrue("Session was not updated", hasSessionUpdated(tokenRo));
        assertNotNull(exported);

        Spreadsheet imported = null;
        try {
			imported = Bubbledocs.getBubbledocs().createSpreadsheet(author, service.getDocXML());
		} catch (IOException | JDOMException e) {
			assertTrue("Failed to import exported spreadsheet", false);
		}     
        assertNotNull(imported);
        assertEquals(ss.getAuthor(), imported.getAuthor());
        assertEquals(ss.getRows(), imported.getRows());
        assertEquals(ss.getColumns(), imported.getColumns());
        assertEquals(ss.getName(), imported.getName());
        assertTrue(!ss.getId().equals(imported.getId()));
        
        assertTrue(ss.getCell(LITERAL_ROW, LITERAL_COLUMN).equals(imported.getCell(LITERAL_ROW, LITERAL_COLUMN)));
        assertTrue(ss.getCell(REFERENCE_ROW, REFERENCE_COLUMN).equals(imported.getCell(REFERENCE_ROW, REFERENCE_COLUMN)));        
    }
    
    //Test case 1
    @Test
    public void successUserReadPermission() throws BubbledocsException, UnsupportedEncodingException {
      	ExportDocumentIntegrator service = new ExportDocumentIntegrator(tokenRo, spreadsheetID);
      	
      	new Expectations() {
      		{
      			sdStore.storeDocument(USERNAME_RO, Integer.toString(spreadsheetID), (byte[]) any);
      		}
      	};
      	
        service.execute();
        assertTrue("Returning empty XML string!", !service.getDocXML().isEmpty());
        assertTrue("Session was not updated", hasSessionUpdated(tokenRo));
    }

    //Test case 2
    @Test
    public void successUserWritePermission() throws BubbledocsException, UnsupportedEncodingException {
      	ExportDocumentIntegrator service = new ExportDocumentIntegrator(tokenWrite, spreadsheetID);
      	
      	new Expectations() {
      		{
      			sdStore.storeDocument(USERNAME_WRITE, Integer.toString(spreadsheetID), (byte[]) any);
      		}
      	};
      	
        service.execute();
        assertTrue("Returning empty XML string!", !service.getDocXML().isEmpty());
    }
    
    @Test
    public void successAuthor() throws BubbledocsException, UnsupportedEncodingException {
      	ExportDocumentIntegrator service = new ExportDocumentIntegrator(tokenAuthor, spreadsheetID);
      	
      	new Expectations() {
      		{
      			sdStore.storeDocument(AUTHOR_USERNAME, Integer.toString(spreadsheetID), (byte[]) any);
      		}
      	};
      	
        service.execute();
        assertTrue("Returning empty XML string!", !service.getDocXML().isEmpty());
    }

    //Test case 3
    @Test(expected = SpreadsheetNotFoundException.class)
    public void invalidDocumentExport() throws BubbledocsException {
    	new ExportDocumentIntegrator(tokenAuthor, DOCID_INVALID).execute();
    }
    
	@Test
	public void failToExportSessionTime() throws BubbledocsException {
		try{
    		new ExportDocumentIntegrator(tokenAuthor, DOCID_INVALID).execute();
    	}catch(BubbledocsException e){
            assertTrue("Session was not updated", hasSessionUpdated(tokenAuthor));
            return;
    	}
		assertTrue(false);
	}

    //Test case 4
    @Test(expected = PermissionNotFoundException.class)
    public void noPermissionsUserExport() throws BubbledocsException {
	    Bubbledocs bubble = Bubbledocs.getBubbledocs();
    	//Temporarily revoke RO user permissions
    	bubble.revokeReadPermission(AUTHOR_USERNAME, USERNAME_RO, spreadsheetID);
       	    	
	    ExportDocumentIntegrator service = new ExportDocumentIntegrator(tokenRo, spreadsheetID);
      	service.execute();
    }    
    
    @Test(expected = UserNotInSessionException.class)
    public void noSessionUserExport() throws BubbledocsException {
    	removeUserFromSession(tokenAuthor);
    	new ExportDocumentIntegrator(tokenAuthor, spreadsheetID).execute();
    }    
    
    @Test(expected = UnavailableServiceException.class)
    public void failRemote() throws UnsupportedEncodingException, BubbledocsException {
    	new Expectations() {
      		{
      			sdStore.storeDocument(USERNAME_RO, Integer.toString(spreadsheetID), (byte[]) any);
      			result = new RemoteInvocationException("");
      		}
      	};

	    new ExportDocumentIntegrator(tokenRo, spreadsheetID).execute();
    }
    
    @Test(expected = CannotStoreDocumentException.class) 
    public void failStoreDoc() throws BubbledocsException {
    	new Expectations() {
    		{
    			sdStore.storeDocument(USERNAME_RO, Integer.toString(spreadsheetID), (byte[]) any);
    			result = new CannotStoreDocumentException("");
    		}
    	};
    	
    	new ExportDocumentIntegrator(tokenRo, spreadsheetID).execute();
    }
    
}
