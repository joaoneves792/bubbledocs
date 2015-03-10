package pt.ulisboa.tecnico.bubbledocs.domain;

import org.jdom2.Element;

public class Prd extends Prd_Base {
    
    public Prd() {
        super();
    }

	@Override
	protected int __getValue__() {
		// TODO calculate product of submatrix
		return 0;
	}

	@Override
	protected Element export() {
		org.jdom2.Element el = new org.jdom2.Element("Prd");
		for(Reference ref : getReferenceSet()) {
			el.addContent(ref.export());
		}
		return el;
	}
 	
}
