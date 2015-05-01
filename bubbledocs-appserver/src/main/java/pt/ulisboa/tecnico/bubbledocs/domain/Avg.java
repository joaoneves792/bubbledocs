package pt.ulisboa.tecnico.bubbledocs.domain;

import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidCellException;

public class Avg extends Avg_Base {
    
	public Avg() {
		super();
	}
	
    public Avg(Reference ref1, Reference ref2) throws InvalidCellException {
        super();
        init(ref1, ref2);
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
