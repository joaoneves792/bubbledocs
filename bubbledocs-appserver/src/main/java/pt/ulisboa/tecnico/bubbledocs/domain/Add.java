package pt.ulisboa.tecnico.bubbledocs.domain;

import java.util.Set;
import java.util.Iterator;

public class Add extends Add_Base {
    
    public Add(String text, Cell arg1, Cell arg2) {
        super();
        super.init(text, arg1, arg2);
    }

    public Add(String text, Cell arg1, Integer arg2) {
        super();
        super.init(text, arg1, arg2);
    }
    public Add(String text, Integer arg1, Cell arg2) {
        super();
        super.init(text, arg1, arg2);
    }
    public Add(String text, Integer arg1, Integer arg2) {
        super();
        super.init(text, arg1, arg2);
    }
   
    public Integer getValue(){
        Integer val1;
        Integer val2;
        Set<Cell> cellSet;
        Iterator<Cell> iter;

        val1 = get_argument1();
        val2 = get_argument2();
        cellSet = getCellsSet();
        iter = cellSet.iterator(); 


        if(val1 == null)
            val1 = iter.next().getValue();

        if(val2 == null)
            val2 = iter.next().getValue();

        return val1 + val2;
    }

}
