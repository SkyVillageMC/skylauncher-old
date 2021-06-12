package hu.bendi.skylauncher.utils;

import org.apache.commons.lang3.SystemUtils;

public class OsUtils {

    public String getOsName() {
        return System.getProperty("os.name");
    }

    public static OS getOs() {
        if (SystemUtils.IS_OS_WINDOWS) {
            return OS.WIN;
        } else if (SystemUtils.IS_OS_MAC) {
            return OS.OSX;
        } else if (SystemUtils.IS_OS_LINUX) {
            return OS.LINUX;
        }
        return OS.OTHER;
    }

    public enum OS {
        WIN,
        OSX,
        LINUX,
        OTHER
    }
}
