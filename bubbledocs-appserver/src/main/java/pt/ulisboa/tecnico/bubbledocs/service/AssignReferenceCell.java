package pt.ulisboa.tecnico.bubbledocs.service;

import pt.ulisboa.tecnico.bubbledocs.domain.Bubbledocs;
import pt.ulisboa.tecnico.bubbledocs.exceptions.BubbledocsException;

// add needed import declarations

public class AssignReferenceCell extends BubbledocsService {
	
	private String _userToken;
    private Integer _spreadsheetId;
    private Integer _cellIdLine;
    private Integer _cellIdColumn;
    private Integer _cellReferenceLine;
    private Integer _cellReferenceColumn;
    private Integer _result;
    
    /*
     * Assigns a reference to a cell
     * @param string with a user token
     * @param integer with the spreadsheet Id
     * @param integer with the cell Id Line
     * @param integer with the cell Id Column
     * @param integer with the cell Reference Line
     * @param integer with the cell Reference Column
     */
    public AssignReferenceCell(String tokenUser, int spreadsheetId, String cellId, String cellReference) {
    	
    	String delims = "[;]";
    	String[] tokensCellId= cellId.split(delims);
    	String[] tokensCellReference = cellReference.split(delims);
    	Integer cellIdLine = Integer.parseInt(tokensCellId[0]);
    	Integer cellIdColumn = Integer.parseInt(tokensCellId[1]);
    	Integer cellReferenceLine = Integer.parseInt(tokensCellReference[0]);
    	Integer cellReferenceColumn = Integer.parseInt(tokensCellReference[1]);
    	
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
    	_result = bubble.AssignReferenceCell(_userToken, _spreadsheetId, _cellIdLine, _cellIdColumn, _cellReferenceLine, _cellReferenceColumn);
        
    }

    public final String getUserToken() {
    	return _userToken;
    }
    
    public Integer getResult() {
        return _result;
    }
    
}
