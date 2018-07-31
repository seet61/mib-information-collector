/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bercut.sml.server.monitoring.mib;

/**
 *
 * @author vlad
 */
public class TypeNotMatchException extends MibException {

    public TypeNotMatchException(String hostname, String mib_path, byte type) {
        super("Type not match. Variable " + MibHelper.createMibUrl(hostname, mib_path).toString() + " expected " + type);
    }
}
