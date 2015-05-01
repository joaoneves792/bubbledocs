package pt.ulisboa.tecnico.bubbledocs.service.integrator;

import pt.ulisboa.tecnico.bubbledocs.exceptions.BubbledocsException;
import pt.ulisboa.tecnico.bubbledocs.service.AssignRangeFunctionToCell;

public class AssignRangeFunctionToCellIntegrator extends BubbledocsIntegrator {

    private Integer spreadsheetId;
    private String cellID;
    private String functionExpression;
    private Integer result;

	public AssignRangeFunctionToCellIntegrator(String tokenUser, int ssId, String cellId, String funcExpr) {
    	userToken = tokenUser;
    	spreadsheetId = ssId;
    	cellID = cellId;
    	functionExpression = funcExpr;
    }
	    
	@Override
	protected void dispatch() throws BubbledocsException {
		AssignRangeFunctionToCell service = new AssignRangeFunctionToCell(userToken, spreadsheetId, cellID, functionExpression);
		service.execute();
		result = service.getResult();
	}
	    
	public Integer getResult() {
	    return result;
	}	   
}