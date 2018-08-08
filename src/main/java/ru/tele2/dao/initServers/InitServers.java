package ru.tele2.dao.initServers;

/***
 * писывает структуру данных из таблицы init_servers
 */
public class InitServers {
    private Integer servId;
    private String hostIp;
    private Integer typeId;

    public Integer getServId() {
        return servId;
    }

    public void setServId(Integer servId) {
        this.servId = servId;
    }

    public String getHostIp() {
        return hostIp;
    }

    public void setHostIp(String hostId) {
        this.hostIp = hostId;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    @Override
    public String toString() {
        return "InitServers{" +
                "servId=" + servId +
                ", hostId='" + hostIp + '\'' +
                ", typeId=" + typeId +
                '}';
    }
}
