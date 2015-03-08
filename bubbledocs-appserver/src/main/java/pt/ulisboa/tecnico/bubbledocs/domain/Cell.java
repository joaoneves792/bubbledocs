package pt.ulisboa.tecnico.bubbledocs.domain;

import java.lang.reflect.Constructor;
import java.util.List;

import org.jdom2.Element;

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
		cellElement.setAttribute("line", get_line().toString());
		cellElement.setAttribute("column", get_column().toString());
		cellElement.setAttribute("protected", get_protected().toString());
		if(getContent() != null)
			cellElement.addContent(getContent().export());
		return cellElement;
	}
	
	@Override
	public int compareTo(Object other) {
		int linDiff = ((Cell)other).get_line() - get_line();
		int colDiff = ((Cell)other).get_column() - get_column();
		return linDiff == 0 ? colDiff : linDiff;
	}
	
	public void addContent(org.jdom2.Element element) {
		__addContent__(element);
	}

	private void __addContent__(org.jdom2.Element element) {
		List<org.jdom2.Attribute> listAttr = element.getAttributes();
		switch(listAttr.size()) {
		case 0:
			if(element.getName().equals("Ref")) {
				setContent(new Reference(element.getChild("Cell")));
			} else {
				Class klass = Class.forName(element.getName());
				Constructor cons = klass.getConstructor(String.class, Cell.class, Cell.class);
				Cell newCell1 = new Cell(element.getChildren().get(0));
				Cell newCell2 = new Cell(element.getChildren().get(1));
				String text   = element.getName() + "(" + newCell1.get_line() + ";" + newCell1.get_column() 
						+ ", " + newCell2.get_line() + ";" + newCell2.get_column() + ")";				
				setContent((Content) cons.newInstance(text, newCell1, newCell2));
			}
			break;
		case 1:
			if(element.getChildren().isEmpty()) {
				setContent(new Literal(element.getAttribute("value").getValue());
			} else {
				if(element.getAttribute("argument1") == null) {
					Class klass = Class.forName(element.getName());
					Constructor cons = klass.getConstructor(String.class, Cell.class, Integer.class);
					Cell newCell = new Cell(element.getChildren().get(0));
					String text = element.getName() + "(" + newCell.get_line() + ";" + newCell.get_column() 
							+ ", " + element.getAttribute("argument2").getValue() + ")";				
					setContent((Content) cons.newInstance(text, newCell, Integer.parseInt(element.getAttribute("argument2").getValue())));
				} else {
					Class klass = Class.forName(element.getName());
					Constructor cons = klass.getConstructor(String.class, Integer.class, Cell.class);
					Cell newCell = new Cell(element.getChildren().get(0));
					String text = element.getName() + "(" + element.getAttribute("argument1").getValue() 
							+ ", " + newCell.get_line() + ";" + newCell.get_column() + ")";				
					setContent((Content) cons.newInstance(text, Integer.parseInt(element.getAttribute("argument1").getValue()), newCell));
				}
			}
			break;
		case 2:
			Class klass = Class.forName(element.getName());
			Constructor cons = klass.getConstructor(String.class, Integer.class, Integer.class);
			String text = element.getName() + "(" + element.getAttribute("argument1").getValue() 
					+ ", " + element.getAttribute("argument2").getValue() + ")";			
			setContent((Content) cons.newInstance(text, Integer.parseInt(element.getAttribute("argument1").getValue()) , Integer.parseInt(element.getAttribute("argument2").getValue())));
			break;
		case 4:
			//FIXME RANGES
			break;
			default:
				//FIXME THROW EXCEPTION
				
		}		
	}
	
	public Cell(org.jdom2.Element e) {
		set_line(Integer.parseInt(e.getAttribute("line").getValue()));
		set_column(Integer.parseInt(e.getAttribute("column").getValue()));
		for(org.jdom2.Element el : e.getChildren() ) {
			__addContent__(el)
		}
		
	}
	
}
