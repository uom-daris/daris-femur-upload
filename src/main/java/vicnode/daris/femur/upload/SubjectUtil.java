package vicnode.daris.femur.upload;

import java.util.Set;

import arc.mf.client.ServerClient;
import arc.xml.XmlDoc;
import arc.xml.XmlStringWriter;

public class SubjectUtil {

    public static String createSubject(ServerClient.Connection cxn,
            String projectCid, MasterSpreadsheet.SubjectRecord record,
            boolean updateIfExists) throws Throwable {
        String subjectCid = findSubject(cxn, projectCid, record);
        boolean exists = subjectCid != null;
        boolean create = !exists;
        if (exists && !updateIfExists) {
            System.out.print("found subject asset " + subjectCid + ". Skip.");
            return subjectCid;
        }
        XmlStringWriter w = new XmlStringWriter();
        if (create) {
            w.add("pid", projectCid);
            String methodCid = cxn
                    .execute("om.pssd.object.describe",
                            "<id>" + projectCid + "</id>")
                    .value("object/method/id");
            w.add("method", methodCid);
            w.add("subject-number", record.specimenNo[0]);
            w.add("fillin", true);
        } else {
            w.add("id", subjectCid);
        }
        w.add("name", record.specimenNoString());
        w.add("description", "specimen no. " + record.specimenNoString());
        w.add("data-use", "unspecified");
        w.push("public");
        w.push("vicnode.daris:femur-subject");
        for (int n : record.specimenNo) {
            w.add("specimen-number", n);
        }
        if (record.vifmCaseNo != null) {
            w.add("vifm-case-number", record.vifmCaseNo);
        }
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
        w.pop();
        System.out.print((create ? "creating" : "updating") + " subject asset "
                + (create ? "..." : (subjectCid + "...")));
        XmlDoc.Element re = cxn.execute(
                create ? "om.pssd.subject.create" : "om.pssd.subject.update",
                w.document());
        if (create) {
            subjectCid = re.value("id");
            System.out.println("created " + subjectCid);
        } else {
            System.out.println("done.");
        }
        /*
         * Add subject tags
         */
        System.out.print("adding subject tags...");
        addSubjectTags(cxn, subjectCid, record);
        System.out.println("done.");
        return subjectCid;
    }

    public static void addSubjectTags(ServerClient.Connection cxn,
            String subjectCid, MasterSpreadsheet.SubjectRecord record)
                    throws Throwable {
        Set<String> tags = record.tags();
        for (String tag : tags) {
            ObjectUtil.addObjectTag(cxn, subjectCid, tag);
        }
    }

    public static String findSubject(ServerClient.Connection cxn,
            String projectCid, MasterSpreadsheet.SubjectRecord record)
                    throws Throwable {
        return findSubject(cxn, projectCid, record.specimenNo[0]);
    }

    public static String findSubject(ServerClient.Connection cxn,
            String projectCid, int specimenNo) throws Throwable {
        XmlStringWriter w = new XmlStringWriter();
        StringBuilder where = new StringBuilder().append("cid in '")
                .append(projectCid)
                .append("' and xpath(vicnode.daris:femur-subject/specimen-number) as string='")
                .append(specimenNo).append("'");
        w.add("where", where.toString());
        w.add("action", "get-cid");
        return cxn.execute("asset.query", w.document()).value("cid");
    }

    public static String findSubject(ServerClient.Connection cxn,
            int specimenNo) throws Throwable {
        return findSubject(cxn, ProjectUtil.findProject(cxn), specimenNo);
    }

}
