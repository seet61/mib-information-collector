package ru.tele2.mib;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ru.bercut.mibsdk.MibException;
import ru.tele2.dao.initServers.InitServers;
import ru.tele2.dao.initServers.InitServersJDBCTemplate;
import ru.tele2.esb.Tools;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

public class MibWorker {
    final static Logger logger = Logger.getLogger(MibWorker.class);
    private static Map conf = null;


    public static void main(String[] args) {
        logger.info(StringUtils.repeat("#", 100));
        logger.info("Application started");

        Tools tools = new Tools();
        conf = tools.get_conf();
        ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
        InitServersJDBCTemplate initServersJDBCTemplate = (InitServersJDBCTemplate) context.getBean("initServersJDBCTemplate");
        List<InitServers> initServers = initServersJDBCTemplate.listInitServers();
        System.out.println(initServers.size());
        for (InitServers serv: initServers) {
            String host = serv.getHostIp();
            Integer hostId = serv.getServId();
            if ("false".equals(conf.get("prom.enabled")) &&
                    (host.contains("10.0.") || host.contains("10.77.") || host.contains("10.251."))
                    ) {
                continue;
            }
            tools.debug(logger, "working with host: " + host);
            try (MibConnector mibConnector = new MibConnector(host)){
                Map<String, Map> hostInfo = mibConnector.getInformationOfHost();
                dbTools.saveHostInfo(hostId, hostInfo);
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
}
