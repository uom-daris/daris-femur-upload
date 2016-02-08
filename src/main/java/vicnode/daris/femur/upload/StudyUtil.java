package vicnode.daris.femur.upload;

import java.util.Collection;
import java.util.Date;

import arc.mf.client.ServerClient;
import arc.xml.XmlStringWriter;

public class StudyUtil {

    public static String createStudy(ServerClient.Connection cxn, String pid,
            String step, String name, String description,
            MasterSpreadsheet.SubjectRecord record, String[] tags)
                    throws Throwable {
        XmlStringWriter w = new XmlStringWriter();
        w.add("pid", pid);
        w.add("fillin", true);
        w.add("step", step);
        if (name != null) {
            w.add("name", name);
        }
        if (description != null) {
            w.add("description", description);
        }
        if (record != null) {
            w.push("meta");
            w.push("vicnode.daris:femur-study");
            w.push("ingest");
            w.add("date", new Date());
            String[] ss = cxn.userId().split(":");
            if (ss != null && ss.length == 2) {
                w.add("domain", ss[0]);
                w.add("user", ss[1]);
            }
            w.pop();
            if (record.age != null || record.sex != null
                    || record.height != null || record.weight != null) {
                w.push("subject");
                if (record.age != null) {
                    w.add("age", record.age);
                }
                if (record.sex != null) {
                    w.add("sex", record.sex);
                }
                if (record.height != null) {
                    w.add("height", record.height);
                }
                if (record.weight != null) {
                    w.add("weight", record.weight);
                }
                w.pop();
            }
            for (int n : record.specimenNo) {
                w.add("specimen-number", n);
            }
            if (record.vifmCaseNo != null) {
                w.add("vifm-case-number", record.vifmCaseNo);
            }
            if (record.specimenType != null) {
                w.add("specimen-type", record.specimenType);
            }
            w.add("autopsy-report-or-medical-questionnaire",
                    record.autopsyReportOrMedicalQuestionnaire);
            w.add("blood", record.blood);
            w.add("mid-shaft-porosity-and-cross-sectional-geometry-data",
                    record.midShaftPorosityAndCrossSectionalGeometryData);
            w.add("hard-ground-sections", record.hardGroundSections);
            w.add("mounted-sections", record.mountedSections);
            w.add("two-inch-glass-plates", record.twoInchGlassPlates);
            w.add("plane-radio-graph-of-pelvis",
                    record.plainRadiographOfPelvis);
            w.pop();
            w.pop();
        }
        String cid = cxn.execute("om.pssd.study.create", w.document())
                .value("id");
        if (tags != null) {
            for (String tag : tags) {
                ObjectUtil.addObjectTag(cxn, cid, tag);
            }
        }
        return cid;
    }

    public static void updateStudy(ServerClient.Connection cxn, String cid,
            String name, String description,
            MasterSpreadsheet.SubjectRecord record, String[] tags)
                    throws Throwable {
        XmlStringWriter w = new XmlStringWriter();
        w.add("id", cid);
        if (name != null) {
            w.add("name", name);
        }
        if (description != null) {
            w.add("description", description);
        }
        if (record != null) {
            w.push("meta");
            w.push("vicnode.daris:femur-study");
            w.push("ingest");
            w.add("date", new Date());
            String[] ss = cxn.userId().split(":");
            if (ss != null && ss.length == 2) {
                w.add("domain", ss[0]);
                w.add("user", ss[1]);
            }
            w.pop();
            if (record.age != null || record.sex != null
                    || record.height != null || record.weight != null) {
                w.push("subject");
                if (record.age != null) {
                    w.add("age", record.age);
                }
                if (record.sex != null) {
                    w.add("sex", record.sex);
                }
                if (record.height != null) {
                    w.add("height", record.height);
                }
                if (record.weight != null) {
                    w.add("weight", record.weight);
                }
                w.pop();
            }
            for (int n : record.specimenNo) {
                w.add("specimen-number", n);
            }
            if (record.vifmCaseNo != null) {
                w.add("vifm-case-number", record.vifmCaseNo);
            }
            if (record.specimenType != null) {
                w.add("specimen-type", record.specimenType);
            }
            w.add("autopsy-report-or-medical-questionnaire",
                    record.autopsyReportOrMedicalQuestionnaire);
            w.add("blood", record.blood);
            w.add("mid-shaft-porosity-and-cross-sectional-geometry-data",
                    record.midShaftPorosityAndCrossSectionalGeometryData);
            w.add("hard-ground-sections", record.hardGroundSections);
            w.add("mounted-sections", record.mountedSections);
            w.add("two-inch-glass-plates", record.twoInchGlassPlates);
            w.add("plane-radio-graph-of-pelvis",
                    record.plainRadiographOfPelvis);
            w.pop();
            w.pop();
        }
        cxn.execute("om.pssd.study.update", w.document());
        if (tags != null) {
            for (String tag : tags) {
                ObjectUtil.addObjectTag(cxn, cid, tag);
            }
        }
    }

    public static String findStudy(ServerClient.Connection cxn, String pid,
            String step, String name) throws Throwable {
        XmlStringWriter w = new XmlStringWriter();
        w.add("id", pid);
        w.add("step", step);
        Collection<String> cids = cxn
                .execute("om.pssd.ex-method.step.study.find", w.document())
                .values("object/id");
        if (cids == null || cids.isEmpty()) {
            return null;
        }
        if (name == null) {
            return cids.iterator().next();
        }
        String cid = cxn.execute("asset.query",
                "<action>get-cid</action><where>xpath(daris:pssd-object/name)='"
                        + name + "' and cid starts with '" + pid
                        + "'</where><size>1</size>")
                .value("cid");
        if (cids.contains(cid)) {
            return cid;
        } else {
            return null;
        }
    }

    public static String findOrCreateStudy(ServerClient.Connection cxn,
            String pid, String step, String name, String description,
            MasterSpreadsheet.SubjectRecord record, String[] tags)
                    throws Throwable {
        String cid = findStudy(cxn, pid, step, name);
        if (cid != null) {
            return cid;
        }
        return createStudy(cxn, pid, step, name, description, record, tags);
    }

}
