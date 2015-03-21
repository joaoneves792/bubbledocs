package pt.ulisboa.tecnico.bubbledocs;

import pt.ulisboa.tecnico.bubbledocs.domain.Bubbledocs;
import pt.ulisboa.tecnico.bubbledocs.domain.User;
import pt.ulisboa.tecnico.bubbledocs.domain.Spreadsheet;
import pt.ulisboa.tecnico.bubbledocs.domain.Literal;
import pt.ulisboa.tecnico.bubbledocs.domain.Reference;
import pt.ulisboa.tecnico.bubbledocs.domain.Add;
import pt.ulisboa.tecnico.bubbledocs.domain.Div;
import pt.ulisboa.tecnico.bubbledocs.exceptions.*;
import pt.ist.fenixframework.FenixFramework;
import pt.ist.fenixframework.TransactionManager;

import javax.transaction.*;

import org.jdom2.JDOMException;

import java.io.IOException;
import java.util.List;

public class BubbleApplication{
    public static void main(String[] args) {
        System.out.println("Welcome to the BubbleDocs application!");
        
        TransactionManager tm = FenixFramework.getTransactionManager();
        boolean committed = false;

        try{
        	tm.begin();
        	
        	Bubbledocs bubble = Bubbledocs.getBubbledocs();
        	populateDomain(bubble);
        	User pf = bubble.getUserByUsername("pf");
        	String ss = null;
            
        	printRegisteredUsers();

        	printSpreadsheetsByUsername("pf");
        	printSpreadsheetsByUsername("ra");        	
        	
        	ss = printSpreadsheetsAsXMLByUsername("pf");
        	
        	deleteSpreadsheetByNameAndUsername("Notas ES", pf);     
        	System.out.println("====================== AFTER DELETING NOTAS ES ===========================");
        	
        	printSpreadsheetsByUsername("pf");
        	
        	System.out.println("====================== IMPORTING NOTAS ES ===========================");
        	importSpreadsheetForUser(pf, ss);    
        	System.out.println("====================== AFTER IMPORTING NOTAS ES ===========================");
        	        	
        	printSpreadsheetsByUsername("pf");
        	
        	printSpreadsheetsAsXMLByUsername("pf");
        	
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
		} catch (CreateRootException e) {
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
    private static void populateDomain(Bubbledocs bubble) throws CreateRootException {
    	if(!bubble.getUserSet().isEmpty()) 
    		return;
    	
    	bubble.getSuperUser();
    	
    	User pf = new User("Paul Door", "pf", "sub");
    	User ra = new User("Step Rabbit", "ra", "cor");
    	
		bubble.addUser(pf);
		bubble.addUser(ra);
		
    	Spreadsheet ss = pf.createSpreadsheet("Notas ES", 300, 20);
    	
    	try {
			ss.getCell(3, 4).setContent(new Literal(5));
			ss.getCell(1, 1).setContent(new Reference(ss.getCell(5, 6)));
			ss.getCell(5, 6).setContent(new Add(new Literal(2), new Reference(ss.getCell(3, 4))));
			ss.getCell(2, 2).setContent(new Div(new Reference(ss.getCell(1, 1)), new Reference(ss.getCell(3, 4))));
		} catch (InvalidCellException e) {
			e.printStackTrace();
		}
    	
     	
    	return;
	}
    
}	
