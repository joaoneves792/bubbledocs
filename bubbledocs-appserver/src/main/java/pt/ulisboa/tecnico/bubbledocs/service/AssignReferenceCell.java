package pt.ulisboa.tecnico.bubbledocs.service;

import pt.ulisboa.tecnico.bubbledocs.domain.Bubbledocs;
import pt.ulisboa.tecnico.bubbledocs.exceptions.BubbledocsException;

// add needed import declarations

public class AssignReferenceCell extends BubbledocsService {
	
	private String _userToken;
    private Integer _spreadsheetId;
    private String _cellId;
    private String _cellReference;
    private Integer _result;
    
    /*
     * Assigns a reference to a cell
     * @param string with a user token
     * @param integer with the spreadsheet Id
     * @param string with cellId
     * @param string with cellReference
     */
    public AssignReferenceCell(String tokenUser, int spreadsheetId, String cellId, String cellReference) {

    	setUserToken(tokenUser);
    	setSpreadsheetId(spreadsheetId);
    	setCellId(cellId);
    	setCellReference(cellReference);

    }

    @Override
    protected void dispatch() throws BubbledocsException {
   	   	Bubbledocs bubble = Bubbledocs.getBubbledocs();
    	_result = bubble.AssignReferenceCell(_userToken, _spreadsheetId, _cellId,_cellReference);
    }

    //Gets
    
    public final String getUserToken() {
    	return _userToken;
    }
    
    public Integer getResult() {
        return _result;
    }
    
    public Integer getSpreadsheetId() {
        return _spreadsheetId;
    }
    
    public String getCellId() {
        return _cellId;
    }
    
    public String getCellReference() {
        return _cellReference;
    }

    //Sets
    
    public void setUserToken(String userToken) {
    	this._userToken = userToken;
    }
    
    public void setResult(Integer result) {
    	this._result = result;
    }
    
    public void setSpreadsheetId(Integer  spreadsheetId) {
    	this._spreadsheetId = spreadsheetId;
    }
    
    public void setCellId(String cellId) {
    	this._cellId = cellId;
    }

    public void setCellReference(String cellReference) {
    	this._cellReference = cellReference;
    }
    
}
