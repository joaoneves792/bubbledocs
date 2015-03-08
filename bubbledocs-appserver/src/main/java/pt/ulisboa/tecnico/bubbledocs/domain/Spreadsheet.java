package pt.ulisboa.tecnico.bubbledocs.domain;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import org.jdom2.output.XMLOutputter;

public class Spreadsheet extends Spreadsheet_Base {
    
    public Spreadsheet(String name, String author, Integer id, Integer lines, Integer columns) {
        super();
        init(name, author, id, lines, columns);
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
    
    public Set<Cell> getCellsByLine(int line) {
    	Set<Cell> cellSet = new TreeSet<Cell>();
    	for(Cell cell : getCellSet()) {
    		if(cell.get_line() == line)
    			cellSet.add(cell);
    	}
    	return Collections.unmodifiableSet(cellSet);
    }
    
    public Cell getCell(int line, int column) {
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
		document.setRootElement(spreadsheet);
		for(Cell cell : getCellSet()) {
			spreadsheet.addContent(cell.export());
		}
		
		org.jdom2.output.XMLOutputter xml =
				new org.jdom2.output.XMLOutputter(org.jdom2.output.Format.getPrettyFormat());
		
		return xml.outputString(document);
	}

}
