package ru.tele2.mib;

import com.bercut.sml.server.monitoring.mib.MibConnection;
import com.bercut.sml.server.monitoring.mib.MibException;

import java.util.List;

public class MibWorker {

    public static void main(String[] args) {
        MibConnection connection = null;
        try {
            /*System.out.println("test");
            System.out.println(System.getProperty("java.library.path"));*/
            connection = new MibConnection("10.78.223.224");
            System.out.println("1: " + connection.isConnected());
            List<String> groups = connection.getGroups("/CMP_SLES_v3.3_SMEV/@artifacts/SE_IEIS_v1.0/");
            System.out.println("len: " + groups.size());
            for (String group: groups) {
                System.out.println("group: " + group);
            }
            System.out.println(connection.getVariable("/CMP_SLES_v3.3_SMEV/Core/Configuration/LogLevel").getValue());
            //System.out.println(connection.getVariable("/CMP_SLES_v3.3_SMEV/Core/Configuration/RegenerateCodeOnRestart").getType());
            System.out.println(connection.getVariable("/CMP_SLES_v3.3_SMEV/Core/Configuration/StatisticsInterval").getValue());
            System.out.println("2: " + connection.isConnected());
        } catch (ru.bercut.mibsdk.MibException e) {
            e.printStackTrace();
        } catch (MibException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.close();
                System.out.println("Closed");
                System.out.println("3: " + connection.isConnected());
            }
        }

    }
}
