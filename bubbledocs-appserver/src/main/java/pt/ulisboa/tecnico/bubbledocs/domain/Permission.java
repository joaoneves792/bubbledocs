package pt.ulisboa.tecnico.bubbledocs.domain;

public class Permission extends Permission_Base {
    
    public Permission() {
        super();
    }
        
    public Permission(Spreadsheet spreadsheet, User user, boolean writePermission) {
    	super();
    	init(spreadsheet, user, writePermission);
    }

	protected void init(Spreadsheet spreadsheet, User user, boolean writePermission) {
		setSpreadsheet(spreadsheet);
    	setUser(user);
    	setWritePermission(writePermission);
	}

    public void clean(){
    	setSpreadsheet(null);
    	setUser(null);
        super.deleteDomainObject();
    }
}
