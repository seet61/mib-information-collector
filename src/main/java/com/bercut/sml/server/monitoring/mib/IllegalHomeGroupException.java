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
public class IllegalHomeGroupException extends Exception {

    public IllegalHomeGroupException(String url) {
        super("Invalid home group " + url);
    }
}
