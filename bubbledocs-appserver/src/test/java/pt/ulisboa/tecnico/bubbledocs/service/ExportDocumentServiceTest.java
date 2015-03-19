package pt.ulisboa.tecnico.bubbledocs.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import pt.ulisboa.tecnico.bubbledocs.domain.User;
import pt.ulisboa.tecnico.bubbledocs.exceptions.BubbledocsException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.SpreadsheetNotFoundException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UnauthorizedOperationException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UserNotFoundException;
import pt.ulisboa.tecnico.bubbledocs.service.ExportDocument;
import pt.ulisboa.tecnico.bubbledocs.domain.*;


// add needed import declarations

public class ExportDocumentServiceTest extends BubbledocsServiceTest {

    // the tokens

    private static final String USERNAME_VALID = "ars";
    private static final String USERNAME_INVALID ="sm";
    private static final String USERNAME_NOT_KNOWN ="acx";
    
    private static final String PASSWORD = "ars";
    private static final String SPREADSHEET = null;
    private String doc;
    private static final int DOCID_INVALID = 0;
    private int DOCID_VALID;
    
    @Override
    public void populate4Test() {
    	
    	User ars = new User("Paul Door", USERNAME_VALID, PASSWORD);
    	User sm = new User("Sergio Moura", USERNAME_INVALID, PASSWORD);
    	
    	Bubbledocs bubble = Bubbledocs.getBubbledocs();   	
		bubble.addUser(ars);
		bubble.addUser(sm);
		
		try {
			Spreadsheet ss = ars.createSpreadsheet("Testa Export", 10, 15);
	    	
	    	ss.getCell(3, 4).setContent(new Literal(5));
			ss.getCell(1, 1).setContent(new Reference(5, 6));
			ss.getCell(5, 6).setContent(new Add(new Literal(2), new Reference(3, 4)));
			ss.getCell(2, 2).setContent(new Div(new Reference(1, 1), new Reference(3, 4)));
			DOCID_VALID=ss.get_id();
			doc = ss.export();
		
        } catch (BubbledocsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }

    @Test
    public void success() throws BubbledocsException {
        
    	ExportDocument service = new ExportDocument(USERNAME_VALID, DOCID_VALID);
        service.execute();
        assertEquals(service.getDocXML(), doc);
    }

    @Test(expected = SpreadsheetNotFoundException.class)
    public void invalidDocumentExport() throws BubbledocsException {
    	//TODO: use invalid DOCID
    	ExportDocument service = new ExportDocument(USERNAME_VALID, DOCID_INVALID);
        service.execute();
    }

    @Test(expected = UserNotFoundException.class)
    public void emptyUsername() throws BubbledocsException {
    	ExportDocument service = new ExportDocument(USERNAME_NOT_KNOWN, DOCID_VALID);
        service.execute();
    }

    
    @Test(expected = UnauthorizedOperationException.class)
    public void accessUsernameNotExist() throws BubbledocsException {
    	ExportDocument service = new ExportDocument(USERNAME_INVALID, DOCID_VALID);
        service.execute();
    }

}
