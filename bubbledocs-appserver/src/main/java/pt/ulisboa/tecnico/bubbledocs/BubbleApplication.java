
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
import java.util.Set;
import java.util.List;

public class BubbleApplication{
    public static void main(String[] args){
        Spreadsheet spreadsheet;
        System.out.println("Started BubbleApllication...");
        
        TransactionManager tm = FenixFramework.getTransactionManager();
        boolean committed = false;

        try{
            tm.begin();
            Bubbledocs bubble = Bubbledocs.getBubbledocs();

            //Assuming that if we dont have users then everything is empty
            if(bubble.getUserSet().isEmpty())
                    populateBubble(bubble);

            //Print all users names, usernames, and passords
            //printAllUsersInfo(bubble);

            //Print which documents each user owns (owns as in author)
            /*try{
                printUsersDocuments(bubble, "pf");
                printUsersDocuments(bubble, "ra");
            }catch(UserNotFoundException e){
                System.out.println(e.getMessage());
            }*/

            /*Export to XML all documents belonging to "pf"
            try{
                exportUsersDocuments(bubble, "pf");
            }catch(UserNotFoundException e){
                System.out.println("Failed to export document: " + e.getMessage());
            }*/

            //Remove pf's "Notas ES" spreadsheet from storage
            //TODO This might not  actually be removing the data from the database!!!!!
            //TODO Crashes with InvalidStateException
            /*spreadsheet = getSpreadsheetByName("Notas ES", bubble.getSpreadsheetsByAuthor("pf"));
            if(null == spreadsheet)
                System.out.println("User pf doesnt have a document named Notas ES");
            else{
                spreadsheet.clean();
                bubble.removeSpreadsheet(spreadsheet);
                spreadsheet = null;
            }*/


            //Print all the documents owned by "pf" 
            /*try{
                printUsersDocuments(bubble, "pf");
            }catch(UserNotFoundException e){
                System.out.println(e.getMessage());
            }*/

            //TODO: Import the spreadsheet we just exported
 
            /*Print all the documents owned by "pf" again...
            try{
                printUsersDocuments(bubble, "pf");
            }catch(UserNotFoundException e){
                System.out.println("Failed to print user documents: " + e.getMessage());
            } */  

            /*Export to XML the document again...        
            try{
                exportUsersDocuments(bubble, "pf");
            }catch(UserNotFoundException e){
                System.out.println("Failed to export document: " + e.getMessage());
            } */  

            //This is a temporary function just to test things
            //TODO REMOVE ME
            testThings(bubble);

            tm.commit();
            committed = true;
        }catch (SystemException| NotSupportedException | RollbackException| HeuristicMixedException | HeuristicRollbackException ex) {
                 System.err.println("Error in execution of transaction: " + ex);
        } finally {
             if (!committed)
             try {
                 tm.rollback();
             } catch (SystemException ex) {
                 System.err.println("Error in roll back of transaction: " + ex);
             }
        }

        FenixFramework.shutdown();
    }

    public static void testThings(Bubbledocs bubble){
        try{
            Spreadsheet notasEs = bubble.getSpreadsheetById(0);

            System.out.println("Cell 1;1 : " + notasEs.getCell(1,1).getValue() );   
            System.out.println("Cell 2;2 : " + notasEs.getCell(2,2).getValue() );   
            System.out.println("Cell 3;4 : " + notasEs.getCell(3,4).getValue() );   
            System.out.println("Cell 5;6 : " + notasEs.getCell(5,6).getValue() );   
        }catch(BubbleCellException | SpreadsheetNotFoundException e){
            System.out.println(e.getMessage());
        } 
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
