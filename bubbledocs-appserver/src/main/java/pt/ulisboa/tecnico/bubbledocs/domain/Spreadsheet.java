package pt.ulisboa.tecnico.bubbledocs.domain;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;



import org.jdom2.JDOMException;

import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidCellException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidExportException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidImportException;

public class Spreadsheet extends Spreadsheet_Base {
    
    public Spreadsheet(String name, String author, Integer id, Integer lines, Integer columns) {
        super();
        init(name, author, id, lines, columns);
    }

	Spreadsheet(String Username, String XMLString) throws InvalidImportException, JDOMException, IOException {
		
		org.jdom2.Document document = __makeDocument__(XMLString);
		
		org.jdom2.Element spreadsheet = document.getRootElement();
		//set_id(Integer.parseInt(spreadsheet.getAttribute("id").getValue()));
		set_lines(Integer.parseInt(spreadsheet.getAttribute("lines").getValue()));
		set_columns(Integer.parseInt(spreadsheet.getAttribute("columns").getValue()));
		
		String XMLAuthor = spreadsheet.getAttribute("author").getValue();
		if(!XMLAuthor.equals(Username))
			throw new InvalidImportException("Attempted to import spreadsheet from another user");
		
		set_author(XMLAuthor);
		set_name(spreadsheet.getAttribute("name").getValue());
		set_date(spreadsheet.getAttribute("date").getValue());
		
		for(org.jdom2.Element cellElement : spreadsheet.getChildren())
			addCell(new Cell(cellElement, get_lines(), get_columns()));	
	}
	
	private org.jdom2.Document __makeDocument__(String XMLString) throws JDOMException, IOException {	
		return new org.jdom2.input.SAXBuilder().build(new java.io.StringReader(XMLString));
	}

	protected void init(String name, String author, Integer id, Integer lines, Integer columns) {
        Cell newCell;

        set_name(name);
        set_author(author);
        set_id(id);
        set_lines(lines);
        set_columns(columns);
        
        java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd");
        set_date(dateFormat.format(new java.util.Date()));
        
        for(int i=1 ; i<=lines; i++){
                for(int j=1 ; j<=columns; j++){
                        newCell = new Cell(i, j, false);
                        addCell(newCell);
                }
        }
    }
    
    public Set<Cell> getCellsByLine(int line) throws InvalidCellException {
    	if(get_lines() < line || line < 1)
    		throw new InvalidCellException("Requested Cell is outside of Spreadsheet : " + line + ".");
    	Set<Cell> cellSet = new TreeSet<Cell>();
    	for(Cell cell : getCellSet()) {
    		if(cell.get_line() == line)
    			cellSet.add(cell);
    	}
    	return Collections.unmodifiableSet(cellSet);
    }
    
    public Cell getCell(int line, int column) throws InvalidCellException {
    	if(get_lines() < line || line < 1)
    		throw new InvalidCellException("Requested Cell is outside of Spreadsheet : " + line + ".");
    	else if(get_columns() < column || column < 1)
    		throw new InvalidCellException("Requested Cell is outside of Spreadsheet : " + column + ".");
    	Set<Cell> cellSet = getCellsByLine(line);
    	for(Cell cell : cellSet) {
    		if(column == cell.get_column()) {
    			return cell;
    		}
    	}
    	return null;
    }

    private org.jdom2.Document __export__() throws InvalidExportException {
		org.jdom2.Document document = new org.jdom2.Document();
		org.jdom2.Element  spreadsheet = new org.jdom2.Element("Spreadsheet");
		spreadsheet.setAttribute("id", get_id().toString());
		spreadsheet.setAttribute("lines", get_lines().toString());
		spreadsheet.setAttribute("columns", get_columns().toString());
		spreadsheet.setAttribute("author", get_author());
		spreadsheet.setAttribute("name", get_name());
		spreadsheet.setAttribute("date", get_date());
		document.setRootElement(spreadsheet);
		for(Cell cell : getCellSet()) {
			try {
				spreadsheet.addContent(cell.export());
			} catch (InvalidCellException e) {
				throw new InvalidExportException("Attempted to export invalid matrix for spreadsheet");
			}
		}
		
		return document;
    }

	public String export() throws InvalidExportException {
		org.jdom2.output.XMLOutputter xml =
				new org.jdom2.output.XMLOutputter(org.jdom2.output.Format.getPrettyFormat());
		
		return xml.outputString(__export__());
	}
    
    
    @Override
    public String toString() {
    	return "<< ID: " + get_id() + " || NAME: " + get_name() + " || AUTHOR: " + get_author() + "|| DATE :" + get_date() + " || LINES: " + get_lines() + " || " + get_columns() + " >>";
    }
    
     /**
      ** Method to recursively erase this spreadsheet (from persistence)
      **/
      public void clean(){
          Set<Cell> cells;
          cells = getCellSet();

          //We need to do two passes to avoid deleting cells that are referenced by other cells 
          for(Cell c : cells){
              c.cleanContents();
          }

          for(Cell c : cells){
              removeCell(c);
              c.clean();
          }
          super.deleteDomainObject();
      }
}


