package vicnode.daris.femur.upload;

import java.io.File;
import java.util.Collection;

import arc.mf.client.ServerClient;
import arc.xml.XmlStringWriter;

public class ProjectUtil {

    public static final String SPECIMEN_TYPE_DICT_NAME = "vicnode.daris:femur.specimen.type";
    public static final String IMAGE_TYPE_DICT_NAME = "vicnode.daris:femur.image.type";

    public static final String CID_ROOT_NAME = "pssd.project";

    public static String createProject(ServerClient.Connection cxn, String name,
            String description, File masterSpreadsheetFile, boolean update)
                    throws Throwable {
        String projectCid = findProject(cxn, name);
        if (projectCid != null && !update) {
            System.out.println("found project asset " + projectCid + ". Skip.");
            return projectCid;
        }

        XmlStringWriter w = new XmlStringWriter();
        if (projectCid == null) {
            w.add("cid-root-name", CID_ROOT_NAME);
            w.add("fillin", true);
            w.add("data-use", "unspecified");
            w.add("name", name);
            w.add("description", description);
            w.push("method");
            w.add("id", MethodUtil.findFemurMethod(cxn));
            w.pop();
            System.out.print("creating project asset ...");
            projectCid = cxn
                    .execute("om.pssd.project.create", w.document(), null, null)
                    .value("id");
            System.out.println("created: " + projectCid);
        } else {
            w.add("id", projectCid);
            w.add("name", name);
            w.add("description", description);
            System.out.print("updating project asset " + projectCid + "...");
            cxn.execute("om.pssd.project.update", w.document(), null, null);
            System.out.println("done");
        }

        System.out.print("adding project attachment: "
                + masterSpreadsheetFile.getAbsolutePath() + "...");
        ObjectUtil.addAttachment(cxn, projectCid, masterSpreadsheetFile,
                update);
        System.out.println("done.");
        System.out.print("creating object tags...");
        ProjectUtil.createObjectTags(cxn, projectCid);
        System.out.println("done.");
        return projectCid;
    }

    public static String findProject(ServerClient.Connection cxn, String name)
            throws Throwable {
        XmlStringWriter w = new XmlStringWriter();
        w.add("action", "get-cid");
        w.add("where",
                "model='om.pssd.project' and xpath(daris:pssd-object/name) as string='"
                        + name + "'");
        return cxn.execute("asset.query", w.document(), null, null)
                .value("cid");
    }

    public static String findProject(ServerClient.Connection cxn)
            throws Throwable {
        return findProject(cxn, Configuration.projectName());
    }

    public static void createTagDictionaries(ServerClient.Connection cxn,
            String projectCid) throws Throwable {
        XmlStringWriter w = new XmlStringWriter();
        w.add("project", projectCid);
        cxn.execute("om.pssd.object.tag.dictionary.create", w.document(), null,
                null);
    }

    public static Collection<String> getSpecimenTypes(
            ServerClient.Connection cxn) throws Throwable {
        return cxn.execute("dictionary.entries.list",
                "<dictionary>" + SPECIMEN_TYPE_DICT_NAME + "</dictionary>",
                null, null).values("term");
    }

    public static Collection<String> getImageTypes(ServerClient.Connection cxn)
            throws Throwable {
        return cxn.execute("dictionary.entries.list",
                "<dictionary>" + IMAGE_TYPE_DICT_NAME + "</dictionary>", null,
                null).values("term");
    }

    public static void createObjectTags(ServerClient.Connection cxn,
            String projectCid) throws Throwable {
        createObjectTags(cxn, projectCid, "subject");
        createObjectTags(cxn, projectCid, "study");
        createObjectTags(cxn, projectCid, "dataset");
    }

    public static void createObjectTags(ServerClient.Connection cxn,
            String projectCid, String type) throws Throwable {

        XmlStringWriter w = new XmlStringWriter();
        w.add("project", projectCid);
        w.add("type", type);
        Collection<String> specimenTypes = ProjectUtil.getSpecimenTypes(cxn);
        for (String specimenType : specimenTypes) {
            w.push("tag");
            w.add("name", specimenType);
            w.pop();
        }
        w.push("tag");
        w.add("name", "short stack CT");
        w.pop();
        Collection<String> imageTypes = ProjectUtil.getImageTypes(cxn);
        for (String imageType : imageTypes) {
            w.push("tag");
            w.add("name", imageType);
            w.pop();
        }
        cxn.execute("om.pssd.object.tag.dictionary.entry.add", w.document(),
                null, null);
    }

}
