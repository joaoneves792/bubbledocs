package pt.ulisboa.tecnico.bubbledocs.domain;

import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidCellException;

public class Avg extends Avg_Base {
    
    public Avg() {
        super();
    }

	@Override
	protected int myValue() throws InvalidCellException{
        Spreadsheet spreadsheet = this.getCell().getSpreadsheet();
	    Cell cellOne = getReferenceOne().getCell();
        Cell cellTwo = getReferenceTwo().getCell();
        int columnsDelta = cellTwo.getColumn()-cellOne.getColumn();
	    int rowsDelta = cellTwo.getRow()-cellOne.getRow();
        int baseColumn = cellOne.getColumn();
        int baseRow = cellOne.getRow();
        int sum = 0;
        int count = (rowsDelta+1)*(columnsDelta+1);
       
        for(int i=0; i<= columnsDelta; i++)
            for(int j=0; j<=rowsDelta; j++)
                sum += spreadsheet.getCell(baseRow+j, baseColumn+i).getContent().calculate();
        
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
