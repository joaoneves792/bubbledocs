package pt.ulisboa.tecnico.bubbledocs.domain;

public class Mul extends Mul_Base {
 
	public Mul() {
		super();
	}
	
	@Override
	protected int __getValue__() {
		return arg1.getValue() * arg2.getValue();	
	}	
	
    /* FIXME REVIEW THIS METHOD FOR NEW DOMAIN MODEL
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
    */
}
