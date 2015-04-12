package pt.ulisboa.tecnico.bubbledocs.service;

import pt.ulisboa.tecnico.bubbledocs.domain.Bubbledocs;
import pt.ulisboa.tecnico.bubbledocs.exceptions.BubbledocsException;

public class AssignLiteralCell extends BubbledocsService {

    private int spreadSheetId;
    private Integer cellIdLine;
    private Integer cellIdColumn;
    private Integer literal;
    private Integer result;

    public AssignLiteralCell(String userToken, int spreadSheetId, String cellId, String literal) {
    	String delims = "[;]";
    	String[] tokensCellId= cellId.split(delims);
    	this.setCellIdLine(Integer.parseInt(tokensCellId[0]));
    	this.setCellIdColumn(Integer.parseInt(tokensCellId[1]));
    	this.setUserToken(userToken);
    	this.setSpreadSheetId(spreadSheetId);
    	this.setLiteral(Integer.parseInt(literal));
    }

    @Override
    protected void dispatch() throws BubbledocsException {
    	Bubbledocs bubble = Bubbledocs.getBubbledocs();
    	result = bubble.AssignLiteralCell(userToken, spreadSheetId, cellIdLine, cellIdColumn, literal);        
    }

	public String getUserToken() {
		return userToken;
	}

	public void setUserToken(String userToken) {
		this.userToken = userToken;
	}

	public int getSpreadSheetId() {
		return spreadSheetId;
	}

	public void setSpreadSheetId(int spreadSheetId) {
		this.spreadSheetId = spreadSheetId;
	}

	public Integer getCellIdLine() {
		return cellIdLine;
	}

	public void setCellIdLine(int cellIdLine) {
		this.cellIdLine = cellIdLine;
	}

	public Integer getCellIdColumn() {
		return cellIdColumn;
	}

	public void setCellIdColumn(int cellIdColumn) {
		this.cellIdColumn = cellIdColumn;
	}

	public Integer getLiteral() {
		return literal;
	}

	public void setLiteral(Integer literal) {
		this.literal = literal;
	}

    public Integer getResult() {
        return result;
    }
}
