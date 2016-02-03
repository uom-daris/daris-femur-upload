package vicnode.daris.femur.upload;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;

import arc.archive.ArchiveOutput;
import arc.archive.ArchiveRegistry;
import arc.mf.client.ServerClient;
import arc.mf.client.archive.Archive;
import arc.streams.StreamCopy;
import arc.streams.StreamCopy.AbortCheck;
import arc.xml.XmlStringWriter;
import vicnode.daris.femur.upload.ArcUtil.ArcType;

public class DatasetUtil {

    public static String createDerivedDataset(ServerClient.Connection cxn,
            String pid, String[] inputDatasets, String name, String description,
            String type, String ctype, String lctype, boolean fillin,
            String filename, String methodId, String methodStep, String source,
            String[] tags, String femurSpecimenType, String femurImageType,
            File f) throws Throwable {
        XmlStringWriter w = new XmlStringWriter();
        w.add("pid", pid);
        if (inputDatasets != null) {
            for (String inputDataset : inputDatasets) {
                String vid = getVid(cxn, inputDataset);
                w.add("input", new String[] { "vid", vid }, inputDataset);
            }
        }
        if (name != null) {
            w.add("name", name);
        }
        if (description != null) {
            w.add("description", description);
        }
        if (type != null) {
            w.add("type", type);
        }
        if (ctype != null) {
            w.add("ctype", ctype);
        }
        if (lctype != null) {
            w.add("lctype", lctype);
        }
        if (fillin) {
            w.add("fillin", fillin);
        }
        if (filename != null) {
            w.add("filename", filename);
        }
        if (methodId != null && methodStep != null) {
            w.push("method");
            w.add("id", methodId);
            w.add("step", methodStep);
            w.pop();
        }
        if (source != null) {
            w.push("meta");
            w.push("mf-note");
            w.add("note", "source: " + source);
            w.pop();
            if (femurSpecimenType != null || femurImageType != null) {
                w.push("vicnode.daris:femur-dataset");
                if (femurSpecimenType != null) {
                    w.add("specimen-type", femurSpecimenType);
                }
                if (femurImageType != null) {
                    w.add("image-type", femurImageType);
                }
                w.pop();
            }
            w.pop();
        }
        ServerClient.Input sci = null;
        if (f != null) {
            sci = createInput(f, Constants.ARC_TYPE, source);
        }
        String cid = cxn.execute("om.pssd.dataset.derivation.create",
                w.document(), sci, null).value("id");
        if (tags != null) {
            for (String tag : tags) {
                ObjectUtil.addObjectTag(cxn, cid, tag);
            }
        }
        return cid;
    }

    public static String createPrimaryDataset(ServerClient.Connection cxn,
            String pid, String name, String description, String type,
            String ctype, String lctype, boolean fillin, String filename,
            String methodId, String methodStep, String source, String[] tags,
            String femurSpecimenType, String femurImageType, File f)
                    throws Throwable {
        XmlStringWriter w = new XmlStringWriter();
        w.add("pid", pid);
        w.push("subject");
        w.add("id", CidUtil.getSubjectCid(pid));
        w.pop();
        if (name != null) {
            w.add("name", name);
        }
        if (description != null) {
            w.add("description", description);
        }
        if (type != null) {
            w.add("type", type);
        }
        if (ctype != null) {
            w.add("ctype", ctype);
        }
        if (lctype != null) {
            w.add("lctype", lctype);
        }
        if (fillin) {
            w.add("fillin", fillin);
        }
        if (filename != null) {
            w.add("filename", filename);
        }
        if (methodId != null && methodStep != null) {
            w.push("method");
            w.add("id", methodId);
            w.add("step", methodStep);
            w.pop();
        }
        if (source != null) {
            w.push("meta");
            w.push("mf-note");
            w.add("note", "source: " + source);
            w.pop();
            if (femurSpecimenType != null || femurImageType != null) {
                w.push("vicnode.daris:femur-dataset");
                if (femurSpecimenType != null) {
                    w.add("specimen-type", femurSpecimenType);
                }
                if (femurImageType != null) {
                    w.add("image-type", femurImageType);
                }
                w.pop();
            }
            w.pop();
        }
        ServerClient.Input sci = null;
        if (f != null) {
            sci = createInput(f, Constants.ARC_TYPE, source);
        }
        String cid = cxn.execute("om.pssd.dataset.primary.create", w.document(),
                sci, null).value("id");
        if (tags != null) {
            for (String tag : tags) {
                ObjectUtil.addObjectTag(cxn, cid, tag);
            }
        }
        return cid;
    }

    private static String getVid(ServerClient.Connection cxn, String cid)
            throws Throwable {
        return cxn.execute("asset.get", "<cid>" + cid + "</cid>")
                .value("asset/@vid");
    }

    public static String findDatasetBySource(ServerClient.Connection cxn,
            String pid, String source) throws Throwable {
        StringBuilder sb = new StringBuilder();
        sb.append("cid starts with '").append(pid)
                .append("' and xpath(mf-note/note)='source: ").append(source)
                .append("'");
        XmlStringWriter w = new XmlStringWriter();
        w.add("where", sb.toString());
        w.add("action", "get-cid");
        return cxn.execute("asset.query", w.document()).value("cid");
    }

    private static ServerClient.Input createInput(File f, ArcType arcType,
            String source) throws Throwable {
        String mimeType = null;
        String ext = null;
        if (f.isDirectory()) {
            mimeType = arcType == null ? ArcType.zip.mimeType()
                    : arcType.mimeType();
            ext = arcType == null ? ArcType.zip.ext() : arcType.ext();
        } else {
            mimeType = null;
            int idx = f.getName().lastIndexOf('.');
            if (idx >= 0) {
                ext = f.getName().substring(idx + 1);
            }
        }
        return new ServerClient.GeneratedInput(mimeType, ext, source, -1,
                null) {

            @Override
            protected void copyTo(OutputStream out, AbortCheck abort)
                    throws Throwable {
                if (f.isFile()) {
                    InputStream in = new BufferedInputStream(
                            new FileInputStream(f));
                    try {
                        StreamCopy.copy(in, out);
                    } finally {
                        in.close();
                        out.close();
                    }
                } else if (f.isDirectory()) {
                    Archive.declareSupportForAllTypes();
                    ArchiveOutput ao = ArchiveRegistry.createOutput(out,
                            arcType == null ? ArcType.zip.mimeType()
                                    : arcType.mimeType(),
                            6, null);
                    try {
                        ArcUtil.arcDir(f, ao);
                    } finally {
                        ao.close();
                    }
                }
            }
        };
    }

}
