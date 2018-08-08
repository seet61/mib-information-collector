package ru.tele2.dao.serverServices;

import java.util.Date;

public class ServerServices {
    private Integer serviceId;
    private Integer servId;
    private String serviceName;
    private String serviceType;
    private String serviceVersion;
    private String dataPort;
    private String httpPort;
    private String servicePort;
    private Date startDate;
    private Date endDate;
    private String systemName;
    private String systemVersion;
    private String dataBase;

    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    public Integer getServId() {
        return servId;
    }

    public void setServId(Integer servId) {
        this.servId = servId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getServiceVersion() {
        return serviceVersion;
    }

    public void setServiceVersion(String serviceVersion) {
        this.serviceVersion = serviceVersion;
    }

    public String getDataPort() {
        return dataPort;
    }

    public void setDataPort(String dataPort) {
        this.dataPort = dataPort;
    }

    public String getHttpPort() {
        return httpPort;
    }

    public void setHttpPort(String httpPort) {
        this.httpPort = httpPort;
    }

    public String getServicePort() {
        return servicePort;
    }

    public void setServicePort(String servicePort) {
        this.servicePort = servicePort;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public String getSystemVersion() {
        return systemVersion;
    }

    public void setSystemVersion(String systemVersion) {
        this.systemVersion = systemVersion;
    }

    public String getDataBase() {
        return dataBase;
    }

    public void setDataBase(String dataBase) {
        this.dataBase = dataBase;
    }

    @Override
    public String toString() {
        return "serverServices{" +
                "serviceId=" + serviceId +
                ", servId=" + servId +
                ", serviceName='" + serviceName + '\'' +
                ", serviceType='" + serviceType + '\'' +
                ", serviceVersion='" + serviceVersion + '\'' +
                ", dataPort='" + dataPort + '\'' +
                ", httpPort='" + httpPort + '\'' +
                ", servicePort='" + servicePort + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", systemName='" + systemName + '\'' +
                ", systemVersion='" + systemVersion + '\'' +
                ", dataBase='" + dataBase + '\'' +
                '}';
    }
}
