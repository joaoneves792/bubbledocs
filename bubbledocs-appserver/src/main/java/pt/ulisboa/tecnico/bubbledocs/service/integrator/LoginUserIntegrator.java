package pt.ulisboa.tecnico.bubbledocs.service.integrator;

import pt.ulisboa.tecnico.bubbledocs.exceptions.BubbledocsException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.RemoteInvocationException;
import pt.ulisboa.tecnico.bubbledocs.service.CreateSession;
import pt.ulisboa.tecnico.bubbledocs.service.LoginUser;
import pt.ulisboa.tecnico.bubbledocs.service.remote.IDRemoteServices;

public class LoginUserIntegrator extends BubbledocsIntegrator {

    private String username;
    private String password;

    public LoginUserIntegrator(String usrname, String passwd) {
        username = usrname;
        password = passwd;
    }
    
    @Override
    protected void dispatch() throws BubbledocsException {
        IDRemoteServices sdId = new IDRemoteServices();
        LoginUser localLoginService = new LoginUser(username, password);
        CreateSession createSessionService = new CreateSession(username, password);
        
        try{
        	sdId.loginUser(username, password);
        }catch(RemoteInvocationException e){
			localLoginService.execute();
        }

        createSessionService.execute();
        userToken = createSessionService.getUserToken();
        
    }

    public final String getUserToken() {
    	return userToken;
    }
}
