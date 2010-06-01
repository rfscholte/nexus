package org.sonatype.nexus.index;

/**
 * Maven ontology.
 * 
 * @author cstamas
 */
public interface MAVEN
{
    /** Maven namespace */
    public static final String MAVEN_NAMESPACE = "urn:maven#";

    public static final Field FILE_NAME =
        new Field( null, MAVEN_NAMESPACE, "fileName", "Artifact File Name" );

    public static final Field FILE_EXTENSION =
        new Field( null, MAVEN_NAMESPACE, "fileExtension", "Artifact File Extension" );
    
    public static final Field REPOSITORY_ID =
        new Field( null, MAVEN_NAMESPACE, "repositoryId", "Artifact Repository ID" );

    public static final Field GROUP_ID = new Field( null, MAVEN_NAMESPACE, "groupId", "Group ID" );

    public static final Field ARTIFACT_ID = new Field( null, MAVEN_NAMESPACE, "artifactId", "Artifact ID" );

    public static final Field VERSION = new Field( null, MAVEN_NAMESPACE, "version", "Version" );

    public static final Field BASE_VERSION = new Field( null, MAVEN_NAMESPACE, "baseVersion", "Base Version" );

    public static final Field CLASSNAMES = new Field( null, MAVEN_NAMESPACE, "classNames", "Artifact Classes" );

    // Artifact Packaging (extension for secondary artifacts!).
    public static final Field PACKAGING = new Field( null, MAVEN_NAMESPACE, "packaging", "Packaging/Extension" );

    public static final Field CLASSIFIER = new Field( null, MAVEN_NAMESPACE, "classifier", "Classifier" );

    // NAME: Artifact Name (from POM)
    public static final Field NAME = new Field( null, MAVEN_NAMESPACE, "name", "Name" );

    // DESCRIPTION: Artifact Description (from POM)
    public static final Field DESCRIPTION = new Field( null, MAVEN_NAMESPACE, "name", "Description" );

    // LAST_MODIFIED: Artifact Last Modified Timestamp (UTC millis)
    public static final Field LAST_MODIFIED =
        new Field( null, MAVEN_NAMESPACE, "lastModified", "Last Modified Timestamp" );

    // SHA1: Artifact SHA1 checksum.
    public static final Field SHA1 = new Field( null, MAVEN_NAMESPACE, "sha1", "SHA1 checksum" );

    // SOURCES_EXISTS: Artifact Sources Presence.
    public static final Field SOURCES_EXISTS = new Field( null, MAVEN_NAMESPACE, "sourcesExists", "Sources exists" );

    // JAVADOCS_EXISTS: Artifact Javadocs Presence.
    public static final Field JAVADOCS_EXISTS = new Field( null, MAVEN_NAMESPACE, "javadocsExists", "Javadocs exists" );

    // SIGNATURE_EXISTS: Artifact Signature Presence.
    public static final Field SIGNATURE_EXISTS =
        new Field( null, MAVEN_NAMESPACE, "signatureExists", "Signature exists" );

    // REMOTE_URL: Artifact's remote URL (if proxied).
    public static final Field REMOTE_URL = new Field( null, MAVEN_NAMESPACE, "remoteUrl", "Remote URL" );

    // PLUGIN_PREFIX: MavenPlugin Artifact Plugin Prefix.
    public static final Field PLUGIN_PREFIX = new Field( null, MAVEN_NAMESPACE, "pluginPrefix", "Plugin Prefix" );

    // PLUGIN_GOALS: MavenPlugin Artifact Plugin Goals (list of strings)
    public static final Field PLUGIN_GOALS = new Field( null, MAVEN_NAMESPACE, "pluginGoals", "Plugin Goals" );

    // INFO: Artifact Info (packaging, lastModified, size, sourcesExists, javadocExists, signatureExists)
    public static final Field INFO = new Field( null, MAVEN_NAMESPACE, "info", "Artifact Info" );
}
