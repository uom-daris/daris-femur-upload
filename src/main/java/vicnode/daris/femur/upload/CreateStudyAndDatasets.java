package vicnode.daris.femur.upload;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import arc.mf.client.ServerClient;
import vicnode.daris.femur.upload.ArcUtil.ArcType;

/**
 * this class is for ingest of
 * "MFC Xtreme pQCT data 2014 etc/MFC_BATCH_XX/xxx-xxxxxxx/BMP"
 * 
 * @author wliu5
 *
 */
public class CreateStudyAndDatasets {

    public static String uploadDerivedDataset(File datasetDir,
            boolean recursive, int specimenNo, String methodStep,
            String studyName, String[] studyTags, String datasetName,
            String datasetDescription, String mimeType, String fileName,
            ArcType arcType, String[] datasetTags, String imageType,
            String input) throws Throwable {
        String source = LocalFileSystem.trimRoot(Configuration.root(),
                datasetDir);
        MasterSpreadsheet sheet = LocalFileSystem.getMasterSpreadsheet();
        MasterSpreadsheet.SubjectRecord record = sheet.getRecord(specimenNo);
        assert record != null;
        ServerClient.Connection cxn = Server.connect();
        try {
            String subjectCid = SubjectUtil.findSubject(cxn, specimenNo);
            if (subjectCid == null) {
                throw new Exception(
                        "Could not find subject with specimen number: "
                                + specimenNo);
            }
            /*
             * check if datasets exist
             */
            String datasetCid = DatasetUtil.findDatasetBySource(cxn, subjectCid,
                    source);
            if (datasetCid != null) {
                System.out.println("Primary Dataset(cid: " + datasetCid
                        + ") from " + source + " already exists.");
                return datasetCid;
            }
            /*
             * update study
             */
            String studyCid = StudyUtil.findStudy(cxn, subjectCid + ".1", "2",
                    null);
            StudyUtil.updateStudy(cxn, studyCid, studyName, studyName, record,
                    studyTags);
            System.out.println("Updated study " + studyCid);
            /*
             * create derived dataset
             */
            String filename = studyName + "-" + datasetDir.getName() + "."
                    + arcType.ext();
            filename = filename.replace(' ', '_');
            Set<String> tags = new HashSet<String>();
            tags.add(record.specimenType);
            if (datasetTags != null) {
                for (String dt : datasetTags) {
                    tags.add(dt);
                }
            }
            System.out
                    .println("Uploading derived dataset: from " + source + ".");
            datasetCid = DatasetUtil.createDerivedDataset(cxn, studyCid,
                    input == null ? null : new String[] { input }, datasetName,
                    datasetDescription, mimeType, arcType.mimeType(), null,
                    filename, source, tags.toArray(new String[tags.size()]),
                    record.specimenType, imageType, datasetDir, recursive,
                    arcType, false);
            System.out.println("Created derived dataset: " + datasetCid
                    + " from " + source + ".");
            return datasetCid;
        } finally {
            Server.disconnect();
        }
    }

    public static void main(String[] args) throws Throwable {
        if (args.length != 14) {
            System.out.println(
                    "Usage: upload <dir> <recursive> <specimen-no> <method-step> <study-name> <study-tags> <name> <description> <mime-type> <filename> <atype> <tags> <image-type> [input-cid]");
            System.exit(1);
        }
        File datasetDir = new File(args[0]);
        boolean recursive = Boolean.parseBoolean(args[1]);
        int specimenNo = Integer.parseInt(args[2]);
        String methodStep = args[3];
        String studyName = args[4];
        String[] studyTags = args[5].split(",");
        String datasetName = args[6];
        String datasetDesc = args[7];
        String mimeType = args[8];
        String filename = args[9];
        ArcType arcType = args[10].equalsIgnoreCase("AAR") ? ArcType.aar
                : ArcType.zip;
        String[] datasetTags = args[11].split(",");
        String imageType = args[12];
        String input = args[13];
        uploadDerivedDataset(datasetDir, recursive, specimenNo, methodStep,
                studyName, studyTags, datasetName, datasetDesc, mimeType,
                filename, arcType, datasetTags, imageType, input);

    }
}
