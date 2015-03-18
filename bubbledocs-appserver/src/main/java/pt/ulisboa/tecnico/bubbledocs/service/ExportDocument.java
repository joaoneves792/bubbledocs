package pt.ulisboa.tecnico.bubbledocs.service;

import pt.ulisboa.tecnico.bubbledocs.domain.Bubbledocs;
import pt.ulisboa.tecnico.bubbledocs.exceptions.BubbledocsException;

// add needed import declarations

public class ExportDocument extends BubbledocsService {
	
	private String userToken;
	private int docId;
	private String docXML;

    public final String getDocXML() {
	return docXML;
    }

    public ExportDocument(String userToken, int docId) {
    	userToken=userToken;
    	docId=docId;
    }

    @Override
    protected void dispatch() throws BubbledocsException {
        Bubbledocs bubble = Bubbledocs.getBubbledocs();
        this.docXML = bubble.exportDocument(userToken, docId);
    }
    
    public String getXMLString() {
    	return docXML;
    }
}
