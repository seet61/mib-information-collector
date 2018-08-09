package ru.tele2.dao.serverServices;


import javax.sql.DataSource;
import java.util.List;

public interface ServerServicesDAO {
    /**
     * This is the method to be used to initialize
     * database resources ie. connection.
     */
    public void setDataSource(DataSource ds);

    /**
     * This is the method to be used to list down
     * a record from the server_services table corresponding
     * to a passed rule.
     */
    public ServerServices searchServerService(String serviceName, int servId);

    /**
     * This is the method to be used to list down
     * a record from the server_services table corresponding
     * to a passed rule.
     */
    public List<ServerServices> searchExternalService(int servId, String dataPort, String servicePort);

    /**
     * This is the method to be used to list down
     * all the records from the init_servers table.
     */
    public List<ServerServices> listServerServices();
}
