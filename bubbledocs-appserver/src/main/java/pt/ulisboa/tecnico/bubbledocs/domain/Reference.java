package pt.ulisboa.tecnico.bubbledocs.domain;

import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidImportException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidReferenceException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidCellException;

public class Reference extends Reference_Base {

    public Reference() {
        super();
    }

    public Reference(int line, int column) {
    	super();
    	init(line, column);
	}

	public final void init(int line, int column) {
    	set_line(line);
    	set_column(column);
    }
    
    @Override
    protected final int __getValue__()  throws InvalidCellException, InvalidReferenceException {
    	Cell referencedCell = getReferencedCell();
    	if(null == referencedCell)
            throw new InvalidCellException("A Reference points to a Cell that does not exist.");
    	Content content = referencedCell.getContent();
    	if(content == null) 
    		throw new InvalidReferenceException("A Reference points to an empty Cell.");
    	return content.getValue();
    }

	private Cell getReferencedCell() throws InvalidCellException {
		return getCell().getSpreadsheet().getCell(get_line(), get_column());
	}
        
    /**
     * Defines XML element for this class
     * @throws InvalidCellException 
     */
    @Override
    public final org.jdom2.Element export() throws InvalidCellException {
    	org.jdom2.Element refElement = new org.jdom2.Element("Reference");
    	refElement.setAttribute("line", get_line().toString());
    	refElement.setAttribute("column", get_column().toString());
    	return refElement;
    }
    
    /**
     * pseudo-constructor for initializing a content from an XML element
     * @param XML JDOM element for this content
     */
    protected final void init(org.jdom2.Element el) throws InvalidImportException {
    	set_line(Integer.parseInt(el.getAttribute("line").getValue()));
    	set_column(Integer.parseInt(el.getAttribute("column").getValue()));
    	/*try {
    		getReferencedCell();
    	} catch(InvalidCellException e) {
    		throw new InvalidImportException("Attempted to import reference to outside of spreadsheet");
    	} */   	
    }
 
    /**
      * Method to erase this Reference (from persistence)
      */
    public void clean(){
    	if(null != getCell()) 
    		setCell(null);
        super.deleteDomainObject();
    }

}
