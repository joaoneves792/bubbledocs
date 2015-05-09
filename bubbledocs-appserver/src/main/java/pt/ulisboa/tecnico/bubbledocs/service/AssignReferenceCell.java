package pt.ulisboa.tecnico.bubbledocs.service;

import pt.ulisboa.tecnico.bubbledocs.domain.Bubbledocs;
import pt.ulisboa.tecnico.bubbledocs.exceptions.BubbleCellException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.BubbledocsException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.SpreadsheetNotFoundException;

// add needed import declarations

public class AssignReferenceCell extends BubbledocsService {
	
    private Integer spreadsheetId;
    private String myCellId;
    private String referencedCell;
	private Integer cellLine;
	private Integer cellColumn;
    
    /*
     * Assigns a reference to a cell
     * @param string with a user token
     * @param integer with the spreadsheet Id
     * @param string with cellId
     * @param string with cellReference
     */
    public AssignReferenceCell(String tokenUser, int ssId, String cellID, String cellRef) {
    	userToken = tokenUser;
    	spreadsheetId = ssId;
    	myCellId = cellID;
    	referencedCell = cellRef;
    }

    @Override
    protected void dispatch() throws BubbledocsException, NumberFormatException {
   	   	Bubbledocs bubble = Bubbledocs.getBubbledocs();
   	   	Integer refCellLine, refCellColumn;
   	   	
   	   	
   	   	//Parse the cells id's
   	   	String delims = "[;]";
    	String[] tokensCellId= myCellId.split(delims);
    	String[] tokensCellReference = referencedCell.split(delims);
    	
    	cellLine = Integer.parseInt(tokensCellId[0]);
    	cellColumn = Integer.parseInt(tokensCellId[1]);
    	refCellLine = Integer.parseInt(tokensCellReference[0]);
    	refCellColumn = Integer.parseInt(tokensCellReference[1]);
   	   	
    	bubble.assignReferenceCell(userToken, spreadsheetId, cellLine, cellColumn, refCellLine, refCellColumn);
    }
    
    public final Integer getResult(){
    	if(cellLine == null || cellColumn == null)
    		return null;
    	
   	   	Bubbledocs bubble = Bubbledocs.getBubbledocs();
   	   	try {
			return bubble.getSpreadsheetById(spreadsheetId).getCell(cellLine, cellColumn).calculate();
		} catch (BubbleCellException | SpreadsheetNotFoundException e) {
			return null;
		}   	
    }
}
