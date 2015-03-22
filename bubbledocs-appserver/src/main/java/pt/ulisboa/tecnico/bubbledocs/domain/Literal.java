package pt.ulisboa.tecnico.bubbledocs.domain;

public class Literal extends Literal_Base {
    
    public Literal() {
        super();
    }

    public Literal(int i) {
		super();
		setValue(i);
	}

	public final void init(Integer value) {
    	setValue(value);
    }
    
    @Override
    protected final int myValue() {
    	return getValue();
    }
    
    /**
     * Defines XML element for this class
     */
    @Override
    public final org.jdom2.Element export() {
    	org.jdom2.Element litElement = new org.jdom2.Element("Literal");
    	litElement.setAttribute("value", getValue().toString());
    	return litElement;
    }

    /**
     * pseudo-constructor for initializing a content from an XML element
     * @param XML JDOM element for this content
     */
    protected final void init(org.jdom2.Element el, Spreadsheet sheet) {
    	setValue(Integer.parseInt(el.getAttribute("value").getValue()));
    }


    /**
      * Method to erase this Literal (from persistence)
      */
    public void clean(){
        super.deleteDomainObject();
    }

    @Override
    public boolean equals(Content other) {
    	if(!(other instanceof Literal))
    		return false;
    	else return getValue() == ((Literal)other).getValue();    				
    }
}
