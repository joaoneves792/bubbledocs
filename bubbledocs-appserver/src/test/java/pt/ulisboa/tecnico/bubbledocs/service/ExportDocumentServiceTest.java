package pt.ulisboa.tecnico.bubbledocs.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.jdom2.JDOMException;
import org.junit.Test;

import pt.ulisboa.tecnico.bubbledocs.domain.Bubbledocs;
import pt.ulisboa.tecnico.bubbledocs.domain.Literal;
import pt.ulisboa.tecnico.bubbledocs.domain.Reference;
import pt.ulisboa.tecnico.bubbledocs.domain.Spreadsheet;
import pt.ulisboa.tecnico.bubbledocs.domain.User;
import pt.ulisboa.tecnico.bubbledocs.exceptions.BubbledocsException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.PermissionNotFoundException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.SpreadsheetNotFoundException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UserNotInSessionException;
import pt.ulisboa.tecnico.bubbledocs.service.ExportDocument;


public class ExportDocumentServiceTest extends BubbledocsServiceTest {

    private static final String AUTHOR_USERNAME = "md";
    private static final String AUTHOR_NAME = "Mehrunes Dagon";
    private static final String AUTHOR_PASSWORD = "dagon";
    
    private static final String USERNAME_RO = "mb";
    private static final String NAME_RO = "Molag Bal";
    private static final String PASSWORD_RO = "bal";

    private static final String USERNAME_WRITE = "mb";
    private static final String NAME_WRITE = "Molag Bal";
    private static final String PASSWORD_WRITE = "bal";
    
    private static final String SPREADHEET_NAME = "Argonian Account Book";
    private static final Integer SPREADHEET_ROWS = 10;
    private static final Integer SPREADHEET_COLUMNS = 15;
    private static final Integer REFERENCE_ROW = 1;
    private static final Integer REFERENCE_COLUMN = 1;
    private static final Integer LITERAL_ROW = 1;
    private static final Integer LITERAL_COLUMN = 1;

    private static final Integer LITERAL_VALUE = 3;

    private static final Integer DOCID_INVALID = -5;
      
    //This is needed throughout the tests
    private Integer spreadsheetID;
    private String token_author;
    private String token_ro;
	private String token_write;
	
	private org.jdom2.Document doc = new org.jdom2.Document();
	
    
    @Override
    public void initializeDomain() {
    	Bubbledocs bubble = Bubbledocs.getBubbledocs();
  	    User author = createUser(AUTHOR_USERNAME, AUTHOR_PASSWORD, AUTHOR_NAME);
        createUser(USERNAME_RO, PASSWORD_RO, NAME_RO);
        createUser(USERNAME_WRITE, PASSWORD_WRITE, NAME_WRITE);
        
        try{
    	   Spreadsheet ss = createSpreadSheet(author, SPREADHEET_NAME, SPREADHEET_ROWS, SPREADHEET_COLUMNS);
     	   spreadsheetID = ss.getId();

     	   //Assign a literal to cell
     	   ss.getCell(LITERAL_ROW ,LITERAL_COLUMN).setContent(new Literal(LITERAL_VALUE));
     	   
     	   //Assign a reference to cell
     	   ss.getCell(REFERENCE_ROW, REFERENCE_COLUMN).setContent(new Reference(ss.getCell(LITERAL_ROW, LITERAL_COLUMN)));     	        	   
     	   
     	   //Give RO user read permissions
     	   bubble.addReadPermission(AUTHOR_USERNAME, USERNAME_RO, spreadsheetID);
     	   //Give WRITE user write permissions
     	   bubble.addWritePermission(AUTHOR_USERNAME, USERNAME_WRITE, spreadsheetID);     	   

           token_author = addUserToSession(AUTHOR_USERNAME, AUTHOR_PASSWORD);
       	   token_ro = addUserToSession(USERNAME_RO, PASSWORD_RO);
       	   token_write = addUserToSession(USERNAME_WRITE, PASSWORD_WRITE);
       	   
       	   org.jdom2.Element spreadsheetElement = new org.jdom2.Element("Spreadsheet");
	       spreadsheetElement.setAttribute("rows", ss.getRows().toString());
	       spreadsheetElement.setAttribute("columns", ss.getColumns().toString());
	       spreadsheetElement.setAttribute("author", ss.getAuthor());
	       spreadsheetElement.setAttribute("name", ss.getName());
	       spreadsheetElement.setAttribute("date", ss.getDate().toString());
	       doc.setRootElement(spreadsheetElement);
	       org.jdom2.Element cells = new org.jdom2.Element("Cells");
	       spreadsheetElement.addContent(cells);
	       
	       org.jdom2.Element litCell = new org.jdom2.Element("Cell");
	       org.jdom2.Element lit     = new org.jdom2.Element("Literal");
	       org.jdom2.Element refCell = new org.jdom2.Element("Cell");
	       org.jdom2.Element ref     = new org.jdom2.Element("Reference");
	       
	       cells.addContent(litCell);
	       cells.addContent(refCell);
	       litCell.addContent(lit);
	       refCell.addContent(ref);
	       
	       litCell.setAttribute("protected", ss.getCell(LITERAL_ROW, LITERAL_COLUMN).getProtectd().toString());
	       litCell.setAttribute("row", LITERAL_ROW.toString());
	       litCell.setAttribute("column", LITERAL_COLUMN.toString());
	       refCell.setAttribute("protected", ss.getCell(REFERENCE_ROW, REFERENCE_COLUMN).getProtectd().toString());
	       refCell.setAttribute("row", REFERENCE_ROW.toString());
	       refCell.setAttribute("column", REFERENCE_COLUMN.toString());
	       
	       lit.setAttribute("value", LITERAL_VALUE.toString());	
	       ref.setAttribute("row", LITERAL_ROW.toString());
	       ref.setAttribute("column", LITERAL_COLUMN.toString());
	       
        } catch (BubbledocsException e) {
     	   assertTrue(false);
        }		
    }
    
    @Test
    public void successCorrectExport() throws BubbledocsException {
      	ExportDocument service = new ExportDocument(token_ro, spreadsheetID);
        service.execute();
        org.jdom2.Document exported = null;
        try {
			exported = new org.jdom2.input.SAXBuilder().build(new java.io.StringReader(service.getDocXML()));
		} catch (IOException | JDOMException e) {
			assertTrue(false);
		} 
        assertNotNull(exported);
        assertEquals(exported, doc);
        /* alternative - I have no idea if any of them work
         * assertTrue(doc.equals(exported));
         */
    }
    
    //Test case 1
    @Test
    public void successUserReadPermission() throws BubbledocsException {
      	ExportDocument service = new ExportDocument(token_ro, spreadsheetID);
        service.execute();
        assertTrue("Returning empty XML string!", !service.getDocXML().isEmpty());
        assertTrue("Session was not updated", hasSessionUpdated(token_ro));
    }

    //Test case 2
    @Test
    public void successUserWritePermission() throws BubbledocsException {
      	ExportDocument service = new ExportDocument(token_write, spreadsheetID);
        service.execute();
        assertTrue("Returning empty XML string!", !service.getDocXML().isEmpty());
    }
    
    @Test
    public void successAuthor() throws BubbledocsException {
      	ExportDocument service = new ExportDocument(token_author, spreadsheetID);
        service.execute();
        assertTrue("Returning empty XML string!", !service.getDocXML().isEmpty());
    }

    //Test case 3
    @Test(expected = SpreadsheetNotFoundException.class)
    public void invalidDocumentExport() throws BubbledocsException {
    	new ExportDocument(token_author, DOCID_INVALID).execute();
    }
    
	@Test
	public void failToExportSessionTime() throws BubbledocsException {
		try{
    		new ExportDocument(token_author, DOCID_INVALID).execute();
    	}catch(BubbledocsException e){
            assertTrue("Session was not updated", hasSessionUpdated(token_author));
            return;
    	}
		assertTrue(false);
	}

    //Test case 4
    @Test(expected = PermissionNotFoundException.class)
    public void noPermissionsUserExport() throws BubbledocsException {
	    Bubbledocs bubble = Bubbledocs.getBubbledocs();
    	//Temporarily revoke RO user permissions
    	bubble.revokeReadPermission(AUTHOR_USERNAME, USERNAME_WRITE, spreadsheetID);
       	    	
	    ExportDocument service = new ExportDocument(token_ro, spreadsheetID);
      	service.execute();
    }    
    
    @Test(expected = UserNotInSessionException.class)
    public void noSessionUserExport() throws BubbledocsException {
    	removeUserFromSession(token_author);
    	new ExportDocument(token_author, spreadsheetID).execute();
    }    
    
    
}
