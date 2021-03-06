package ru.tele2.dao.initServers;

import javax.sql.DataSource;
import java.util.List;

public interface InitServersDAO {
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
    public InitServers searchInitServer(String rule);

    /**
     * This is the method to be used to list down
     * all the records from the init_servers table.
     */
    public List<InitServers> listInitServers();
}
