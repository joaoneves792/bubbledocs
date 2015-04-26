package pt.ulisboa.tecnico.bubbledocs.service.integrator;

import pt.ulisboa.tecnico.bubbledocs.exceptions.BubbledocsException;
import pt.ulisboa.tecnico.bubbledocs.service.AssignReferenceCell;

public class AssignReferenceCellIntegrator extends BubbledocsIntegrator {
	
	 	private Integer spreadsheetId;
	    private String myCellId;
	    private String referencedCell;
	    private Integer result;
	    
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
	    }
	@Override
    protected void dispatch() throws BubbledocsException, NumberFormatException {
		
		AssignReferenceCell localService = new AssignReferenceCell(userToken, spreadsheetId, myCellId, referencedCell); 
		
		localService.execute();
		
		result = localService.getResult();
		
	}
	
	public final Integer getResult() {
		return result;
	}

}
