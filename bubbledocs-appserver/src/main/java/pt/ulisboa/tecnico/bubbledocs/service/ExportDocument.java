package pt.ulisboa.tecnico.bubbledocs.service;

import pt.ulisboa.tecnico.bubbledocs.exceptions.BubbledocsException;

// add needed import declarations

public class ExportDocument extends BubbledocsService {
    private byte[] docXML;

    public byte[] getDocXML() {
	return docXML;
    }

    public ExportDocument(String userToken, int docId) {
	// add code here
    }

    @Override
    protected void dispatch() throws BubbledocsException {
	// add code here
    }
}
