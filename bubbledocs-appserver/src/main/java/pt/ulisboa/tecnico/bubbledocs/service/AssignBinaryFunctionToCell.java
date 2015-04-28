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
import pt.ulisboa.tecnico.bubbledocs.exceptions.BubbledocsException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidFunctionException;


public class AssignBinaryFunctionToCell extends BubbledocsService {
	
    private Integer spreadsheetId;
    private String cellID;
    private String functionExpression;
    private Integer result;
    
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
    protected void dispatch() throws BubbledocsException, NumberFormatException {
   	   	Bubbledocs bubble = Bubbledocs.getBubbledocs();
   	   	Integer cellLine, cellColumn;
   	   	Integer literalOne = null;
   	   	Integer literalTwo = null;
   	   	Integer referenceOneRow, referenceOneColumn, referenceTwoRow, referenceTwoColumn;
   	   	String[] referenceTokens;
   	   	SimpleContent argOne, argTwo;
   	   	Function function;
   	   	
   	   	
   	   	//Parse the id for the cell to be assigned this function
	   	String[] cellTokens = cellID.split(PARSE_CELL);
	   	cellLine = Integer.parseInt(cellTokens[0]);
	   	cellColumn = Integer.parseInt(cellTokens[1]);
    		   	
	   	bubble.assertSessionAndWritePermission(userToken,spreadsheetId, cellLine, cellColumn);
   	   	Spreadsheet spreadsheet = bubble.getSpreadsheetById(spreadsheetId);
	   	
   	   	//Assert that the function expression is in an expected format
        if (!Pattern.matches(BINARY_FUNCTION, functionExpression)) 
            throw new InvalidFunctionException(functionExpression); 

	   	//Parse the function expression
   	   	String tokens[] = functionExpression.split(PARSE_BINARY_FUNCTION);
   	   	String functionType = tokens[0];
   	   	String firstArg = tokens[1];
   	   	String secondArg = tokens[2];
   	   	
   	   	//Get the first argument
   	   	try{
   	   		literalOne = Integer.parseInt(firstArg);
   	   		argOne = new Literal(literalOne);
   	   	}catch(NumberFormatException e){
   	   		//Parse a reference
   	   		referenceTokens = firstArg.split(PARSE_CELL);
   	   		referenceOneRow = Integer.parseInt(referenceTokens[0]);
   	   		referenceOneColumn = Integer.parseInt(referenceTokens[1]);
   	   		argOne = new Reference(spreadsheet.getCell(referenceOneRow, referenceOneColumn));
   	   	}

   	   	//Get the second argument
   	   	try{
   	   		literalTwo = Integer.parseInt(secondArg);
   	   		argTwo = new Literal(literalTwo);
   	   	}catch(NumberFormatException e){
   	   		//Parse a reference
   	   		referenceTokens = firstArg.split(PARSE_CELL);
   	   		referenceTwoRow = Integer.parseInt(referenceTokens[0]);
   	   		referenceTwoColumn = Integer.parseInt(referenceTokens[1]);
   	   		argTwo = new Reference(spreadsheet.getCell(referenceTwoRow, referenceTwoColumn));
   	   	}

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
    	result = spreadsheet.getCell(cellLine, cellColumn).calculate();
    }
    
    public final Integer getResult(){
    	return result;
    }
}
