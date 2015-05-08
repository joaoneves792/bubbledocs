package pt.ulisboa.tecnico.bubbledocs.service;

import pt.ulisboa.tecnico.bubbledocs.exceptions.BubbledocsException;

public class ImportDocumentService extends BubbledocsService {

	private final String spreadsheetXML;
	
	public ImportDocumentService(String token, String ssXML) {
		userToken      = token;
		spreadsheetXML = ssXML;
	}
	
	@Override
	protected void dispatch() throws BubbledocsException {
		// TODO Auto-generated method stub

	}

}
