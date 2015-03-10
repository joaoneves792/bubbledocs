package pt.ulisboa.tecnico.bubbledocs.domain;

import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidReferenceException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidCellException;

public class Reference extends Reference_Base {

    public Reference() {
        super();
    }

    public Reference(Cell cell) {
    	super();
    	setReferencedCell(cell);
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
    
    
    /**
     * Defines XML element for this class
     */
    @Override
    public final org.jdom2.Element export() {
    	org.jdom2.Element refElement = new org.jdom2.Element("Reference");
    	Cell referencedCell = getReferencedCell();
		if(referencedCell != null) {
    		org.jdom2.Element cellElement = new org.jdom2.Element("ReferencedCell");
    		cellElement.setAttribute("line", referencedCell.get_line().toString());
    		cellElement.setAttribute("column", referencedCell.get_column().toString());
    		refElement.addContent(cellElement);
    	}
    	return refElement;
    }
    
    /**
     * pseudo-constructor for initializing a content from an XML element
     * @param XML JDOM element for this content
     */
    protected final void init(org.jdom2.Element el) {
    	org.jdom2.Element cellElement = el.getChild("ReferencedCell");
    	if(null == cellElement) {
    		setReferencedCell(null);
    		return;
    	}
    	
		int line = Integer.parseInt(cellElement.getAttribute("line").getValue());
		int column = Integer.parseInt(cellElement.getAttribute("column").getValue());
		
		
		
    }
 
    /**
      * Method to erase this Reference (from persistence)
      */
    public void clean(){
        setReferencedCell(null);
        super.deleteDomainObject();
    }

}
