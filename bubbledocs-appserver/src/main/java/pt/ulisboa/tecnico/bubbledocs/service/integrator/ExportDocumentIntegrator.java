package pt.ulisboa.tecnico.bubbledocs.service.integrator;

import pt.ulisboa.tecnico.bubbledocs.domain.Bubbledocs;
import pt.ulisboa.tecnico.bubbledocs.exceptions.BubbledocsException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.RemoteInvocationException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UnavailableServiceException;
import pt.ulisboa.tecnico.bubbledocs.service.ExportDocument;
import pt.ulisboa.tecnico.bubbledocs.service.GetUserNameForToken;
import pt.ulisboa.tecnico.bubbledocs.service.remote.StoreRemoteServices;

	public class ExportDocumentIntegrator extends BubbledocsIntegrator {

		private int docId;
		private String docXML;
	    
	    public ExportDocumentIntegrator(String userTok, int ssId) {
	    	userToken=userTok;
	    	docId=ssId;
	    }
	    
	    @Override
	    protected void dispatch() throws BubbledocsException {
	    	
	        StoreRemoteServices sdStore = new StoreRemoteServices();
	        ExportDocument service = new ExportDocument(userToken, docId);
	        GetUserNameForToken getUsernameService = new GetUserNameForToken(userToken);
	        getUsernameService.execute();
	        
	        String ssName = Bubbledocs.getBubbledocs().getSpreadsheetById(docId).getName();
	        
	        try {
	        	service.execute();
	        	docXML = service.getDocXML();
	        	sdStore.storeDocument(getUsernameService.getUsername(), ssName, docXML.getBytes());
	        } catch (RemoteInvocationException e) {
	        	throw new UnavailableServiceException("SD-Store offline.");
	        }
	    }
	    
	    public final String getDocXML() {
		    return docXML;
	    }
	}