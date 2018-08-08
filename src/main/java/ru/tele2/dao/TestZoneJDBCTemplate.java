package ru.tele2.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import ru.tele2.dao.initServers.InitServers;
import ru.tele2.dao.initServers.InitServersDAO;
import ru.tele2.dao.initServers.InitServersMapper;
import ru.tele2.dao.serverServices.ServerServices;
import ru.tele2.dao.serverServices.ServerServicesDAO;
import ru.tele2.dao.serverServices.ServerServicesMapper;

import javax.sql.DataSource;
import java.util.List;

public class TestZoneJDBCTemplate implements InitServersDAO, ServerServicesDAO {
    private DataSource dataSource;
    private JdbcTemplate jdbcTemplate;

    @Override
    public void setDataSource(DataSource ds) {
        this.dataSource = ds;
        this.jdbcTemplate = new JdbcTemplate(this.dataSource);
    }

    @Override
    public List<InitServers> listInitServers() {
        String SQL = "select * from init_servers LIMIT 1;";
        List<InitServers> initServers = this.jdbcTemplate.query(SQL, new InitServersMapper());
        return initServers;
    }

    @Override
    public ServerServices searchServerService(String serviceName, int servId) {
        String SQL = "SELECT * " +
                       "FROM server_services " +
                      "where end_date > current_timestamp " +
                        "and service_name = ? " +
                        "and serv_id = ? ;";
        ServerServices serverServices = this.jdbcTemplate.queryForObject(SQL, new ServerServicesMapper(), serviceName, servId);
        return serverServices;
    }

    @Override
    public List<ServerServices> listServerServices() {
        String SQL = "SELECT * " +
                "FROM server_services " +
                "where end_date > current_timestamp;";
        List<ServerServices> listServerServices = this.jdbcTemplate.query(SQL, new ServerServicesMapper());
        return listServerServices;
    }
}
