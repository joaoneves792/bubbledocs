package pt.ulisboa.tecnico.bubbledocs.domain;

public class Sub extends Sub_Base {

	public Sub() {
		super();
	}
	
	@Override
	protected int __getValue__() {
		return arg1.getValue() - arg2.getValue();	
	}
	
    
    /* FIXME REVIEW THIS METHOD FOR NEW DOMAIN
    @Override
    public org.jdom2.Element export() {
    	org.jdom2.Element subElement = new org.jdom2.Element("Sub");
    	if(null != get_argument1()) {
    		subElement.setAttribute("argument1", get_argument1().toString());
    	}
    	if(null != get_argument2()) {
    		subElement.setAttribute("argument2", get_argument2().toString());
    	}
    	if(!getCellsSet().isEmpty()) {
    		org.jdom2.Element cells = new org.jdom2.Element("Cells");
    		subElement.addContent(cells);
    		for(Cell cell : getCellsSet()) {
    			cells.addContent(cell.export());
    		}
    	}
    	return subElement;
    }
    */
    
}
