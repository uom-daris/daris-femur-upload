package vicnode.daris.femur.upload;

import java.io.File;

import arc.mf.client.RemoteServer;
import arc.mf.client.ServerClient;
import vicnode.daris.femur.upload.MasterSpreadsheet.SubjectRecord;

public class CreateProjectAndSubjects {

    public static void main(String[] args) throws Throwable {
        RemoteServer server = new RemoteServer(Constants.HOST, Constants.PORT,
                true, Constants.SSL);
        ServerClient.Connection cxn = null;
        try {
            cxn = server.open();
            cxn.connect(Constants.DOMAIN, Constants.USER, Constants.PASSWORD);
            File masterSpreadsheetFile = new File(
                    Constants.MASTER_SPREADSHEET_FILE_PATH);
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
        String projectCid = ProjectUtil.createProject(cxn,
                Constants.PROJECT_NAME, Constants.PROJECT_DESCRIPTION,
                masterSpreadsheetFile, true);
        System.out.println("done.");
        System.out.print("reading master spreadsheet: "
                + masterSpreadsheetFile.getAbsolutePath() + "...");
        MasterSpreadsheet masterSpreadsheet = new MasterSpreadsheet(
                masterSpreadsheetFile);
        System.out.println("done.");
        for (SubjectRecord record : masterSpreadsheet) {
            SubjectUtil.createSubject(cxn, projectCid, record, update);
            Thread.sleep(1000);
        }
    }

}
