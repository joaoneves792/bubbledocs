
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
            printAllUsersInfo(bubble);

            //Print which documents each user owns (owns as in author)
            try{
                printUsersDocuments(bubble, "pf");
                printUsersDocuments(bubble, "ra");
            }catch(UserNotFoundException e){
                System.out.println(e.getMessage());
            }

            //Export to XML all documents belonging to "pf"
            exportUsersDocuments(bubble, "pf");

            //Remove pf's "Notas ES" spreadsheet from storage
            //TODO This is not actually removing the data from the database!!!!!
            spreadsheet = getSpreadsheetByName("Notas ES", bubble.getSpreadsheetsByAuthor("pf"));
            if(null == spreadsheet)
                System.out.println("User pf doesnt have a document named Notas ES");
            else{
                bubble.removeSpreadsheet(spreadsheet);
                spreadsheet = null;
            }


            //Print all the documents owned by "pf" 
            try{
                printUsersDocuments(bubble, "pf");
            }catch(UserNotFoundException e){
                System.out.println(e.getMessage());
            }

            //TODO: Import the spreadsheet we just exported
 
            //Print all the documents owned by "pf" again...
            try{
                printUsersDocuments(bubble, "pf");
            }catch(UserNotFoundException e){
                System.out.println(e.getMessage());
            }   

            //Export to XML the document again...        
            exportUsersDocuments(bubble, "pf");

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

    private static void populateBubble(Bubbledocs bubble){
        Root root;
        Spreadsheet notasEs;
        Cell cell;

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
            notasEs = bubble.createSpreadsheet(bubble.getUserByUsername("pf"), "Notas ES", 300, 20);
            cell = notasEs.getCell(3,4);
            cell.setContent(new Literal("5"));
            cell = notasEs.getCell(1,1);
            cell.setContent(new Reference("=5;6", notasEs.getCell(5,6))); 
            cell = notasEs.getCell(5,6);
            cell.setContent(new Add("=ADD(2,3;4)", 2, notasEs.getCell(3,4)));
            cell = notasEs.getCell(2,2);
            cell.setContent(new Div("=DIV(1;1,3;4)", notasEs.getCell(1,1), notasEs.getCell(3,4)));
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
