
package pt.ulisboa.tecnico.bubbledocs.service.integration.system;

import static org.junit.Assert.assertTrue;
import mockit.Mocked;
import mockit.StrictExpectations;

import org.junit.Test;

import pt.ulisboa.tecnico.bubbledocs.exceptions.BubbledocsException;
import pt.ulisboa.tecnico.bubbledocs.exceptions.RemoteInvocationException;
import pt.ulisboa.tecnico.bubbledocs.service.integrator.AssignBinaryFunctionToCellIntegrator;
import pt.ulisboa.tecnico.bubbledocs.service.integrator.AssignLiteralCellIntegrator;
import pt.ulisboa.tecnico.bubbledocs.service.integrator.AssignRangeFunctionToCellIntegrator;
import pt.ulisboa.tecnico.bubbledocs.service.integrator.AssignReferenceCellIntegrator;
import pt.ulisboa.tecnico.bubbledocs.service.integrator.CreateSpreadsheetIntegrator;
import pt.ulisboa.tecnico.bubbledocs.service.integrator.CreateUserIntegrator;
import pt.ulisboa.tecnico.bubbledocs.service.integrator.DeleteUserIntegrator;
import pt.ulisboa.tecnico.bubbledocs.service.integrator.ExportDocumentIntegrator;
import pt.ulisboa.tecnico.bubbledocs.service.integrator.GetSpreadsheetContentIntegrator;
import pt.ulisboa.tecnico.bubbledocs.service.integrator.ImportDocumentIntegrator;
import pt.ulisboa.tecnico.bubbledocs.service.integrator.LoginUserIntegrator;
import pt.ulisboa.tecnico.bubbledocs.service.integrator.RenewPasswordIntegrator;
import pt.ulisboa.tecnico.bubbledocs.service.remote.IDRemoteServices;
import pt.ulisboa.tecnico.bubbledocs.service.remote.StoreRemoteServices;

public class LocalSystemTest extends SystemTest {
	
	private static final String ROOT_USERNAME = "root";
	private static final String ROOT_PASSWORD = "root";
	private static final String ROOT_SPREADSHEET_NAME = "Argonian Account Book";
	private static final Integer ROOT_SPREADSHEET_ROWS = 1;
	private static final Integer ROOT_SPREADSHEET_COLUMNS = 2;
	private static final String ROOT_LITERAL_CELL_ID = "1;1";
	private static final String ROOT_LITERAL = "42";
	private static final String ANOTHER_ROOT_LITERAL = "1";
	
	private static final String ROOT_SPREADSHEET_REPRESENTATION = "[ 1 \"\" ]\n";
	
	private static final String EXPORTER_USERNAME = "mehrunes";
	private static final String EXPORTER_EMAIL = "mehrunes@deadlands.oblivion";
	private static final String EXPORTER_NAME = "Mehrunes Dagon";
	private static final String EXPORTER_PASSWORD = "dagon";
	
	private static final String EXPORTER_SPREADSHEET_NAME = "Mythic Dawn Commentaries";
	private static final int EXPORTER_SPREADSHEET_ROWS = 5;
	private static final int EXPORTER_SPREADSHEET_COLUMNS = 3;
	private static final String EXPORTER_SPREADSHEET_ID = "1";
	
	private static final String PW_RENEWER_USERNAME = "hermaeus";
	private static final String PW_RENEWER_EMAIL = "hermaeus@apocrypha.oblivion";
	private static final String PW_RENEWER_NAME = "Hermaeus Mora";
	private static final String PW_RENEWER_PASSWORD = "mora";
	
    private static final String EXPORTER_SPREADSHEET_REPRESENTATION =
    		"[ 2 3 12 ]\n" + 
    		"[ 1 6 6 ]\n"  + 
    		"[ 5 2 6 ]\n"  +
    	    "[ 4 0 0 ]\n"  +
    		"[ 5 #VALUE \"\" ]\n";
    
	
	@Mocked
	private IDRemoteServices SDID;
	
	@Mocked
	private StoreRemoteServices SDStore;
    
    @Test
    public final void run() throws BubbledocsException {
    	
    	new StrictExpectations() {
    		{
    			new IDRemoteServices();
    			SDID.loginUser(ROOT_USERNAME, ROOT_PASSWORD);
    			result = null;
    			
    			new IDRemoteServices();
    			SDID.createUser(EXPORTER_USERNAME, EXPORTER_EMAIL);
    			result = null;
    			
    			new IDRemoteServices();
    			SDID.createUser(PW_RENEWER_USERNAME, PW_RENEWER_EMAIL);
    			result = null;
    			
    			new IDRemoteServices();
    			SDID.loginUser(EXPORTER_USERNAME, EXPORTER_PASSWORD);
    			result = null;
    			
    			new StoreRemoteServices();
    			SDStore.storeDocument(EXPORTER_USERNAME, EXPORTER_SPREADSHEET_ID, (byte[]) any);
    			result = null;
    		}
    	};
    	
    	
    	LoginUserIntegrator rootLogin = new LoginUserIntegrator(ROOT_USERNAME, ROOT_PASSWORD);
    	rootLogin.execute();
    	
    	String rootToken = rootLogin.getUserToken();
    	
    	CreateSpreadsheetIntegrator rootSpreadsheetCreator = 
    			new CreateSpreadsheetIntegrator(rootToken, ROOT_SPREADSHEET_NAME, ROOT_SPREADSHEET_ROWS, ROOT_SPREADSHEET_COLUMNS);
    	rootSpreadsheetCreator.execute();
    	
    	assertTrue("Spreadsheet IDs not generated correctly", rootSpreadsheetCreator.getSheetId() == 0);
    	
    	int rootSpreadsheetID = rootSpreadsheetCreator.getSheetId();
    	
    	new AssignLiteralCellIntegrator(rootToken, rootSpreadsheetID, ROOT_LITERAL_CELL_ID, ROOT_LITERAL).execute();
    	new AssignLiteralCellIntegrator(rootToken, rootSpreadsheetID, ROOT_LITERAL_CELL_ID, ANOTHER_ROOT_LITERAL).execute();
    	
    	GetSpreadsheetContentIntegrator rootSpreadsheet = new GetSpreadsheetContentIntegrator(rootToken, rootSpreadsheetID);
    	rootSpreadsheet.execute();    	
    	
    	assertTrue("Could not overwrite root Spreadsheet Cell", rootSpreadsheet.getResult().equals(ROOT_SPREADSHEET_REPRESENTATION));
    	
    	new CreateUserIntegrator(rootToken, EXPORTER_USERNAME, EXPORTER_EMAIL, EXPORTER_NAME).execute();
    	new CreateUserIntegrator(rootToken, PW_RENEWER_USERNAME, PW_RENEWER_EMAIL, PW_RENEWER_NAME).execute();
    	
    	LoginUserIntegrator exporterLogin = new LoginUserIntegrator(EXPORTER_USERNAME, EXPORTER_PASSWORD);  
    	exporterLogin.execute();
    	
    	String exporterToken = exporterLogin.getUserToken();
    	
    	CreateSpreadsheetIntegrator exporterSpreadsheetCreator =
    			new CreateSpreadsheetIntegrator(exporterToken, EXPORTER_SPREADSHEET_NAME, EXPORTER_SPREADSHEET_ROWS, EXPORTER_SPREADSHEET_COLUMNS);
    	exporterSpreadsheetCreator.execute();
    	
    	int exporterSpreadsheetID = exporterSpreadsheetCreator.getSheetId();
    	
    	new AssignLiteralCellIntegrator(exporterToken, exporterSpreadsheetID, "1;1", "2").execute();
    	new AssignBinaryFunctionToCellIntegrator(exporterToken, exporterSpreadsheetID, "1;2", "=ADD(1;1,1)").execute();
    	new AssignBinaryFunctionToCellIntegrator(exporterToken, exporterSpreadsheetID, "1;3", "=MUL(2;3,1;1)").execute();

    	new AssignLiteralCellIntegrator(exporterToken, exporterSpreadsheetID, "2;1", "1").execute();
    	new AssignBinaryFunctionToCellIntegrator(exporterToken, exporterSpreadsheetID, "2;2", "=MUL(1;1,1;2)").execute();
    	new AssignReferenceCellIntegrator(exporterToken, exporterSpreadsheetID, "2;3", "2;2").execute();

    	new AssignBinaryFunctionToCellIntegrator(exporterToken, exporterSpreadsheetID, "3;1", "=ADD(1;1,1;2)").execute();
    	new AssignBinaryFunctionToCellIntegrator(exporterToken, exporterSpreadsheetID, "3;2", "=SUB(1;2,2;1)").execute();
    	new AssignBinaryFunctionToCellIntegrator(exporterToken, exporterSpreadsheetID, "3;3", "=DIV(1;3,1;1)").execute();
    
    	new AssignRangeFunctionToCellIntegrator(exporterToken, exporterSpreadsheetID, "4;1", "=AVG(1;1:3;3)").execute();
    	new AssignRangeFunctionToCellIntegrator(exporterToken, exporterSpreadsheetID, "4;2", "=PRD(1;3:4;3)").execute();
    	new AssignLiteralCellIntegrator(exporterToken, exporterSpreadsheetID, "4;3", "0").execute();
    	
    	new AssignLiteralCellIntegrator(exporterToken, exporterSpreadsheetID, "5;1", "5").execute();
    	new AssignBinaryFunctionToCellIntegrator(exporterToken, exporterSpreadsheetID, "5;2", "=DIV(5;1,0)").execute();
    	
    	GetSpreadsheetContentIntegrator exportedSpreadsheet = new GetSpreadsheetContentIntegrator(exporterToken, exporterSpreadsheetID);
    	exportedSpreadsheet.execute();
    	String exportedSpreadsheetRepresentation = exportedSpreadsheet.getResult();
    	
    	assertTrue("Unexpected Spreadsheet Representation", exportedSpreadsheetRepresentation.equals(EXPORTER_SPREADSHEET_REPRESENTATION));
    	
    	ExportDocumentIntegrator exporter = new ExportDocumentIntegrator(exporterToken, exporterSpreadsheetID);
    	exporter.execute();
    	
    	String exportedXML = exporter.getDocXML();
    	
    	new StrictExpectations() {
    		{
    			new StoreRemoteServices();
    			SDStore.loadDocument(EXPORTER_USERNAME, EXPORTER_SPREADSHEET_ID);
    			result = exportedXML.getBytes();
    			
    			new IDRemoteServices();
    			SDID.loginUser(PW_RENEWER_USERNAME, PW_RENEWER_PASSWORD);
    			result = null;
    			
    			new IDRemoteServices();
    			SDID.renewPassword(PW_RENEWER_USERNAME);
    			result = null;
    			
    			new IDRemoteServices();
    			SDID.loginUser(EXPORTER_USERNAME, EXPORTER_PASSWORD);
    			result = new RemoteInvocationException("");
    			
    			new IDRemoteServices();
    			SDID.removeUser(EXPORTER_USERNAME);
    			result = null;
    		}
    	};    	
    	
    	ImportDocumentIntegrator importer = new ImportDocumentIntegrator(exporterToken, exporterSpreadsheetID); 
    	importer.execute();    	

    	int importedSpreadsheetId = importer.getSpreadsheetId();
    	
    	GetSpreadsheetContentIntegrator importedSpreadsheet = new GetSpreadsheetContentIntegrator(exporterToken, importedSpreadsheetId);
    	importedSpreadsheet.execute();
    	String importedSpreadsheetRepresentation = importedSpreadsheet.getResult();
    	
    	assertTrue("Imported and Exported Spreadsheets do not match", importedSpreadsheetRepresentation.equals(exportedSpreadsheetRepresentation));
    	    	
    	LoginUserIntegrator passwordRenewerLogin = new LoginUserIntegrator(PW_RENEWER_USERNAME, PW_RENEWER_PASSWORD);  
    	passwordRenewerLogin.execute();
    	
    	String passwordRenewerToken = passwordRenewerLogin.getUserToken();
    	
    	new RenewPasswordIntegrator(passwordRenewerToken).execute();
    	
    	exporterLogin.execute();
    	exporterToken = exporterLogin.getUserToken();
    	
    	CreateSpreadsheetIntegrator anotherExporterSpreadsheetCreator =
    			new CreateSpreadsheetIntegrator(exporterToken, EXPORTER_SPREADSHEET_NAME, EXPORTER_SPREADSHEET_ROWS, EXPORTER_SPREADSHEET_COLUMNS);
    	anotherExporterSpreadsheetCreator.execute();
    	
    	int anotherExporterSpreadsheetID = anotherExporterSpreadsheetCreator.getSheetId();
    			
		assertTrue("Spreadsheet ID generation incoherent", 
    			rootSpreadsheetID < exporterSpreadsheetID && 
    			exporterSpreadsheetID < importedSpreadsheetId &&
    			importedSpreadsheetId < anotherExporterSpreadsheetID);
    	
    	new DeleteUserIntegrator(rootToken, EXPORTER_USERNAME).execute();
    }
}
