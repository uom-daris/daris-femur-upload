package vicnode.daris.femur.upload;

import java.io.File;

import arc.mf.client.RemoteServer;
import arc.mf.client.ServerClient;
import vicnode.daris.femur.upload.MasterSpreadsheet.SubjectRecord;

public class CreateProjectAndSubjects {

    public static final String MASTER_SPREADSHEET_FILE_PATH = "/Users/wliu5/Downloads/MFC catalogue June 2013.xls";
    public static final String HOST = "mediaflux.vicnode.org.au";
    public static final int PORT = 443;
    public static final boolean SSL = true;
    public static final String DOMAIN = "system";
    public static final String USER = "wilson";
    public static final String PASSWORD = "Guess_me_now8";
    public static final String PROJECT_NAME = "Melbourne Femur Collection";
    public static final String PROJECT_DESCRIPTION = "Melbourne Femur Collection";

    public static void main(String[] args) throws Throwable {
        RemoteServer server = new RemoteServer(HOST, PORT, true, SSL);
        ServerClient.Connection cxn = null;
        try {
            cxn = server.open();
            cxn.connect(DOMAIN, USER, PASSWORD);
            File masterSpreadsheetFile = new File(MASTER_SPREADSHEET_FILE_PATH);
            createOrUpdateProjectAndSubjects(cxn, masterSpreadsheetFile, true);
        } finally {
            if (cxn != null) {
                cxn.close();
            }
        }
    }

    public static void createOrUpdateProjectAndSubjects(
            ServerClient.Connection cxn, File masterSpreadsheetFile,
            boolean update) throws Throwable {
        /*
         * create/update project
         */
        System.out.print("creating/updating project...");
        String projectCid = ProjectUtil.createProject(cxn, PROJECT_NAME,
                PROJECT_DESCRIPTION, masterSpreadsheetFile, true);
        System.out.println("done.");
        System.out.print("reading master spreadsheet: " + masterSpreadsheetFile.getAbsolutePath() + "...");
        MasterSpreadsheet masterSpreadsheet = new MasterSpreadsheet(
                masterSpreadsheetFile);
        System.out.println("done.");
        for (SubjectRecord record : masterSpreadsheet) {
            SubjectUtil.createSubject(cxn, projectCid, record, update);
            Thread.sleep(1000);
        }
    }

}
