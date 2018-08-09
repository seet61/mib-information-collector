package ru.tele2.dao.serverServices;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ServerServicesMapper implements RowMapper<ServerServices> {

    @Override
    public ServerServices mapRow(ResultSet resultSet, int i) throws SQLException {
        ServerServices serverServices = new ServerServices();
        serverServices.setServiceId(resultSet.getInt("service_id"));
        serverServices.setServId(resultSet.getInt("serv_id"));
        serverServices.setServiceName(resultSet.getString("service_name"));
        serverServices.setServiceType(resultSet.getString("service_type"));
        serverServices.setServiceVersion(resultSet.getString("service_type"));
        serverServices.setDataPort(resultSet.getString("data_port"));
        serverServices.setHttpPort(resultSet.getString("http_port"));
        serverServices.setServicePort(resultSet.getString("service_port"));
        serverServices.setStartDate(resultSet.getDate("start_date"));
        serverServices.setEndDate(resultSet.getDate("end_date"));
        serverServices.setSystemName(resultSet.getString("system_name"));
        serverServices.setSystemVersion(resultSet.getString("system_version"));
        serverServices.setDataBase(resultSet.getString("data_base"));
        return serverServices;
    }
}
