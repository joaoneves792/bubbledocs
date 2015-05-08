package pt.ulisboa.tecnico.bubbledocs.service.integrator;

import pt.ulisboa.tecnico.bubbledocs.exceptions.BubbledocsException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidImportException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.RemoteInvocationException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UnauthorizedUserException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UnavailableServiceException;
import pt.ulisboa.tecnico.bubbledocs.service.GetUserNameForToken;
import pt.ulisboa.tecnico.bubbledocs.service.ImportDocumentService;
import pt.ulisboa.tecnico.bubbledocs.service.remote.StoreRemoteServices;

public class ImportDocumentIntegrator extends BubbledocsIntegrator {
	
	private final String docName;
	private String spreadsheetXML;

	public ImportDocumentIntegrator(String token, String doc) {
		userToken = token;
		docName   = doc;
	}
	
	
	@Override
	protected void dispatch() throws BubbledocsException {
		StoreRemoteServices SDStore = new StoreRemoteServices();
		GetUserNameForToken guft    = new GetUserNameForToken(userToken);
		guft.execute();
		
		String username = guft.getUsername();
		
		try {
			byte[] ary = SDStore.loadDocument(username, docName);
			if(ary == null) throw new InvalidImportException("");
			else spreadsheetXML = new String(ary);
		} catch(RemoteInvocationException e) {
			throw new UnavailableServiceException("SDStore Offline");
		}
		
		ImportDocumentService localImport = new ImportDocumentService(userToken, spreadsheetXML);
		localImport.execute();
	}
}
