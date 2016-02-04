package vicnode.daris.femur.upload;

import java.io.File;

import arc.mf.client.RemoteServer;
import arc.mf.client.ServerClient;
import vicnode.daris.femur.upload.MasterSpreadsheet.SubjectRecord;

public class CreateProjectAndSubjects {

    public static void main(String[] args) throws Throwable {
        RemoteServer server = new RemoteServer(Configuration.host(),
                Configuration.port(), true, Configuration.ssl());
        ServerClient.Connection cxn = null;
        try {
            cxn = server.open();
            cxn.connect(Configuration.domain(), Configuration.user(),
                    Configuration.password());
            File masterSpreadsheetFile = new File(
                    Configuration.masterSpreadsheetPath());
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
                Configuration.projectName(), Configuration.projectDescription(),
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
