package com.xesnet.sshtaskmanager.util;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;


/**
 * @author Pierre PINON
 */
public class PathUtil {

    private static final String CHARSET_UTF8 = "UTF-8";

    /**
     * Get Jar location
     *
     * @return Jar directory
     */
    public static String getJarDir() {
        return getJarDir(PathUtil.class);
    }

    /**
     * Get Jar location
     *
     * @param clazz
     *         Based on class location
     * @return String
     */
    public static String getJarDir(Class<?> clazz) {
        return decodeUrl(new File(clazz.getProtectionDomain().getCodeSource().getLocation().getPath()).getParent());
    }

    /**
     * Decode Url
     *
     * @param url
     * @return decoded url
     */
    private static String decodeUrl(String url) {
        if (url == null) {
            return null;
        }

        try {
            return URLDecoder.decode(url, CHARSET_UTF8);
        } catch (UnsupportedEncodingException e) {
            return url;
        }
    }
}
