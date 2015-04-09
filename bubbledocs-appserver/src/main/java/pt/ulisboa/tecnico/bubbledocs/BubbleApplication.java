package pt.ulisboa.tecnico.bubbledocs;

import pt.ulisboa.tecnico.bubbledocs.domain.Bubbledocs;
import pt.ulisboa.tecnico.bubbledocs.domain.Permission;
import pt.ulisboa.tecnico.bubbledocs.domain.Session;
import pt.ulisboa.tecnico.bubbledocs.domain.User;
import pt.ulisboa.tecnico.bubbledocs.domain.Spreadsheet;
import pt.ulisboa.tecnico.bubbledocs.domain.Literal;
import pt.ulisboa.tecnico.bubbledocs.domain.Reference;
import pt.ulisboa.tecnico.bubbledocs.domain.Add;
import pt.ulisboa.tecnico.bubbledocs.domain.Div;
import pt.ulisboa.tecnico.bubbledocs.exceptions.*;
import pt.ulisboa.tecnico.bubbledocs.service.AssignLiteralCell;
import pt.ulisboa.tecnico.bubbledocs.service.AssignReferenceCell;
import pt.ulisboa.tecnico.bubbledocs.service.CreateSpreadSheet;
import pt.ulisboa.tecnico.bubbledocs.service.CreateUser;
import pt.ulisboa.tecnico.bubbledocs.service.ExportDocument;
import pt.ulisboa.tecnico.bubbledocs.service.LoginUser;
import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.FenixFramework;
import pt.ist.fenixframework.TransactionManager;

import javax.transaction.*;

import org.jdom2.JDOMException;

import java.io.IOException;
import java.util.List;


@SuppressWarnings("unused")
public class BubbleApplication{
	
	private static String pfToken;
	private static String rootToken;
	private static int spreadsheetID;

	public static void main(String[] args) {
        System.out.println("Welcome to the BubbleDocs application!");
        
        TransactionManager tm = FenixFramework.getTransactionManager();
        boolean committed = false;
        
        try{ 
        	//Beware: don't ever place exception throwing instructions between a commit and the next begin otherwise rollback will crash!
        	//		what you can do is place committed=true; committed=false; around your non transactional code to avoid the rollback
        	
        	//FIXME Do we really need a transaction for everything or can we group some of these things
        	tm.begin();
        	Bubbledocs bubble = Bubbledocs.getBubbledocs();
        	populateDomain(bubble);
        	tm.commit();
        	
        	tm.begin();        	
        	User pf = bubble.getUserByUsername("pf"); //still needed for import
        	String ss = null;
            printRegisteredUsers();
        	tm.commit();

        	tm.begin();        	
        	printSpreadsheetsByUsername("pf");
        	tm.commit();
        	
        	tm.begin();
        	printSpreadsheetsByUsername("ra");        	
        	tm.commit();
        	
        	tm.begin();
        	ExportDocument exportSpreadsheet = new ExportDocument(pfToken, spreadsheetID);
    		exportSpreadsheet.execute();
    		ss = exportSpreadsheet.getDocXML();
    		System.out.println(ss);
        	tm.commit();
        	
        	tm.begin();
        	deleteSpreadsheetByNameAndUsername("Notas ES", pf);   
        	tm.commit();
        	
        	tm.begin();
        	System.out.println("====================== AFTER DELETING NOTAS ES ============================");
        	printSpreadsheetsByUsername("pf");
        	tm.commit();
        	
        	tm.begin();
        	System.out.println("======================== IMPORTING NOTAS ES ===============================");
        	importSpreadsheetForUser(pf, ss); 
        	tm.commit();
        	
        	tm.begin();
        	System.out.println("====================== AFTER IMPORTING NOTAS ES ===========================");
        	printSpreadsheetsByUsername("pf");
        	tm.commit();
        	
        	tm.begin();
        	ExportDocument exportSpreadsheet2 = new ExportDocument(pfToken, spreadsheetID+1); //yuck
    		exportSpreadsheet2.execute();
    		ss = exportSpreadsheet2.getDocXML();
    		System.out.println(ss);
        	tm.commit();
        	committed = true;
        
        } catch (SystemException| NotSupportedException | RollbackException| HeuristicMixedException | HeuristicRollbackException ex) {
            System.err.println("Error in execution of transaction: " + ex);
        } catch (InvalidExportException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		} catch (InvalidImportException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		} catch (JDOMException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		} catch (UnauthorizedUserException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		} catch (SpreadsheetNotFoundException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		} catch (UserNotFoundException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println(e.getMessage());
			e.printStackTrace(); 
		} catch (InvalidCellException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		} catch (InvalidLoginException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		} catch (CreateUserException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		} catch (CreateSpreadsheetException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		} catch (SpreadsheetWriteException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		} catch (BubbledocsException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}  finally {
             if (!committed)
             try {
                 tm.rollback();
             } catch (SystemException ex) {
                 System.err.println("Error in roll back of transaction: " + ex);
             }
		}
        	
        //FenixFramework.shutdown();
    }


    //@Atomic
	public static void importSpreadsheetForUser(User pf, String ss)
			throws InvalidImportException, JDOMException, IOException, InvalidCellException, UserNotFoundException {
		System.out.println("Importing back 'Notas ES'");
		pf.createSpreadsheet(ss);
	}

	//@Atomic
	public static void deleteSpreadsheetByNameAndUsername(String spreadsheetName, User pf)
			throws UnauthorizedUserException, SpreadsheetNotFoundException {
		List<Spreadsheet> pfSpreadsheets = pf.findSpreadsheetsByName(spreadsheetName);
		for(Spreadsheet sheet : pfSpreadsheets) {
			pf.deleteSpreadsheet(sheet.getId());
		}
	}

	/* Deprecated as of R_2
	//@Atomic
	public static String printSpreadsheetsAsXMLByUsername(String username)
			throws InvalidExportException {
		String ss = null;
		System.out.println("XML for Spreadsheets from user '" + username + "' :"); 
		for(Spreadsheet sheet : Bubbledocs.getBubbledocs().getSpreadsheetSet()) {
			if(sheet.getAuthor().equals(username)) {
				ss = sheet.export();
				System.out.println(ss);
			}
		}
		return ss;
	}
	*/

	//@Atomic
	public static void printSpreadsheetsByUsername(String username) {
		System.out.println("Spreadsheets from user '" + username + "' :"); 
		for(Spreadsheet sheet : Bubbledocs.getBubbledocs().getSpreadsheetSet()) {
			if(sheet.getAuthor().equals(username)) {
				System.out.println(sheet.toString());
			}
		}
	}

	//@Atomic
	public static void printRegisteredUsers() {
		System.out.println("Registered users are:");
		for(User user : Bubbledocs.getBubbledocs().getUserSet()) {
			System.out.println(user.toString());
		}
	}

   //@Atomic
    private static void populateDomain(Bubbledocs bubble) throws InvalidLoginException, CreateUserException, CreateSpreadsheetException, SpreadsheetWriteException, SpreadsheetNotFoundException {
    	if(!bubble.getUserSet().isEmpty()) {
    		for(Session s : bubble.getSessionSet()) {
				if(s.hasExpired()) s.update();
    			if(s.getUser().getUsername().equals("root")) {
    				rootToken = "root" + s.getTokenInt();
    			} else if(s.getUser().getUsername().equals("pf")) {
    				pfToken = "pf" + s.getTokenInt();
    			}
    		}
    		for(Permission p : bubble.getPermissionSet()) {
    			if(p.getUser().getUsername().equals("pf")) {
    				spreadsheetID = p.getSpreadsheet().getId();
    			}
    		}
    		return;
    	}
    	
    	bubble.getSuperUser();

    	LoginUser loginRoot = new LoginUser("root", "root");
    	try {
			loginRoot.execute();
		} catch (BubbledocsException e1) {
			throw new InvalidLoginException("Failed to login [username = root] on domain population");
		}
    	
    	rootToken = loginRoot.getUserToken();
    	
    	CreateUser pfCreation = null;
    	CreateUser raCreation = null;
		try {
			pfCreation = new CreateUser(rootToken, "pf", "sub", "Paul Door");
	    	raCreation = new CreateUser(rootToken, "ra", "cor", "Step Rabbit");
	    	pfCreation.execute();    
			raCreation.execute();
		} catch (BubbledocsException e) {
			throw new CreateUserException("Failed to create users on domain population");
		} 
    	
		LoginUser loginPf = new LoginUser("pf", "sub");
		try {
			loginPf.execute();
		} catch (BubbledocsException e2) {
			throw new InvalidLoginException("Failed to login [username = pf] on domain population");
		}
		pfToken = loginPf.getUserToken();
		
    	CreateSpreadSheet createNotasES = new CreateSpreadSheet(pfToken, "Notas ES", 300, 20);
    	try {
			createNotasES.execute();
		} catch (BubbledocsException e1) {
			throw new CreateSpreadsheetException("Failed to create Spreadsheet on domain population");
		}
    	
    	spreadsheetID = createNotasES.getSheetId();
    	
    	AssignLiteralCell lit = new AssignLiteralCell(pfToken, spreadsheetID, "3;4", "5");
    	AssignReferenceCell ref = new AssignReferenceCell(pfToken, spreadsheetID, "1;1", "5;6");
    	try {
			lit.execute();
			ref.execute();
		} catch (BubbledocsException e1) {
			throw new SpreadsheetWriteException("Failed to write on spreadsheet on domain population.");
		}
    	
    	
    	/* Assign Add and assign Div services do not exist yet
    	 * The literals and references here do NOT occupy a cell, so they are not assigned
    	 */
    	Spreadsheet ss = bubble.getSpreadsheetById(spreadsheetID);
    	
    	try {
			ss.getCell(5, 6).setContent(new Add(new Literal(2), new Reference(ss.getCell(3, 4))));
			ss.getCell(2, 2).setContent(new Div(new Reference(ss.getCell(1, 1)), new Reference(ss.getCell(3, 4))));
		} catch (InvalidCellException e) {
			e.printStackTrace();
		}
     	
    	return;
	}
    
}	
