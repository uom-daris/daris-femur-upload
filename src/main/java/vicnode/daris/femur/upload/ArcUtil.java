package vicnode.daris.femur.upload;

import java.io.File;
import java.io.FilenameFilter;

import arc.archive.ArchiveOutput;
import arc.archive.ArchiveRegistry;
import arc.mf.client.archive.Archive;

public class ArcUtil {
    public static final int BUFFER_SIZE = 8192;

    public static enum ArcType {
        aar, zip;
        public String mimeType() {
            if (aar == this) {
                return "application/arc-archive";
            } else {
                return "application/zip";
            }
        }
        
        public String ext(){
            return this.name().toLowerCase();
        }
    }

    public static void arcDir(File dir, File outFile, ArcType type)
            throws Throwable {
        Archive.declareSupportForAllTypes();
        ArchiveOutput output = ArchiveRegistry.createOutput(outFile,
                type.mimeType(), 6, null);
        try {
            arcDir(dir, output);
        } finally {
            output.close();
        }
    }

    public static void arcDir(File dir, ArchiveOutput output) throws Throwable {
        arc(dir.listFiles(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                return !name.equals(".DS_Store");
            }
        }), dir.getAbsolutePath(), output);
    }

    private static void arc(File[] files, String baseDir, ArchiveOutput output)
            throws Throwable {

        for (File f : files) {
            String name = f.getAbsolutePath();
            if (name.startsWith(baseDir)) {
                name = name.substring(baseDir.length());
            }
            if (name.startsWith(System.getProperty("file.separator"))) {
                name = name.substring(1);
            }
            if (f.isDirectory()) {
                arc(f.listFiles(), baseDir, output);
            } else {
                output.add(null, name, f);
            }
        }
    }
}
