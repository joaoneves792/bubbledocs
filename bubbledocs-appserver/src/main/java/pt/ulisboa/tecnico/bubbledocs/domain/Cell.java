package pt.ulisboa.tecnico.bubbledocs.domain;

import pt.ulisboa.tecnico.bubbledocs.exceptions.BubbleCellException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidCellException;

public class Cell extends Cell_Base implements Comparable {

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

	org.jdom2.Element export() {
		org.jdom2.Element cellElement = new org.jdom2.Element("Cell");
		cellElement.setAttribute("Line", get_line().toString());
		cellElement.setAttribute("Column", get_column().toString());
		cellElement.addContent(getContent().export());
		return cellElement;
	}
	
	@Override
	public int compareTo(Object other) {
		int linDiff = ((Cell)other).get_line() - get_line();
		int colDiff = ((Cell)other).get_column() - get_column();
		return linDiff == 0 ? colDiff : linDiff;
	}
}
