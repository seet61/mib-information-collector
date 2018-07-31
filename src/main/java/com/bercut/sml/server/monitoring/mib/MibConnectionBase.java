package com.bercut.sml.server.monitoring.mib;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import ru.bercut.mibsdk.MibException;
import ru.bercut.mibsdk.remote.ClientNative;
import ru.bercut.mibsdk.remote.NmsConnectionPointContainer;
import ru.bercut.mibsdk.remote.NmsContainer2;
import ru.bercut.mibsdk.remote.NmsContainerFactory;
import ru.bercut.mibsdk.remote.NmsOptions;
import ru.bercut.mibsdk.remote.SimpleClient;

/**
 *
 * @author vlad-k
 */
public class MibConnectionBase {

//    public static final String DEFAULT_LOGIN = "root";
//    public static final String DEFAULT_PASSWORD = "bercut";
    private final String hostname;
    private final String userName;
    private final String password;
    private NmsContainer2 nms = null;
    private ClientNative clientNative = null;

    private SimpleClient simpleClient = null;
    private NmsConnectionPointContainer connectionPointContainer ;    
    private final AtomicBoolean connected = new AtomicBoolean();

    public MibConnectionBase(String hostname) throws MibException {
        this.hostname = hostname;
        this.userName = "root";
        this.password = "bercut";
        //checkNms();
    }

    public MibConnectionBase(String hostname, String user, String password) {
        this.hostname = hostname;
        this.userName = user;
        this.password = password;
    }

    public String getHostname() {
        return hostname;
    }

    public boolean isConnected() {
        return connected.get();
        //return clientNative.getStatus() == 3; // ST_ONLINE=3
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public synchronized void close() {
        if (nms != null) {
            try {
                clientNative.pause();
            } catch (MibException ex) {
            }
            clientNative.delete();
            nms = null;
            simpleClient = null;
            connected.set(false);
        }
    }

    protected synchronized SimpleClient getSimpleClient() throws ru.bercut.mibsdk.MibException {
        checkNms();
        return simpleClient;
    }
    
    public synchronized ClientNative getClientNative() throws ru.bercut.mibsdk.MibException {
        checkNms();
        return clientNative;
    }

    public NmsConnectionPointContainer getConnectionPointContainer() {
        return connectionPointContainer;
    }
    
    

    private void checkNms() throws ru.bercut.mibsdk.MibException {
        if (nms == null) {
            nms = NmsContainerFactory.getInstance().createContainer();
            clientNative = nms.getClientNative();
            clientNative.setOption(NmsOptions.OPT_AGENTNAME, hostname.getBytes());
            clientNative.setOption(NmsOptions.OPT_USERNAME, getUserName().getBytes());
            clientNative.setOption(NmsOptions.OPT_PASSWORD, getPassword().getBytes());
            clientNative.resume();
            try {
                for (int i = 0; !connected.get() && i < 50; i++) {
                    Thread.sleep(100);
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(MibConnectionBase.class.getName()).log(Level.SEVERE, null, ex);
            }
            simpleClient = nms.getSimpleClient();
            connectionPointContainer = nms.getConnectionPointContainer();
            connected.set(true);
        }
    }
}
