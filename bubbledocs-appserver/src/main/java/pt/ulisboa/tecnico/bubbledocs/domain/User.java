package pt.ulisboa.tecnico.bubbledocs.domain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.JDOMException;

import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidCellException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidImportException;
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
    
    public Spreadsheet createSpreadsheet(String name, int lines, int columns) {
    	return Bubbledocs.getBubbledocs().createSpreadsheet(this, name, lines, columns);
    }
    
    public void deleteSpreadsheet(int spreadsheetId) throws UnauthorizedUserException, SpreadsheetNotFoundException {
    	Bubbledocs.getBubbledocs().deleteSpreadsheet(this, spreadsheetId);
    }
    
    public void protectSpreadsheetCell(int spreadSheetId, int line, int column) throws UnauthorizedUserException, SpreadsheetNotFoundException, InvalidCellException {
    	Bubbledocs.getBubbledocs().protectSpreadsheetCell(this, spreadSheetId, line, column);
    }
    
    public void unProtectSpreadsheetCell(int spreadSheetId, int line, int column) throws UnauthorizedUserException, SpreadsheetNotFoundException, InvalidCellException {
    	Bubbledocs.getBubbledocs().unProtectSpreadsheetCell(this, spreadSheetId, line, column);
    }

	public List<Spreadsheet> findSpreadsheetsByName(String str) {
		List <Spreadsheet> mySpreadsheets = Bubbledocs.getBubbledocs().getSpreadsheetsByAuthor(get_username());
		List <Spreadsheet> mySpreadsheetsWithThisName = new ArrayList<Spreadsheet>();
		for(Spreadsheet s : mySpreadsheets) {
			if(s.get_name().equals(str)) {
				mySpreadsheetsWithThisName.add(s);
			}
		}
		return mySpreadsheetsWithThisName;
	}
	
	@Override
	public String toString() {
		return "<< USERNAME: " + get_username() + " || NAME: " + get_name() + " || PASSWORD: " + get_passwd() + " >>";
	}

	public void createSpreadsheet(String ss) throws InvalidImportException, JDOMException, IOException {
		Bubbledocs.getBubbledocs().createSpreadsheet(this, ss);		
	}
}
