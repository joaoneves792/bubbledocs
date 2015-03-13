package pt.ulisboa.tecnico.bubbledocs.service;

// add needed import declarations

public class ExportDocument extends BubbleDocsService {
    private byte[] docXML;

    public byte[] getDocXML() {
	return docXML;
    }

    public ExportDocument(String userToken, int docId) {
	// add code here
    }

    @Override
    protected void dispatch() throws BubbleDocsException {
	// add code here
    }
}
