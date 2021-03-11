package com.malicia.mrg.util;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Logger;

public class WhereIAm {
    public static void displayWhereIAm(String methodName, Logger logger) {
        String ret = methodName;
        int length = ret.length();
        if (length < 70) {
            int lngMid = (70 - length) / 2;
            int lngComp = 70 - (length + lngMid + lngMid);
            ret = StringUtils.repeat("-", lngMid) + methodName + StringUtils.repeat(" ", lngComp) + StringUtils.repeat("-", lngMid);
        }
        logger.info(ret);
    }
}
