package pt.ulisboa.tecnico.bubbledocs.domain;

import org.jdom2.Element;

public class Avg extends Avg_Base {
    
    public Avg() {
        super();
    }

	@Override
	protected int __getValue__() {
		// TODO calculate average of submatrix
		return 0;
	}

	@Override
	protected Element export() {
		org.jdom2.Element el = new org.jdom2.Element("Avg");
		for(Reference ref : getReferenceSet()) {
			el.addContent(ref.export());
		}
		return el;
	}
 
}
