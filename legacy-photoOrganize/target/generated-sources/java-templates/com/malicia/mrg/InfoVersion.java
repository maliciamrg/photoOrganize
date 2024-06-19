package com.malicia.mrg;

public class InfoVersion {

    private static final String PROJECT_VERSION = "1.0";
    private static final String PROJECT_GROUP_ID = "malicia.mrg";
    private static final String PROJECT_ARTIFACT_ID = "legacy-photoOrganize";
    private static final String BUILD_NUMBER = "20240619.9233929";

    private InfoVersion() {
        throw new IllegalStateException("Utility class");
    }

    public static String getProjectVersion() {
        return PROJECT_VERSION;
    }

    public static String getProjectGroupId() {
        return PROJECT_GROUP_ID;
    }

    public static String getProjectArtifactId() {
        return PROJECT_ARTIFACT_ID;
    }

    public static String getBuildNumber() {
        return BUILD_NUMBER;
    }


    public static String showVersionInfo() {
        return "\n" +
                "\n" +
                "GroupId : " + getProjectGroupId() + "\n" +
                "ArtifactId : " + getProjectArtifactId() + "\n" +
                "Version : " + getProjectVersion() + "\n" +
                "BuildNumber : " + getBuildNumber() + "\n" +
                "\n";
    }
}

