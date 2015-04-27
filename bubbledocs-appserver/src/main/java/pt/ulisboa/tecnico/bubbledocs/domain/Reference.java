package pt.ulisboa.tecnico.bubbledocs.domain;

import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidImportException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidReferenceException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidCellException;

public class Reference extends Reference_Base {

    public Reference() {
        super();
    }

    public Reference(Cell referencedCell) {
    	super();
    	setReferencedCell(referencedCell);
    	referencedCell.addReferences(this);
	}

    @Override
    protected final int myValue()  throws InvalidCellException, InvalidReferenceException {
    	Cell referencedCell = getReferencedCell();
    	if(null == referencedCell)
            throw new InvalidCellException("A Reference points to a Cell that does not exist.");
    	Content content = referencedCell.getContent();
    	if(content == null) 
    		throw new InvalidReferenceException("A Reference points to an empty Cell.");
    	return content.calculate();
    }

    /**
     * Defines XML element for this class
     * @throws InvalidCellException 
     */
    @Override
    public final org.jdom2.Element export() throws InvalidCellException {
    	org.jdom2.Element refElement = new org.jdom2.Element("Reference");
    	refElement.setAttribute("row", getReferencedCell().getRow().toString());
    	refElement.setAttribute("column", getReferencedCell().getColumn().toString());
    	return refElement;
    }
    
    /**
     * pseudo-constructor for initializing a content from an XML element
     * @param XML JDOM element for this content
     * @throws InvalidCellException 
     */
    protected final void init(org.jdom2.Element el, Spreadsheet sheet) throws InvalidImportException, InvalidCellException {
    	int referencedRow = Integer.parseInt(el.getAttribute("row").getValue()),
    		referencedCol = Integer.parseInt(el.getAttribute("column").getValue());
    	Cell referencedCell = sheet.getCell(referencedRow, referencedCol);
    	setReferencedCell(referencedCell); 
    	referencedCell.addReferences(this);
    }
 
    /**
      * Method to erase this Reference (from persistence)
      */
    public void clean(){
    	if(null != getCell()) 
    		setCell(null);
    	if(null != getReferencedCell())
    		setReferencedCell(null);
        super.deleteDomainObject();
    }

    @Override
    public boolean equals(Content other) {
    	if(!(other instanceof Reference))
    		return false;
    	else return getReferencedCell().getRow() == ((Reference)other).getReferencedCell().getRow() &&
    			    getReferencedCell().getColumn() == ((Reference)other).getReferencedCell().getColumn();
    }
}
