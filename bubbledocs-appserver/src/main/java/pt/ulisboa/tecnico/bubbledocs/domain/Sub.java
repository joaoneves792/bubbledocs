package pt.ulisboa.tecnico.bubbledocs.domain;

import java.util.Iterator;
import java.util.Set;

import pt.ulisboa.tecnico.bubbledocs.exceptions.BubbleCellException;

public class Sub extends Sub_Base {

    public Sub(String text, Cell arg1, Cell arg2) {
        super();
        super.init(text, arg1, arg2);
    }

    public Sub(String text, Cell arg1, Integer arg2) {
        super();
        super.init(text, arg1, arg2);
    }
    public Sub(String text, Integer arg1, Cell arg2) {
        super();
        super.init(text, arg1, arg2);
    }
    public Sub(String text, Integer arg1, Integer arg2) {
        super();
        super.init(text, arg1, arg2);
    }
    
    @Override
    public Integer getValue() throws BubbleCellException{
    	
    	Integer arg1;
    	Integer arg2;
    	
    	Set<Cell> cells = getCellsSet();
    	Iterator<Cell> iterator = cells.iterator();
    	
    	arg1 = get_argument1();
    	arg2 = get_argument2();
    	
    	if(arg1 == null)
    		arg1 = iterator.next().getValue();
    	if(arg2 == null)
    		arg2 = iterator.next().getValue();
    	
    	return arg1 - arg2;
    }
}
