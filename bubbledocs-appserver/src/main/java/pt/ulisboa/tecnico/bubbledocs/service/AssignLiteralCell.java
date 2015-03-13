package pt.ulisboa.tecnico.bubbledocs.service;

import pt.ulisboa.tecnico.bubbledocs.exceptions.BubbledocsException;

// add needed import declarations

public class AssignLiteralCell extends BubbledocsService {
    private String result;

    public AssignLiteralCell(String accessUsername, int docId, String cellId,
            String literal) {
	// add code here	
    }

    @Override
    protected void dispatch() throws BubbledocsException {
	// add code here
    }

    public String getResult() {
        return result;
    }

}
