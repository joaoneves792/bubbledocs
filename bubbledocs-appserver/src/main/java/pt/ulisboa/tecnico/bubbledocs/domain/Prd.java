package pt.ulisboa.tecnico.bubbledocs.domain;

import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidCellException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidReferenceException;

public class Prd extends Prd_Base {
    
	public Prd() {
		super();
	}
	
    	public Prd(Reference rangeStart, Reference rangeEnd) throws InvalidCellException{
    		super();
    		init(rangeStart, rangeEnd);
    	}

	@Override
	protected int myValue() throws InvalidCellException{
        Spreadsheet spreadsheet = this.getReferenceOne().getReferencedCell().getSpreadsheet();
	    Cell cellOne = getReferenceOne().getReferencedCell();
        Cell cellTwo = getReferenceTwo().getReferencedCell();
        int columnsDelta = cellTwo.getColumn()-cellOne.getColumn();
	    int rowsDelta = cellTwo.getRow()-cellOne.getRow();
        int baseColumn = cellOne.getColumn();
        int baseRow = cellOne.getRow();
        int product = 1;
       
        for(int i=0; i<=columnsDelta; i++)
            for(int j=0; j<=rowsDelta; j++){
            	Content content = spreadsheet.getCell(baseRow+j, baseColumn+i).getContent();
            	if (null != content)
            		try{
            			product *= content.calculate();
            		}catch(InvalidCellException | InvalidReferenceException | CellDivisionByZeroException e){
            			continue;
            		}
            }
        
        return product;
	}

    @Override
    public boolean equals(Content other) {
    	if(!(other instanceof Prd))
    		return false;
    	else return getReferenceOne().equals(((Prd)other).getReferenceOne()) &&
    		    	getReferenceTwo().equals(((Prd)other).getReferenceTwo());
    }
}
