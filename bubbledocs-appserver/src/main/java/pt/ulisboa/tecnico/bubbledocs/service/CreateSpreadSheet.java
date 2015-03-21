package pt.ulisboa.tecnico.bubbledocs.service;

import pt.ulisboa.tecnico.bubbledocs.domain.Bubbledocs;
import pt.ulisboa.tecnico.bubbledocs.exceptions.BubbledocsException;

// add needed import declarations

public class CreateSpreadSheet extends BubbledocsService {
    private int sheetId;  // id of the new sheet
    private int rows;
    private int columns;
    private String name;
    private String userToken;

    public int getSheetId() {
        return sheetId;
    }

    public CreateSpreadSheet(String userTok, String ssName, int ssRows, int ssColumns) {
        rows = ssRows;
        columns = ssColumns;
        name = ssName;
        userToken = userTok;
    }

    @Override
    protected void dispatch() throws BubbledocsException {
        Bubbledocs bubble;
        
        bubble = Bubbledocs.getBubbledocs();
        sheetId = bubble.createSpreadsheet(userToken, name, rows, columns);

    }

}
