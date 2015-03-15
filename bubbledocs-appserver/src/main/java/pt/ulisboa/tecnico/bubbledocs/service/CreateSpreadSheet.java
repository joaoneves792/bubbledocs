package pt.ulisboa.tecnico.bubbledocs.service;

import pt.ulisboa.tecnico.bubbledocs.domain.Bubbledocs;
import pt.ulisboa.tecnico.bubbledocs.exceptions.BubbledocsException;

// add needed import declarations

public class CreateSpreadSheet extends BubbledocsService {
    private int _sheetId;  // id of the new sheet
    private int _rows;
    private int _columns;
    private String _name;
    private String _userToken;

    public int getSheetId() {
        return _sheetId;
    }

    public CreateSpreadSheet(String userToken, String name, int rows, int columns) {
        _rows = rows;
        _columns = columns;
        _name = name;
        _userToken = userToken;
    }

    @Override
    protected void dispatch() throws BubbledocsException {
        Bubbledocs bubble;
        
        bubble = Bubbledocs.getBubbledocs();
        _sheetId = bubble.createSpreadsheet(_userToken, _name, _rows, _columns);

    }

}
