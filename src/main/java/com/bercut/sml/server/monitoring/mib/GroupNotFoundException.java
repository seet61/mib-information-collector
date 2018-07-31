/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bercut.sml.server.monitoring.mib;

/**
 *
 * @author vlad
 */
@SuppressWarnings("serial")
public class GroupNotFoundException extends MibException {

    public GroupNotFoundException(String hostname, String mib_path) {
        super("Group " + MibHelper.createMibUrl(hostname, mib_path).toString() + " not found");
    }
}
