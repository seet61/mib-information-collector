package ru.tele2.mib;

/****** deprecated ********/


import org.apache.log4j.Logger;
import ru.tele2.esb.Tools;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

public class DbTools {
    final static Logger logger = Logger.getLogger(DbTools.class);
    private Map conf = null;
    private Tools tools = new Tools();
    private Connection connection = null;

    public DbTools() {
        this.conf = tools.get_conf();
        this.getDbConnection();
    }

    /***
     * Получение коннекта к БД
     * @return connection
     */
    private void getDbConnection() {
        /**
         * Коннект к БД
         */
        String url = (String) this.conf.get("db.url");
        String name = (String) this.conf.get("db.login");
        String password = (String) this.conf.get("db.password");
        try {
            Class.forName("org.postgresql.Driver");
            DriverManager.setLoginTimeout(Integer.parseInt((String) this.conf.get("db.connect.timeout")));
            this.tools.debug(logger, url + " " + name + " " + password);
            connection = DriverManager.getConnection(url, name, password);
            if (!connection.isClosed()) {
                this.tools.debug(logger, "Connection to DB was opened");
            }
        } catch (SQLException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());
        } catch (ClassNotFoundException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());
        }
    }

    /***
     * Закрытие коннекта к БД
     */
    public void closeDbConnection() {
        /**
         * Закрытие коннекта к БД
         */
        if (this.connection != null) {
            try {
                this.connection.close();
                this.tools.debug(logger,"Connection to DB was closed");
            }
            catch (Exception e) {
                this.tools.debug(logger,"Can't close connection with DB: " + e.getMessage());
            }
        }
    }

    /***
     * Получение списка серверов для анализа
     * @return List<String> серверов
     */
    public Map<String, String> getStartedHosts() {
        Map<String, String> hosts = new HashMap<>();

        String SQL_SELECT_HOSTS = "select serv_id, host_ip from init_servers LIMIT 1;";

        try(Statement stmt = this.connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(SQL_SELECT_HOSTS);
            while (rs.next()) {
                hosts.put(rs.getString("host_ip"), rs.getString("serv_id"));
            }
        } catch (SQLException e ) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());
        }

        return hosts;
    }

    /***
     * Сохранение информации о сервере
     * @param hostId id хоста
     * @param hostInfo Map<String, Map> с информацией о хосте
     */
    public void saveHostInfo(String hostId, Map<String, Map> hostInfo) {
        for (Map.Entry<String, Map> info: hostInfo.entrySet()) {
            String service = info.getKey();
            int serviceId = this.getServiceId(hostId, service);
            Map<String, Map> portTypeInfo = info.getValue();
            for (Map.Entry<String, Map> portInfo: portTypeInfo.entrySet()) {
                String portType = portInfo.getKey();
                Map<String,Map> externalConnection = portInfo.getValue();
                /*this.tools.debug(logger,"Information for save hostId: " + hostId + " service: " + service
                        + " serviceId: " + serviceId + " portType: " + portType + " externalConnection: " + externalConnection);*/
                for (Map.Entry<String, Map> extCon: externalConnection.entrySet()) {
                    String name = extCon.getKey();
                    String dataAddress = "";
                    String serviceAddress = "";
                    try {
                        Map<String, String> connection = extCon.getValue();
                        dataAddress = connection.get("dataAddress");
                        serviceAddress = connection.get("serviceAddress");
                    } catch (Exception e) {
                        continue;
                    }
                    //to do get host id
                }
            }
        }
    }

    private int getServiceId(String hostId, String service) {
        int serviceId = -1;

        String SQL_SELECT_SERVICE = "SELECT service_id\n" +
                "  FROM public.server_services\n" +
                " where end_date > current_timestamp\n" +
                "   and service_name = ? \n" +
                "   and serv_id = ?;";

        try(PreparedStatement stmt = this.connection.prepareStatement(SQL_SELECT_SERVICE)) {
            stmt.setString(1, service);
            stmt.setInt(2, Integer.parseInt(hostId));
            ResultSet rs = stmt.executeQuery();
            rs.next();
            serviceId = rs.getInt("service_id");
        } catch (SQLException e ) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());
        }

        return serviceId;
    }
}
