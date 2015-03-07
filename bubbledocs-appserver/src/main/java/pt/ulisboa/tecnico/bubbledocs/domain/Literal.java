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
}
