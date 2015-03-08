package pt.ulisboa.tecnico.bubbledocs.domain;

import java.util.Set;
import java.util.Iterator;

import pt.ulisboa.tecnico.bubbledocs.exceptions.BubbleCellException;

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
   
    public Integer getValue() throws BubbleCellException{
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
    
    @Override
    public org.jdom2.Element export() {
    	org.jdom2.Element addElement = new org.jdom2.Element("Add");
    	if(null != get_argument1()) {
    		addElement.setAttribute("argument1", get_argument1().toString());
    	}
    	if(null != get_argument2()) {
    		addElement.setAttribute("argument2", get_argument2().toString());
    	}
    	if(!getCellsSet().isEmpty()) {
    		org.jdom2.Element cells = new org.jdom2.Element("Cells");
    		addElement.addContent(cells);
    		for(Cell cell : getCellsSet()) {
    			cells.addContent(cell.export());
    		}
    	}
    	return addElement;
    }

}
