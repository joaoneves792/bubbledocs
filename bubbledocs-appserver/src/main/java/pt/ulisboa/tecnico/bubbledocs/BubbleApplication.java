
package pt.ulisboa.tecnico.bubbledocs;

import pt.ulisboa.tecnico.bubbledocs.domain.Bubbledocs;
import pt.ulisboa.tecnico.bubbledocs.domain.User;
import pt.ulisboa.tecnico.bubbledocs.domain.Root;
import pt.ulisboa.tecnico.bubbledocs.domain.Spreadsheet;
import pt.ulisboa.tecnico.bubbledocs.domain.Cell;
import pt.ulisboa.tecnico.bubbledocs.domain.Literal;
import pt.ulisboa.tecnico.bubbledocs.domain.Reference;
import pt.ulisboa.tecnico.bubbledocs.domain.Add;
import pt.ulisboa.tecnico.bubbledocs.domain.Sub;
import pt.ulisboa.tecnico.bubbledocs.domain.Mul;
import pt.ulisboa.tecnico.bubbledocs.domain.Div;
import pt.ulisboa.tecnico.bubbledocs.domain.SimpleContent;
import pt.ulisboa.tecnico.bubbledocs.exceptions.*;
import pt.ist.fenixframework.FenixFramework;
import pt.ist.fenixframework.TransactionManager;

import javax.transaction.*;

import java.io.IOException;
import java.util.Set;
import java.util.List;

public class BubbleApplication{
    public static void main(String[] args) throws IOException, UserNotFoundException {
        System.out.println("Welcome to the BubbleDocs application!");
        
        TransactionManager tm = FenixFramework.getTransactionManager();
        boolean committed = false;

        try{
        	tm.begin();
        	Bubbledocs bubble = Bubbledocs.getBubbledocs();
        	populateDomain(bubble);
            
        	System.out.println("Registered users are:");
        	for(User user : bubble.getUserSet()) {
        		System.out.println(user.toString());
        	}

        	System.out.println("Spreadsheets from user 'pf':"); 
        	for(Spreadsheet sheet : bubble.getSpreadsheetSet()) {
        		if(sheet.get_author().equals("pf")) {
        			System.out.println(sheet.toString());
        		}
        	}
        	
        	System.out.println("Spreadsheets from user 'ra':"); 
        	for(Spreadsheet sheet : bubble.getSpreadsheetSet()) {
        		if(sheet.get_author().equals("ra")) {
        			System.out.println(sheet.toString());
        		}
        	}
        	
        	System.out.println("XML for Spreadsheets from user 'pf':"); 
        	for(Spreadsheet sheet : bubble.getSpreadsheetSet()) {
        		if(sheet.get_author().equals("pf")) {
        			System.out.println(sheet.export());
        		}
        	}
        	
        	//FIXME REMOVER DO ESTADO PERSISTENTE A FOLHA NOTAS ES DO UTILIZADOR PF
        	
        	System.out.println("Spreadsheets from user 'pf' [after removing 'Notas ES']:"); 
        	for(Spreadsheet sheet : bubble.getSpreadsheetSet()) {
        		if(sheet.get_author().equals("pf")) {
        			System.out.println(sheet.toString());
        		}
        	}
        	
        	//FIXME IMPORTAR UMA FOLHA DE CÁLCULO
        	
        	System.out.println("Spreadsheets from user 'pf' [after importing 'Stuff']:"); 
        	for(Spreadsheet sheet : bubble.getSpreadsheetSet()) {
        		if(sheet.get_author().equals("pf")) {
        			System.out.println(sheet.toString());
        		}
        	}
        	
        	System.out.println("XML for Spreadsheets from user 'pf' [after removing 'Notas ES' and importing 'Stuff':"); 
        	for(Spreadsheet sheet : bubble.getSpreadsheetSet()) {
        		if(sheet.get_author().equals("pf")) {
        			System.out.println(sheet.export());
        		}
        	}
        	
            tm.commit();
            committed = true;
            
        } catch (SystemException| NotSupportedException | RollbackException| HeuristicMixedException | HeuristicRollbackException ex) {
                 System.err.println("Error in execution of transaction: " + ex);
        } finally {
             if (!committed)
             try {
                 tm.rollback();
             } catch (SystemException ex) {
                 System.err.println("Error in roll back of transaction: " + ex);
             }
        }

        //FenixFramework.shutdown();
    }

    private static void populateDomain(Bubbledocs bubble) {
    	if(!bubble.getUserSet().isEmpty()) 
    		return;
    	
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

	public static void testThings(Bubbledocs bubble){
    	/*
        try{
            Spreadsheet notasEs = bubble.getSpreadsheetById(0);

            System.out.println("Cell 1;1 : " + notasEs.getCell(1,1).getValue() );   
            System.out.println("Cell 2;2 : " + notasEs.getCell(2,2).getValue() );   
            System.out.println("Cell 3;4 : " + notasEs.getCell(3,4).getValue() );   
            System.out.println("Cell 5;6 : " + notasEs.getCell(5,6).getValue() );   
        }catch(BubbleCellException | SpreadsheetNotFoundException e){
            System.out.println(e.getMessage());
        } 
        */
    	try {
			System.out.println(bubble.export(bubble.getSpreadsheetById(0)));
		} catch (SpreadsheetNotFoundException e) {
			e.printStackTrace();
		}
    	return;
    }

    private static void populateBubble(Bubbledocs bubble){
        Root root;
        Spreadsheet notasEs;
        Cell cell1;
        Cell cell2;
        Cell cell3;
        Cell cell4;

        //Get the root user
        root = Root.getRoot();

        //Add two users
        try{
            root.addUser("Paul Door", "pf", "sub");
            root.addUser("Step Rabbit", "ra", "cor");
        }catch(UserAlreadyExistsException e){
            System.out.println(e.getMessage());
        }

        //Create a Spreadsheet belonging to pf and add content to it
        try{
            Literal lit;
            Literal lit2;
            Reference ref;
            Reference ref2;
            Reference ref3;
            Reference ref4;
            Add add;
            Div div;
   
            notasEs = bubble.createSpreadsheet(bubble.getUserByUsername("pf"), "Notas ES", 300, 20);
            
            //Literal 5 in 3;4
            cell1 = notasEs.getCell(3,4);
            lit = new Literal();
            lit.init(5);
            cell1.setContent(lit);
            
            //Reference to 5;6 IN 1;1
            cell2 = notasEs.getCell(1,1);
            ref = new Reference();
            ref.init(notasEs.getCell(3,4));
            cell2.setContent(ref);
    
            //Add 2 and 3;4 in 5;6
            cell3 = notasEs.getCell(5,6);
            add = new Add();
            lit2 = new Literal();
            lit2.init(2);
            ref2 = new Reference();
            ref2.init(notasEs.getCell(3,4));
            add.init(lit2, ref2);
            cell3.setContent(add);
            
            //Div 1;1 by 3;4 in 2;2
            cell4 = notasEs.getCell(2,2);
            div = new Div();
            ref3 = new Reference();
            ref3.init(notasEs.getCell(1,1));
            ref4 = new Reference();
            ref4.init(notasEs.getCell(3,4));
            div.init(ref3, ref4);
            cell4.setContent(div);   
    
        }catch(UserNotFoundException | InvalidCellException e){ 
            System.out.println(e.getMessage());
        }
    }

    private static void printAllUsersInfo(Bubbledocs bubble){
        Set<User> usersSet;

        System.out.println("Users on this bubbledocs instance:");
        System.out.println("Name | Username | Password");

        usersSet = bubble.getUserSet();
        for(User u : usersSet)
            System.out.println(u.get_name() + " | " + u.get_username() + " | " + u.get_passwd());

    }

    private static Spreadsheet getSpreadsheetByName(String name, List<Spreadsheet> spreadsheets){
        for(Spreadsheet s : spreadsheets)
                if(s.get_name().equals(name))
                        return s;
        return null;
    }

    private static void exportUsersDocuments(Bubbledocs bubble, String user)throws UserNotFoundException{
        List<Spreadsheet> spreadsheets;
       
        spreadsheets = bubble.getSpreadsheetsByAuthor(user);
        if(spreadsheets.isEmpty())
            System.out.println("<This user has no documents>");
        else
            for(Spreadsheet s : spreadsheets)
                    bubble.export(s);
        
    }

    private static void printUsersDocuments(Bubbledocs bubble, String user)throws UserNotFoundException {
        List<Spreadsheet> spreadsheets;
       
        spreadsheets = bubble.getSpreadsheetsByAuthor(user);

        System.out.println(user + "'s documents:");
        if(spreadsheets.isEmpty())
            System.out.println("<This user has no documents>");
        else
            for(Spreadsheet s : spreadsheets)
                System.out.println(s.get_name() + " | id: " + s.get_id());

    }
}
