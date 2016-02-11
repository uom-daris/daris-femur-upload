package vicnode.daris.femur.upload;

import java.io.File;
import java.util.Arrays;

import arc.mf.client.ServerClient;
import vicnode.daris.femur.upload.ArcUtil.ArcType;

public class FemurUpload {

    public static final String PROJECT_CID = "1128.1.1";

    private static String createDerivedDataset(String[] args) throws Throwable {
        String source = null;
        String pid = null;
        String inputCid = null;
        String name = null;
        String description = null;
        String type = null;
        String ctype = null;
        String lctype = null;
        ArcType atype = null;
        String specimenType = null;
        String imageType = null;
        String filename = null;
        String[] tags = null;
        String input = null;
        boolean recursive = false;
        boolean fillin = false;

        for (int i = 0; i < args.length;) {
            if ("--pid".equals(args[i])) {
                pid = args[i + 1];
                i += 2;
            } else if ("--source".equals(args[i])) {
                source = args[i + 1];
                i += 2;
            } else if ("--input-cid".equals(args[i])) {
                inputCid = args[i + 1];
                i += 2;
            } else if ("--name".equals(args[i])) {
                name = args[i + 1];
                i += 2;
            } else if ("--description".equals(args[i])) {
                description = args[i + 1];
                i += 2;
            } else if ("--type".equals(args[i])) {
                type = args[i + 1];
                i += 2;
            } else if ("--ctype".equals(args[i])) {
                ctype = args[i + 1];
                i += 2;
            } else if ("--lctype".equals(args[i])) {
                lctype = args[i + 1];
                i += 2;
            } else if ("--atype".equals(args[i])) {
                atype = ArcType.valueOf(args[i + 1].toLowerCase());
                i += 2;
            } else if ("--specimen-type".equals(args[i])) {
                specimenType = args[i + 1];
                i += 2;
            } else if ("--image-type".equals(args[i])) {
                imageType = args[i + 1];
                i += 2;
            } else if ("--tags".equals(args[i])) {
                tags = args[i + 1].split(",");
                i += 2;
            } else if ("--filename".equals(args[i])) {
                filename = args[i + 1];
                i += 2;
            } else if ("--input".equals(args[i])) {
                input = args[i + 1];
                i += 2;
            } else if ("--recursive".equals(args[i])) {
                recursive = true;
                i++;
            } else if ("--fillin".equals(args[i])) {
                fillin = true;
                i++;
            } else {
                System.err.println("Error: unexpected argument: " + args[i]);
                i++;
            }
        }
        if (pid == null) {
            System.err.println("Error: missing argument --pid");
            printUsage("create", "derivation");
            System.exit(1);
        }
        if (source == null) {
            System.err.println("Error: missing argument --pid");
            printUsage("create", "derivation");
            System.exit(1);
        }
        if (input == null) {
            System.err.println("Error: missing input file/directory.");
            printUsage("create", "derivation");
            System.exit(1);
        }
        File f = new File(input);
        if (!f.exists()) {
            System.err.println("Error: " + input + " does not exist.");
            printUsage("create", "derivation");
            System.exit(1);
        }
        ServerClient.Connection cxn = Server.connect();
        try {
            String datasetCid = DatasetUtil.findDatasetBySource(cxn, pid,
                    source);
            if (datasetCid != null) {
                System.out.println("dataset " + datasetCid + " created from "
                        + source + " already exists.");
            } else {
                datasetCid = DatasetUtil.createDerivedDataset(cxn, pid,
                        inputCid == null ? null : new String[] { inputCid },
                        name, description, type, ctype, lctype, filename,
                        source, tags, specimenType, imageType, f, recursive,
                        atype, fillin);
            }
            return datasetCid;
        } finally {
            Server.disconnect();
        }

    }

    private static String createPrimaryDataset(String[] args) throws Throwable {
        String source = null;
        String pid = null;
        String name = null;
        String description = null;
        String type = null;
        String ctype = null;
        String lctype = null;
        ArcType atype = null;
        String specimenType = null;
        String imageType = null;
        String filename = null;
        String[] tags = null;
        String input = System.getProperty("user.dir");
        System.out.println(input);
        boolean recursive = false;
        boolean fillin = false;

        for (int i = 0; i < args.length;) {
            if ("--pid".equals(args[i])) {
                pid = args[i + 1];
                i += 2;
            } else if ("--source".equals(args[i])) {
                source = args[i + 1];
                i += 2;
            } else if ("--name".equals(args[i])) {
                name = args[i + 1];
                i += 2;
            } else if ("--description".equals(args[i])) {
                description = args[i + 1];
                i += 2;
            } else if ("--type".equals(args[i])) {
                type = args[i + 1];
                i += 2;
            } else if ("--ctype".equals(args[i])) {
                ctype = args[i + 1];
                i += 2;
            } else if ("--lctype".equals(args[i])) {
                lctype = args[i + 1];
                i += 2;
            } else if ("--atype".equals(args[i])) {
                atype = ArcType.valueOf(args[i + 1].toLowerCase());
                i += 2;
            } else if ("--specimen-type".equals(args[i])) {
                specimenType = args[i + 1];
                i += 2;
            } else if ("--image-type".equals(args[i])) {
                imageType = args[i + 1];
                i += 2;
            } else if ("--tags".equals(args[i])) {
                tags = args[i + 1].split(",");
                i += 2;
            } else if ("--filename".equals(args[i])) {
                filename = args[i + 1];
                i += 2;
            } else if ("--input".equals(args[i])) {
                input = args[i + 1];
                i += 2;
            } else if ("--recursive".equals(args[i])) {
                recursive = true;
                i++;
            } else if ("--fillin".equals(args[i])) {
                fillin = true;
                i++;
            } else {
                System.err.println("Error: unexpected argument: " + args[i]);
                i++;
            }
        }
        if (pid == null) {
            System.err.println("Error: missing argument --pid");
            printUsage("create", "derivation");
            System.exit(1);
        }
        if (source == null) {
            System.err.println("Error: missing argument --pid");
            printUsage("create", "derivation");
            System.exit(1);
        }
        if (input == null) {
            System.err.println("Error: missing input file/directory.");
            printUsage("create", "derivation");
            System.exit(1);
        }
        File f = new File(input);
        if (!f.exists()) {
            System.err.println("Error: " + input + " does not exist.");
            printUsage("create", "derivation");
            System.exit(1);
        }
        ServerClient.Connection cxn = Server.connect();
        try {
            String datasetCid = DatasetUtil.findDatasetBySource(cxn, pid,
                    source);
            if (datasetCid != null) {
                System.out.println("dataset " + datasetCid + " created from "
                        + source + " already exists.");
            } else {
                datasetCid = DatasetUtil.createPrimaryDataset(cxn, pid, name,
                        description, type, ctype, lctype, filename, source,
                        tags, specimenType, imageType, f, recursive, atype,
                        fillin);
            }
            return datasetCid;
        } finally {
            Server.disconnect();
        }

    }

    private static void createStudy(String[] args) throws Throwable {
        String specimenNo = null;
        String name = null;
        String description = null;
        String step = null;
        String studyType = null;
        String[] tags = null;
        boolean checkExistence = false;
        for (int i = 0; i < args.length;) {
            if ("--specimen-no".equals(args[i])) {
                specimenNo = args[i + 1];
                i += 2;
            } else if ("--name".equals(args[i])) {
                name = args[i + 1];
                i += 2;
            } else if ("--description".equals(args[i])) {
                description = args[i + 1];
                i += 2;
            } else if ("--step".equals(args[i])) {
                step = args[i + 1];
                i += 2;
            } else if ("--type".equals(args[i])) {
                studyType = args[i + 1];
                i += 2;
            } else if ("--tags".equals(args[i])) {
                tags = args[i + 1].split(",");
                i += 2;
            } else if ("--check-existence".equals(args[i])) {
                checkExistence = true;
                i++;
            } else {
                System.err.println(
                        "Unexpected argument: " + args[i] + ". Ignored.");
                i++;
            }
        }
        if (specimenNo == null) {
            System.err.println("Error: missing argument --specimen-no.");
            printUsage("create", "study");
            System.exit(1);
        }
        String exMethodId = PROJECT_CID + "." + specimenNo + ".1";
        if (step == null && studyType == null) {
            System.err.println(
                    "Error: missing arguments. Either --step or --type must be specified.");
            printUsage("create", "study");
            System.exit(1);
        }
        MasterSpreadsheet.SubjectRecord record = LocalFileSystem
                .getMasterSpreadsheet().getRecord(Integer.parseInt(specimenNo));
        if (record == null) {
            throw new Exception("No record found for specimen number: "
                    + specimenNo + " in master spreadsheet.");
        }
        ServerClient.Connection cxn = Server.connect();
        try {
            if (checkExistence) {
                StudyUtil.findOrCreateStudy(cxn, exMethodId, step, name,
                        description, record, tags);
            } else {
                StudyUtil.createStudy(cxn, exMethodId, step, name, description,
                        record, tags);
            }
        } finally {
            Server.disconnect();
        }
    }

    public static void main(String[] args) throws Throwable {
        if (args == null || args.length < 2) {
            printUsage(null, null);
            System.exit(1);
        }
        String action = args[0];
        String objectType = args[1];
        if ("create".equalsIgnoreCase(action)) {
            if ("study".equalsIgnoreCase(objectType)) {
                createStudy(Arrays.copyOfRange(args, 2, args.length));
            } else if ("primary".equalsIgnoreCase(objectType)) {
                createPrimaryDataset(Arrays.copyOfRange(args, 2, args.length));
            } else if ("derivation".equalsIgnoreCase(objectType)) {
                createDerivedDataset(Arrays.copyOfRange(args, 2, args.length));
            } else {
                System.err.println("Error: invalid object type: " + objectType);
                printUsage("create", null);
                System.exit(1);
            }
        } else if ("update".equalsIgnoreCase(action)) {
            if ("study".equalsIgnoreCase(objectType)) {
                updateStudy(Arrays.copyOfRange(args, 2, args.length));
            } else if ("primary".equalsIgnoreCase(objectType)) {
                updatePrimaryDataset(Arrays.copyOfRange(args, 2, args.length));
            } else if ("derivation".equalsIgnoreCase(objectType)) {
                updateDerivedDataset(Arrays.copyOfRange(args, 2, args.length));
            } else {
                System.err.println("Error: invalid object type: " + objectType);
                printUsage("update", null);
                System.exit(1);
            }
        } else {
            System.err.println("Error: invalid action: " + action);
            printUsage(null, null);
            System.exit(1);
        }
    }

    private static void printUsage(String action, String objectType) {
        if (action == null) {
            System.out.println(
                    "Usage: <create|update> <study|primary|derivation> [options]");
            return;
        }
        if (objectType == null) {
            System.out.println("Usage: " + action
                    + " <study|primary|derivation> [options]");
            return;
        }
        System.out
                .println("Usage: " + action + " " + objectType + " [options]");
        System.out.println("Options:");
        if ("create".equalsIgnoreCase(action)) {
            if ("study".equalsIgnoreCase(objectType)) {
                System.out.println(
                        "        --specimen-no <number>          The specimen number of the subject.");
                System.out.println(
                        "        --name <name>                   The name of the study.");
                System.out.println(
                        "        --description <description>     The description of the study.");
                System.out.println(
                        "        --step <step>                   The method step of the study.");
                System.out.println(
                        "        --study-type <type>             The study type.");
                System.out.println(
                        "        --tags <tag1,tag2>              The tags for the study. Separated with comma.");
                System.out.println(
                        "        --check-existence               If given it will check for existing study and ignore if the study with the same name exists.");
            }
            if ("primary".equalsIgnoreCase(objectType)) {
                System.out.println(
                        "        --pid <study-cid>               The citeable id of the parent study.");
                System.out.println(
                        "        --name <name>                   The name of the dataset.");
                System.out.println(
                        "        --description <description>     The description of the dataset.");
                System.out.println(
                        "        --source <source>               The source location of the dataset.");
                System.out.println(
                        "        --type <type>                   The mime type of the dataset.");
                System.out.println(
                        "        --ctype <type>                  The content mime type of the dataset.");
                System.out.println(
                        "        --lctype <type>                 The logical content mime type of the dataset.");
                System.out.println(
                        "        --specimen-type <specimen-type> The femur specimen type of the dataset.");
                System.out.println(
                        "        --image-type <image-type>       The femur image type of the dataset.");
                System.out.println(
                        "        --filename <filename>           The name for the content file.");
                System.out.println(
                        "        --tags <tag1,tag2>              The tags for the dataset. Separated with comma.");
                System.out.println(
                        "        --atype <archive-type>          The content archive type. Must be aar or zip.");
                System.out.println(
                        "        --input <input-dir>             The path to the input directory.");
                System.out.println(
                        "        --recursive                     If specified, include the input directory recursively.");
                System.out.println(
                        "        --fillin                        If specified, fill in the cid gap when creating dataset.");

            }
            if ("derivation".equalsIgnoreCase(objectType)) {
                System.out.println(
                        "        --pid <study-cid>               The citeable id of the parent study.");
                System.out.println(
                        "        --input-cid <cid>               The citeable id of the input dataset.");
                System.out.println(
                        "        --name <name>                   The name of the dataset.");
                System.out.println(
                        "        --description <description>     The description of the dataset.");
                System.out.println(
                        "        --source <source>               The source location of the dataset.");
                System.out.println(
                        "        --type <type>                   The mime type of the dataset.");
                System.out.println(
                        "        --ctype <type>                  The content mime type of the dataset.");
                System.out.println(
                        "        --lctype <type>                 The logical content mime type of the dataset.");
                System.out.println(
                        "        --specimen-type <specimen-type> The femur specimen type of the dataset.");
                System.out.println(
                        "        --image-type <image-type>       The femur image type of the dataset.");
                System.out.println(
                        "        --filename <filename>           The name for the content file.");
                System.out.println(
                        "        --tags <tag1,tag2>              The tags for the dataset. Separated with comma.");
                System.out.println(
                        "        --atype <archive-type>          The content archive type. Must be aar or zip.");
                System.out.println(
                        "        --input <input-dir>             The path to the input directory.");
                System.out.println(
                        "        --recursive                     If specified, include the input directory recursively.");
                System.out.println(
                        "        --fillin                        If specified, fill in the cid gap when creating dataset.");
            }
        }
        if ("update".equalsIgnoreCase(action)) {
            if ("study".equalsIgnoreCase(objectType)) {
                System.out.println(
                        "        --cid <cid>                     The citeable id of the dataset.");
                System.out.println(
                        "        --name <name>                   The name of the dataset.");
                System.out.println(
                        "        --description <description>     The description of the dataset.");
                System.out.println(
                        "        --tags <tag1,tag2>              The tags for the dataset. Separated with comma.");

            }
            if ("primary".equalsIgnoreCase(objectType)) {
                System.out.println(
                        "        --cid <cid>                     The citeable id of the dataset.");
                System.out.println(
                        "        --name <name>                   The name of the dataset.");
                System.out.println(
                        "        --description <description>     The description of the dataset.");
                System.out.println(
                        "        --source <source>               The source location of the dataset.");
                System.out.println(
                        "        --type <type>                   The mime type of the dataset.");
                System.out.println(
                        "        --ctype <type>                  The content mime type of the dataset.");
                System.out.println(
                        "        --lctype <type>                 The logical content mime type of the dataset.");
                System.out.println(
                        "        --specimen-type <specimen-type> The femur specimen type of the dataset.");
                System.out.println(
                        "        --image-type <image-type>       The femur image type of the dataset.");
                System.out.println(
                        "        --filename <filename>           The name for the content file.");
                System.out.println(
                        "        --tags <tag1,tag2>              The tags for the dataset. Separated with comma.");

            }
            if ("derivation".equalsIgnoreCase(objectType)) {
                System.out.println(
                        "        --cid <cid>                     The citeable id of the dataset.");
                System.out.println(
                        "        --input-cid <cid>               The citeable id of the input dataset.");
                System.out.println(
                        "        --name <name>                   The name of the dataset.");
                System.out.println(
                        "        --description <description>     The description of the dataset.");
                System.out.println(
                        "        --source <source>               The source location of the dataset.");
                System.out.println(
                        "        --type <type>                   The mime type of the dataset.");
                System.out.println(
                        "        --ctype <type>                  The content mime type of the dataset.");
                System.out.println(
                        "        --lctype <type>                 The logical content mime type of the dataset.");
                System.out.println(
                        "        --specimen-type <specimen-type> The femur specimen type of the dataset.");
                System.out.println(
                        "        --image-type <image-type>       The femur image type of the dataset.");
                System.out.println(
                        "        --filename <filename>           The name for the content file.");
                System.out.println(
                        "        --tags <tag1,tag2>              The tags for the dataset. Separated with comma.");

            }
        }
    }

    private static void updateDerivedDataset(String[] args) throws Throwable {
        String cid = null;
        String source = null;
        String inputCid = null;
        String name = null;
        String description = null;
        String type = null;
        String ctype = null;
        String lctype = null;
        String specimenType = null;
        String imageType = null;
        String filename = null;
        String[] tags = null;

        for (int i = 0; i < args.length;) {
            if ("--cid".equals(args[i])) {
                cid = args[i + 1];
                i += 2;
            } else if ("--input-cid".equals(args[i])) {
                inputCid = args[i + 1];
                i += 2;
            } else if ("--source".equals(args[i])) {
                source = args[i + 1];
                i += 2;
            } else if ("--name".equals(args[i])) {
                name = args[i + 1];
                i += 2;
            } else if ("--description".equals(args[i])) {
                description = args[i + 1];
                i += 2;
            } else if ("--type".equals(args[i])) {
                type = args[i + 1];
                i += 2;
            } else if ("--ctype".equals(args[i])) {
                ctype = args[i + 1];
                i += 2;
            } else if ("--lctype".equals(args[i])) {
                lctype = args[i + 1];
                i += 2;
            } else if ("--specimen-type".equals(args[i])) {
                specimenType = args[i + 1];
                i += 2;
            } else if ("--image-type".equals(args[i])) {
                imageType = args[i + 1];
                i += 2;
            } else if ("--tags".equals(args[i])) {
                tags = args[i + 1].split(",");
                i += 2;
            } else if ("--filename".equals(args[i])) {
                filename = args[i + 1];
                i += 2;
            } else {
                System.err.println("Error: unexpected argument: " + args[i]);
                i++;
            }
        }
        if (cid == null) {
            System.err.println("Error: missing argument --cid");
            printUsage("update", "primary");
            System.exit(1);
        }
        ServerClient.Connection cxn = Server.connect();
        try {
            DatasetUtil.updateDerivedDataset(cxn, cid,
                    inputCid == null ? null : new String[] { inputCid }, name,
                    description, type, ctype, lctype, filename, source, tags,
                    specimenType, imageType);
        } finally {
            Server.disconnect();
        }
    }

    private static void updatePrimaryDataset(String[] args) throws Throwable {
        String source = null;
        String cid = null;
        String name = null;
        String description = null;
        String type = null;
        String ctype = null;
        String lctype = null;
        String specimenType = null;
        String imageType = null;
        String filename = null;
        String[] tags = null;

        for (int i = 0; i < args.length;) {
            if ("--cid".equals(args[i])) {
                cid = args[i + 1];
                i += 2;
            } else if ("--source".equals(args[i])) {
                source = args[i + 1];
                i += 2;
            } else if ("--name".equals(args[i])) {
                name = args[i + 1];
                i += 2;
            } else if ("--description".equals(args[i])) {
                description = args[i + 1];
                i += 2;
            } else if ("--type".equals(args[i])) {
                type = args[i + 1];
                i += 2;
            } else if ("--ctype".equals(args[i])) {
                ctype = args[i + 1];
                i += 2;
            } else if ("--lctype".equals(args[i])) {
                lctype = args[i + 1];
                i += 2;
            } else if ("--specimen-type".equals(args[i])) {
                specimenType = args[i + 1];
                i += 2;
            } else if ("--image-type".equals(args[i])) {
                imageType = args[i + 1];
                i += 2;
            } else if ("--tags".equals(args[i])) {
                tags = args[i + 1].split(",");
                i += 2;
            } else if ("--filename".equals(args[i])) {
                filename = args[i + 1];
                i += 2;
            } else {
                System.err.println("Error: unexpected argument: " + args[i]);
                i++;
            }
        }
        if (cid == null) {
            System.err.println("Error: missing argument --cid");
            printUsage("update", "primary");
            System.exit(1);
        }
        ServerClient.Connection cxn = Server.connect();
        try {
            DatasetUtil.updatePrimaryDataset(cxn, cid, name, description, type,
                    ctype, lctype, filename, source, tags, specimenType,
                    imageType);
        } finally {
            Server.disconnect();
        }
    }

    private static void updateStudy(String[] args) throws Throwable {
        String cid = null;
        String name = null;
        String description = null;
        String[] tags = null;
        for (int i = 0; i < args.length;) {
            if ("--cid".equals(args[i])) {
                cid = args[i + 1];
                i += 2;
            } else if ("--name".equals(args[i])) {
                name = args[i + 1];
                i += 2;
            } else if ("--description".equals(args[i])) {
                description = args[i + 1];
                i += 2;
            } else if ("--tags".equals(args[i])) {
                tags = args[i + 1].split(",");
                i += 2;
            } else {
                System.err.println(
                        "Unexpected argument: " + args[i] + ". Ignored.");
                i++;
            }
        }
        if (cid == null) {
            System.err.println("Error: missing argument --cid.");
            printUsage("update", "study");
            System.exit(1);
        }
        String specimenNo = CidUtil.getLastPart(CidUtil.getParentCid(cid, 2));
        MasterSpreadsheet.SubjectRecord record = LocalFileSystem
                .getMasterSpreadsheet().getRecord(Integer.parseInt(specimenNo));
        if (record == null) {
            throw new Exception("No record found for specimen number: "
                    + specimenNo + " in master spreadsheet.");
        }
        ServerClient.Connection cxn = Server.connect();
        try {
            StudyUtil.updateStudy(cxn, cid, name, description, record, tags);
        } finally {
            Server.disconnect();
        }

    }

}
