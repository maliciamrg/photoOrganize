package com.malicia.mrg.util;


import com.github.fracpete.processoutput4j.core.StreamingProcessOutputType;
import com.github.fracpete.processoutput4j.core.StreamingProcessOwner;
import com.malicia.mrg.Main;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class Output implements StreamingProcessOwner {

    private static final Logger LOGGER = LogManager.getLogger(Output.class);

    public StreamingProcessOutputType getOutputType() {
        return StreamingProcessOutputType.BOTH;
    }

    public void processOutput(String line, boolean stdout) {
        if (stdout) {
            if (line.contains("total size is") || line.contains("bytes  received") || line.contains("deleting")) {
                LOGGER.info(line);
            } else {
                LOGGER.debug(line);
            }
        } else {
            LOGGER.error(line);
        }
    }
}