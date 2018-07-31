package com.bercut.sml.server.monitoring.mib;

/**
 *
 * @author vlad
 */
public class VarNotFoundException extends MibException {

    public VarNotFoundException(String hostname, String mib_path) {
        super("Variable " + MibHelper.createMibUrl(hostname, mib_path).toString() + " not found");
    }
}
