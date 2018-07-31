/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bercut.sml.server.monitoring.mib;

import java.util.StringTokenizer;

/**
 *
 * @author vlad-k
 */
public class Version implements Comparable<Version>, java.io.Serializable {

    private static final long serialVersionUID = -968645745395577725L;

    public static class IllegalVersionException extends Exception {

        public IllegalVersionException(String src) {
        }
    }
    public final int v1;
    public final int v2;
    public final int v3;
    public final int v4;

    public Version() {
        this.v1 = 0;
        this.v2 = 0;
        this.v3 = 0;
        this.v4 = 0;
    }

    public Version(int v1, int v2, int v3, int v4) {
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
        this.v4 = v4;
    }

    public static Version valueOf(String src) throws IllegalVersionException {
        StringTokenizer st = new StringTokenizer(src, ".");
        int v1;
        int v2;
        int v3;
        int v4;

        try {
            if (st.hasMoreTokens()) {
                v1 = Integer.parseInt(st.nextToken());
                if (st.hasMoreTokens() && v1 >= 0) {
                    v2 = Integer.parseInt(st.nextToken());
                    if (st.hasMoreTokens() && v2 >= 0) {
                        v3 = Integer.parseInt(st.nextToken());
                        if (st.hasMoreTokens() && v3 >= 0) {
                            v4 = Integer.parseInt(st.nextToken());

                            if (!st.hasMoreTokens() && v4 >= 0) {
                                return new Version(v1, v2, v3, v4);
                            }
                        }
                    }
                }
            }
        } catch (NumberFormatException ex) {
        }

        throw new IllegalVersionException(src);
    }

    @Override
    public String toString() {
        return v1 + "." + v2 + "." + v3 + "." + v4;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != getClass()) {
            return false;
        }

        if (obj == this) {
            return true;
        }
        Version v = (Version) obj;
        return this.v1 == v.v1 && this.v2 == v.v2 && this.v3 == v.v3 && this.v4 == v.v4;

    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + this.v1;
        hash = 29 * hash + this.v2;
        hash = 29 * hash + this.v3;
        hash = 29 * hash + this.v4;
        return hash;
    }

    public boolean isZero() {
        return this.v1 == 0 && this.v2 == 0 && this.v3 == 0 && this.v4 == 0;
    }

    @Override
    public int compareTo(Version ver) {
        return (this.v1 < ver.v1 ? -1 : (this.v1 == ver.v1
                ? (this.v2 < ver.v2 ? -1 : (this.v2 == ver.v2
                ? (this.v3 < ver.v3 ? -1 : (this.v3 == ver.v3
                ? (this.v4 < ver.v4 ? -1 : (this.v4 == ver.v4 ? 0 : 1))
                : 1))
                : 1))
                : 1));
    }

    /**
     * Версия в формате x.y
     *
     * @return строка
     */
    public String getShortString() {
        return v1 + "." + v2;
    }
}
