package pt.ulisboa.tecnico.bubbledocs.service;

import pt.ulisboa.tecnico.bubbledocs.exceptions.BubbledocsException;

// add needed import declarations

public class ExportDocument extends BubbledocsService {
	
	private String _userToken;
	private String _docId;
	
    private byte[] docXML;

    public final byte[] getDocXML() {
	return docXML;
    }

    public ExportDocument(String userToken, int docId) {
    	_userToken = userToken;
    	_docId = docId;
    }

    @Override
    protected void dispatch() throws BubbledocsException {
    	
    	Bubbledocs bubble;       
        bubble = Bubbledocs.getBubbledocs();
        //TODO try catch block and handle exceptions
        
       //try { 
        
        	this.setDocXML(bubble.exportSpreadsheetById(_userToken, _docID));
        
       // } catch () {}
    }
}
