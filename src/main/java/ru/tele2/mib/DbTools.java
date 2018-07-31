package ru.tele2.mib;

import org.apache.log4j.Logger;
import ru.tele2.esb.Tools;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DbTools {
    final static Logger logger = Logger.getLogger(DbTools.class);
    private Map conf = null;
    private Tools tools = new Tools();
    private Connection connection = null;

    public DbTools() {
        conf = tools.get_conf();
        getDbConnection();
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
        this.tools.debug(logger,"Получена конфигурация подключения к БД " + url);
        try {
            Class.forName("org.postgresql.Driver");
            DriverManager.setLoginTimeout(Integer.parseInt((String) this.conf.get("db.connect.timeout")));
            this.tools.debug(logger, url + " " + name + " " + password);
            connection = DriverManager.getConnection(url, name, password);
            this.tools.debug(logger,"Драйвер подключен");
            if (!connection.isClosed()) {
                this.tools.debug(logger, "Подключились к БД");
            }
        } catch (SQLException e) {
            logger.error(e.getStackTrace());
        } catch (ClassNotFoundException e) {
            logger.error(e.getStackTrace());
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
                this.tools.debug(logger,"Соединение с БД закрыто");
            }
            catch (Exception e) {
                this.tools.debug(logger,"Не закрыто соединение с БД: " + e.getMessage());
            }
        }
    }

    public List<String> getStartedHosts() {
        List<String> hosts = new ArrayList<String>();

        String SQL_SELECT_HOSTS = "select host from mon_started_hosts;";

        try(Statement stmt = this.connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(SQL_SELECT_HOSTS);
            while (rs.next()) {
                hosts.add(rs.getString("host"));
            }
        } catch (SQLException e ) {
            this.logger.error(e.getStackTrace());
        }

        return hosts;
    }
}