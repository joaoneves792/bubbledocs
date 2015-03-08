package pt.ulisboa.tecnico.bubbledocs.domain;

public class Literal extends Literal_Base {
    
    public Literal(String value) {
        super();
        init(value);
    }
    protected void init(String value){
        set_value(Integer.valueOf(value));
        super.init(value);
    }

    public Integer getValue(){
        return get_value();
    }
    
    @Override
    public org.jdom2.Element export() {
    	org.jdom2.Element litElement = new org.jdom2.Element("Lit");
    	litElement.setAttribute("value", get_value().toString());
    	return litElement;
    }
}
