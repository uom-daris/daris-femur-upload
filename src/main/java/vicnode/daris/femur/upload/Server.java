package vicnode.daris.femur.upload;

import arc.mf.client.RemoteServer;
import arc.mf.client.ServerClient;

public class Server {

    private static RemoteServer _server;
    private static ServerClient.Connection _cxn;

    public static ServerClient.Connection connect() throws Throwable {
        if (_cxn == null) {
            _server = new RemoteServer(Constants.HOST, Constants.PORT, true,
                    Constants.SSL);
            _cxn = _server.open();
            _cxn.connect(Constants.DOMAIN, Constants.USER, Constants.PASSWORD);
        }
        return _cxn;
    }

    public static void disconnect() throws Throwable {
        if (_cxn != null) {
            _cxn.closeAndDiscard();
            _cxn = null;
        }
    }

}
