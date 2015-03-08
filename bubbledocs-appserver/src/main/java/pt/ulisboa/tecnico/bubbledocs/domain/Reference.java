package pt.ulisboa.tecnico.bubbledocs.domain;

import pt.ulisboa.tecnico.bubbledocs.exceptions.BubbleCellException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidReferenceException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidCellException;

public class Reference extends Reference_Base {

    public Reference(String value, Cell referencedCell) {
        super();
        init(value, referencedCell);
    }
    protected void init(String value, Cell referencedCell){
        super.init(value);
        setReferenceCell(referencedCell);
    }

    public Integer getValue() throws BubbleCellException{
        Cell cell;
        Content content;
        cell = getReferenceCell();
        if(cell == null)
                throw new InvalidCellException("A Reference is trying to access a Cell that does not exist!");
        content = cell.getContent();
        if(content == null)
                throw new InvalidReferenceException("A Reference is pointing to an empty Cell!");

        return content.getValue();
    }
    
    @Override
    public org.jdom2.Element export() {
    	org.jdom2.Element refElement = new org.jdom2.Element("Ref");
    	refElement.setAttribute("value", toString());
    	if(getCell() != null) {
    		org.jdom2.Element cell = new org.jdom2.Element("Cell");
    		refElement.addContent(cell);
    		cell.addContent(getCell().export());    		
    	}
    	return refElement;
    }
    
    public Reference(org.jdom2.Element e) {
    	super();
    	String u = e.getAttributeValue("line");
    	String v = e.getAttributeValue("column");
    	set_text("=" + u + ";" + v);
    	setReferenceCell(new Cell(e));
    }

}
