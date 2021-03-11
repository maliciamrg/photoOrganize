package com.malicia.mrg.util;


import com.github.fracpete.processoutput4j.core.StreamingProcessOutputType;
import com.github.fracpete.processoutput4j.core.StreamingProcessOwner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.DecimalFormat;
import java.text.NumberFormat;


public class Output implements StreamingProcessOwner {

    private static final Logger LOGGER = LogManager.getLogger(Output.class);
    private int i = 0;
    NumberFormat formatter = new DecimalFormat("00000");

    public StreamingProcessOutputType getOutputType() {
        return StreamingProcessOutputType.BOTH;
    }

    public void processOutput(String line, boolean stdout) {
        if (stdout) {
            i++;
            String lline = "[" + formatter.format(i) + "] " + line;
            if (line.contains("total size is") || line.contains("bytes  received") || line.contains("deleting")) {
                LOGGER.info(lline);
            } else {
                if ((i % 10) == 0) {
                    LOGGER.info(lline);
                } else {
                    LOGGER.debug(lline);
                }
            }
        } else {
            LOGGER.error(line);
        }
    }
}