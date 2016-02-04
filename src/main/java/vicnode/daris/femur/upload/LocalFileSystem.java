package vicnode.daris.femur.upload;

import java.io.File;

public class LocalFileSystem {

    public static String trimRoot(String root, File f) {
        String path = f.getAbsolutePath();
        if (path.startsWith(root)) {
            path = path.substring(root.length());
        }
        if (path.startsWith("/") || path.startsWith(File.separator)) {
            path = path.substring(1);
        }
        return path;
    }

    public static MasterSpreadsheet getMasterSpreadsheet() throws Throwable {
        return new MasterSpreadsheet(
                new File(Configuration.masterSpreadsheetPath()));
    }

}
