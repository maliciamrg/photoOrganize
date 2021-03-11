package com.malicia.mrg.util;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Logger;

public class WhereIAm {

    private static final int LN_CHAMP = 130;
    private static final String STR_REMP = "-";
    private static final String STR_VIDE = " ";

    private WhereIAm() {
        throw new IllegalStateException("Utility class");
    }

    public static void displayWhereIAm(String methodName, Logger logger) {

        String ret = methodName;
        int length = ret.length();
        if (length < LN_CHAMP) {
            int lngMid = (LN_CHAMP - length) / 2;
            int lngComp = LN_CHAMP - (length + lngMid + lngMid);
            ret = StringUtils.repeat(STR_REMP, lngMid) + methodName + StringUtils.repeat(STR_VIDE, lngComp) + StringUtils.repeat(STR_REMP, lngMid);
        }
        logger.info(ret);
    }
}
