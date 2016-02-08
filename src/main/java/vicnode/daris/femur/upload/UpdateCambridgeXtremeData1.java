package vicnode.daris.femur.upload;

import arc.mf.client.ServerClient;
import arc.xml.XmlDoc;
import arc.xml.XmlStringWriter;

public class UpdateCambridgeXtremeData1 {

    public static final int[] SPECIMEN_NUMBERS = new int[] { 443, 452, 456, 458,
            464, 465, 466, 467, 469, 470, 473, 474, 477, 483, 485, 493, 494 };

    public static final String SOURCE_ROOT = "Cambridge Xtreme/Xtreme pQCT data-Cambridge/VIFM";

    private static void doPostIngestMetadataUpdate(ServerClient.Connection cxn,
            int specimenNo, MasterSpreadsheet ms) throws Throwable {
        String subjectCid = "1128.1.1." + specimenNo;
        String studyCid = subjectCid + ".1.1";
        String datasetCid = studyCid + ".1";
        updateSubjectDicomMetadata(cxn, subjectCid, datasetCid);
        MasterSpreadsheet.SubjectRecord record = ms.getRecord(specimenNo);
        assert record != null;
        updateStudyMetadata(cxn, studyCid, record);
        updateDatasetMetadata(cxn, datasetCid, specimenNo, record);
    }

    private static void updateDatasetMetadata(ServerClient.Connection cxn,
            String datasetCid, int specimenNo,
            MasterSpreadsheet.SubjectRecord record) throws Throwable {
        XmlDoc.Element ae = cxn
                .execute("asset.get", "<cid>" + datasetCid + "</cid>")
                .element("asset");
        String ext = ae.value("content/type/@ext");

        XmlStringWriter w = new XmlStringWriter();
        w.add("cid", datasetCid);
        w.push("meta");
        w.push("vicnode.daris:femur-dataset",
                new String[] { "ns", "om.pssd.dataset", "tag", "pssd.meta" });
        w.add("specimen-type", record.specimenType);
        w.add("image-type", "pQCT");
        w.pop();
        w.push("mf-note",
                new String[] { "ns", "om.pssd.dataset", "tag", "pssd.meta" });
        w.add("note", "source: " + SOURCE_ROOT + specimenNo);
        w.pop();
        w.push("daris:pssd-filename", new String[] {});
        w.add("original",
                "Cambridge-Xtreme-pQCT-VIFM" + specimenNo + "." + ext);
        w.pop();
        w.pop();
        cxn.execute("asset.set", w.document());
        ObjectUtil.addObjectTag(cxn, datasetCid, "pQCT");
        ObjectUtil.addObjectTag(cxn, datasetCid, record.specimenType);
    }

    private static void updateStudyMetadata(ServerClient.Connection cxn,
            String studyCid, MasterSpreadsheet.SubjectRecord record)
                    throws Throwable {
        StudyUtil.updateStudy(cxn, studyCid, "Cambridge Xtreme pQCT",
                "Cambridge Xtreme pQCT", record, new String[] { "pQCT" });
    }

    private static void updateSubjectDicomMetadata(ServerClient.Connection cxn,
            String subjectCid, String datasetCid) throws Throwable {
        XmlDoc.Element dicomMetadata = DatasetUtil.getDicomMetadata(cxn,
                datasetCid);
        String patientName = dicomMetadata.value("de[@tag='00100010']/value");
        String patientId = dicomMetadata.value("de[@tag='00100020']/value");
        if (patientName != null && patientId != null) {
            XmlStringWriter w = new XmlStringWriter();
            w.add("cid", subjectCid);
            w.push("meta");
            w.push("mf-dicom-patient", new String[] { "ns", "pssd.private" });
            if (patientId != null) {
                w.add("id", patientId);
            }
            if (patientName != null) {
                int idx = patientName.indexOf('^');
                if (idx >= 0) {
                    w.add("name", new String[] { "type", "first" },
                            patientName.substring(idx + 1));
                    w.add("name", new String[] { "type", "last" },
                            patientName.substring(0, idx));
                } else {
                    w.add("name", new String[] { "type", "last" }, patientName);
                }
            }
            w.pop();
            w.pop();
            cxn.execute("asset.set", w.document());
        }
    }

    public static void main(String[] args) throws Throwable {
        ServerClient.Connection cxn = Server.connect();
        try {
            for (int specimenNo : SPECIMEN_NUMBERS) {
                System.out.println("Processing " + specimenNo);
                doPostIngestMetadataUpdate(cxn, specimenNo,
                        LocalFileSystem.getMasterSpreadsheet());
            }
        } finally {
            Server.disconnect();
        }
    }
}
