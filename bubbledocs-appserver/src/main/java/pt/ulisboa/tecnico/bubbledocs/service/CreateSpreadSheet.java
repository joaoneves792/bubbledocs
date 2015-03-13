package pt.ulisboa.tecnico.bubbledocs.service;

import pt.ulisboa.tecnico.bubbledocs.exceptions.BubbledocsException;

// add needed import declarations

public class CreateSpreadSheet extends BubbledocsService {
    private int sheetId;  // id of the new sheet

    public int getSheetId() {
        return sheetId;
    }

    public CreateSpreadSheet(String userToken, String name, int rows,
            int columns) {
	// add code here
    }

    @Override
    protected void dispatch() throws BubbledocsException {
	// add code here
    }

}
