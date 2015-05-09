package pt.ulisboa.tecnico.bubbledocs.service;

import java.util.regex.Pattern;

import pt.ulisboa.tecnico.bubbledocs.domain.Add;
import pt.ulisboa.tecnico.bubbledocs.domain.Bubbledocs;
import pt.ulisboa.tecnico.bubbledocs.domain.Div;
import pt.ulisboa.tecnico.bubbledocs.domain.Function;
import pt.ulisboa.tecnico.bubbledocs.domain.Literal;
import pt.ulisboa.tecnico.bubbledocs.domain.Mul;
import pt.ulisboa.tecnico.bubbledocs.domain.Reference;
import pt.ulisboa.tecnico.bubbledocs.domain.SimpleContent;
import pt.ulisboa.tecnico.bubbledocs.domain.Spreadsheet;
import pt.ulisboa.tecnico.bubbledocs.domain.Sub;
import pt.ulisboa.tecnico.bubbledocs.exceptions.BubbleCellException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.BubbledocsException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidCellException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidFunctionException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.SpreadsheetNotFoundException;


public class AssignBinaryFunctionToCell extends BubbledocsService {
	
    private Integer spreadsheetId;
    private String cellID;
    private String functionExpression;
    
    private Integer cellColumn = null;
	private Integer cellLine = null;
    
    
    final static String ZERO = "0"; 
    final static String NEGATIVE = "-(?!0)\\d+";
    final static String POSITIVE = "(?!0)\\d+";
    final static String LITERAL = "(" + NEGATIVE + "|" + ZERO + "|" + POSITIVE + ")"; 
    final static String CELL = POSITIVE + ";" + POSITIVE; 
    final static String PARSE_CELL = ";"; 
    
    final static String BINARY_OPERATOR = "(ADD|SUB|MUL|DIV)"; 
    final static String ARGUMENT = "(" + LITERAL + "|" + CELL + ")"; 
    final static String BINARY_FUNCTION = "=" + BINARY_OPERATOR + "\\("  + ARGUMENT + "," + ARGUMENT + "\\)"; 
    final static String PARSE_BINARY_FUNCTION = "[=(,)]";
	
    
    public AssignBinaryFunctionToCell(String tokenUser, int ssId, String cellId, String funcExpr) {
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
        if (!Pattern.matches(BINARY_FUNCTION, functionExpression)) 
            throw new InvalidFunctionException(functionExpression); 

	   	//Parse the function expression
   	   	String tokens[] = functionExpression.split(PARSE_BINARY_FUNCTION);
   	   	String functionType = tokens[1]; //CAREFULL because of "=" in the parse string the first token is an empty string!
   	   	String firstArg = tokens[2];
   	   	String secondArg = tokens[3];
   	   	

   	   	//Get the first argument
   	   	SimpleContent argOne = parseArgument(spreadsheet, firstArg);
   	   	//Get the second argument
   	   	SimpleContent argTwo = parseArgument(spreadsheet, secondArg);

   	   	//Create the appropriate function
    	switch (functionType){
			case "ADD":
				function = new Add(argOne, argTwo);
				break;
			case "SUB":
				function = new Sub(argOne, argTwo);
				break;
			case "MUL":
				function = new Mul(argOne, argTwo);
				break;
			case "DIV":
				function = new Div(argOne, argTwo);
				break;
			default :
				throw new InvalidFunctionException(functionExpression); //This should have been checked by the parsing
    	}
    	
    	//Assign it and get the value of this cell
    	spreadsheet.assignFunctionCell(cellLine, cellColumn, function);
    }

	private SimpleContent parseArgument(Spreadsheet spreadsheet, String argExpr) throws InvalidCellException {
		Integer literal;
		Integer referenceRow;
		Integer referenceColumn;
		String[] referenceTokens;
		SimpleContent arg;
		try{
   	   		literal = Integer.parseInt(argExpr);
   	   		arg = new Literal(literal);
   	   	}catch(NumberFormatException e){
   	   		//Parse a reference
   	   		referenceTokens = argExpr.split(PARSE_CELL);
   	   		referenceRow = Integer.parseInt(referenceTokens[0]);
   	   		referenceColumn = Integer.parseInt(referenceTokens[1]);
   	   		arg = new Reference(spreadsheet.getCell(referenceRow, referenceColumn));
   	   	}
		return arg;
	}
    
    public final Integer getResult(){
    	if(cellLine == null || cellColumn == null)
    		return null;
    	
   	   	Bubbledocs bubble = Bubbledocs.getBubbledocs();
   	   	try {
			return bubble.getSpreadsheetById(spreadsheetId).getCell(cellLine, cellColumn).calculate();
		} catch (BubbleCellException | SpreadsheetNotFoundException e) {
			return null;
		}   	
    	
    }
}
