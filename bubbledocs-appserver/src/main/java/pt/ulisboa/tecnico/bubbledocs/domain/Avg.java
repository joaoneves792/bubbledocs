package pt.ulisboa.tecnico.bubbledocs.domain;

public class Avg extends Avg_Base {
    
    public Avg() {
        super();
    }

	@Override
	protected int myValue() {
		// TODO calculate average of submatrix
		return 0;
	}

    @Override
    public boolean equals(Content other) {
    	if(!(other instanceof Avg))
    		return false;
    	else return getReferenceOne().equals(((Prd)other).getReferenceOne()) &&
    		    	getReferenceTwo().equals(((Prd)other).getReferenceTwo());
    }
}
