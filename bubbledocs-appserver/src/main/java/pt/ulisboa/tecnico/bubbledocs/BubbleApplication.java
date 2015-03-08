
package pt.ulisboa.tecnico.bubbledocs;

import pt.ulisboa.tecnico.bubbledocs.domain.Bubbledocs;
import pt.ulisboa.tecnico.bubbledocs.domain.User;

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

            //populateDomain();
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

    /*private void populateBubble(Bubbledocs bubble){
         User user1 = new User(

     
         bubble.createSpreadsheet( 
        //public Spreadsheet createSpreadsheet(User author, String name, int lines, int columns) {
    }*/
}
