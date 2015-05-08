package pt.ulisboa.tecnico.bubbledocs.domain;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import org.jdom2.JDOMException;

import pt.ulisboa.tecnico.bubbledocs.exceptions.BubbleCellException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidCellException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidExportException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidImportException;

public class Spreadsheet extends Spreadsheet_Base {
    
    public Spreadsheet(String name, String author, Integer id, Integer rows, Integer columns) {
        super();
        init(name, author, id, rows, columns);
    }

	Spreadsheet(String Username, String XMLString) throws InvalidImportException, JDOMException, IOException, InvalidCellException {
		
		org.jdom2.Document document = makeDocument(XMLString);
		
		org.jdom2.Element spreadsheet = document.getRootElement();
		//set_id(Integer.parseInt(spreadsheet.getAttribute("id").getValue()));
		setRows(Integer.parseInt(spreadsheet.getAttribute("rows").getValue()));
		setColumns(Integer.parseInt(spreadsheet.getAttribute("columns").getValue()));
		
		/*
		String XMLAuthor = spreadsheet.getAttribute("author").getValue();
		if(!XMLAuthor.equals(Username))
			throw new InvalidImportException("Attempted to import spreadsheet from another user. AUTHOR: " + XMLAuthor + " RESQUESTED : " + Username);
		*/
		
		setAuthor(Username);
		setName(spreadsheet.getAttribute("name").getValue());
		setDate(org.joda.time.LocalDate.parse(spreadsheet.getAttribute("date").getValue()));
		
		for(org.jdom2.Element cellElement : spreadsheet.getChild("Cells").getChildren())
			importCell(cellElement);	
	}
	
	private void importCell(org.jdom2.Element cellElement) throws InvalidCellException, InvalidImportException {
		int row = Integer.parseInt(cellElement.getAttribute("row").getValue()),
				 column = Integer.parseInt(cellElement.getAttribute("column").getValue());
		
		Cell cell = getCell(row, column);
		
		boolean protectd = Boolean.parseBoolean(cellElement.getAttribute("protected").getValue());
		
		if(row < 1 || getRows() < row) 
			throw new InvalidImportException("Attempted to Import a Cell outside of Spreadsheet row Bounds (Bound: " + getRows() + " , Cell: " + row + ").");
		else if(column < 1 || getColumns() < column)
			throw new InvalidImportException("Attempted to Import a Cell outside of Spreadsheet Column Bounds (Bound: " + getColumns() + " , Cell: " + column + ").");
		
		cell.setRow(row);
		cell.setColumn(column);
		cell.setProtectd(protectd);
		java.util.List<org.jdom2.Element> content = cellElement.getChildren();
		
		if(null == content) return;
		else
			for(org.jdom2.Element el : content)	{
				//hack - in reality this loop should only unroll once
				String contentName = el.getName();
				if(contentName.equals("Add")) {
					Add add = new Add();
					add.init(el, this);
					cell.setContent(add);
				} else if(contentName.equals("Sub")) {
					Sub sub = new Sub();
					sub.init(el, this);
					cell.setContent(sub);
				} else if(contentName.equals("Mul")) {
					Mul mul = new Mul();
					mul.init(el, this);
					cell.setContent(mul);
				} else if(contentName.equals("Div")) {
					Div div = new Div();
					div.init(el, this);
					cell.setContent(div);
				} else if(contentName.equals("Literal")) {
					Literal lit = new Literal();
					lit.init(el, this);
					cell.setContent(lit);
				} else if(contentName.equals("Reference")) {
					Reference ref = new Reference();
					ref.init(el, this);
					cell.setContent(ref);
				} else if(contentName.equals("Prd")) {
					Prd prd = new Prd();
					prd.init(el, this);
					cell.setContent(prd);
				} else if(contentName.equals("Avg")) {
					Avg avg = new Avg();
					avg.init(el, this);
					cell.setContent(avg);
				} else {
					throw new InvalidImportException("Attempted to Import Invalid Cell Content.");
				}
			}		
	}
	
	private org.jdom2.Document makeDocument(String XMLString) throws JDOMException, IOException {	
		return new org.jdom2.input.SAXBuilder().build(new java.io.StringReader(XMLString));
	}

	protected void init(String name, String author, Integer id, Integer rows, Integer columns) {
        setName(name);
        setAuthor(author);
        setId(id);
        setRows(rows);
        setColumns(columns);
        setDate(org.joda.time.LocalDate.now());
	}
    
    public Set<Cell> getCellsByRow(int row) throws InvalidCellException {
    	if(getRows() < row || row < 1)
    		throw new InvalidCellException("Requested Cell is outside of Spreadsheet : " + row + ".");
    	Set<Cell> cellSet = new TreeSet<Cell>();
    	for(Cell cell : getCellSet()) {
    		if(cell.getRow() == row)
    			cellSet.add(cell);
    	}
    	return Collections.unmodifiableSet(cellSet);
    }
    
    public TreeSet<Cell> getSortedCellsByRow(int row) throws InvalidCellException {
    	return new TreeSet<Cell>(getCellsByRow(row));
    }
    
    public Cell getCell(int row, int column) throws InvalidCellException {
    	if(getColumns() < column || column < 1)
    		throw new InvalidCellException("Requested Cell is outside of Spreadsheet : " + column + ".");
    	Set<Cell> cellSet = getCellsByRow(row);
    	for(Cell cell : cellSet) {
    		if(column == cell.getColumn()) {
    			return cell;
    		}
    	}
    	Cell lazyCell = new Cell(row, column, false);
    	addCell(lazyCell);
    	lazyCell.setSpreadsheet(this);
    	return lazyCell;
    }
    
    public Integer assignFunctionCell(Integer cellRow, Integer cellColumn, Function function ) throws InvalidCellException, BubbleCellException {
    	Cell modifiedCell = getCell(cellRow, cellColumn);
    	modifiedCell.setContent(function);    	
        return modifiedCell.calculate();
    }

    private org.jdom2.Document myExport() throws InvalidExportException {
		org.jdom2.Document document = new org.jdom2.Document();
		org.jdom2.Element  spreadsheet = new org.jdom2.Element("Spreadsheet");
		//spreadsheet.setAttribute("id", get_id().toString());
		spreadsheet.setAttribute("rows", getRows().toString());
		spreadsheet.setAttribute("columns", getColumns().toString());
		spreadsheet.setAttribute("author", getAuthor());
		spreadsheet.setAttribute("name", getName());
		spreadsheet.setAttribute("date", getDate().toString());
		document.setRootElement(spreadsheet);
		org.jdom2.Element cells = new org.jdom2.Element("Cells");
		spreadsheet.addContent(cells);
		for(Cell cell : getCellSet()) {
			try {
				cells.addContent(cell.export());
			} catch (InvalidCellException e) {
				throw new InvalidExportException("Attempted to export invalid matrix for spreadsheet");
			}
		}
		
		return document;
    }

	public String export() throws InvalidExportException {
		org.jdom2.output.XMLOutputter xml =
				new org.jdom2.output.XMLOutputter(org.jdom2.output.Format.getPrettyFormat());
		
		return xml.outputString(myExport());
	}
	    
    
    public String toString() {
    	return "<< ID: " + getId() + " || NAME: " + getName() + " || AUTHOR: " + getAuthor() + "|| DATE :" + getDate() + " || rows: " + getRows() + " || " + getColumns() + " >>";
    }
    
    public String describe() throws InvalidCellException {
    	int rows = getRows();
    	String theString = "";
    	
    	for(int i = 1; i <= rows; i++) {
    		Set<Cell> myRow = getSortedCellsByRow(i);
    		if(myRow.size() > 0) {
    			theString += "[";
    			for(Cell cell : myRow)
    				theString += " " + cell.toString();
				theString += " ]\n";	    		
    		}
    	}
   		return theString;
    }
    
     /**
      * Method to recursively erase this spreadsheet (from persistence)
      **/
      public void clean() {
          for(Cell c : getCellSet()){
              c.clean();
          }
          //Bubbledocs.getBubbledocs().removeSpreadsheet(this);
          super.deleteDomainObject();
      }
      
      
      public boolean equal(Object o) throws InvalidCellException {
    	  if(!(o instanceof Spreadsheet))
    		  return false;
    	  else {
    		  Spreadsheet s = (Spreadsheet) o;
    		  if(s.getRows() != getRows() || s.getColumns() != getColumns())
    			  return false;
    		  
    		  for(int r = 1; r <= s.getRows(); r++)
    			  for(int c = 1; c <= s.getColumns(); c++)
    				  if(!s.getCell(r,c).equals(getCell(r,c)))
    					  return false;
    			  
    		  return true;
    	  }
      }
}


