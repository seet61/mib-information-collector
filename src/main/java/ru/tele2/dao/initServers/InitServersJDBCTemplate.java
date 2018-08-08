package ru.tele2.dao.initServers;

import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.List;

public class InitServersJDBCTemplate implements InitServersDAO{
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
        List<InitServers> initServers = jdbcTemplate.query(SQL, new InitServersMapper());
        return initServers;
    }
}
