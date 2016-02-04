package vicnode.daris.femur.upload;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import arc.mf.client.ServerClient;
import vicnode.daris.femur.upload.ArcUtil.ArcType;

public class CreateStudyAndDatasets {

    static void distributePICT() throws Throwable {
        File rootDir = new File(
                Configuration.root() + "/" + Configuration.tiles100umSony());
        File imagesDir = new File(Configuration.root() + "/"
                + Configuration.tiles100umSony() + "/IMAGES");
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
                Configuration.root() + "/" + Configuration.tiles100umSony());
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
        String source = LocalFileSystem.trimRoot(Configuration.root(), dir);
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
        datasetCid = DatasetUtil
                .createDerivedDataset(cxn, studyCid, null, null,
                        "100 micrometre tiles sony camera", "tiff/series",
                        ArcType.zip.mimeType(), null, true,
                        "100µm_tiles_sony_camera-" + name + ".zip", exMethodCid,
                        "1", source,
                        new String[] { "microradiography",
                                record.specimenType },
                        record.specimenType, "microradiography", dir, true,
                        ArcType.zip);
        System.out.println(
                "Created dataset: " + datasetCid + " from " + source + ".");
    }

    public static void upload100umSpotDatasets() throws Throwable {
        File rootDir = new File(
                Configuration.root() + "/" + Configuration.tiles100umSpot());
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
        String source = LocalFileSystem.trimRoot(Configuration.root(), dir);
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
        datasetCid = DatasetUtil
                .createDerivedDataset(cxn, studyCid, null, null,
                        "100 micrometre tiles spot camera", "tiff/series",
                        ArcType.zip.mimeType(), null, true,
                        "100µm_tiles_spot_camera-" + name + ".zip", exMethodCid,
                        "1", source,
                        new String[] { "microradiography",
                                record.specimenType },
                        record.specimenType, "microradiography", dir, true,
                        ArcType.zip);
        System.out.println(
                "Created dataset: " + datasetCid + " from " + source + ".");
    }

    public static void uploadSpring8Sept2008Datasets(int specimenNo,
            String name, File dirPrimary, String namePrimary,
            File dirReconstructed, String nameReconstructed) throws Throwable {
        String sourcePrimary = LocalFileSystem.trimRoot(Configuration.root(),
                dirPrimary);
        String sourceReconstructed = LocalFileSystem
                .trimRoot(Configuration.root(), dirReconstructed);

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
            String exMethodCid = subjectCid + ".1";
            /*
             * check if datasets exist
             */
            String primaryDatasetCid = DatasetUtil.findDatasetBySource(cxn,
                    subjectCid, sourcePrimary);
            String reconstructedDatasetCid = DatasetUtil
                    .findDatasetBySource(cxn, subjectCid, sourceReconstructed);
            if (primaryDatasetCid != null && reconstructedDatasetCid != null) {
                System.out.println("Primary Dataset(cid: " + primaryDatasetCid
                        + ") from " + sourcePrimary + " already exists.");
                System.out.println("Reconstructed Dataset(cid: "
                        + reconstructedDatasetCid + ") from "
                        + sourceReconstructed + " already exists.");
                return;
            }
            /*
             * create / find study
             */
            String studyName = "Spring8 Sept 2008";
            String studyCid = StudyUtil.findOrCreateStudy(cxn, exMethodCid, "2",
                    studyName, studyName + " - " + name.toLowerCase(), record,
                    new String[] { "Clinical CT", "microCT" });
            System.out.println("Created/Found study " + studyCid);
            /*
             * create primary dataset
             */
            if (primaryDatasetCid == null) {
                System.out.println("Uploading primary dataset: from "
                        + sourcePrimary + ".");
                primaryDatasetCid = DatasetUtil.createPrimaryDataset(cxn,
                        studyCid, namePrimary,
                        "Spring8 raw data in Hipic format", "hipic/series",
                        ArcType.aar.mimeType(), null, true,
                        "Spring8_Sept_2008-" + name.toLowerCase() + ".aar",
                        exMethodCid, "2", sourcePrimary,
                        new String[] { "Clinical CT", "microCT",
                                record.specimenType },
                        record.specimenType, "microCT", dirPrimary, false,
                        ArcType.aar);
                System.out.println("Created primary dataset: "
                        + primaryDatasetCid + " from " + sourcePrimary + ".");
            }
            /*
             * create reconstructed dataset
             */
            if (reconstructedDatasetCid == null) {
                System.out.println("Uploading reconstructed dataset: from "
                        + sourceReconstructed + ".");
                reconstructedDatasetCid = DatasetUtil.createDerivedDataset(cxn,
                        studyCid, new String[] { primaryDatasetCid },
                        nameReconstructed,
                        "Reconstructed Spring8 data in TIFF format",
                        "tiff/series", ArcType.aar.mimeType(), null, true,
                        "Spring8_Sept_2008-" + name + "-Reconstructed.aar",
                        exMethodCid, "2", sourceReconstructed,
                        new String[] { "Clinical CT", "microCT",
                                record.specimenType },
                        record.specimenType, "microCT", dirReconstructed, true,
                        ArcType.aar);
                System.out.println("Created reconstructed dataset: "
                        + reconstructedDatasetCid + " from "
                        + sourceReconstructed + ".");
            }
        } finally {
            Server.disconnect();
        }
    }

    public static void uploadSpring8Sept2008Datasets(String name,
            File dirPrimary, File dirReconstructed) throws Throwable {
        int specimenNo = Integer.parseInt(StringUtil.trimNonDigits(name));
        uploadSpring8Sept2008Datasets(specimenNo, name, dirPrimary,
                "Spring8 Raw - " + name, dirReconstructed,
                "Spring8 Reconstructed - " + name);
    }

    public static void uploadSpring8Sept2008Datasets(File parent, String name)
            throws Throwable {
        File dirPrimary = new File(parent, name);
        File dirReconstructed = new File(dirPrimary, "Reconstructed sections");
        if (!dirReconstructed.exists()) {
            dirReconstructed = new File(dirPrimary, "reconstructed sections");
        }
        uploadSpring8Sept2008Datasets(name, dirPrimary, dirReconstructed);
    }

    public static void main(String[] args) throws Throwable {
        // DONE
        // distributePICT();
        // upload100umSonyDatasets();
        // upload100umSpotDatasets();

        // DOING
        uploadSpring8Sept2008Datasets(new File(args[0]), args[1]);
    }
}
