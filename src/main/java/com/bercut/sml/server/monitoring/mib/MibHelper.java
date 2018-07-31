/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bercut.sml.server.monitoring.mib;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 *
 * @author vlad-k
 */
public class MibHelper {

    public static String unscreenPath(String path) {
        String res = "";
        int path_len = path.length();
        byte[] byte_res = new byte[path_len];
        int count = 0;
        int i = 0;
        while (i < path_len) {
            char cur_symbol = path.charAt(i);
            byte cur_byte;
            if (cur_symbol != '&') {
                cur_byte = (byte) cur_symbol;
            } else {
                if (path.charAt(i) == '&' && path.charAt(i + 1) != '&') {
                    if (path.charAt(i + 1) != ' ') {
                        String byte_value_str = path.substring(i + 1, i + 3);
                        try {
                            cur_byte = (byte) Integer.parseInt(byte_value_str, 16);
                            i += 2;
                        } catch (NumberFormatException ex) {
                            cur_byte = (byte) cur_symbol;
                        }
                    } else {
                        cur_byte = (byte) '&';
                    }
                } else {
                    cur_byte = (byte) '&';
                    i++;
                }
            }
            i++;
            byte_res[count] = cur_byte;
            count++;
        }
        if (count > 0) {
            byte_res = java.util.Arrays.copyOf(byte_res, count);
            try {
                String codepage = "Cp1251";//ContainerEnvironment.getScreenCodepageName();
                res = new String(byte_res, codepage);
            } catch (UnsupportedEncodingException ex) {
            }
        }
        return res;
    }
    private static final String pattern = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890<!.[]@&()_->";

    private static boolean checkSymbol(Character symbol) {
        return pattern.indexOf(symbol) >= 0;
    }

    public static String screenPath(String path) {
        String res = "";
        String codepage = "Cp1251";//ContainersFactoryImpl.getScreenCodepageName();
        try {
            byte[] str = path.getBytes(codepage);
            int len = str.length;
            for (int i = 0; i < len; i++) {
                String cur_char;
                byte cur_byte = str[i];
                Character symbol = path.charAt(i);
                if (checkSymbol(symbol)) {
                    if (symbol != '&') {
                        cur_char = symbol.toString();
                    } else {
                        cur_char = "&&";
                    }
                } else {
                    cur_char = String.format("&%02x", cur_byte);
                }
                res += cur_char;
            }
        } catch (UnsupportedEncodingException ex) {
        }
        return res;
    }

    public static String stripDog(String src) {
        if (src.startsWith("i@")) {
            return src.substring(2);
        }
        return src;
    }

    public static String createMibUrl(String host, String mibPath) {
        return host + mibPath;
//        try {
//            return new URL("mib", host, -1, mibPath);
//        } catch (MalformedURLException ex) {
//            // такого не может быть!
//            return null;
//        }
    }

    static String addDog(String screenPath) {
        return "i@" + screenPath;
    }

    public static String[] getMibPathParts(String mibPath){
        return mibPath.split("/");
    }
}
