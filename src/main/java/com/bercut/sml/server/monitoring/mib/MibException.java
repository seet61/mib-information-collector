/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bercut.sml.server.monitoring.mib;

/**
 *
 * @author vlad
 */
public class MibException extends Exception {

    /**
     * Creates a new instance of
     * <code>MIbException</code> without detail message.
     */
    public MibException() {
    }

    /**
     * Constructs an instance of
     * <code>MIbException</code> with the specified detail message.
     *
     * @param msg the detail message.
     */
    public MibException(String msg) {
        super(msg);
    }
}
