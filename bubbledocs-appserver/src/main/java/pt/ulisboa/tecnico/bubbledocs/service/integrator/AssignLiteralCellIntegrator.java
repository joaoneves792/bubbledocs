package pt.ulisboa.tecnico.bubbledocs.service.integrator;

import pt.ulisboa.tecnico.bubbledocs.exceptions.BubbledocsException;
import pt.ulisboa.tecnico.bubbledocs.service.AssignLiteralCell;

public class AssignLiteralCellIntegrator extends BubbledocsIntegrator {

		private int spreadsheetId;
	    private String cellId;
	    private String literal;
	    private Integer result;

	    public AssignLiteralCellIntegrator(String userTok, int spreadSheetId, String cell, String lit) {
	    	cellId = cell;
	    	userToken = userTok;
	    	spreadsheetId = spreadSheetId;
	    	literal = lit;
	    }
	    
	    @Override
	    protected void dispatch() throws BubbledocsException {
	    	
	        AssignLiteralCell service = new AssignLiteralCell(userToken, spreadsheetId, cellId, literal);
	        service.execute();
	        result = service.getResult();
	    }
	    
	    public Integer getResult() {
	        return result;
	    }
	    
	}