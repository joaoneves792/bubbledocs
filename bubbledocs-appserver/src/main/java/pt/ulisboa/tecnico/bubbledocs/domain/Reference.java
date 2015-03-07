package pt.ulisboa.tecnico.bubbledocs.domain;

import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidReferenceException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.InvalidCellException;

public class Reference extends Reference_Base {

    public Reference(String value, Cell referencedCell) {
        super();
        init(value, referencedCell);
    }
    protected void init(String value, Cell referencedCell){
        super.init(value);
        setReferenceCell(referencedCell);
    }

    public Integer getValue(){
        Cell cell;
        Content content;
        cell = getReferenceCell();
        Integer value;
        if(cell == null)
                throw new InvalidCellException("A Reference is trying to access a Cell that does not exist!");
        content = cell.getContent();
        if(content == null)
                throw new InvalidReferenceException("A Reference is pointing to an empty Cell!");

        return content.getValue();
    }

}
