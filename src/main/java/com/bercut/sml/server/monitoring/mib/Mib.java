package com.bercut.sml.server.monitoring.mib;

import java.util.Date;
import java.util.List;
import javax.xml.namespace.QName;

/**
 *
 * @author vlad
 */
public class Mib {

    protected final com.bercut.sml.server.monitoring.mib.MibConnection mib;

    public Mib(com.bercut.sml.server.monitoring.mib.MibConnection mib) {
        this.mib = mib;
    }


    public void createGroup(String mib_path) throws com.bercut.sml.server.monitoring.mib.MibException {
        mib.createGroup(mib_path);
    }


    public Long getValueInteger64(String mib_path, boolean is_required) throws MibException {
        try {
            return mib.getInt64Value(mib_path);
        } catch (com.bercut.sml.server.monitoring.mib.MibException ex) {
            getValueCheckException(ex, is_required);
            return null;
        }
    }


    public Long getValueDword(String mib_path, boolean is_required) throws MibException {
        try {
            return mib.getDwordValue(mib_path);
        } catch (com.bercut.sml.server.monitoring.mib.MibException ex) {
            getValueCheckException(ex, is_required);
            return null;
        }
    }


    public QName getValueQName(String mib_path, boolean is_required) throws MibException {
        String res;
        try {
            res = mib.getStringValue(mib_path);
        } catch (com.bercut.sml.server.monitoring.mib.MibException ex) {
            getValueCheckException(ex, is_required);
            return null;
        }
        return QName.valueOf(res);
    }


    public String strToKeyString(String value, boolean is_table) {
        if (is_table) {
            return MibHelper.unscreenPath(MibHelper.stripDog(value));
        }
        return MibHelper.unscreenPath(value);
    }


    public String keyToStr(String value, boolean is_table) {
        if (is_table) {
            return MibHelper.addDog(MibHelper.screenPath(value));
        }
        return MibHelper.screenPath(value);
    }


    public QName strToKeyQName(String value, boolean is_table) {
        if (is_table) {
            return QName.valueOf(MibHelper.stripDog(MibHelper.unscreenPath(value)));
        }
        return QName.valueOf(MibHelper.unscreenPath(value));
    }


    public String keyToStr(QName value, boolean is_table) {
        if (is_table) {
            return MibHelper.addDog(MibHelper.screenPath(value.toString()));
        }
        return MibHelper.screenPath(value.toString());
    }


    public Long strToKeyLong(String value, boolean is_table) {
        if (is_table) {
            return Long.valueOf(MibHelper.stripDog(value));
        }
        return Long.valueOf(value);
    }


    public Long getValueDword64(String mib_path, boolean is_required) throws MibException {
        try {
            return mib.getDword64Value(mib_path);
        } catch (com.bercut.sml.server.monitoring.mib.MibException ex) {
            getValueCheckException(ex, is_required);
            return null;
        }
    }


    public List<String> getGroupNames(String mib_path) throws MibException {
        return mib.getGroups(mib_path);
    }


    public String getValuePassword(String string, boolean is_required) {
        return "";
    }


    public void deleteGroup(String mib_path) throws MibException {
        mib.deleteGroup(mib_path);
    }


    public Date getValueDate(String mib_path, boolean is_required) throws MibException {
        try {
            return mib.getDataTimeValue(mib_path);
        } catch (com.bercut.sml.server.monitoring.mib.MibException ex) {
            getValueCheckException(ex, is_required);
            return null;
        }       
    }

    static class UpdaterImpl {

        private final com.bercut.sml.server.monitoring.mib.MibConnection.Updater updater;

        private UpdaterImpl(com.bercut.sml.server.monitoring.mib.MibConnection mib, String mib_path) throws com.bercut.sml.server.monitoring.mib.MibException {
            this.updater = mib.createUpdater(mib_path);
        }

    
        public void close() throws com.bercut.sml.server.monitoring.mib.MibException {
            updater.close();
        }

    
        public void setValueInteger(String var_name, Integer value, boolean is_required) throws com.bercut.sml.server.monitoring.mib.MibException {
            if (value == null && !is_required) {
                return;
            }
            updater.setIntValue(var_name, value);
        }

    
        public void setValueString(String var_name, String value, boolean is_required) throws com.bercut.sml.server.monitoring.mib.MibException {
            if (value == null && !is_required) {
                return;
            }
            updater.setStringValue(var_name, value);
        }

    
        public void setValueDouble(String var_name, Double value, boolean is_required) throws com.bercut.sml.server.monitoring.mib.MibException {
            if (value == null && !is_required) {
                return;
            }
            updater.setDoubleValue(var_name, value);
        }

    
        public void setValueBoolean(String var_name, Boolean value, boolean is_required) throws com.bercut.sml.server.monitoring.mib.MibException {
            if (value == null && !is_required) {
                return;
            }
            updater.setBooleanValue(var_name, value);
        }

    
        public void setValueEnum(String var_name, Enum value, boolean is_required) throws com.bercut.sml.server.monitoring.mib.MibException {
            updater.setStringValue(var_name, value.toString());
        }

    
        public void setValueDword(String var_name, Long value, boolean is_required) throws com.bercut.sml.server.monitoring.mib.MibException {
            if (value == null && !is_required) {
                return;
            }
            updater.setDwordValue(var_name, value);
        }

    
        public void setValueInteger64(String var_name, Long value, boolean is_required) throws com.bercut.sml.server.monitoring.mib.MibException {
            if (value == null && !is_required) {
                return;
            }
            updater.setInt64Value(var_name, value);
        }

    
        public void setValueQName(String var_name, QName value, boolean is_required) throws com.bercut.sml.server.monitoring.mib.MibException {
            if (value == null && !is_required) {
                return;
            }
            updater.setQNameValue(var_name, value);
        }

    
        public void setValuePassword(String var_name, String value, boolean is_required) throws com.bercut.sml.server.monitoring.mib.MibException {
            if (value == null && !is_required) {
                return;
            }
            updater.setPasswordValue(var_name, value);
        }
    }


    public UpdaterImpl createUpdater(String mib_path) throws com.bercut.sml.server.monitoring.mib.MibException {
        return new UpdaterImpl(mib, mib_path);
    }

    public Integer getValueInteger(String mib_path, boolean is_required) throws com.bercut.sml.server.monitoring.mib.MibException {
        try {
            return mib.getIntValue(mib_path);
        } catch (com.bercut.sml.server.monitoring.mib.MibException ex) {
            getValueCheckException(ex, is_required);
            return null;
        }
    }


    public String getValueString(String mib_path, boolean is_required) throws com.bercut.sml.server.monitoring.mib.MibException {
        try {
            return mib.getStringValue(mib_path);
        } catch (com.bercut.sml.server.monitoring.mib.MibException ex) {
            getValueCheckException(ex, is_required);
            return null;
        }
    }


    public Double getValueDouble(String mib_path, boolean is_required) throws com.bercut.sml.server.monitoring.mib.MibException {
        try {
            return mib.getDoubleValue(mib_path);
        } catch (com.bercut.sml.server.monitoring.mib.MibException ex) {
            getValueCheckException(ex, is_required);
            return null;
        }
    }
    
    public List<MibConnection.Variable> getVariables(String mib_path) throws com.bercut.sml.server.monitoring.mib.MibException {
        try {
            return mib.getVariables(mib_path, false);
        } catch (com.bercut.sml.server.monitoring.mib.MibException ex) {
            getValueCheckException(ex, true);
            return null;
        }        
    }    


    public Boolean getValueBoolean(String mib_path, boolean is_required) throws com.bercut.sml.server.monitoring.mib.MibException {
        try {
            return mib.getBooleanValue(mib_path);
        } catch (com.bercut.sml.server.monitoring.mib.MibException ex) {
            getValueCheckException(ex, is_required);
            return null;
        }
    }


    public Enum getValueEnum(String mib_path, Class<? extends Enum> clz, boolean is_required) throws com.bercut.sml.server.monitoring.mib.MibException {
        String tmp;
        try {
            tmp = mib.getStringValue(mib_path);
            for (Enum enum1 : clz.getEnumConstants()) {
                if (enum1.toString().equalsIgnoreCase(tmp)) {
                    return enum1;
                }
            }
//            return Enum.valueOf(clz, tmp);
//        } catch (IllegalArgumentException ex) {
            if (is_required) {
                throw new com.bercut.sml.server.monitoring.mib.IllegalValueException(mib.getHostname(), mib_path, tmp);
            }
            return null;
        } catch (com.bercut.sml.server.monitoring.mib.MibException ex) {
            getValueCheckException(ex, is_required);
            return null;
        }
    }

    private void getValueCheckException(com.bercut.sml.server.monitoring.mib.MibException ex, boolean req) throws MibException {
        if (!req && (ex instanceof VarNotFoundException
                || ex instanceof TypeNotMatchException
                || ex instanceof GroupNotFoundException)) {
            return;
        }
        throw ex;
    }
    

}
