package com.malicia.mrg;

import com.malicia.mrg.util.WhereIAm;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;

public class TimeTracker {
    static HashMap<String, Long> startTime = new HashMap<>();


    public static void startTimer(String methodName, Logger loggerToTime) {
        startTime.put(methodName,System.currentTimeMillis());
        loggerToTime.info(repeatString("-+-",startTime.size()) + methodName);
    }

    public static void endTimer(String methodName, Logger loggerToTime) {
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime.get(methodName);

        // Convert elapsed time to hours, minutes, seconds, and milliseconds
        long hours = elapsedTime / 3600000; // 1 hour = 60 minutes * 60 seconds * 1000 milliseconds
        long minutes = (elapsedTime % 3600000) / 60000; // 1 minute = 60 seconds * 1000 milliseconds
        long seconds = ((elapsedTime % 3600000) % 60000) / 1000; // 1 second = 1000 milliseconds
        long milliseconds = elapsedTime % 1000; // Remaining milliseconds

        // Format the output to force 2 digits for hours, minutes, seconds and 3 digits for milliseconds
        String formattedTime = String.format("%02d:%02d:%02d.%03d", hours, minutes, seconds, milliseconds);

        loggerToTime.info(repeatString("-+-",startTime.size()) + methodName);
        loggerToTime.info(repeatString("   ",startTime.size()) + "Time taken: " + formattedTime);
        startTime.remove(methodName);
    }
    public static String repeatString(String s, int n) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < n; i++) {
            result.append(s);
        }
        return result.toString();
    }
}
