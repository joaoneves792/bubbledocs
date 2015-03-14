package pt.ulisboa.tecnico.bubbledocs.domain;

public class Session extends Session_Base {
    
    public Session() {
        super();
    }

    public Session( String username, Integer tokenInt, String date){
        super();
        init(username, tokenInt, date);
    }

    protected void init( String username, Integer tokenInt, String date){
        set_username(username);
        set_tokenInt(tokenInt);
        set_date(date);
    }    


    public void clean(){
        super.deleteDomainObject();
    }

}
