package pt.ulisboa.tecnico.bubbledocs.service;

import java.io.IOException;

import org.jdom2.JDOMException;

import pt.ulisboa.tecnico.bubbledocs.domain.Bubbledocs;
import pt.ulisboa.tecnico.bubbledocs.domain.Spreadsheet;
import pt.ulisboa.tecnico.bubbledocs.exceptions.BubbledocsException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidImportException;

public class ImportDocumentService extends BubbledocsService {

	private final String XMLString;
	private Spreadsheet spreadsheet;
	
	public ImportDocumentService(String token, String spreadsheetXML) {
		userToken = token;
		XMLString = spreadsheetXML;
	}
	
	@Override
	protected void dispatch() throws BubbledocsException {
		Bubbledocs bubble = Bubbledocs.getBubbledocs();
		GetUserNameForToken guft = new GetUserNameForToken(userToken);
		guft.execute();
		String username = guft.getUsername();
		try {
			spreadsheet = bubble.createSpreadsheet(bubble.getUserByUsername(username), XMLString);
		} catch (IOException e) {
			throw new InvalidImportException("IO : Failed to import spreadsheet.");
		} catch (JDOMException e) {
			throw new InvalidImportException("JDOM : Failed to import spreadsheet.");
		}
	}

	public Spreadsheet getSpreadsheet() {
		return spreadsheet;
	}
	
}
