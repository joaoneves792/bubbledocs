package pt.ulisboa.tecnico.bubbledocs.service;

import pt.ulisboa.tecnico.bubbledocs.exceptions.BubbledocsException;

// add needed import declarations

public class AssignReferenceCell extends BubbledocsService {
    private String result;

    public AssignReferenceCell(String tokenUser, int docId, String cellId,
            String reference) {
	// add code here
    }

    @Override
    protected void dispatch() throws BubbledocsException {
	// add code here
    }

    public final String getResult() {
        return result;
    }
}
