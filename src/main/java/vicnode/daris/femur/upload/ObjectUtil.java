package vicnode.daris.femur.upload;

import java.io.File;

import arc.mf.client.ServerClient;
import arc.mf.client.ServerClient.FileInput;
import arc.xml.XmlStringWriter;

public class ObjectUtil {

    public static String addAttachment(ServerClient.Connection cxn, String cid,
            File file, boolean replace) throws Throwable {
        String attachmentAssetId = findAttachment(cxn, cid, file.getName());
        if (attachmentAssetId != null && !replace) {
            return attachmentAssetId;
        }

        XmlStringWriter w = new XmlStringWriter();

        if (attachmentAssetId == null) {
            String ns = namespaceOf(cxn, cid);
            w.add("name", file.getName());
            w.add("namespace", ns);
            w.push("related");
            w.add("from-cid", new String[] { "relationship", "attachment" },
                    cid);
            w.pop();
        } else {
            w.add("id", attachmentAssetId);
        }
        ServerClient.Input in = new FileInput(file);
        try {
            cxn.execute(
                    attachmentAssetId == null ? "asset.create" : "asset.set",
                    w.document(), in, null);
        } finally {
            in.close();
        }
        return attachmentAssetId;
    }

    public static String findAttachment(ServerClient.Connection cxn, String cid,
            String fileName) throws Throwable {
        XmlStringWriter w = new XmlStringWriter();
        w.add("where",
                new StringBuilder().append("name='").append(fileName)
                        .append("' and related to{attached-to}(cid='")
                        .append(cid).append("')").toString());
        return cxn.execute("asset.query", w.document(), null, null).value("id");
    }

    public static String namespaceOf(ServerClient.Connection cxn, String cid)
            throws Throwable {
        return cxn.execute("asset.get", "<cid>" + cid + "</cid>", null, null)
                .value("asset/namespace");
    }

    public static void addObjectTag(ServerClient.Connection cxn, String cid,
            String tag) throws Throwable {
        cxn.execute("om.pssd.object.tag.add",
                "<cid>" + cid + "</cid><tag><name>" + tag + "</name></tag>",
                null, null);

    }

    public static String assetIdFromCid(ServerClient.Connection cxn, String cid)
            throws Throwable {
        return cxn.execute("asset.get", "<cid>" + cid + "</cid>", null, null)
                .value("asset/@id");
    }
}
