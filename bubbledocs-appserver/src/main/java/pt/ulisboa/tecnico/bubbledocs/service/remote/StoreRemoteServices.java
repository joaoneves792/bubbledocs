package pt.ulisboa.tecnico.bubbledocs.service.remote;

import pt.ulisboa.tecnico.bubbledocs.exceptions.CannotLoadDocumentException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.CannotStoreDocumentException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.RemoteInvocationException;


public class StoreRemoteServices {
	
	public void storeDocument(String username, String docName, byte[] document)
		throws CannotStoreDocumentException, RemoteInvocationException {
		// TODO : the connection and invocation of the remote service
	}
	
	public byte[] loadDocument(String username, String docName)
		throws CannotLoadDocumentException, RemoteInvocationException {
		
		// TODO : the connection and invocation of the remote service
		return null;
	}
	
}