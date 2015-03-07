package pt.ulisboa.tecnico.bubbledocs.domain;

import pt.ulisboa.tecnico.bubbledocs.exceptions.SpreadsheetNotFoundException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UnauthorizedUserException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UserNotFoundException;

public class User extends User_Base {
    
	public User() {
        super();
    }
	
    public User(String name, String username, String passwd) {
        super();
        init(name, username, passwd);
    }
    
    protected void init(String name, String username, String passwd) {
    	set_name(name);
    	set_passwd(passwd);
    	set_username(username);    	
    }
    
    public void addReadPermission(String granted, int spreadsheetId) throws UserNotFoundException, UnauthorizedUserException, SpreadsheetNotFoundException {
    	Bubbledocs.getBubbledocs().addReadPermission(this, granted, spreadsheetId);
    }
    
    public void addWritePermission(String granted, int spreadsheetId) throws UnauthorizedUserException, SpreadsheetNotFoundException, UserNotFoundException {
    	Bubbledocs.getBubbledocs().addWritePermission(this, granted, spreadsheetId);
    }
    
    public void revokeReadPermission(String granted, int spreadsheetId) throws UnauthorizedUserException, SpreadsheetNotFoundException, UserNotFoundException {
    	Bubbledocs.getBubbledocs().revokeReadPermission(this, granted, spreadsheetId);
    }
    
    public void revokeWritePermission(String granted, int spreadsheetId) throws UnauthorizedUserException, SpreadsheetNotFoundException, UserNotFoundException {
    	Bubbledocs.getBubbledocs().revokeWritePermission(this, granted, spreadsheetId);
    }
    
    public Spreadsheet createSpreadsheet(String author, String name, int lines, int columns) {
    	return Bubbledocs.getBubbledocs().createSpreadsheet(this, name, lines, columns);
    }
    
    public void deleteSpreadsheet(int spreadsheetId) throws UnauthorizedUserException, SpreadsheetNotFoundException {
    	Bubbledocs.getBubbledocs().deleteSpreadsheet(this, spreadsheetId);
    }
    
    public void protectSpreadsheetCell(int spreadSheetId, int line, int column) throws UnauthorizedUserException, SpreadsheetNotFoundException {
    	Bubbledocs.getBubbledocs().protectSpreadsheetCell(this, spreadSheetId, line, column);
    }
    
    public void unProtectSpreadsheetCell(int spreadSheetId, int line, int column) throws UnauthorizedUserException, SpreadsheetNotFoundException {
    	Bubbledocs.getBubbledocs().unProtectSpreadsheetCell(this, spreadSheetId, line, column);
    }
}
