package pt.ulisboa.tecnico.bubbledocs.service.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import pt.ulisboa.tecnico.bubbledocs.domain.Bubbledocs;
import pt.ulisboa.tecnico.bubbledocs.domain.Cell;
import pt.ulisboa.tecnico.bubbledocs.domain.Literal;
import pt.ulisboa.tecnico.bubbledocs.domain.Reference;
import pt.ulisboa.tecnico.bubbledocs.domain.Root;
import pt.ulisboa.tecnico.bubbledocs.domain.Spreadsheet;
import pt.ulisboa.tecnico.bubbledocs.domain.User;
import pt.ulisboa.tecnico.bubbledocs.exceptions.BubbledocsException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UnknownBubbledocsUserException;
import pt.ulisboa.tecnico.bubbledocs.service.AssignReferenceCell;
import pt.ulisboa.tecnico.bubbledocs.service.LoginUser;

public class AssignReferenceCellTest extends BubbledocsServiceTest{
	
	//private static final String USERNAME = "ars";

    @Override
    public void populate4Test() {

    	User pf = createUser( "pf", "sub", "Paul Door");
    	
    	Spreadsheet ss = pf.createSpreadsheet("Notas ES", 300, 20);
    	
    	//ss.getCell(3, 4).setContent(new Literal(5));
    	//ss.getCell(5, 6).setContent(new Literal(2));
		//ss.getCell(1, 1).setContent(new Reference(5, 6));
		//ss.getCell(6, 2).setContent(new Reference(1, 1));
		
    }
    
    @Test(expected = UnknownBubbledocsUserException.class)
    public void testCase1() throws BubbledocsException {
    	
    	populate4Test();
    	
    	//AssignReferenceCell service = new AssignReferenceCell(String tokenUser, int spreadsheetId, String cellId, String cellReference);
        //service.execute();
        
    	
    }
    
    
}
