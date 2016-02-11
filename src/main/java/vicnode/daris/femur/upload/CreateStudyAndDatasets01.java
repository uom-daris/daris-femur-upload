package vicnode.daris.femur.upload;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.Set;

import arc.mf.client.ServerClient;
import vicnode.daris.femur.upload.ArcUtil.ArcType;

public class CreateStudyAndDatasets01 {

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
                        ArcType.zip.mimeType(), null,
                        "100µm_tiles_sony_camera-" + name + ".zip", source,
                        new String[] { "microradiography",
                                record.specimenType },
                        record.specimenType, "microradiography", dir, true,
                        ArcType.zip, true);
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
                        ArcType.zip.mimeType(), null,
                        "100µm_tiles_spot_camera-" + name + ".zip", source,
                        new String[] { "microradiography",
                                record.specimenType },
                        record.specimenType, "microradiography", dir, true,
                        ArcType.zip, true);
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
                        ArcType.aar.mimeType(), null,
                        "Spring8_Sept_2008-" + name.toLowerCase() + ".aar",
                        sourcePrimary,
                        new String[] { "Clinical CT", "microCT",
                                record.specimenType },
                        record.specimenType, "microCT", dirPrimary, false,
                        ArcType.aar, true);
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
                        "tiff/series", ArcType.aar.mimeType(), null,
                        "Spring8_Sept_2008-" + name + "-Reconstructed.aar",
                        sourceReconstructed,
                        new String[] { "Clinical CT", "microCT",
                                record.specimenType },
                        record.specimenType, "microCT", dirReconstructed, true,
                        ArcType.aar, true);
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

    public static String uploadPrimaryDataset(File datasetDir,
            boolean recursive, int specimenNo, String methodStep,
            String studyName, String[] studyTags, String datasetName,
            String datasetDescription, String mimeType, ArcType arcType,
            String[] datasetTags, String imageType) throws Throwable {
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
            String exMethodCid = subjectCid + ".1";
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
             * create / find study
             */
            String studyCid = StudyUtil.findOrCreateStudy(cxn, exMethodCid,
                    methodStep, studyName,
                    studyName + " - " + datasetDir.getName().toLowerCase(),
                    record, studyTags);
            System.out.println("Created/Found study " + studyCid);
            /*
             * create primary dataset
             */
            System.out
                    .println("Uploading primary dataset: from " + source + ".");
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
            datasetCid = DatasetUtil.createPrimaryDataset(cxn, studyCid,
                    datasetName, datasetDescription, mimeType,
                    arcType.mimeType(), null, filename, source,
                    tags.toArray(new String[tags.size()]), record.specimenType,
                    imageType, datasetDir, recursive, arcType, true);
            System.out.println("Created primary dataset: " + datasetCid
                    + " from " + source + ".");
            return datasetCid;
        } finally {
            Server.disconnect();
        }
    }

    public static String uploadDerivedDataset(File datasetDir,
            boolean recursive, int specimenNo, String methodStep,
            String studyName, String[] studyTags, String datasetName,
            String datasetDescription, String mimeType, ArcType arcType,
            String[] datasetTags, String imageType, String input)
                    throws Throwable {
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
            String exMethodCid = subjectCid + ".1";
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
             * create / find study
             */
            String studyCid = StudyUtil.findOrCreateStudy(cxn, exMethodCid,
                    methodStep, studyName,
                    studyName + " - " + datasetDir.getName().toLowerCase(),
                    record, studyTags);
            System.out.println("Created/Found study " + studyCid);
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
                    arcType, true);
            System.out.println("Created derived dataset: " + datasetCid
                    + " from " + source + ".");
            return datasetCid;
        } finally {
            Server.disconnect();
        }
    }

    public static void main(String[] args) throws Throwable {
        if (args.length != 12 && args.length != 13) {
            System.out.println(
                    "Usage: upload <dir> <recursive> <specimen-no> <method-step> <study-name> <study-tags> <name> <description> <mime-type> <atype> <tags> <image-type> [input-cid]");
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
        ArcType arcType = args[9].equalsIgnoreCase("AAR") ? ArcType.aar
                : ArcType.zip;
        String[] datasetTags = args[10].split(",");
        String imageType = args[11];
        if (args.length > 12) {
            String input = args[12];
            if (input.equalsIgnoreCase("null")) {
                input = null;
            }
            uploadDerivedDataset(datasetDir, recursive, specimenNo, methodStep,
                    studyName, studyTags, datasetName, datasetDesc, mimeType,
                    arcType, datasetTags, imageType, input);
        } else {
            uploadPrimaryDataset(datasetDir, recursive, specimenNo, methodStep,
                    studyName, studyTags, datasetName, datasetDesc, mimeType,
                    arcType, datasetTags, imageType);
        }
    }
}
