package pt.ulisboa.tecnico.bubbledocs.service;

import pt.ulisboa.tecnico.bubbledocs.exceptions.BubbledocsException;

// add needed import declarations

public class ExportDocument extends BubbledocsService {
	
	private String userToken;
	private String docId;
	
    private String docXML;

    public final String getDocXML() {
	return docXML;
    }

    public ExportDocument(String userToken, int docId) {
    	this.setUserToken(userToken);
    	this.setDocId(docId);
    }

    @Override
    protected void dispatch() throws BubbledocsException {
    	
    	Bubbledocs bubble;       
        bubble = Bubbledocs.getBubbledocs();
        
	        Session session = bubble.getSessionByToken(userToken);
	        
	        if(checkSessionExpired(session)){
	            clearSession(session);
	            throw new UserNotInSessionException(userToken + "'s Session expired!");
	        }else
	            bubble.updateSessionTime(session);
	
	        bubble.getPermission(userToken, docId);
	        
	        this.setDocXML(bubble.getSpreadSheetById(docId).export());

    }
}
