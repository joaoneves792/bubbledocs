package pt.ulisboa.tecnico.bubbledocs.domain;

import java.text.ParseException;

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


    /**
     * Method to be called every time a user accesses bubbledocs and his session is still valid
     * @param Session
     */
    public void update() {
        set_date(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(new java.util.Date()));
    }

    
    /**
     * Private method to check if a session was last accessed over 2 hours ago
     * @param Session
     * @throws ParseException 
     */
    public boolean hasExpired() {
        java.util.Date date;
        java.util.Date sessionDate;
        java.text.SimpleDateFormat dateFormat;
        long differenceMilliseconds;
        final long TWO_HOURS = 7200000; //Two hours in milliseconds

        dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
			sessionDate = dateFormat.parse(get_date());
		} catch (ParseException e) {
			return true;
		}
        date = new java.util.Date();
            
        differenceMilliseconds = date.getTime() - sessionDate.getTime();

        if(TWO_HOURS < differenceMilliseconds)
        	return true;
        else
        	return false;
    }
    
    
    public void clean(){
        super.deleteDomainObject();
    }

}
