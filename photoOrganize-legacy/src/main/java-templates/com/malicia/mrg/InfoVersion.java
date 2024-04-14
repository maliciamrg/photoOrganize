package com.malicia.mrg;

public class InfoVersion {

    private static final String PROJECT_VERSION = "${project.version}";
    private static final String PROJECT_GROUP_ID = "${project.groupId}";
    private static final String PROJECT_ARTIFACT_ID = "${project.artifactId}";
    private static final String BUILD_NUMBER = "${buildNumber}";

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

