package pt.ulisboa.tecnico.bubbledocs.domain;

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
    public int getvalue(){
        //Just a stub
        //TODO RETURN -oo
        return 0;
    }
}
