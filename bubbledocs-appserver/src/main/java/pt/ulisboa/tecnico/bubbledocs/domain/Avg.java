package pt.ulisboa.tecnico.bubbledocs.domain;

import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidCellException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidReferenceException;

public class Avg extends Avg_Base {
    
	public Avg() {
		super();
	}
	
    public Avg(Reference rangeStart, Reference rangeEnd) throws InvalidCellException{
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
        int sum = 0;
        int count = (rowsDelta+1)*(columnsDelta+1);
       
        for(int i=0; i<= columnsDelta; i++)
            for(int j=0; j<=rowsDelta; j++){
            	Content content = spreadsheet.getCell(baseRow+j, baseColumn+i).getContent();
            	if (null != content)
            		try{
            			sum += content.calculate();
            		}catch(InvalidCellException | InvalidReferenceException | CellDivisionByZeroException e){
            			continue;
            		}
			}
            	
                
        return sum/count;
	}

    @Override
    public boolean equals(Content other) {
    	if(!(other instanceof Avg))
    		return false;
    	else return getReferenceOne().equals(((Prd)other).getReferenceOne()) &&
    		    	getReferenceTwo().equals(((Prd)other).getReferenceTwo());
    }
}
