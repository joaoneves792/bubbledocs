package pt.ulisboa.tecnico.bubbledocs.service.integrator;

import pt.ulisboa.tecnico.bubbledocs.exceptions.BubbledocsException;
import pt.ulisboa.tecnico.bubbledocs.service.AssignBinaryFunctionToCell;

public class AssignBinaryFunctionToCellIntegrator extends BubbledocsIntegrator {

    private Integer spreadsheetId;
    private String cellID;
    private String functionExpression;
    private AssignBinaryFunctionToCell service;

	public AssignBinaryFunctionToCellIntegrator(String tokenUser, int ssId, String cellId, String funcExpr) {
    	userToken = tokenUser;
    	spreadsheetId = ssId;
    	cellID = cellId;
    	functionExpression = funcExpr;
    	
    	service = new AssignBinaryFunctionToCell(userToken, spreadsheetId, cellID, functionExpression);

    }
	    
	@Override
	protected void dispatch() throws BubbledocsException {
		service.execute();
	}
	    
	public String getResult() throws BubbledocsException {
		Integer n = service.getResult();
		if(n == null)
			return service.INVALID;
		else return n.toString();
	}	   
}