
package pt.ulisboa.tecnico.bubbledocs;

import pt.ulisboa.tecnico.bubbledocs.domain.Bubbledocs;
import pt.ulisboa.tecnico.bubbledocs.domain.User;
import pt.ulisboa.tecnico.bubbledocs.domain.Spreadsheet;
import pt.ulisboa.tecnico.bubbledocs.domain.Cell;
import pt.ulisboa.tecnico.bubbledocs.domain.Literal;

import pt.ulisboa.tecnico.bubbledocs.exceptions.*;

import pt.ist.fenixframework.FenixFramework;
import pt.ist.fenixframework.TransactionManager;

import javax.transaction.*;

public class BubbleApplication{
    public static void main(String[] args){
        System.out.println("Started BubbleApllication...");
        
        TransactionManager tm = FenixFramework.getTransactionManager();
        boolean committed = false;

        try{
            tm.begin();
            Bubbledocs bubble = Bubbledocs.getBubbledocs();

            //Assuming that if we dont have users then everything is empty
            if(bubble.getUserSet().isEmpty())
                    populateBubble(bubble);

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
    }

    private static void populateBubble(Bubbledocs bubble){
        User user1;
        Spreadsheet spreadsheet1;
        Cell cell;

        System.out.println("Populating Bubbledocs");    

        try{
            user1 = new User("Joao Neves", "joaon", "12345");
            bubble.addUser(user1);
            spreadsheet1 = bubble.createSpreadsheet(user1, "myDoc.doc", 5, 5);
            cell = spreadsheet1.getCell(1,1);

            cell.setContent(new Literal("99"));

            System.out.println(cell.getValue());

            //bubble.addWritePermission(user1, "joaon", spreadsheet1.get_id());
        //}catch( InvalidCellException | UnauthorizedUserException | SpreadsheetNotFoundException | UserNotFoundException e){
        }catch( BubbleCellException e){
            System.out.println(e.getMessage());
        }
    }
}
