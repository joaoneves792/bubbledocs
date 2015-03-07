package pt.ulisboa.tecnico.bubbledocs.domain;

public class Permission extends Permission_Base {
    
    public Permission() {
        super();
    }
        
    public Permission(int spreadsheetId, String username, boolean writePermission) {
    	super();
    	init(spreadsheetId, username, writePermission);
    }

	protected void init(int spreadsheetId, String username,	boolean writePermission) {
		set_spreadsheetId(spreadsheetId);
    	set_username(username);
    	set_writePermission(writePermission);
	}
}
