package vicnode.daris.femur.upload;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import arc.mf.client.ServerClient;

public class CreateStudyAndDatasets {

    private static void distributePICT() throws Throwable {
        File rootDir = new File(
                Constants.ROOT + "/" + Constants.Tiles_100µm_Sony);
        File imagesDir = new File(
                Constants.ROOT + "/" + Constants.Tiles_100µm_Sony + "/IMAGES");
        File[] imageFiles = imagesDir.listFiles(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith("PICT.TIF");
            }
        });
        for (File imageFile : imageFiles) {
            File[] dirs = rootDir.listFiles(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    return f.isDirectory() && f.getName()
                            .startsWith(imageFile.getName().substring(0, 3));
                }
            });
            if (dirs != null) {
                for (File dir : dirs) {
                    File targetFile = new File(dir, imageFile.getName());
                    if (!targetFile.exists()) {
                        Files.copy(Paths.get(imageFile.getAbsolutePath()),
                                Paths.get(targetFile.getAbsolutePath()),
                                StandardCopyOption.REPLACE_EXISTING);
                        System.out.println(imageFile.getName() + " -> "
                                + dir.getName() + "/");
                    }
                }
            }

        }
    }

    public static void upload100umSonyDatasets() throws Throwable {
        File rootDir = new File(
                Constants.ROOT + "/" + Constants.Tiles_100µm_Sony);
        File[] dirs = rootDir.listFiles(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                if (name.matches("^\\d+\\w*")) {
                    int n = Integer.parseInt(name.substring(0, 3));
                    return n >= 231 && n <= 284;
                }
                return false;
            }
        });
        if (dirs != null && dirs.length > 0) {
            MasterSpreadsheet sheet = LocalFileSystem.getMasterSpreadsheet();
            ServerClient.Connection cxn = Server.connect();
            try {
                for (File dir : dirs) {
                    upload100umSonyDataset(dir, sheet, cxn);
                }
            } finally {
                Server.disconnect();
            }
        }
    }

    public static void upload100umSonyDataset(File dir,
            MasterSpreadsheet spreadsheet, ServerClient.Connection cxn)
                    throws Throwable {
        String name = dir.getName();
        String specimenNo = name;
        if (!name.matches("\\d+")) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < name.length(); i++) {
                char c = name.charAt(i);
                if (Character.isDigit(c)) {
                    sb.append(c);
                } else {
                    break;
                }
            }
            specimenNo = sb.toString();
        }
        MasterSpreadsheet.SubjectRecord record = spreadsheet
                .getRecord(Integer.parseInt(specimenNo));
        assert record != null;
        String subjectCid = SubjectUtil.findSubject(cxn,
                Integer.parseInt(specimenNo));
        if (subjectCid == null) {
            throw new Exception("Could not find subject with specimen number: "
                    + specimenNo);
        }
        String exMethodCid = subjectCid + ".1";
        String source = LocalFileSystem.trimRoot(Constants.ROOT, dir);
        String datasetCid = DatasetUtil.findDatasetBySource(cxn, subjectCid,
                source);
        if (datasetCid != null) {
            System.out.println("Dataset(cid: " + datasetCid + ") from " + source
                    + " already exists.");
            return;
        }
        String studyCid = StudyUtil.findOrCreateStudy(cxn, exMethodCid, "1",
                "100µm Tiles Sony Camera", "100µm Tiles Sony Camera", record,
                null);
        System.out.println("Created study: " + studyCid + ".");
        datasetCid = DatasetUtil.createDerivedDataset(cxn, studyCid, null, null,
                "100 micrometre tiles sony camera", "tiff/series",
                Constants.ARC_TYPE.mimeType(), null, true,
                "100µm_tiles_sony_camera-" + name + ".zip", exMethodCid, "1",
                source,
                new String[] { "microradiography", record.specimenType },
                record.specimenType, "microradiography", dir);
        System.out.println(
                "Created dataset: " + datasetCid + " from " + source + ".");
    }

    public static void upload100umSpotDatasets() throws Throwable {
        File rootDir = new File(
                Constants.ROOT + "/" + Constants.Tiles_100µm_Spot);
        File[] dirs = rootDir.listFiles(new FileFilter() {

            @Override
            public boolean accept(File f) {
                if (f.isDirectory()) {
                    String name = f.getName();
                    int n = Integer.parseInt(name.substring(0, 3));
                    return n >= 231 && n <= 284;
                }
                return false;
            }

        });
        if (dirs != null && dirs.length > 0) {
            MasterSpreadsheet sheet = LocalFileSystem.getMasterSpreadsheet();
            ServerClient.Connection cxn = Server.connect();
            try {
                for (File dir : dirs) {
                    upload100umSpotDataset(dir, sheet, cxn);
                }
            } finally {
                Server.disconnect();
            }
        }

    }

    public static void upload100umSpotDataset(File dir,
            MasterSpreadsheet spreadsheet, ServerClient.Connection cxn)
                    throws Throwable {
        String name = dir.getName();
        String specimenNo = name;
        if (!name.matches("\\d+")) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < name.length(); i++) {
                char c = name.charAt(i);
                if (Character.isDigit(c)) {
                    sb.append(c);
                } else {
                    break;
                }
            }
            specimenNo = sb.toString();
        }
        MasterSpreadsheet.SubjectRecord record = spreadsheet
                .getRecord(Integer.parseInt(specimenNo));
        assert record != null;
        String subjectCid = SubjectUtil.findSubject(cxn,
                Integer.parseInt(specimenNo));
        if (subjectCid == null) {
            throw new Exception("Could not find subject with specimen number: "
                    + specimenNo);
        }
        String exMethodCid = subjectCid + ".1";
        String source = LocalFileSystem.trimRoot(Constants.ROOT, dir);
        String datasetCid = DatasetUtil.findDatasetBySource(cxn, subjectCid,
                source);
        if (datasetCid != null) {
            System.out.println("Dataset(cid: " + datasetCid + ") from " + source
                    + " already exists.");
            return;
        }
        String studyCid = StudyUtil.findOrCreateStudy(cxn, exMethodCid, "1",
                "100µm Tiles Spot Camera", "100µm Tiles Spot Camera", record,
                null);
        System.out.println("Created study: " + studyCid + ".");
        datasetCid = DatasetUtil.createDerivedDataset(cxn, studyCid, null, null,
                "100 micrometre tiles spot camera", "tiff/series",
                Constants.ARC_TYPE.mimeType(), null, true,
                "100µm_tiles_spot_camera-" + name + ".zip", exMethodCid, "1",
                source,
                new String[] { "microradiography", record.specimenType },
                record.specimenType, "microradiography", dir);
        System.out.println(
                "Created dataset: " + datasetCid + " from " + source + ".");
    }

    public static void main(String[] args) throws Throwable {
        distributePICT();
        upload100umSonyDatasets();
        upload100umSpotDatasets();
    }
}
