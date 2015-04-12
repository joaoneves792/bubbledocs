package pt.ulisboa.tecnico.bubbledocs.service;

import pt.ulisboa.tecnico.bubbledocs.domain.Bubbledocs;
import pt.ulisboa.tecnico.bubbledocs.exceptions.BubbledocsException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.RemoteInvocationException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UnavailableServiceException;
import pt.ulisboa.tecnico.bubbledocs.service.remote.StoreRemoteServices;

// add needed import declarations

public class ExportDocument extends BubbledocsService {
	
	private int docId;
	private String docXML;

    public final String getDocXML() {
	    return docXML;
    }

    public ExportDocument(String userTok, int ssId) {
    	userToken=userTok;
    	docId=ssId;
    }

    @Override
    protected void dispatch() throws BubbledocsException {
        Bubbledocs bubble = Bubbledocs.getBubbledocs();
        
        StoreRemoteServices sdStore = new StoreRemoteServices();
        
        docXML = bubble.exportDocument(userToken, docId);
        String ssName = Bubbledocs.getBubbledocs().getSpreadsheetById(docId).getName();
        try {
        	sdStore.storeDocument(userToken.split("\\d")[0], ssName, docXML.getBytes());
        } catch (RemoteInvocationException e) {
        	throw new UnavailableServiceException("SD-Store offline.");
        }
    }
    
}
