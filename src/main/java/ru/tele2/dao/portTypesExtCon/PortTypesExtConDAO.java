package ru.tele2.dao.portTypesExtCon;

import javax.sql.DataSource;
import java.util.List;

public interface PortTypesExtConDAO {
    /**
     * This is the method to be used to initialize
     * database resources ie. connection.
     */
    public void setDataSource(DataSource ds);

    /**
     * This is the method to be used to create
     * a record in the port_types_ext_con table.
     */
    public void createPortTypeExtCon(Integer serviceId, String portType, Integer extServiceId);

    /**
     * This is the method to be used to list down
     * a record from the port_types_ext_con table corresponding
     * to a passed rule.
     */
    public List<PortTypesExtCon> searchPortTypeExtCons(Integer serviceId, String portType);

    /**
     * This is the method to be used to list down
     * a record from the port_types_ext_con table corresponding
     * to a passed rule.
     */
    public void searchAndDeleteDeadPortTypeExtCons();

    /**
     * This is the method to be used to delete
     * a record from the port_types_ext_con table corresponding
     * to a passed rule.
     */
    public void deletePortTypeExtCon(Integer serviceId, String portType, Integer extServiceId);
}
