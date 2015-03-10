package pt.ulisboa.tecnico.bubbledocs.domain;

public class Literal extends Literal_Base {
    
    public Literal() {
        super();
    }

    public Literal(int i) {
		super();
		set_value(i);
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

    /**
     * pseudo-constructor for initializing a content from an XML element
     * @param XML JDOM element for this content
     */
    protected final void init(org.jdom2.Element el) {
    	set_value(Integer.parseInt(el.getAttribute("value").getValue()));
    }
    
}
