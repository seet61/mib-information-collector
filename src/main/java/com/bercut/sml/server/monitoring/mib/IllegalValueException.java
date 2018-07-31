/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bercut.sml.server.monitoring.mib;

/**
 *
 * @author vlad
 */
public class IllegalValueException extends MibException {

    public IllegalValueException(String hostname, String mib_path, String value) {
        super("Illegal variable " + MibHelper.createMibUrl(hostname, mib_path).toString() + " value " + value);
    }
}
