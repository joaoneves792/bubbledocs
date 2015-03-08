
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
        System.out.println("Populating Bubbledocs");    
        User user1 = new User("Joao Neves", "joaon", "12345");
        bubble.addUser(user1);
        bubble.createSpreadsheet(user1, "myDoc.doc", 5, 5);
    }
}
