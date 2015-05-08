package pt.ulisboa.tecnico.bubbledocs.domain;

import pt.ulisboa.tecnico.bubbledocs.exceptions.BubbleCellException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidCellException;

public class Cell extends Cell_Base implements Comparable<Cell> {

	private final String EMPTY = "\"\"";
	
    public Cell(int row, int column, boolean prot) {
        super();
        init(row, column, prot);
      }

    public int getHash() {
    	return ( (getRow() + getColumn()) * (getRow() + getColumn() + 1) + getColumn() ) / 2;
    }
    
    protected void init(int row, int column, boolean prot) {
        setColumn(column);
        setRow(row);
        setProtectd(prot);
    }

    public Integer calculate() throws BubbleCellException {
        Content content = getContent();
        if(content == null)
                return null;
        return content.calculate();
    }

    public String toString(){
        Content content = getContent();
        if(content == null)
                return EMPTY;
        return content.toString();
    }

    /**
     * 
     * @return XML element for this cell
     * @throws InvalidCellException 
     */
	org.jdom2.Element export() throws InvalidCellException {
		org.jdom2.Element cellElement = new org.jdom2.Element("Cell");
		cellElement.setAttribute("row", getRow().toString());
		cellElement.setAttribute("column", getColumn().toString());
		cellElement.setAttribute("protected", getProtectd().toString());
		if(getContent() != null)
			cellElement.addContent(getContent().export());
		return cellElement;
	}
	
	@Override
	public int compareTo(Cell other) {
		int linDiff = getRow() - other.getRow();
		int colDiff = getColumn() - other.getColumn();
		return linDiff == 0 ? colDiff : linDiff;
	}

	/**
	 * Method to erase this Cell (from persistence)
	 */
	public void clean() {
        getSpreadsheet().removeCell(this);
        setSpreadsheet(null);
        	
        for(Reference ref : getReferencesSet()) {
        	ref.setReferencedCell(null);
        }
        	
        Content content = getContent();
        if(null != content) {
        	setContent(null);
        	content.clean();
        }
        super.deleteDomainObject();
	}
	
	public boolean equals(Cell other) {
		return other.getRow() == getRow() &&
			   other.getColumn() == getColumn() &&
			   (other.getContent() == null ? other.getContent() == null : other.getContent().equals(getContent()));
	}
}
