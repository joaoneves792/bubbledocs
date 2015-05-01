package pt.ulisboa.tecnico.bubbledocs.domain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.JDOMException;

import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidCellException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidImportException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.PermissionNotFoundException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.SpreadsheetNotFoundException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UnauthorizedUserException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.UserNotFoundException;

public class User extends User_Base {
    
	public User() {
        super();
    }
	
    public User(String name, String username, String email) {
    	super();
    	init(name, username, email);
    }
    
    protected void init(String name, String username, String email) {
    	setName(name);
    	setEmail(email);
    	setUsername(username);  
    	setPasswd(null);
    }
    
    public void addReadPermission(String granted, int spreadsheetId) throws UserNotFoundException, UnauthorizedUserException, SpreadsheetNotFoundException, PermissionNotFoundException {
    	Bubbledocs.getBubbledocs().addReadPermission(getUsername(), granted, spreadsheetId);
    }
    
    public void addWritePermission(String granted, int spreadsheetId) throws UnauthorizedUserException, SpreadsheetNotFoundException, UserNotFoundException, PermissionNotFoundException {
    	Bubbledocs.getBubbledocs().addWritePermission(getUsername(), granted, spreadsheetId);
    }
    
    public void revokeReadPermission(String granted, int spreadsheetId) throws UnauthorizedUserException, SpreadsheetNotFoundException, UserNotFoundException, PermissionNotFoundException {
    	Bubbledocs.getBubbledocs().revokeReadPermission(getUsername(), granted, spreadsheetId);
    }
    
    public void revokeWritePermission(String granted, int spreadsheetId) throws UnauthorizedUserException, SpreadsheetNotFoundException, UserNotFoundException, PermissionNotFoundException {
    	Bubbledocs.getBubbledocs().revokeWritePermission(getUsername(), granted, spreadsheetId);
    }
    
    public Spreadsheet createSpreadsheet(String name, int lines, int columns) {
    	return Bubbledocs.getBubbledocs().createSpreadsheet(this, name, lines, columns);
    }
    
    public void deleteSpreadsheet(int spreadsheetId) throws UnauthorizedUserException, SpreadsheetNotFoundException {
    	Bubbledocs.getBubbledocs().deleteSpreadsheet(getUsername(), spreadsheetId);
    }
    
    public void protectSpreadsheetCell(int spreadSheetId, int line, int column) throws UnauthorizedUserException, SpreadsheetNotFoundException, InvalidCellException, PermissionNotFoundException, UserNotFoundException {
    	Bubbledocs.getBubbledocs().protectSpreadsheetCell(getUsername(), spreadSheetId, line, column);
    }
    
    public void unProtectSpreadsheetCell(int spreadSheetId, int line, int column) throws UnauthorizedUserException, SpreadsheetNotFoundException, InvalidCellException, PermissionNotFoundException, UserNotFoundException {
    	Bubbledocs.getBubbledocs().unProtectSpreadsheetCell(getUsername(), spreadSheetId, line, column);
    }

	public List<Spreadsheet> findSpreadsheetsByName(String str) {
		List <Spreadsheet> mySpreadsheets = Bubbledocs.getBubbledocs().getSpreadsheetsByAuthor(getUsername());
		List <Spreadsheet> mySpreadsheetsWithThisName = new ArrayList<Spreadsheet>();
		for(Spreadsheet s : mySpreadsheets) {
			if(s.getName().equals(str)) {
				mySpreadsheetsWithThisName.add(s);
			}
		}
		return mySpreadsheetsWithThisName;
	}
	
	@Override
	public String toString() {
		return "<< USERNAME: " + getUsername() + " || NAME: " + getName() + " || PASSWORD: " + getPasswd() + " >>";
	}

	public void createSpreadsheet(String ss) throws InvalidImportException, JDOMException, IOException, UserNotFoundException, InvalidCellException {
		Bubbledocs.getBubbledocs().createSpreadsheet(this, ss);		
	}

}
