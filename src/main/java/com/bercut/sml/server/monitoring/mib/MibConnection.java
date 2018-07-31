package com.bercut.sml.server.monitoring.mib;

//import com.bercut.sml.server.atlas.AtlasController;
import java.io.UnsupportedEncodingException;
import java.nio.ByteOrder;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.xml.namespace.QName;
import ru.bercut.mibsdk.MibRetcode;
import ru.bercut.mibsdk.MibType;
import ru.bercut.mibsdk.remote.Nms2Flags;
import ru.bercut.mibsdk.remote.SimpleClient;

/**
 *
 * @author vlad-k
 */
public class MibConnection extends MibConnectionBase {

    private boolean isUpdaterOpen = false;

    public MibConnection(String hostname) throws ru.bercut.mibsdk.MibException {
        super(hostname);
    }
    
    public MibConnection(String hostname, String user, String password) {
        super(hostname, user, password);
    }    


    public String getStringValue(String mib_path) throws MibException {
        try {
            return getSimpleClient().getStringValue(mib_path, null);
        } catch (ru.bercut.mibsdk.MibException ex) {
            throw getVarException(ex, mib_path, MibType.MIB_STRING);
        }
    }


    public List<String> getGroups(String mib_path) throws MibException {
        return getGroups(mib_path, false);
    }


    public List<String> getGroups(String mib_path, boolean full) throws MibException {
        List<String> res = new LinkedList<String>();
        try {
            List lst = getSimpleClient().getEntryList(mib_path, full ? 0 : Nms2Flags.MIB_SKIP_FULLPATH);
            for (Iterator it = lst.iterator(); it.hasNext();) {
                Object itm = it.next();
                res.add(((ru.bercut.mibsdk.Item) itm).getName());
            }
        } catch (ru.bercut.mibsdk.MibException ex) {
            if (ex.getErrorCode() == MibRetcode.MIB_GROUP_NOT_FOUND) {
                throw new GroupNotFoundException(getHostname(), mib_path);
            }
            throw new InternalErrorException(getHostname(), mib_path, ex.getErrorCode());
        }
        return res;
    }

 
//    public com.bercut.Mib getMibImpl() {
//        com.bercut.Mib mib = new MibImpl(this);
//        return mib;
//    }


     public static class Variable {

        private final String name;
        private final int type;
        private final Object value;

        Variable(String name, int type, Object value) {
            this.name = name;
            this.type = type;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public int getType() {
            return type;
        }

        public Object getValue() {
            return value;
        }
    }

    /**
     * Получить список переменных с их значениями. FIXME: Работает только для
     * переменных типов MIB_STRING, MIB_BYTE, MIB_WORD, MIB_DWORD, MIB_DWORD64,
     * MIB_SHORTINT, MIB_INT, MIB_LONGINT, MIB_INT64,
     *
     * @param mib_path MIB-путь, типа /Agent/.
     * @param full true имя переменной содержит полный путь или нет.
     * @return всегда возвращает объект. Содержит список переменных.
     * @throws MibException
     */
    public List<Variable> getVariables(String mib_path, boolean full) throws MibException {
        List<Variable> res = new LinkedList<Variable>();
        List lst;

        try {
            lst = getSimpleClient().getEntryList(mib_path, (full ? 0 : Nms2Flags.MIB_SKIP_FULLPATH) | 0x00020000 | Nms2Flags.MIB_VALUE_DATA);
        } catch (ru.bercut.mibsdk.MibException ex) {
            if (ex.getErrorCode() == MibRetcode.MIB_GROUP_NOT_FOUND) {
                throw new GroupNotFoundException(getHostname(), mib_path);
            }
            throw new InternalErrorException(getHostname(), mib_path, ex.getErrorCode());
        }

        for (Iterator it = lst.iterator(); it.hasNext();) {
            ru.bercut.mibsdk.Item itm = (ru.bercut.mibsdk.Item) it.next();
            Object obj = bytes2obj(itm.getType(), itm.getData(), itm.getSize());

            res.add(new Variable(itm.getName(), itm.getType(), obj));
        }
        return res;
    }
    private static final Object EMPTY_OBJECT = new Object();

    private static Object bytes2obj(int type, byte[] data, int len) {
        if (data == null || len == 0) {
            return EMPTY_OBJECT;
        }

        switch (type) {
            case MibType.MIB_STRING:
            case MibType.MIB_PASSWD:
                try {
                    return bytes2str(data);
                } catch (UnsupportedEncodingException ex) {
                    return EMPTY_OBJECT;
                }
            case MibType.MIB_BYTE:
            case MibType.MIB_WORD:
            case MibType.MIB_DWORD:
            case MibType.MIB_DWORD64:
            case MibType.MIB_SHORTINT:
            case MibType.MIB_INT:
            case MibType.MIB_LONGINT:
            case MibType.MIB_INT64:
                return new Long(dig2long(data, len));
            case MibType.MIB_DOUBLE:
                return Double.longBitsToDouble(dig2long(data, len));
            case MibType.MIB_FLOAT:
                return Float.intBitsToFloat((int) (dig2long(data, len)));
            default:
                return EMPTY_OBJECT;
        }
    }

    private static String bytes2str(byte[] data) throws UnsupportedEncodingException {
        int len = data.length;
        while (len > 0 && data[len - 1] == 0) {
            len--;
        }
        return new String(data, 0, len, "cp1251");
    }

    private static long dig2long(byte[] data, int len) {
        long res = 0;
        if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
            for (int i = len - 1; i >= 0; --i) {
                res = (res << 8) | (data[i] & 0xff);
            }
        } else {
            for (int i = 0; i < len; ++i) {
                res = (res << 8) | (data[i] & 0xff);
            }
        }
        return res;
    }

    /**
     * прочитать переменную любого типа. Делается через метод getVariables
     * потому что Java NMS по другому не умеет
     *
     * @param mibPath
     * @return
     * @throws MibException
     */
    public Variable getVariable(String mibPath) throws MibException {
        List<Variable> variables = getVariables(mibPath + "*", true);
        for (Variable variable : variables) {
            if (variable.getName().equals(mibPath)) {
                if (variable.getValue().getClass().equals(Object.class)) {
                    throw new UnsupportedMibTypeException(getHostname(), mibPath, variable.getType());
                }
                return variable;
            }
        }
        throw new VarNotFoundException(getHostname(), mibPath);
    }


    public boolean groupExists(String mib_path) throws MibException {
        try {
            getSimpleClient().getBoolValue(mib_path + "no_exist_var", null);
            return true;
        } catch (ru.bercut.mibsdk.MibException ex) {
            if (ex.getErrorCode() == MibRetcode.MIB_VAR_NOT_FOUND
                    || ex.getErrorCode() == MibRetcode.MIB_NO_MORE) {
                return true;
            }
            if (ex.getErrorCode() != MibRetcode.MIB_GROUP_NOT_FOUND) {
                throw getDefaultException(ex, mib_path);
            }
            return false;
        }
    }


    public double getDoubleValue(String mib_path) throws MibException {
        try {
            return getSimpleClient().getDoubleValue(mib_path, null);
        } catch (ru.bercut.mibsdk.MibException ex) {
            throw getVarException(ex, mib_path, MibType.MIB_DOUBLE);
        }
    }


    public boolean getBooleanValue(String mib_path) throws MibException {
        try {
            return getSimpleClient().getBoolValue(mib_path, null);
        } catch (ru.bercut.mibsdk.MibException ex) {
            throw getVarException(ex, mib_path, MibType.MIB_BOOLEAN);
        }
    }


    public int getIntValue(String mib_path) throws MibException {
        try {
            return getSimpleClient().getLongValue(mib_path, null);
        } catch (ru.bercut.mibsdk.MibException ex) {
            throw getVarException(ex, mib_path, MibType.MIB_LONGINT);
        }
    }


    public long getInt64Value(String mib_path) throws MibException {
        try {
            return getSimpleClient().getInt64Value(mib_path, null);
        } catch (ru.bercut.mibsdk.MibException ex) {
            throw getVarException(ex, mib_path, MibType.MIB_INT64);
        }
    }


    public long getDwordValue(String mib_path) throws MibException {
        try {
            SimpleClient simpleClient = getSimpleClient();
            
            //long currentTimeMillis1 = System.currentTimeMillis();
            long dwordValue = simpleClient.getDwordValue(mib_path, null);
            //long currentTimeMillis2 = System.currentTimeMillis();  
            
            //AtlasController.getLogger().writeDebug("getDwordValue=" + (currentTimeMillis2 - currentTimeMillis1));
            
            return dwordValue;
        } catch (ru.bercut.mibsdk.MibException ex) {
            throw getVarException(ex, mib_path, MibType.MIB_DWORD);
        }
    }


    public long getDword64Value(String mib_path) throws MibException {
        try {
            return getSimpleClient().getDword64Value(mib_path, null);
        } catch (ru.bercut.mibsdk.MibException ex) {
            throw getVarException(ex, mib_path, MibType.MIB_DWORD64);
        }
    }

    private static int getInt(String src, int pos, int len) {
        try {
            String tmp = src.substring(pos, pos + len);
            return Integer.valueOf(tmp);
        } catch (IndexOutOfBoundsException ex) {
            return -1;
        } catch (NumberFormatException ex) {
            return -1;
        }
    }


    public Date getDataTimeValue(String mib_path) throws MibException {
        try {
            // DD.MM.YYYY HH.MM.SS
            // 0123456789012345678
            String s = getSimpleClient().getDateTimeValue(mib_path, null);
            return new Date(
                    getInt(s, 6, 4) - 1900, getInt(s, 3, 2) - 1, getInt(s, 0, 2),
                    getInt(s, 11, 2), getInt(s, 14, 2), getInt(s, 17, 2));
        } catch (ru.bercut.mibsdk.MibException ex) {
            throw getVarException(ex, mib_path, MibType.MIB_DWORD64);
        }
    }

    private MibException getVarException(ru.bercut.mibsdk.MibException ex, String mib_path, byte type) {
        switch (ex.getErrorCode()) {
            case MibRetcode.MIB_VAR_NOT_FOUND:
            case MibRetcode.MIB_NO_MORE:
                return new VarNotFoundException(getHostname(), mib_path);
            case MibRetcode.MIB_GROUP_NOT_FOUND:
                return new GroupNotFoundException(getHostname(), mib_path);
            case MibRetcode.MIB_TYPE_NOT_MATCH:
            // если места для приемника не хватает, скорее всего указан не тот тип
            case MibRetcode.MIB_OUT_OF_MEMORY:
                return new TypeNotMatchException(getHostname(), mib_path, type);
        }
        return getDefaultException(ex, mib_path);
    }

    private MibException getDefaultException(ru.bercut.mibsdk.MibException ex, String mib_path) {
        return new InternalErrorException(getHostname(), mib_path, ex.getErrorCode());
    }

    void setIntValue(String var_name, int value) throws MibException {
        try {
            getSimpleClient().setLongValue(var_name, value, null);
        } catch (ru.bercut.mibsdk.MibException ex) {
            throw getDefaultException(ex, var_name);
        }
    }

    void setStringValue(String var_name, String value) throws MibException {
        try {
            getSimpleClient().setStringValue(var_name, value, null);
        } catch (ru.bercut.mibsdk.MibException ex) {
            throw getDefaultException(ex, var_name);
        }
    }

    void setDoubleValue(String var_name, double value) throws MibException {
        try {
            getSimpleClient().setDoubleValue(var_name, value, null);
        } catch (ru.bercut.mibsdk.MibException ex) {
            throw getDefaultException(ex, var_name);
        }
    }

    void setDwordValue(String var_name, long value) throws MibException {
        try {
            getSimpleClient().setDwordValue(var_name, value, null);
        } catch (ru.bercut.mibsdk.MibException ex) {
            throw getDefaultException(ex, var_name);
        }
    }

    void setBooleanValue(String var_name, boolean value) throws MibException {
        try {
            getSimpleClient().setBoolValue(var_name, value, null);
        } catch (ru.bercut.mibsdk.MibException ex) {
            throw getDefaultException(ex, var_name);
        }
    }
    

    public void createGroup(String mib_path) throws MibException {
        try {
            getSimpleClient().createGroup(mib_path, null);
        } catch (ru.bercut.mibsdk.MibException ex) {
            throw getDefaultException(ex, mib_path);
        }
    }


    public void removeGroup(String mib_path) throws MibException {
        try {
            getSimpleClient().removeGroup(mib_path);
        } catch (ru.bercut.mibsdk.MibException ex) {
            throw getDefaultException(ex, mib_path);
        }
    }


    public void removeValue(String mib_path) throws MibException {
        try {
            getSimpleClient().removeValue(mib_path);
        } catch (ru.bercut.mibsdk.MibException ex) {
            throw getDefaultException(ex, mib_path);
        }
    }


    public String getPassword(String mib_path) throws MibException {
        try {
            return getSimpleClient().getPasswordValue(mib_path, null);
        } catch (ru.bercut.mibsdk.MibException ex) {
            throw getDefaultException(ex, mib_path);
        }
    }

    void setInt64Value(String var_name, Long value) throws MibException {
        try {
            getSimpleClient().setInt64Value(var_name, value, null);
        } catch (ru.bercut.mibsdk.MibException ex) {
            throw getDefaultException(ex, var_name);
        }
    }

    void setPasswordValue(String var_name, String value) throws MibException {
        try {
            getSimpleClient().setPasswordValue(var_name, value, null);
        } catch (ru.bercut.mibsdk.MibException ex) {
            throw getDefaultException(ex, var_name);
        }
    }

    void deleteGroup(String mib_path) throws MibException {
        try {
            getSimpleClient().removeGroup(mib_path, null);
        } catch (ru.bercut.mibsdk.MibException ex) {
            throw getDefaultException(ex, mib_path);
        }
    }

    public static class Record {

        public String key;
        public String path;

        public Record(String key, String path) {
            this.key = key;
            this.path = path;
        }
    }

    public List<Record> getAtlasTable(String mib_path) throws MibException {
        List<Record> res = new LinkedList<Record>();
        for (String grp_name : getGroups(mib_path)) {
            res.add(new Record(MibHelper.unscreenPath(MibHelper.stripDog(grp_name)), mib_path + grp_name + "/"));
        }
        return res;
    }

    public static class GetVersionException extends Exception {

        public GetVersionException(String message) {
            super(message);
        }
    }

    public Version getVersion(String mib_path) throws GetVersionException {
        Throwable err;
        try {
            return Version.valueOf(this.getStringValue(mib_path));
        } catch (Version.IllegalVersionException ex) {
            err = ex;
        } catch (GroupNotFoundException ex) {
            return null;
        } catch (VarNotFoundException ex) {
            return null;
        } catch (MibException ex) {
            err = ex;
        }
        throw new GetVersionException("Error reading version of the application. Mib path: " + mib_path + ". Cause: " + err.getMessage());
    }

    void beginGroupUpdate(String mib_path) throws MibException {
        try {
            getSimpleClient().beginGroupUpdate(mib_path, null);
        } catch (ru.bercut.mibsdk.MibException ex) {
            throw getDefaultException(ex, mib_path);
        }
    }

    void endGroupUpdate() throws MibException {
        try {
            getSimpleClient().endGroupUpdate(null);
        } catch (ru.bercut.mibsdk.MibException ex) {
            throw getDefaultException(ex, "");
        }
    }

    public static class Updater  {

        private final MibConnection mib;
        private final String path;

        private Updater(MibConnection mib, String mib_path) throws MibException {
            if (mib.isUpdaterOpen) {
                throw new MibException("Updater already open");
            }
            this.mib = mib;
            this.path = mib_path;
            mib.beginGroupUpdate(mib_path);
            mib.isUpdaterOpen = true;
        }

    
        public void close() throws MibException {
            mib.endGroupUpdate();
            mib.isUpdaterOpen = false;
        }

    
        public void setIntValue(String var_name, int value) throws MibException {
            mib.setIntValue(path + var_name, value);
        }

    
        public void setStringValue(String var_name, String value) throws MibException {
            mib.setStringValue(path + var_name, value);
        }

    
        public void setDoubleValue(String var_name, double value) throws MibException {
            mib.setDoubleValue(path + var_name, value);
        }

    
        public void setBooleanValue(String var_name, boolean value) throws MibException {
            mib.setBooleanValue(path + var_name, value);
        }

    
        public void setDwordValue(String var_name, long value) throws MibException {
            mib.setDwordValue(path + var_name, value);
        }

    
        public void setInt64Value(String var_name, long value) throws MibException {
            mib.setInt64Value(path + var_name, value);
        }

    
        public void setQNameValue(String var_name, QName value) throws MibException {
            mib.setStringValue(path + var_name, value.toString());
        }

    
        public void setPasswordValue(String var_name, String value) throws MibException {
            mib.setPasswordValue(path + var_name, value.toString());
        }
    }


    public Updater createUpdater(String mib_path) throws MibException {
        return new Updater(this, mib_path);
    }
//    public int getInteger(String mib_path) {
//        try {
//            return getSimpleClient().getLongValue(mib_path, null);
//        } catch (ru.bercut.mibsdk.MibException ex) {
//            Logger.getLogger(Mib.class.getName()).log(Level.SEVERE, null, ex);
//            return 0;
//        }
//    }
//
//    public double getDouble(String mib_path) {
//        try {
//            return getSimpleClient().getDoubleValue(mib_path, null);
//        } catch (ru.bercut.mibsdk.MibException ex) {
//            Logger.getLogger(Mib.class.getName()).log(Level.SEVERE, null, ex);
//            return 0;
//        }
//    }
//
//    public boolean getBoolean(String mib_path) {
//        try {
//            return getSimpleClient().getBoolValue(mib_path, null);
//        } catch (ru.bercut.mibsdk.MibException ex) {
//            Logger.getLogger(Mib.class.getName()).log(Level.SEVERE, null, ex);
//            return false;
//        }
//    }
//
//    public long getDWord(String mib_path) {
//        try {
//            return getSimpleClient().getDwordValue(mib_path, null);
//        } catch (ru.bercut.mibsdk.MibException ex) {
//            Logger.getLogger(Mib.class.getName()).log(Level.SEVERE, null, ex);
//            return 0;
//        }
//    }
//    public URL getUrl(String mib_path) {
//        try {
//            return new URL(this.getString(mib_path));
//        } catch (MalformedURLException ex) {
//            return null;
//        }
//    }
//
//    public QName getQName(String mib_path) {
//        try {
//            return QName.valueOf(this.getString(mib_path));
//        } catch (IllegalArgumentException ex) {
//            return null;
//        }
//    }
}
