package com.bercut.sml.server.monitoring.mib;

/**
 *
 * @author vlad-k
 */
public class UnsupportedMibTypeException extends MibException {

    public UnsupportedMibTypeException(String hostname, String mib_path, int mibType) {
        super("Unsupported mib type " + mibType + " variable " + MibHelper.createMibUrl(hostname, mib_path));
    }
}
