package ru.tele2.mib;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ru.bercut.mibsdk.MibException;
import ru.tele2.dao.initServers.InitServers;
import ru.tele2.dao.TestZoneJDBCTemplate;
import ru.tele2.dao.portTypesExtCon.PortTypesExtCon;
import ru.tele2.dao.serverServices.ServerServices;
import ru.tele2.esb.Tools;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public class MibWorker {
    final static Logger logger = Logger.getLogger(MibWorker.class);
    private static Map conf = null;
    private static TestZoneJDBCTemplate testZoneJDBCTemplate = null;

    public static void main(String[] args) {
        logger.info(StringUtils.repeat("#", 100));
        logger.info("Application started");

        Tools tools = new Tools();
        conf = tools.get_conf();
        ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
        testZoneJDBCTemplate = (TestZoneJDBCTemplate) context.getBean("testZoneJDBCTemplate");
        List<InitServers> initServers = testZoneJDBCTemplate.listInitServers();
        for (InitServers serv: initServers) {
            String host = serv.getHostIp();
            Integer servId = serv.getServId();
            if ("false".equals(conf.get("prom.enabled")) &&
                    (host.contains("10.0.") || host.contains("10.77.") || host.contains("10.251."))) {
                continue;
            }
            tools.debug(logger, "working with host: " + host);
            try (MibConnector mibConnector = new MibConnector(host)){
                Map<String, Map> hostInfo = mibConnector.getInformationOfHost();
                saveInformationAboutHost(servId, hostInfo);
            } catch (MibException e) {
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                logger.error(sw.toString());
            } catch (Exception e) {
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                logger.error(sw.toString());
            }
        }
        logger.info("Application stopped");
    }

    /***
     * Save information to DB
     * @param servId
     * @param hostInfo
     */
    private static void saveInformationAboutHost(Integer servId, Map<String, Map> hostInfo) {
        for (Map.Entry<String, Map> entry : hostInfo.entrySet()) {
            ServerServices serverService = testZoneJDBCTemplate.searchServerService(entry.getKey(), servId);
            Map<String, Map> servicePortTypeConnectionsInfo = entry.getValue();
            for (Map.Entry<String, Map> servicePortTypeConnection : servicePortTypeConnectionsInfo.entrySet()) {
                String portType = servicePortTypeConnection.getKey();
                List<PortTypesExtCon> cacheSearchPortTypeExtCons = testZoneJDBCTemplate.searchPortTypeExtCons(serverService.getServiceId(), portType);
                Map<String, Map> externalConnectionsInfo = servicePortTypeConnection.getValue();
                for (Map.Entry<String, Map> externalConnection : externalConnectionsInfo.entrySet()) {
                    if ("local".equals(externalConnection.getKey())) {
                        continue;
                    }
                    Map<String, String> info = externalConnection.getValue();
                    if (logger.isDebugEnabled()) {
                        logger.debug("serverService: " + serverService.getServiceName() + " portType: " + portType + " externalConnection: " + externalConnection);
                    }
                    try {
                        InitServers extServ = testZoneJDBCTemplate.searchInitServer(info.get("dataAddress").toString().split(":")[0]);
                        String dataPort = info.get("dataAddress").toString().split(":")[1];
                        String servicePort = info.get("serviceAddress").toString().split(":")[1];
                        List<ServerServices> extService = testZoneJDBCTemplate.searchExternalService(extServ.getServId(), dataPort, servicePort);
                        if (extService.size() > 0) {
                            testZoneJDBCTemplate.createPortTypeExtCon(serverService.getServiceId(), portType, extService.get(0).getServiceId());
                        }
                    } catch (Exception e) {
                        testZoneJDBCTemplate.createPortTypeExtCon(serverService.getServiceId(), portType, -1);
                    }
                }
            }
        }
    }
}
