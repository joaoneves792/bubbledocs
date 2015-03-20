package pt.ulisboa.tecnico.bubbledocs.domain;

import java.text.ParseException;

import org.joda.time.Hours;

public class Session extends Session_Base {
    
    public Session() {
        super();
    }

    public Session(User user, Integer tokenInt, org.joda.time.LocalDate date) {
        super();
        init(user, tokenInt, date);
    }

    protected void init(User user, Integer tokenInt, org.joda.time.LocalDate date) {
        setUser(user);
        setTokenInt(tokenInt);
        setDate(date);
        user.setSession(this);
    }    


    /**
     * Method to be called every time a user accesses bubbledocs and his session is still valid
     * @param Session
     */
    public void update() {
        setDate(org.joda.time.LocalDate.now());
    }

    
    /**
     * Private method to check if a session was last accessed over 2 hours ago
     * @param Session
     * @throws ParseException 
     */
    public boolean hasExpired() {
        /*java.util.Date date;
        java.util.Date sessionDate;
        long differenceMilliseconds;
        final long TWO_HOURS = 7200000; //Two hours in milliseconds
        
        sessionDate = getDate().toDate();		
        date = new java.util.Date();            
        differenceMilliseconds = date.getTime() - sessionDate.getTime();

        if(TWO_HOURS < differenceMilliseconds)
        	return true;
        else 
        	return false;
        */	
    	
    	return (2 <= Hours.hoursBetween(getDate(), org.joda.time.LocalDate.now()).getHours());
    }
    
    
    public void clean(){
    	setUser(null);
        super.deleteDomainObject();
    }

}
