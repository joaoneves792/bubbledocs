package pt.ulisboa.tecnico.bubbledocs.domain;

public class Reference extends Reference_Base {

    public Reference(String value, Cell referencedCell) {
        super();
        init(value, referencedCell);
    }
    protected void init(String value, Cell referencedCell){
        set_value(value);
        setReferenceCell(referencedCell);
    }

    public Integer getValue(){
        Cell cell;
        cell = getReferenceCell();
        if(cell == null)
                return null;
        return cell.getValue();
    }

    public String toString(){
        return get_value();
    }

}
