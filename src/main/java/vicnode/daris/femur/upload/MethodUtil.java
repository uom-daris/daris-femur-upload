package vicnode.daris.femur.upload;

import arc.mf.client.ServerClient;

public class MethodUtil {

    public static final String FEMUR_METHOD_NAME = "Melbourne Femur Collection";

    public static String findMethod(ServerClient.Connection cxn, String name)
            throws Throwable {
        return cxn.execute("om.pssd.method.find", "<name>" + name + "</name>",
                null, null).value("id");
    }

    public static String findFemurMethod(ServerClient.Connection cxn)
            throws Throwable {
        return findMethod(cxn, FEMUR_METHOD_NAME);
    }

}
