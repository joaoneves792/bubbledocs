package pt.ulisboa.tecnico.bubbledocs.service;

import pt.ulisboa.tecnico.bubbledocs.domain.Bubbledocs;
import pt.ulisboa.tecnico.bubbledocs.exceptions.BubbledocsException;

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
        docXML = bubble.exportDocument(userToken, docId);
        
    }
    
}
