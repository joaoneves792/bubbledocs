package pt.ulisboa.tecnico.bubbledocs.domain;

import pt.ulisboa.tecnico.bubbledocs.exceptions.BubbleCellException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidCellException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidImportException;

public class Cell extends Cell_Base implements Comparable<Cell> {

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
                return null;
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
		int linDiff = other.getRow() - getRow();
		int colDiff = other.getColumn() - getColumn();
		return linDiff == 0 ? colDiff : linDiff;
	}

/*
	public Cell(org.jdom2.Element cellElement, Spreadsheet sheet) throws InvalidImportException, InvalidCellException {
		int row = Integer.parseInt(cellElement.getAttribute("row").getValue()),
		 column = Integer.parseInt(cellElement.getAttribute("column").getValue());
		
		boolean protectd = Boolean.parseBoolean(cellElement.getAttribute("protected").getValue());
		
		int spreadsheetrows = sheet.getRows(), 
	     spreadsheetColumns = sheet.getColumns();
				
		if(row < 1 || spreadsheetrows < row) 
			throw new InvalidImportException("Attempted to Import a Cell outside of Spreadsheet row Bounds (Bound: " + spreadsheetrows + " , Cell: " + row + ").");
		else if(column < 1 || spreadsheetColumns < column)
			throw new InvalidImportException("Attempted to Import a Cell outside of Spreadsheet Column Bounds (Bound: " + spreadsheetColumns + " , Cell: " + column + ").");
		
		setRow(row);
		setColumn(column);
		setProtectd(protectd);
		java.util.List<org.jdom2.Element> content = cellElement.getChildren();
		
		if(null == content) return;
		else
			for(org.jdom2.Element el : content)	{
				//hack - in reality this loop should only unroll once
				String contentName = el.getName();
				if(contentName.equals("Add")) {
					Add add = new Add();
					add.init(el, sheet);
					setContent(add);
				} else if(contentName.equals("Sub")) {
					Sub sub = new Sub();
					sub.init(el, sheet);
					setContent(sub);
				} else if(contentName.equals("Mul")) {
					Mul mul = new Mul();
					mul.init(el, sheet);
					setContent(mul);
				} else if(contentName.equals("Div")) {
					Div div = new Div();
					div.init(el, sheet);
					setContent(div);
				} else if(contentName.equals("Literal")) {
					Literal lit = new Literal();
					lit.init(el, sheet);
					setContent(lit);
				} else if(contentName.equals("Reference")) {
					Reference ref = new Reference();
					ref.init(el, sheet);
					setContent(ref);
				} else if(contentName.equals("Prd")) {
					Prd prd = new Prd();
					prd.init(el, sheet);
					setContent(prd);
				} else if(contentName.equals("Avg")) {
					Avg avg = new Avg();
					avg.init(el, sheet);
					setContent(avg);
				} else {
					throw new InvalidImportException("Attempted to Import Invalid Cell Content.");
				}
			}
	}
*/

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
}
