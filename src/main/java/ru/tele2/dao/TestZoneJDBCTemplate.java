package ru.tele2.dao;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.tele2.dao.initServers.InitServers;
import ru.tele2.dao.initServers.InitServersDAO;
import ru.tele2.dao.initServers.InitServersMapper;
import ru.tele2.dao.portTypesExtCon.PortTypesExtCon;
import ru.tele2.dao.portTypesExtCon.PortTypesExtConDAO;
import ru.tele2.dao.portTypesExtCon.PortTypesExtConMapper;
import ru.tele2.dao.serverServices.ServerServices;
import ru.tele2.dao.serverServices.ServerServicesDAO;
import ru.tele2.dao.serverServices.ServerServicesMapper;
import ru.tele2.esb.Tools;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

public class TestZoneJDBCTemplate implements InitServersDAO, ServerServicesDAO, PortTypesExtConDAO {
    private DataSource dataSource;
    private JdbcTemplate jdbcTemplate;
    final static Logger logger = Logger.getLogger(TestZoneJDBCTemplate.class);
    private Tools tools = new Tools();
    private Map conf = tools.get_conf();

    @Override
    public void setDataSource(DataSource ds) {
        this.dataSource = ds;
        this.jdbcTemplate = new JdbcTemplate(this.dataSource);
    }

    @Override
    public InitServers searchInitServer(String rule) {
        this.tools.debug(logger, "searchInitServer with params: " + rule);
        String SQL = "select * from init_servers where host_ip = ? ;";
        return this.jdbcTemplate.queryForObject(SQL, new InitServersMapper(), rule);
    }

    @Override
    public List<InitServers> listInitServers() {
        this.tools.debug(logger, "listInitServers");
        String SQL = "select * from init_servers LIMIT 1;";
        return this.jdbcTemplate.query(SQL, new InitServersMapper());
    }

    @Override
    public ServerServices searchServerService(String serviceName, int servId) {
        this.tools.debug(logger, "searchServerService with params: " + serviceName + " " + servId);
        String SQL = "SELECT * " +
                       "FROM server_services " +
                      "where end_date > current_timestamp " +
                        "and service_name = ? " +
                        "and serv_id = ? ;";
        return this.jdbcTemplate.queryForObject(SQL, new ServerServicesMapper(), serviceName, servId);
    }

    @Override
    public List<ServerServices> searchExternalService(int servId, String dataPort, String servicePort) {
        this.tools.debug(logger, "searchExternalService with params: " + servId + " " + dataPort + " " + servicePort);
        String SQL = "SELECT * " +
                       "FROM server_services " +
                      "where end_date > current_timestamp " +
                        "and serv_id = ? " +
                        "and data_port = ? " +
                        "and service_port = ? ;";
        List<ServerServices> serverServices = this.jdbcTemplate.query(SQL, new ServerServicesMapper(), servId, dataPort, servicePort);
        return serverServices;
    }

    @Override
    public List<ServerServices> listServerServices() {
        this.tools.debug(logger, "listServerServices");
        String SQL = "SELECT * " +
                "FROM server_services " +
                "where end_date > current_timestamp;";
        return this.jdbcTemplate.query(SQL, new ServerServicesMapper());
    }

    private boolean checkPortTypeExtConExist(Integer serviceId, String portType, Integer extServiceId) {
        this.tools.debug(logger, "checkPortTypeExtConExist with params: " + serviceId + " " + portType + " " + extServiceId);
        String SQL = "SELECT * FROM port_types_ext_con where service_id = ? and port_type = ? and ext_service_id = ? ;";
        List<PortTypesExtCon> list = this.jdbcTemplate.query(SQL, new PortTypesExtConMapper(), serviceId, portType, extServiceId);
        return list.size() > 0;
    }

    @Override
    public void createPortTypeExtCon(Integer serviceId, String portType, Integer extServiceId) {
        if(!checkPortTypeExtConExist(serviceId, portType, extServiceId)) {
            String SQL = "insert into port_types_ext_con(service_id, port_type, ext_service_id) values(?, ?, ?) ;";
            this.tools.debug(logger, "createPortTypeExtCon with params: " + serviceId + " " + portType + " " + extServiceId + " does not exist");
            this.jdbcTemplate.update(SQL, serviceId, portType, extServiceId);
        }
    }

    @Override
    public List<PortTypesExtCon> searchPortTypeExtCons(Integer serviceId, String portType) {
        this.tools.debug(logger, "searchPortTypeExtCons with params: " + serviceId + " " + portType);
        String SQL = "select * from port_types_ext_con where service_id = ? and port_type = ? ;";
        return this.jdbcTemplate.query(SQL, new PortTypesExtConMapper(), serviceId, portType);
    }

    @Override
    public void deletePortTypeExtCon(Integer serviceId, String portType, Integer extServiceId) {
        if(!checkPortTypeExtConExist(serviceId, portType, extServiceId)) {
            this.tools.debug(logger, "searchPortTypeExtCons with params: " + serviceId + " " + portType + " " + extServiceId + " was deleted");
            String SQL = "delete from port_types_ext_con where service_id = ? and port_type = ? and ext_service_id = ? ;";
            this.jdbcTemplate.update(SQL, serviceId, portType, extServiceId);
        }
    }
}
