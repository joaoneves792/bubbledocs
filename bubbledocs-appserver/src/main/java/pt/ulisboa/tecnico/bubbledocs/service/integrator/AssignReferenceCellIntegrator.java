package pt.ulisboa.tecnico.bubbledocs.service.integrator;

import pt.ulisboa.tecnico.bubbledocs.exceptions.BubbledocsException;
import pt.ulisboa.tecnico.bubbledocs.service.AssignReferenceCell;

public class AssignReferenceCellIntegrator extends BubbledocsIntegrator {
	
	 	private Integer spreadsheetId;
	    private String myCellId;
	    private String referencedCell;
	    private AssignReferenceCell service;
	    
	    /*
	     * Assigns a reference to a cell
	     * @param string with a user token
	     * @param integer with the spreadsheet Id
	     * @param string with cellId
	     * @param string with cellReference
	     */
	    public AssignReferenceCellIntegrator(String tokenUser, int ssId, String cellID, String cellRef) {
	    	userToken = tokenUser;
	    	spreadsheetId = ssId;
	    	myCellId = cellID;
	    	referencedCell = cellRef;
	    	service = new AssignReferenceCell(userToken, spreadsheetId, myCellId, referencedCell);
	    }
	@Override
    protected void dispatch() throws BubbledocsException, NumberFormatException {
		service.execute();
	}
	
	public final String getResult() throws BubbledocsException {
		Integer n = service.getResult();
		if(n == null)
			return service.INVALID;
		else return n.toString();
	}

}
