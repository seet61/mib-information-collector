package com.bercut.sml.server.monitoring.mib;

import ru.bercut.mibsdk.MibRetcode;
import ru.bercut.mibsdk.remote.NmsRetcode;

/**
 *
 * @author vlad
 */
public class InternalErrorException extends MibException {

    private final int errorCode;


    public InternalErrorException(String hostname, String mib_path, int errorCode) {
        super("Internal error. Variable/group " + MibHelper.createMibUrl(hostname, mib_path).toString() + ". Error code: " + getMibConst(errorCode));
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
    
    public String getUserMessage() {
        return getMibConst(errorCode);
    }

    public boolean isConnectedException() {
        if (errorCode == NmsRetcode.NMS_NOT_CONNECTED || 
                errorCode == MibRetcode.MIB_AGENT_NOT_FOUND) {
            return true;
        }
        return false;
    }
    
    private static String getMibConst(int errorCode) {
        switch (errorCode) {
            case MibRetcode.MIB_OK:
                return "OK";
            case MibRetcode.MIB_GROUP_NOT_FOUND:
                return "GROUP_NOT_FOUND";
            case MibRetcode.MIB_INCORRECT_PATH:
                return "INCORRECT_PATH";
            case MibRetcode.MIB_MASK:
                return "MASK";
            case MibRetcode.MIB_OUT_OF_SPACE:
                return "OUT_OF_SPACE";
            case MibRetcode.MIB_TYPE_NOT_MATCH:
                return "TYPE_NOT_MATCH";
            case MibRetcode.MIB_UNKNOWN_TYPE:
                return "UNKNOWN_TYPE";
            case MibRetcode.MIB_INCORRECT_LEN:
                return "INCORRECT_LEN";
            case MibRetcode.MIB_INVALID_ARG:
                return "INVALID_ARG";
            case MibRetcode.MIB_VAR_NOT_FOUND:
                return "VAR_NOT_FOUND";
            case MibRetcode.MIB_NO_MORE:
                return "NO_MORE";
            case MibRetcode.MIB_OUT_OF_MEMORY:
                return "OUT_OF_MEMORY";
            case MibRetcode.MIB_VALUE_EXIST:
                return "VALUE_EXIST";
            case MibRetcode.MIB_SET_VALUE_CANCELED:
                return "SET_VALUE_CANCELED";
            case MibRetcode.MIB_USER_NOT_FOUND:
                return "USER_NOT_FOUND";
            case MibRetcode.MIB_GROUP_ALREADY_EXIST:
                return "GROUP_ALREADY_EXIST";
            case MibRetcode.MIB_SHARED_GROUP:
                return "SHARED_GROUP";
            case MibRetcode.MIB_USER_NOW_ACTIVE:
                return "USER_NOW_ACTIVE";
            case MibRetcode.MIB_READONLY_VALUE:
                return "READONLY_VALUE";
            case MibRetcode.MIB_REBIND_NOTIFY:
                return "REBIND_NOTIFY";
            case MibRetcode.MIB_NOTIFY_NOT_FOUND:
                return "NOTIFY_NOT_FOUND";
            case MibRetcode.MIB_UNABLE_CREATE_FILE:
                return "UNABLE_CREATE_FILE";
            case MibRetcode.MIB_UNABLE_OPEN_FILE:
                return "UNABLE_OPEN_FILE";
            case MibRetcode.MIB_UNKNOWN_FORMAT:
                return "UNKNOWN_FORMAT";
            case MibRetcode.MIB_FILE_NOT_FOUND:
                return "FILE_NOT_FOUND";
            case MibRetcode.MIB_FILE_READ_ERROR:
                return "FILE_READ_ERROR";
            case MibRetcode.MIB_FILE_WRITE_ERROR:
                return "FILE_WRITE_ERROR";
            case MibRetcode.MIB_LOW_FREE_SPACE:
                return "LOW_FREE_SPACE";
            case MibRetcode.MIB_CRITICAL_LOW_FREE_SPACE:
                return "CRITICAL_LOW_FREE_SPACE";
            case MibRetcode.MIB_LOGIN_FAIL:
                return "LOGIN_FAIL";
            case MibRetcode.MIB_USER_ALREADY_LOGIN:
                return "USER_ALREADY_LOGIN";
            case MibRetcode.MIB_NOT_LOGINED:
                return "NOT_LOGINED";
            case MibRetcode.MIB_UNKNOWN_HANDLE:
                return "UNKNOWN_HANDLE";
            case MibRetcode.MIB_ACCESS_DENIAL:
                return "ACCESS_DENIAL";
            case MibRetcode.MIB_SEND_ERROR:
                return "SEND_ERROR";
            case MibRetcode.MIB_ALREADY_CONNECTED:
                return "ALREADY_CONNECTED";
            case MibRetcode.MIB_AGENT_NOT_FOUND:
                return "AGENT_NOT_FOUND";
            case MibRetcode.MIB_UNKNOWN:
                return "UNKNOWN";
            case MibRetcode.MIB_NOT_CONNECTED:
                return "NOT_CONNECTED";
            case MibRetcode.MIB_INCORRECT_VERSION:
                return "INCORRECT_VERSION";
            case MibRetcode.MIB_NOINTERFACE:
                return "NOINTERFACE";
            case MibRetcode.MIB_CLASS_NOT_FOUND:
                return "CLASS_NOT_FOUND";
            case MibRetcode.MIB_ALREADY_ACTIVATED:
                return "ALREADY_ACTIVATED";
            case MibRetcode.MIB_NOT_ACTIVATED:
                return "NOT_ACTIVATED";
            case MibRetcode.MIB_NOT_OWNER:
                return "NOT_OWNER";
            case MibRetcode.MIB_INSTANCE_NOT_FOUND:
                return "INSTANCE_NOT_FOUND";
            case MibRetcode.MIB_REBIND:
                return "REBIND";
            case MibRetcode.MIB_NOBIND:
                return "NOBIND";
            case MibRetcode.MIB_NOTIMPL:
                return "NOTIMPL";
            case MibRetcode.MIB_NEW_INSTANCE:
                return "NEW_INSTANCE";
            case MibRetcode.MIB_NEW_CLASS:
                return "NEW_CLASS";
            case NmsRetcode.NMS_NOT_CONNECTED:
                return "NMS_NOT_CONNECTED";                
            default:
                return "Unknown code " + errorCode;
        }
    }
}