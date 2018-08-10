package ru.tele2.mib;

import com.bercut.sml.server.monitoring.mib.MibConnection;
import org.apache.log4j.Logger;
import ru.bercut.mibsdk.MibException;
import ru.tele2.esb.Tools;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

/***
 * Класс для работы с MIB
 */
public class MibConnector implements AutoCloseable {
    final static Logger logger = Logger.getLogger(MibConnector.class);
    private Tools tools = new Tools();
    private Map conf = null;
    private String host;
    private MibConnection connection;

    /**
     * Конструктор класса
     * @param host адрес с которым работаем
     * @throws MibException
     */
    public MibConnector(String host) throws MibException {
        this.conf = tools.get_conf();
        this.host = host;
        this.connection = new MibConnection(host);
        tools.debug(logger, "Init connection for host: " + host);
    }

    /***
     * Получение информации из MIB хоста
     * @throws com.bercut.sml.server.monitoring.mib.MibException
     */
    public Map<String, Map> getInformationOfHost() throws com.bercut.sml.server.monitoring.mib.MibException {
        List<String> rootServices = getRootServices();
        Map<String, Map> serviceInfo = new HashMap<>();
        for (String service: rootServices) {
            Map<String, Map<String, Map>> servicePortTypeConnectionsInfo = new HashMap<>();
            servicePortTypeConnectionsInfo.clear();
            tools.debug(logger, "Host: " + this.host + " Working with service: " + service);
            String mibVersion = getMibVersion(service);
            List<String> portTypes = this.connection.getGroups(String.format((String) this.conf.get("service.portTypeRouting"), service, mibVersion));
            for (String portType: portTypes) {
                Map<String, Map> externalConnectionsInfo = new HashMap<>();
                Set<String> externalConnectionsNames = getNamesOfExternalConnections(service, mibVersion, portType);
                for (String externalConnectionName: externalConnectionsNames) {
                    Map<String, String> info = getExternalConnectionsInfo(service, mibVersion, externalConnectionName);
                    if (info.size() == 0) {
                        portType = replaceHexToChar(portType);
                        logger.error("Host: " + this.host + " service: " + service + " portType: " + portType
                                + " externalConnectionName: " + externalConnectionName + " !!!no information!!!");
                    }
                    externalConnectionsInfo.put(externalConnectionName, info);
                }
                portType = replaceHexToChar(portType);
                servicePortTypeConnectionsInfo.put(portType, externalConnectionsInfo);
            }
            serviceInfo.put(service, servicePortTypeConnectionsInfo);
            this.tools.debug(logger, "Host: " + this.host + " service: " + service + " serviceInfo: " + serviceInfo.toString());
        }
        return serviceInfo;
    }

    /***
     * Получение информации о коннектах portType
     * @param service имя сервиса
     * @param mibVersion версия mib
     * @param externalConnectionName имя внешнего коннекта
     * @return Map с информацией о соединениях
     */
    private Map getExternalConnectionsInfo(String service, String mibVersion, String externalConnectionName) {
        Map<String, String> info = new HashMap<>();
        String tcp = "LIB_MessageBus_v3.2".equals(mibVersion)? "TcpFi" : "Tcp";
        try {
            info.put("dataAddress", this.connection.getStringValue(String.format((String) this.conf.get("service.externalConnections.dataAddress"),
                    service, mibVersion, externalConnectionName, tcp)));
            info.put("serviceAddress", this.connection.getStringValue(String.format((String) this.conf.get("service.externalConnections.serviceAddress"),
                    service, mibVersion, externalConnectionName, tcp)));
        } catch (com.bercut.sml.server.monitoring.mib.MibException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());
        }
        return info;
    }

    /***
     * Получение списка имен ExternalConnections соединений из ConditionalRouteTable и ScalingTable
     * @param service имя сервиса
     * @param mibVersion версия mib
     * @param portType имя portType
     * @return Set<String> с именами
     */
    private Set<String> getNamesOfExternalConnections(String service, String mibVersion, String portType) throws com.bercut.sml.server.monitoring.mib.MibException {
        Set<String> externalConnections = new HashSet<>();
        externalConnections.addAll(getConditionalScalingTable(service, mibVersion, portType));
        externalConnections.addAll(getScalingTable(service, mibVersion, portType));
        return externalConnections;
    }

    /***
     * Получение списка уникальных имен ExternalConnections из ScalingTable
     * @param service имя сервиса
     * @param mibVersion версия mib
     * @param portType имя portType
     * @return
     * @throws com.bercut.sml.server.monitoring.mib.MibException
     */
    private Set<String> getScalingTable(String service, String mibVersion, String portType) throws com.bercut.sml.server.monitoring.mib.MibException {
        Set<String> connections = new HashSet<>();
        List<String> groups = this.connection.getGroups(String.format((String) this.conf.get("service.scalingTable"), service, mibVersion, portType));
        for (String group: groups) {
            connections.add(this.connection.getStringValue(String.format((String) this.conf.get("service.scalingTable.connectionName"),
                    service, mibVersion, portType, group)));
        }
        return connections;
    }

    /***
     * Получение списка уникальных имен ExternalConnections из ConditionalRouteTable
     * @param service имя сервиса
     * @param mibVersion версия mib
     * @param portType имя portType
     * @return
     * @throws com.bercut.sml.server.monitoring.mib.MibException
     */
    private Set<String> getConditionalScalingTable(String service, String mibVersion, String portType) throws com.bercut.sml.server.monitoring.mib.MibException {
        Set<String> connections = new HashSet<>();
        String path = String.format((String) this.conf.get("service.conditionalRouteTable"), service, mibVersion, portType);
        if (!this.connection.groupExists(path)) {
            return connections;
        }
        List<String> groups = this.connection.getGroups(path);
        for (String group: groups) {
            List<String> subGroups = this.connection.getGroups(String.format((String) this.conf.get("service.conditionalScalingTable"), service, mibVersion, portType, group));
            for (String subGroup: subGroups) {
                connections.add(this.connection.getStringValue(String.format((String) this.conf.get("service.conditionalScalingTable.connectionName"),
                        service, mibVersion, portType, group, subGroup)));
            }
        }
        return connections;
    }

    /***
     * Заменяем hex коды символов самими символами
     * @param portType Строка для преобразования
     * @return Преобразованная строка
     */
    private String replaceHexToChar(String portType) {
        portType = portType.replace("i@", "");
        int index = portType.indexOf("&");
        while (index != -1) {
            String hex = portType.substring(index+1, index+3);
            portType = portType.replace(portType.substring(index, index+3), Character.toString((char) Integer.parseInt(hex, 16)));
            index = portType.indexOf("&");
        }
        return portType;
    }

    /***
     * Получение версии MIB для корректного составления путей
     * @return
     * @param service имя сервиса
     */
    private String getMibVersion(String service) throws com.bercut.sml.server.monitoring.mib.MibException {
        List<String> groups = this.connection.getGroups(String.format((String) this.conf.get("service.mibVersion"), service));
        String mib = null;
        for (String group: groups) {
            if (group.contains("LIB_MessageBus")) {
                mib = group;
            }
        }
        return mib;
    }

    /***
     * Получение списка сервисов на данном хосте(SG, SLES, LWSA)
     * @return List<String> с именами сервисов
     * @throws com.bercut.sml.server.monitoring.mib.MibException
     */
    public List<String> getRootServices() throws com.bercut.sml.server.monitoring.mib.MibException {
        List<String> rootServices = new ArrayList<>();
        List<String> groups = this.connection.getGroups("/");
        for (String group:groups) {
            if (group.contains("SG") || group.contains("SLES") || group.contains("LWSA")) {
                rootServices.add(group);
            }
        }
        tools.debug(logger, "Host: " + this.host + " List<String> groups: " + groups.toString());
        return rootServices;
    }

    /***
     * Переопределение метода из интерфейса AutoCloseable
     * @throws Exception
     */
    @Override
    public void close() throws Exception {
        if (this.connection != null) {
            this.connection.close();
            tools.debug(logger, "Connection for host: " + host + " was closed");
        }
    }
}
