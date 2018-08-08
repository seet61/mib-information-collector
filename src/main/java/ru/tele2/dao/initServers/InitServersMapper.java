package ru.tele2.dao.initServers;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InitServersMapper implements RowMapper<InitServers> {
    @Override
    public InitServers mapRow(ResultSet resultSet, int i) throws SQLException {
        InitServers initServers = new InitServers();
        initServers.setServId(resultSet.getInt("serv_id"));
        initServers.setHostIp(resultSet.getString("host_ip"));
        initServers.setTypeId(resultSet.getInt("type_id"));
        return initServers;
    }
}
