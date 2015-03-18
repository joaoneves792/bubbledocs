package pt.ulisboa.tecnico.bubbledocs.service.test;

import org.junit.Test;

import pt.ulisboa.tecnico.bubbledocs.domain.Bubbledocs;
import pt.ulisboa.tecnico.bubbledocs.domain.Literal;
import pt.ulisboa.tecnico.bubbledocs.domain.Reference;
import pt.ulisboa.tecnico.bubbledocs.domain.Spreadsheet;
import pt.ulisboa.tecnico.bubbledocs.domain.User;
import pt.ulisboa.tecnico.bubbledocs.exceptions.BubbledocsException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidCellException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.ProtectedCellException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.SpreadsheetNotFoundException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UnauthorizedUserException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UserNotInSessionException;
import pt.ulisboa.tecnico.bubbledocs.service.AssignReferenceCell;

public class AssignReferenceCellTest extends BubbledocsServiceTest{
	
	private static final String USERNAME = "ars";
	private static final String PASSWORD = "ars";
	
	private static final int DOCID_INVALID = 0;
	
	private static final String USERNAME_NOPERMISSION = "ra";
	private static final String PASSWORD_NOPERMISSION = "cor";
	
	String token;
	String token_nopermission;
	Spreadsheet ss;
	User ars;
	
    @Override
    public void populate4Test() {

    	ars = new User("Paul Door", USERNAME, PASSWORD);
    	Bubbledocs bubble = Bubbledocs.getBubbledocs();   	
    	bubble.addUser(ars);
    	
    	User ra = new User("Step Rabbit", "ra", "cor");
    	bubble.addUser(ra);
    	
    	try {
    		ss = ars.createSpreadsheet("Testa Export", 10, 15);

    		ss.getCell(3, 4).setContent(new Literal(5));
    		ss.getCell(1, 1).setContent(new Reference(5, 6));
    		
    		ss.getCell(6, 7).setContent(new Literal(12));
    		ss.getCell(6, 7).set_protected(true);
    		
    		token = addUserToSession(USERNAME, PASSWORD);
    		token_nopermission = addUserToSession(USERNAME_NOPERMISSION, PASSWORD_NOPERMISSION);
    		
    		} catch (BubbledocsException e) {
    			// TODO Auto-generated catch block
    		e.printStackTrace();
    		}
    	}
    
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testCase1() throws BubbledocsException {
    	
    	populate4Test();
    	AssignReferenceCell service = new AssignReferenceCell(token, ss.get_id(), "9999;" , "2;5");
        service.execute();
    }
    
    @Test(expected = InvalidCellException.class)
    public void testCase2() throws BubbledocsException {
    	
    	populate4Test();
    	AssignReferenceCell service = new AssignReferenceCell(token, ss.get_id(), "5;400" , "2;5");
        service.execute();
    }
    
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testCase3() throws BubbledocsException {
    	
    	populate4Test();
    	AssignReferenceCell service = new AssignReferenceCell(token, ss.get_id(), "12;14" , "$%&*@");
        service.execute();
    }
    
    @Test(expected = InvalidCellException.class)
    public void testCase4() throws BubbledocsException {
    	
    	populate4Test();
    	AssignReferenceCell service = new AssignReferenceCell(token, ss.get_id(), "5;8" , "0;-1");
        service.execute();
    }
    
    //Spreadsheet Invalida com ID = 0
    @Test(expected = SpreadsheetNotFoundException.class)
    public void testCase5() throws BubbledocsException {
    	
    	populate4Test();
    	AssignReferenceCell service = new AssignReferenceCell(token, DOCID_INVALID, "5;8" , "0;-1");
        service.execute();
    }
    
    //Cell (6;7) esta protegida
    @Test(expected = ProtectedCellException.class)
    public void testCase6() throws BubbledocsException {
    	
    	populate4Test();
    	AssignReferenceCell service = new AssignReferenceCell(token, ss.get_id(), "6;7" , "2;5");
        service.execute();
    }
    
  //Remove user da sessao e tenta de seguida fazer AssignReferenceCell
    @Test(expected = UserNotInSessionException.class)
    public void testCase7() throws BubbledocsException {
    	
    	populate4Test();
    	
    	removeUserFromSession(token);
    	
    	AssignReferenceCell service = new AssignReferenceCell(token, ss.get_id(), "6;7" , "2;5");
        service.execute();
    }
    
    //protected cell
    @Test(expected = UnauthorizedUserException.class)
    public void testCase8() throws BubbledocsException {
    	
    	populate4Test();
        
        AssignReferenceCell service = new AssignReferenceCell(token_nopermission, ss.get_id(), "4;4" , "3;4");
        service.execute();
    }
    
  //caso de sucesso
    @Test
    public void testCase9() throws BubbledocsException {
    	//TODO caso sem resultado na wiki ... para remover?
    }
    
  //caso de sucesso
    @Test
    public void testCase10() throws BubbledocsException {
    	
    	populate4Test();
        
        AssignReferenceCell service = new AssignReferenceCell(token, ss.get_id(), "15;4" , "3;4");
        service.execute();
    }
    
}
