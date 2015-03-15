package pt.ulisboa.tecnico.bubbledocs.service;

import java.util.Random;

import pt.ulisboa.tecnico.bubbledocs.domain.Bubbledocs;
import pt.ulisboa.tecnico.bubbledocs.domain.Permission;
import pt.ulisboa.tecnico.bubbledocs.domain.Session;
import pt.ulisboa.tecnico.bubbledocs.domain.Spreadsheet;
import pt.ulisboa.tecnico.bubbledocs.exceptions.BubbledocsException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.ExpiredSessionException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidCellException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.SpreadsheetNotFoundException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UnauthorizedUserException;


// add needed import declarations

public class AssignReferenceCell extends BubbledocsService {
	
	private String _userToken;
    private Integer _spreadsheetId;
    private Integer _cellIdLine;
    private Integer _cellIdColumn;
    private Integer _cellReferenceLine;
    private Integer _cellReferenceColumn;
    
    Random rand = new Random();
    
    /**
     * Assigns a reference to a cell
     * @param string with a user token
     * @param integer with the spreadsheet Id
     * @param integer with the cell Id Line
     * @param integer with the cell Id Column
     * @param integer with the cell Reference Line
     * @param integer with the cell Reference Column
     */
    public AssignReferenceCell(String tokenUser, Integer spreadsheetId, Integer cellIdLine, Integer cellIdColumn,
            Integer cellReferenceLine, Integer cellReferenceColumn) {
    	
    	_userToken = tokenUser;
    	_spreadsheetId = spreadsheetId;
    	_cellIdLine = cellIdLine;
    	_cellIdColumn = cellIdColumn;
    	_cellReferenceLine = cellReferenceLine;
    	_cellReferenceColumn = cellReferenceColumn;
    }

    @Override
    protected void dispatch() throws BubbledocsException {
    	
        Bubbledocs bubble = Bubbledocs.getBubbledocs();
        Session session;
        Permission userPermission;
        
        try{
        	session = bubble.getSessionByToken(_userToken);
        	
        	//TODO handle Exceptions
        	
        	/*
        	if(bubble.checkSessionExpired(session)){
        		throw new ExpiredSessionException("User session has expired.");
        	}
        	
        	userPermission = bubble.getPermission(session.get_username(), _spreadsheetId);
        	if(!userPermission.get_writePermission()){
        		throw new UnauthorizedUserException(" Assign reference to CellUser doesnt havepermission to write in Spreadsheet");
        	}
        	*/
        	Spreadsheet spreadsheet = bubble.getSpreadsheetById(_spreadsheetId);
        	spreadsheet.getCell(_cellIdLine, _cellIdColumn).setContent(spreadsheet.getCell(_cellReferenceLine, _cellReferenceColumn).getContent());

		} catch (InvalidCellException | SpreadsheetNotFoundException e) {
		}
    }

    public final String getUserToken() {
    	return _userToken;
    }
}
