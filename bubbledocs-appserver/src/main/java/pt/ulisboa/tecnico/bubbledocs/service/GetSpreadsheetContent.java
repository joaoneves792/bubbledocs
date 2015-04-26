package pt.ulisboa.tecnico.bubbledocs.service;

import pt.ulisboa.tecnico.bubbledocs.domain.Bubbledocs;
import pt.ulisboa.tecnico.bubbledocs.exceptions.BubbledocsException;

public class GetSpreadsheetContent extends BubbledocsService {

	private String result;
	private final int spreadsheetID;
	
	public GetSpreadsheetContent(String userToken, int spreadsheetID) {
		this.userToken = userToken;
		this.spreadsheetID = spreadsheetID;
		result = "";
	}
	
	@Override
	protected void dispatch() throws BubbledocsException {
		result = Bubbledocs.getBubbledocs().getSpreadsheetById(spreadsheetID).toString();
	}
	
	public String getResult() {
		return result;
	}
}
