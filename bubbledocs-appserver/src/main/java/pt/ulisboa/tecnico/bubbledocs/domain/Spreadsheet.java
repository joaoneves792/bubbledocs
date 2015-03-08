package pt.ulisboa.tecnico.bubbledocs.domain;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.jdom2.Document;
import org.jdom2.output.XMLOutputter;

import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidCellException;

public class Spreadsheet extends Spreadsheet_Base {
    
    public Spreadsheet(String name, String author, Integer id, Integer lines, Integer columns) {
        super();
        init(name, author, id, lines, columns);
    }
    
    /* this is also an XML export */
    public Spreadsheet(org.jdom2.Document document) throws InvalidCellException {
		super();
		
		org.jdom2.Element spreadsheet = document.getRootElement();
		set_name(spreadsheet.getAttribute("name").getValue());
		set_author(spreadsheet.getAttribute("author").getValue());
		set_id(Integer.parseInt(spreadsheet.getAttribute("id").getValue()));
		set_lines(Integer.parseInt(spreadsheet.getAttribute("lines").getValue()));
		set_Columns(Integer.parseInt(spreadsheet.getAttribute("columns").getValue()));
		
		for(org.jdom2.Element cell : spreadsheet.getChildren()) {
			Cell newCell = 
					new Cell(Integer.parseInt(cell.getAttribute("line").getValue()), 
							Integer.parseInt(cell.getAttribute("column").getValue()),
							Boolean.parseBoolean(cell.getAttribute("protected").getValue()));
			__addCell__(newCell);
			
			if(cell.getChildren().isEmpty()) continue;
			
			cell.addContent(cell.getChildren().get(0));		
		}
	}

    private void __addCell__(Cell cell) throws InvalidCellException {
    	if(getCell(cell.get_line(), cell.get_column()) != null) {
    		addCell(cell);
    	}
    }
    
	protected void init(String name, String author, Integer id, Integer lines, Integer columns) {
        Cell newCell;

        set_name(name);
        set_author(author);
        set_id(id);
        set_lines(lines);
        set_Columns(columns);
        
        for(int i=1 ; i<=lines; i++){
                for(int j=1 ; j<=columns; j++){
                        newCell = new Cell(j, i, false);
                        addCell(newCell);
                }
        }
    }
    
    public Set<Cell> getCellsByLine(int line) throws InvalidCellException {
    	if(get_lines() < line)
    		throw new InvalidCellException("Requested Cell is outside of Spreadsheet : " + line + ".");
    	Set<Cell> cellSet = new TreeSet<Cell>();
    	for(Cell cell : getCellSet()) {
    		if(cell.get_line() == line)
    			cellSet.add(cell);
    	}
    	return Collections.unmodifiableSet(cellSet);
    }
    
    public Cell getCell(int line, int column) throws InvalidCellException {
    	if(get_lines() < line)
    		throw new InvalidCellException("Requested Cell is outside of Spreadsheet : " + line + ".");
    	else if(get_Columns() < line)
    		throw new InvalidCellException("Requested Cell is outside of Spreadsheet : " + column + ".");
    	Set<Cell> cellSet = getCellsByLine(line);
    	for(Cell cell : cellSet) {
    		if(column == cell.get_column()) {
    			return cell;
    		}
    	}
    	return null;
    }

	public String export() {
		org.jdom2.Document document = new org.jdom2.Document();
		org.jdom2.Element  spreadsheet = new org.jdom2.Element("Spreadsheet");
		spreadsheet.setAttribute("id", get_id().toString());
		spreadsheet.setAttribute("lines", get_lines().toString());
		spreadsheet.setAttribute("columns", get_Columns().toString());
		spreadsheet.setAttribute("author", get_author());
		spreadsheet.setAttribute("name", get_name());
		spreadsheet.setAttribute("date", get_creationDate());
		document.setRootElement(spreadsheet);
		for(Cell cell : getCellSet()) {
			spreadsheet.addContent(cell.export());
		}
		
		org.jdom2.output.XMLOutputter xml =
				new org.jdom2.output.XMLOutputter(org.jdom2.output.Format.getPrettyFormat());
		
		return xml.outputString(document);
	}
}
