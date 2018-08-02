package ru.tele2.mib;

import com.bercut.sml.server.monitoring.mib.MibConnection;
import org.apache.log4j.Logger;
import ru.bercut.mibsdk.MibException;
import ru.tele2.esb.Tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/***
 * Класс для работы с MIB
 */
public class MibConnector {
    final static Logger logger = Logger.getLogger(DbTools.class);
    private Tools tools = new Tools();
    private Map conf = null;
    private String host;
    private MibConnection connection;

    public MibConnector(String host) throws MibException {
        this.conf = tools.get_conf();
        this.host = host;
        this.connection = new MibConnection(host);
        tools.debug(logger, "Init connection for host: " + host);
    }

    public void closeConnection() {
        if (this.connection != null) {
            this.connection.close();
            tools.debug(logger, "Connection for host: " + host + " was closed");
        }
    }

    public void getInformationOfHost() throws com.bercut.sml.server.monitoring.mib.MibException {
        getRootServices();
        
    }


    public List<String> getRootServices() throws com.bercut.sml.server.monitoring.mib.MibException {
        List<String> rootServices = new ArrayList<>();
        List<String> groups = connection.getGroups("/");
        for (String group:groups) {
            if (group.contains("SG") || group.contains("SLES") || group.contains("LWSA")) {
                rootServices.add(group);
            }
        }
        return rootServices;
    }
}
