package pt.ulisboa.tecnico.bubbledocs.domain;

import pt.ulisboa.tecnico.bubbledocs.exceptions.BubbleCellException;

//This is an abstract class!!
public class Content extends Content_Base {
    
    public Content() {
        super();
    }

    protected void init(String text){
        set_text(text);
    }

    public String toString(){
        return get_text();
    }    
    public Integer getValue() throws BubbleCellException {
        //Just a stub
        return Integer.MIN_VALUE;
    }

	public org.jdom2.Element export() {
		return new org.jdom2.Element("Content");
	}
}
