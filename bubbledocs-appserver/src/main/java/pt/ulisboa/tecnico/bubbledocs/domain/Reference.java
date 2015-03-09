package pt.ulisboa.tecnico.bubbledocs.domain;

import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidReferenceException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidCellException;

public class Reference extends Reference_Base {

    public Reference() {
        super();
    }

    public final void init(Cell cell) {
    	setReferencedCell(cell);
    }
    
    @Override
    protected final int __getValue__()  throws InvalidCellException, InvalidReferenceException {
    	Cell cell = getReferencedCell();
    	if(null == cell)
            throw new InvalidCellException("A Reference points to a Cell that does not exist.");
    	Content content = cell.getContent();
    	if(content == null) 
    		throw new InvalidReferenceException("A Reference points to an empty Cell.");
    	return content.getValue();
    }
    
    
    //FIXME review line 35!!
    @Override
    public org.jdom2.Element export() {
    	org.jdom2.Element refElement = new org.jdom2.Element("Ref");
    	if(getReferencedCell() != null) {
    		org.jdom2.Element cell = new org.jdom2.Element("Cell");
    		refElement.addContent(cell);
    		cell.addContent(getReferencedCell().export());
    	}
    	return refElement;
    }

    /*
    public Reference(org.jdom2.Element e) {
    	super();
    	String u = e.getAttributeValue("line");
    	String v = e.getAttributeValue("column");
    	set_text("=" + u + ";" + v);
    	setReferenceCell(new Cell(e));
    }
    */

}
