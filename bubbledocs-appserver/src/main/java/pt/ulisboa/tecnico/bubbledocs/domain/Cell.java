package pt.ulisboa.tecnico.bubbledocs.domain;

import pt.ulisboa.tecnico.bubbledocs.exceptions.BubbleCellException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidImportException;

public class Cell extends Cell_Base implements Comparable<Cell> {

    public Cell(int line, int column, boolean prot) {
        super();
        init(column, line, prot);
      }

    public int myHashCode() {
    	return ( (get_line() + get_column()) * (get_line() + get_column() + 1) + get_column() ) / 2;
    }
    
    protected void init(int line, int column, boolean prot) {
        set_column(column);
        set_line(line);
        set_protected(prot);
    }

    public Integer getValue() throws BubbleCellException{
        Content content;
        content = getContent();
        if(content == null)
                return null;
        return content.getValue();
    }

    public String toString(){
        Content content;
        content = getContent();
        if(content == null)
                return null;
        return content.toString();
    }

    /**
     * 
     * @return XML element for this cell
     */
	org.jdom2.Element export() {
		org.jdom2.Element cellElement = new org.jdom2.Element("Cell");
		cellElement.setAttribute("line", get_line().toString());
		cellElement.setAttribute("column", get_column().toString());
		cellElement.setAttribute("protected", get_protected().toString());
		if(getContent() != null)
			cellElement.addContent(getContent().export());
		return cellElement;
	}
	
    
	@Override
	public int compareTo(Cell other) {
		int linDiff = other.get_line() - get_line();
		int colDiff = other.get_column() - get_column();
		return linDiff == 0 ? colDiff : linDiff;
	}

	public Cell(org.jdom2.Element cellElement, int spreadsheetLines, int spreadsheetColumns) throws InvalidImportException {
		int line = Integer.parseInt(cellElement.getAttribute("line").getValue());
		int column = Integer.parseInt(cellElement.getAttribute("column").getValue());
		boolean protectd = Boolean.parseBoolean(cellElement.getAttribute("protected").getValue());
		
		if(line < 1 || spreadsheetLines < line) 
			throw new InvalidImportException("Attempted to Import a Cell outside of Spreadsheet Line Bounds");
		else if(column < 1 || spreadsheetColumns < column)
			throw new InvalidImportException("Attempted to Import a Cell outside of Spreadsheet Column Bounds");
		
		set_line(line);
		set_column(column);
		set_protected(protectd);
		java.util.List<org.jdom2.Element> content = cellElement.getChildren();
		
		if(null == content) return;
		else
			for(org.jdom2.Element el : content)	{
				//hack - in reality this loop should only unroll once
				String contentName = el.getName();
				if(contentName.equals("Add")) {
					Add add = new Add();
					add.init(el);
					setContent(add);
				} else if(contentName.equals("Sub")) {
					Sub sub = new Sub();
					sub.init(el);
					setContent(sub);
				} else if(contentName.equals("Mul")) {
					Mul mul = new Mul();
					mul.init(el);
					setContent(mul);
				} else if(contentName.equals("Div")) {
					Div div = new Div();
					div.init(el);
					setContent(div);
				} else if(contentName.equals("Literal")) {
					Literal lit = new Literal();
					lit.init(el);
					setContent(lit);
				} else if(contentName.equals("Reference")) {
					Reference ref = new Reference();
					ref.init(el);
					setContent(ref);
				} else if(contentName.equals("Prd")) {
					Prd prd = new Prd();
					prd.init(el);
					setContent(prd);
				} else if(contentName.equals("Avg")) {
					Avg avg = new Avg();
					avg.init(el);
					setContent(avg);
				} else {
					throw new InvalidImportException("Attempted to Import Invalid Cell Content.");
				}
			}
	}
}
