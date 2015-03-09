package pt.ulisboa.tecnico.bubbledocs.domain;

import pt.ulisboa.tecnico.bubbledocs.exceptions.BubbleCellException;

public class Cell extends Cell_Base implements Comparable<Cell> {

    public Cell(int column, int line, boolean prot) {
        super();
        init(column, line, prot);
      }

    protected void init(int column, int line, boolean prot) {
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


}
