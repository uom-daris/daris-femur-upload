package vicnode.daris.femur.stats;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import arc.mf.client.ServerClient;
import arc.xml.XmlDoc;
import arc.xml.XmlStringWriter;
import vicnode.daris.femur.upload.CidUtil;
import vicnode.daris.femur.upload.Server;

public class FemurStatUtils {

    public static void main(String[] args) throws Throwable {
         PrintWriter out = new PrintWriter(new
         FileWriter("/Users/wliu5/Desktop/femur-datasets.csv"));
         listDatasets("1128.1.1", out);

    }

    public static void listDatasets(String projectCid, PrintWriter out)
            throws Throwable {
        ServerClient.Connection cxn = Server.connect();
        try {
            XmlStringWriter w = new XmlStringWriter();
            w.add("where", "cid starts with '" + projectCid
                    + "' and model='om.pssd.dataset'");
            w.add("action", "get-values");
            w.add("size", "infinity");
            w.add("xpath", new String[] { "ename", "cid" }, "cid");
            w.add("xpath", new String[] { "ename", "name" },
                    "meta/daris:pssd-object/name");
            w.add("xpath", new String[] { "ename", "source" },
                    "meta/mf-note/note");
            List<XmlDoc.Element> aes = cxn.execute("asset.query", w.document())
                    .elements("asset");
            if (aes == null || aes.isEmpty()) {
                return;
            }
            SortedMap<String, String> sortedMap = new TreeMap<String, String>(
                    new Comparator<String>() {

                        @Override
                        public int compare(String cid1, String cid2) {
                            return CidUtil.compare(cid1, cid2);
                        }
                    });
            for (XmlDoc.Element ae : aes) {
                sortedMap.put(ae.value("cid"), datasetToString(cxn, ae));
            }
            for (String cid : sortedMap.keySet()) {
                out.println(sortedMap.get(cid));
                out.flush();
            }
        } finally {
            Server.disconnect();
        }
    }

    private static String datasetToString(ServerClient.Connection cxn,
            XmlDoc.Element ae) throws Throwable {
        StringBuilder sb = new StringBuilder();
        String cid = ae.value("cid");
        String specimenNo = CidUtil.getLastPart(CidUtil.getSubjectCid(cid));
        String name = ae.value("name");
        Thread.sleep(500);
        String studyName = cxn
                .execute("asset.get",
                        "<cid>" + CidUtil.getParentCid(cid) + "</cid>")
                .value("asset/meta/daris:pssd-object/name");
        sb.append(specimenNo).append(",");
        sb.append(cid).append(",");
        sb.append('"').append(name).append('"').append(",");
        sb.append('"').append(studyName).append('"').append(",");
        sb.append('"');
        Collection<String> svs = ae.values("source");
        if (svs != null && !svs.isEmpty()) {
            List<String> sources = new ArrayList<String>(svs);
            int size = sources.size();
            for (int i = 0; i < size; i++) {
                String source = sources.get(i);
                sb.append(source.replaceAll("^source: ", ""));
                if (i < size - 1) {
                    sb.append('\r');
                }
            }
        }
        sb.append('"');
        sb.append(",");
        System.out.println(sb.toString());
        return sb.toString();
    }

    static void listSubjects(String projectCid, PrintWriter out)
            throws Throwable {
        ServerClient.Connection cxn = Server.connect();
        try {
            XmlStringWriter w = new XmlStringWriter();
            w.add("where", "cid in '1128.1.1'");
            w.add("action", "get-value");
            w.add("size", "infinity");
            w.add("xpath", new String[] { "ename", "cid" }, "cid");
            w.add("xpath", new String[] { "ename", "name" },
                    "meta/daris:pssd-object/name");
            List<XmlDoc.Element> aes = cxn.execute("asset.query", w.document())
                    .elements("asset");
            if (aes == null || aes.isEmpty()) {
                return;
            }
            SortedMap<String, String> sortedMap = new TreeMap<String, String>(
                    new Comparator<String>() {

                        @Override
                        public int compare(String cid1, String cid2) {
                            return CidUtil.compare(cid1, cid2);
                        }
                    });
            for (XmlDoc.Element ae : aes) {
                sortedMap.put(ae.value("cid"), datasetToString(cxn, ae));
            }
            for (XmlDoc.Element ae : aes) {
                String cid = ae.value("cid");
                String name = ae.value("name");
                XmlStringWriter w2 = new XmlStringWriter();
                w2.add("cid", cid);
                w2.push("meta");
                w2.push("daris:pssd-object");
                w2.add("name", name.replace("100µm", "100 micrometre"));
                w2.pop();
                w2.pop();
                cxn.execute("asset.set", w2.document());
                System.out.println("Updated " + cid);
            }
        } finally {
            Server.disconnect();
        }
    }

    static void fixNames() throws Throwable {
        ServerClient.Connection cxn = Server.connect();
        try {
            XmlStringWriter w = new XmlStringWriter();
            w.add("where",
                    "cid starts with '1128.1.1' and model='om.pssd.dataset' and xpath(daris:pssd-object/name) starts with '100µm'");
            w.add("action", "get-value");
            w.add("size", "infinity");
            w.add("xpath", new String[] { "ename", "cid" }, "cid");
            w.add("xpath", new String[] { "ename", "name" },
                    "meta/daris:pssd-object/name");
            List<XmlDoc.Element> aes = cxn.execute("asset.query", w.document())
                    .elements("asset");
            if (aes == null || aes.isEmpty()) {
                return;
            }
            for (XmlDoc.Element ae : aes) {
                String cid = ae.value("cid");
                String name = ae.value("name");
                XmlStringWriter w2 = new XmlStringWriter();
                w2.add("cid", cid);
                w2.push("meta");
                w2.push("daris:pssd-object");
                w2.add("name", name.replace("100µm", "100 micrometre"));
                w2.pop();
                w2.pop();
                cxn.execute("asset.set", w2.document());
                System.out.println("Updated " + cid);
            }
        } finally {
            Server.disconnect();
        }
    }
}
