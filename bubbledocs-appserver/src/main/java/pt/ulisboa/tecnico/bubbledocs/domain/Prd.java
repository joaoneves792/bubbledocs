package pt.ulisboa.tecnico.bubbledocs.domain;

public class Prd extends Prd_Base {
    
    public Prd() {
        super();
    }

	@Override
	protected int myValue() {
		// TODO calculate product of submatrix
		return 0;
	}

    @Override
    public boolean equals(Content other) {
    	if(!(other instanceof Prd))
    		return false;
    	else return getReferenceOne().equals(((Prd)other).getReferenceOne()) &&
    		    	getReferenceTwo().equals(((Prd)other).getReferenceTwo());
    }
}
