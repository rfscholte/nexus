package org.sonatype.nexus.index;

import java.util.List;

import org.apache.maven.artifact.versioning.ArtifactVersion;

public interface MavenArtifactInfoRecord
    extends ArtifactInfoRecord
{
    String getFileName();

    void setFileName( String fname );

    String getFileExtension();

    void setFileExtension( String fextension );

    String getGroupId();

    void setGroupId( String groupId );

    String getArtifactId();

    void setArtifactId( String artifactId );

    String getVersion();

    void setVersion( String version );

    ArtifactVersion getArtifactVersion();

    void setArtifactVersion( ArtifactVersion artifactVersion );

    String getClassifier();

    void setClassifier( String classifier );

    String getPackaging();

    void setPackaging( String packaging );

    String getName();

    void setName( String name );

    String getDescription();

    void setDescription( String description );

    ArtifactAvailablility getSourcesExists();

    void setSourcesExists( ArtifactAvailablility sourcesExists );

    ArtifactAvailablility getJavadocExists();

    void setJavadocExists( ArtifactAvailablility javadocExists );

    ArtifactAvailablility getSignatureExists();

    void setSignatureExists( ArtifactAvailablility signatureExists );

    String getClassNames();

    void setClassNames( String classNames );

    String getRemoteUrl();

    void setRemoteUrl( String remoteUrl );

    String getPrefix();

    void setPrefix( String prefix );

    List<String> getGoals();

    void setGoals( List<String> goals );
}
