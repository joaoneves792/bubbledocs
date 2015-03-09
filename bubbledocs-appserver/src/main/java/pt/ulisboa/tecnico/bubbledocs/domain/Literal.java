package pt.ulisboa.tecnico.bubbledocs.domain;

public class Literal extends Literal_Base {
    
    public Literal() {
        super();
    }

    public final void init(Integer value) {
    	set_value(value);
    }
    
    @Override
    protected final int __getValue__() {
    	return get_value();
    }
    
    /**
     * Defines XML element for this class
     */
    @Override
    public final org.jdom2.Element export() {
    	org.jdom2.Element litElement = new org.jdom2.Element("Literal");
    	litElement.setAttribute("value", get_value().toString());
    	return litElement;
    }
    
}
