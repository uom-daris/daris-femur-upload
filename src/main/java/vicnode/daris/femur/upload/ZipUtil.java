package vicnode.daris.femur.upload;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtil {
    public static final int BUFFER_SIZE = 8192;

    public static long zipDir(File dir, File zip) throws Throwable {
        return zipDir(dir, new FileOutputStream(zip));
    }

    private static long zipDir(File dir, OutputStream output) throws Throwable {
        CheckedOutputStream cos = new CheckedOutputStream(
                new BufferedOutputStream(output), new CRC32());
        ZipOutputStream zos = new ZipOutputStream(cos);
        try {
            zip(dir.listFiles(), dir.getAbsolutePath(), zos);
            return cos.getChecksum().getValue();
        } finally {
            zos.close();
            cos.close();
        }
    }

    private static void zip(File[] files, String baseDir, ZipOutputStream zos)
            throws Throwable {
        byte buffer[] = new byte[BUFFER_SIZE];
        for (File f : files) {
            String name = f.getAbsolutePath();
            if (name.startsWith(baseDir)) {
                name = name.substring(baseDir.length());
            }
            if (name.startsWith(System.getProperty("file.separator"))) {
                name = name.substring(1);
            }
            if (f.isDirectory()) {
                zip(f.listFiles(), baseDir, zos);
            } else {
                if (name.endsWith(".DS_Store")) {
                    continue;
                }
                ZipEntry entry = new ZipEntry(name);
                entry.setSize(f.length());
                entry.setCrc(crc32(f));
                zos.putNextEntry(entry);
                BufferedInputStream is = new BufferedInputStream(
                        new FileInputStream(f), BUFFER_SIZE);
                int count;
                try {
                    while ((count = is.read(buffer, 0, BUFFER_SIZE)) != -1) {
                        zos.write(buffer, 0, count);
                    }
                } finally {
                    is.close();
                }
                zos.closeEntry();
            }
        }
    }

    public static long crc32(File f) throws Throwable {

        CheckedInputStream cis = new CheckedInputStream(
                new BufferedInputStream(new FileInputStream(f)), new CRC32());
        try {
            while (cis.read() != -1) {
            }
            return cis.getChecksum().getValue();
        } finally {
            cis.close();
        }
    }

}
