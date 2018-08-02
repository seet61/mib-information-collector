package ru.tele2.mib;

import org.apache.log4j.Logger;
import ru.tele2.esb.Tools;

import java.util.List;

public class MibWorker {
    final static Logger logger = Logger.getLogger(MibWorker.class);

    public static void main(String[] args) {
        logger.info("Application started");

        Tools tools = new Tools();
        tools.debug(logger, "test");
        final DbTools dbTools = new DbTools();
        List<String> startedHosts = dbTools.getStartedHosts();
        for (String host : startedHosts) {
            tools.debug(logger, "working with host: " + host);
        }

        dbTools.closeDbConnection();
        logger.info("Application stopped");
    }
}
