package ru.tele2.dao.portTypesExtCon;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PortTypesExtConMapper implements RowMapper<PortTypesExtCon> {
    @Override
    public PortTypesExtCon mapRow(ResultSet resultSet, int i) throws SQLException {
        PortTypesExtCon portTypesExtCon = new PortTypesExtCon();
        portTypesExtCon.setServiceId(resultSet.getInt("service_id"));
        portTypesExtCon.setPortType(resultSet.getString("port_type"));
        portTypesExtCon.setExtServiceId(resultSet.getInt("ext_service_id"));
        return portTypesExtCon;
    }
}
