package pt.ulisboa.tecnico.bubbledocs.domain;

public class Add extends Add_Base {
    
    public Add() {
        super();
    }

	@Override
	protected final int __getValue__() {
		return arg1.getValue() + arg2.getValue();		
	}

    /* FIXME REVIEW THIS METHOD FOR NEW DOMAIN MODEL
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
    */

}
