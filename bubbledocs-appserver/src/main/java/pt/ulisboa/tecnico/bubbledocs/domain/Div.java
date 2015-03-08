package pt.ulisboa.tecnico.bubbledocs.domain;

import pt.ulisboa.tecnico.bubbledocs.exceptions.DivisionByZeroException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.BubbleCellException;

import java.util.Iterator;
import java.util.Set;

public class Div extends Div_Base {
    
    public Div(String text, Cell arg1, Cell arg2) {
        super();
        super.init(text, arg1, arg2);
    }

    public Div(String text, Cell arg1, Integer arg2) {
        super();
        super.init(text, arg1, arg2);
    }
    public Div(String text, Integer arg1, Cell arg2) {
        super();
        super.init(text, arg1, arg2);
    }
    public Div(String text, Integer arg1, Integer arg2) {
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
    	
    	if(arg2 == 0)
    		throw new DivisionByZeroException("Cannot make a division by zero!!");
    	
    	return arg1 / arg2;
    		
    }
    
    @Override
    public org.jdom2.Element export() {
    	org.jdom2.Element divElement = new org.jdom2.Element("Div");
    	if(null != get_argument1()) {
    		divElement.setAttribute("argument1", get_argument1().toString());
    	}
    	if(null != get_argument2()) {
    		divElement.setAttribute("argument2", get_argument2().toString());
    	}
    	if(!getCellsSet().isEmpty()) {
    		org.jdom2.Element cells = new org.jdom2.Element("Cells");
    		divElement.addContent(cells);
    		for(Cell cell : getCellsSet()) {
    			cells.addContent(cell.export());
    		}
    	}
    	return divElement;
    }
}
