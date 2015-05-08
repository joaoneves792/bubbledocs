package pt.ulisboa.tecnico.bubbledocs.service.integrator;

import pt.ulisboa.tecnico.bubbledocs.exceptions.BubbledocsException;
import pt.ulisboa.tecnico.bubbledocs.service.CreateSpreadSheet;

public class CreateSpreadsheetIntegrator extends BubbledocsIntegrator {
	
	private final CreateSpreadSheet service;
	
	public CreateSpreadsheetIntegrator(String userToken, String ssName, int ssRows, int ssCols) {
		this.userToken = userToken;
		
		service = new CreateSpreadSheet(userToken, ssName, ssRows, ssCols);
	}
	
	@Override
	protected void dispatch() throws BubbledocsException {
		service.execute();
	}

	public int getSheetId() {
		return service.getSheetId();
	}
			
}
