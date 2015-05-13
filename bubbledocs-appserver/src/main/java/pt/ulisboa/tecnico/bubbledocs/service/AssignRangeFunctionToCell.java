package pt.ulisboa.tecnico.bubbledocs.service;

import java.util.regex.Pattern;

import pt.ulisboa.tecnico.bubbledocs.domain.Avg;
import pt.ulisboa.tecnico.bubbledocs.domain.Bubbledocs;
import pt.ulisboa.tecnico.bubbledocs.domain.Function;
import pt.ulisboa.tecnico.bubbledocs.domain.Prd;
import pt.ulisboa.tecnico.bubbledocs.domain.Reference;
import pt.ulisboa.tecnico.bubbledocs.domain.Spreadsheet;
import pt.ulisboa.tecnico.bubbledocs.exceptions.BubbledocsException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.CellDivisionByZeroException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidCellException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidFunctionException;


public class AssignRangeFunctionToCell extends BubbledocsService {
	
    private Integer spreadsheetId;
    private String cellID;
    private String functionExpression;
	private Integer cellLine = null;
	private Integer cellColumn = null;
    
    final static String ZERO = "0"; 
    final static String NEGATIVE = "-(?!0)\\d+";
    final static String POSITIVE = "(?!0)\\d+";

    final static String CELL = POSITIVE + ";" + POSITIVE; 
    final static String PARSE_CELL = ";"; 
    
    final static String RANGE = CELL + ":" + CELL;
    final static String PARSE_RANGE = ":"; 
    
    final static String RANGE_OPERATOR = "(AVG|PRD)";
    final static String RANGE_FUNCTION = "=" + RANGE_OPERATOR + "\\(" + RANGE + "\\)";
    final static String PARSE_RANGE_FUNCTION = "[=()]";

    
    public AssignRangeFunctionToCell(String tokenUser, int ssId, String cellId, String funcExpr) {
    	userToken = tokenUser;
    	spreadsheetId = ssId;
    	cellID = cellId;
    	functionExpression = funcExpr;
    }

    @Override
    protected void dispatch() throws BubbledocsException {
   	   	Bubbledocs bubble = Bubbledocs.getBubbledocs();
   	   	Function function;
   	   	
   	   	
   	   	//Parse the id for the target cell
	   	String[] cellTokens = cellID.split(PARSE_CELL);
	   	try{
	   		cellLine = Integer.parseInt(cellTokens[0]);
		   	cellColumn = Integer.parseInt(cellTokens[1]);
	   	}catch(NumberFormatException e){
	   		throw new InvalidCellException("Bad cell reference " + cellID);
	   	}
    		   	
	   	//Assert permissions and validness of the spreadsheet and target cell
	   	bubble.assertSessionAndWritePermission(userToken,spreadsheetId, cellLine, cellColumn);
   	   	Spreadsheet spreadsheet = bubble.getSpreadsheetById(spreadsheetId);
	   	
   	   	//Assert that the function expression is in an expected format
        if (!Pattern.matches(RANGE_FUNCTION, functionExpression)) 
            throw new InvalidFunctionException(functionExpression); 

	   	//Parse the function expression
   	   	String tokens[] = functionExpression.split(PARSE_RANGE_FUNCTION);
   	   	String functionType = tokens[1]; //CAREFULL because of "=" in the parse string the first token is an empty string!
   	   	String argument = tokens[2];
   	   	
   	   	String rangeReferences[] = argument.split(PARSE_RANGE);
   	   	
   	   	//Get the range start
   	   	Reference rangeStart = parseReference(spreadsheet, rangeReferences[0]);
   	   	//Get the range end
   	   	Reference rangeEnd = parseReference(spreadsheet, rangeReferences[1]);
	   	
   	   	//Create the appropriate function
    	switch (functionType){
			case "AVG":
				function = new Avg(rangeStart, rangeEnd);
				break;
			case "PRD":
				function = new Prd(rangeStart, rangeEnd);
				break;
			default :
				throw new InvalidFunctionException(functionExpression); //This should have been checked by the parsing
    	}
    	
    	//Assign it and get the value of this cell
    	spreadsheet.assignFunctionCell(cellLine, cellColumn, function);
    }

	private Reference parseReference(Spreadsheet spreadsheet, String refExpression) throws InvalidCellException {
		String[] cellTokens = refExpression.split(PARSE_CELL);
   		Integer referenceRow = Integer.parseInt(cellTokens[0]);
   		Integer referenceColumn = Integer.parseInt(cellTokens[1]);
   		return new Reference(spreadsheet.getCell(referenceRow, referenceColumn));
	}
    
    public final Integer getResult() throws BubbledocsException {
    	if(cellLine == null || cellColumn == null)
    		execute();
    	
   	   	Bubbledocs bubble = Bubbledocs.getBubbledocs();
   	   	try {
   	   		return bubble.getSpreadsheetById(spreadsheetId).getCell(cellLine, cellColumn).calculate();
   	   	} catch (CellDivisionByZeroException e) {
   	   		return null;
   	   	}
    }
}
