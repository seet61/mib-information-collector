package ru.tele2.dao.portTypesExtCon;

public class PortTypesExtCon {
    private Integer serviceId;
    private String portType;
    private Integer extServiceId;

    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    public String getPortType() {
        return portType;
    }

    public void setPortType(String portType) {
        this.portType = portType;
    }

    public Integer getExtServiceId() {
        return extServiceId;
    }

    public void setExtServiceId(Integer extServiceId) {
        this.extServiceId = extServiceId;
    }

    @Override
    public String toString() {
        return "portTypesExtCon{" +
                "serviceId=" + serviceId +
                ", portType='" + portType + '\'' +
                ", extServiceId=" + extServiceId +
                '}';
    }
}
