package pt.ulisboa.tecnico.bubbledocs.domain;

import java.util.Iterator;
import java.util.Set;

import pt.ulisboa.tecnico.bubbledocs.exceptions.BubbleCellException;

public class Mul extends Mul_Base {

    public Mul(String text, Cell arg1, Cell arg2) {
        super();
        super.init(text, arg1, arg2);
    }

    public Mul(String text, Cell arg1, Integer arg2) {
        super();
        super.init(text, arg1, arg2);
    }
    public Mul(String text, Integer arg1, Cell arg2) {
        super();
        super.init(text, arg1, arg2);
    }
    public Mul(String text, Integer arg1, Integer arg2) {
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
    	
    	return arg1 * arg2;
    }
    
    @Override
    public org.jdom2.Element export() {
    	org.jdom2.Element mulElement = new org.jdom2.Element("Mul");
    	if(null != get_argument1()) {
    		mulElement.setAttribute("argument1", get_argument1().toString());
    	}
    	if(null != get_argument2()) {
    		mulElement.setAttribute("argument2", get_argument2().toString());
    	}
    	if(!getCellsSet().isEmpty()) {
    		org.jdom2.Element cells = new org.jdom2.Element("Cells");
    		mulElement.addContent(cells);
    		for(Cell cell : getCellsSet()) {
    			cells.addContent(cell.export());
    		}
    	}
    	return mulElement;
    }
    
}
