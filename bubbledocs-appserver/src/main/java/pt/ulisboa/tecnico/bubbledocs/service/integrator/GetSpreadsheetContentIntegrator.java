package pt.ulisboa.tecnico.bubbledocs.service.integrator;

import pt.ulisboa.tecnico.bubbledocs.exceptions.BubbledocsException;
import pt.ulisboa.tecnico.bubbledocs.service.GetSpreadsheetContent;

public class GetSpreadsheetContentIntegrator extends BubbledocsIntegrator {

	private final GetSpreadsheetContent service;
	
	public GetSpreadsheetContentIntegrator(String userToken, int spreadsheetID) {
		this.userToken = userToken;
		
		service = new GetSpreadsheetContent(userToken, spreadsheetID);
	}
	
	@Override
	protected void dispatch() throws BubbledocsException {
		service.execute();
	}

	public String getResult() {
		return service.getResult();
	}
			
}
