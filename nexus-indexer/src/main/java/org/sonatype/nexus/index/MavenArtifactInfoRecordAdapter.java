package org.sonatype.nexus.index;

import java.util.List;

import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.sonatype.nexus.artifact.Gav;
import org.sonatype.nexus.artifact.IllegalArtifactCoordinateException;
import org.sonatype.nexus.artifact.VersionUtils;

/**
 * Adapter for indexing Maven artifacts.
 * 
 * @author cstamas
 */
public class MavenArtifactInfoRecordAdapter
    extends AbstractArtifactInfoRecordAdapter
    implements MavenArtifactInfoRecord
{
    private ArtifactVersion artifactVersion;

    public MavenArtifactInfoRecordAdapter( ArtifactInfoRecord record )
    {
        super( record );
    }

    // ==

    public String getFileName()
    {
        return (String) getArtifactInfoRecord().get( MAVEN.FILE_NAME ).getValue();
    }

    public void setFileName( String fname )
    {
        getArtifactInfoRecord().addFieldValue( new StringFieldValue( MAVEN.FILE_NAME, fname ) );
    }

    public String getFileExtension()
    {
        return (String) getArtifactInfoRecord().get( MAVEN.FILE_EXTENSION ).getValue();
    }

    public void setFileExtension( String fextension )
    {
        getArtifactInfoRecord().addFieldValue( new StringFieldValue( MAVEN.FILE_EXTENSION, fextension ) );
    }

    public String getGroupId()
    {
        return (String) getArtifactInfoRecord().get( MAVEN.GROUP_ID ).getValue();
    }

    public void setGroupId( String groupId )
    {
        getArtifactInfoRecord().addFieldValue( new StringFieldValue( MAVEN.GROUP_ID, groupId ) );
    }

    public String getArtifactId()
    {
        return (String) getArtifactInfoRecord().get( MAVEN.ARTIFACT_ID ).getValue();
    }

    public void setArtifactId( String artifactId )
    {
        getArtifactInfoRecord().addFieldValue( new StringFieldValue( MAVEN.ARTIFACT_ID, artifactId ) );
    }

    public String getVersion()
    {
        return (String) getArtifactInfoRecord().get( MAVEN.VERSION ).getValue();
    }

    public void setVersion( String version )
    {
        getArtifactInfoRecord().addFieldValue( new StringFieldValue( MAVEN.VERSION, version ) );

        this.artifactVersion = null;
    }

    public ArtifactVersion getArtifactVersion()
    {
        if ( artifactVersion == null )
        {
            artifactVersion = new DefaultArtifactVersion( getVersion() );
        }

        return artifactVersion;
    }

    public void setArtifactVersion( ArtifactVersion artifactVersion )
    {
        this.artifactVersion = artifactVersion;

        setVersion( this.artifactVersion.toString() );
    }

    public String getClassifier()
    {
        return (String) getArtifactInfoRecord().get( MAVEN.CLASSIFIER ).getValue();
    }

    public void setClassifier( String classifier )
    {
        getArtifactInfoRecord().addFieldValue( new StringFieldValue( MAVEN.CLASSIFIER, classifier ) );
    }

    public String getPackaging()
    {
        return (String) getArtifactInfoRecord().get( MAVEN.PACKAGING ).getValue();
    }

    public void setPackaging( String packaging )
    {
        getArtifactInfoRecord().addFieldValue( new StringFieldValue( MAVEN.PACKAGING, packaging ) );
    }

    public String getName()
    {
        return (String) getArtifactInfoRecord().get( MAVEN.NAME ).getValue();
    }

    public void setName( String name )
    {
        getArtifactInfoRecord().addFieldValue( new StringFieldValue( MAVEN.NAME, name ) );
    }

    public String getDescription()
    {
        return (String) getArtifactInfoRecord().get( MAVEN.DESCRIPTION ).getValue();
    }

    public void setDescription( String description )
    {
        getArtifactInfoRecord().addFieldValue( new StringFieldValue( MAVEN.DESCRIPTION, description ) );
    }

    public ArtifactAvailablility getSourcesExists()
    {
        return (ArtifactAvailablility) getArtifactInfoRecord().get( MAVEN.SOURCES_EXISTS ).getValue();
    }

    public void setSourcesExists( ArtifactAvailablility sourcesExists )
    {
        getArtifactInfoRecord().addFieldValue(
            new ArtifactAvailablilityFieldValue( MAVEN.SOURCES_EXISTS, sourcesExists ) );
    }

    public ArtifactAvailablility getJavadocExists()
    {
        return (ArtifactAvailablility) getArtifactInfoRecord().get( MAVEN.JAVADOCS_EXISTS ).getValue();
    }

    public void setJavadocExists( ArtifactAvailablility javadocExists )
    {
        getArtifactInfoRecord().addFieldValue(
            new ArtifactAvailablilityFieldValue( MAVEN.JAVADOCS_EXISTS, javadocExists ) );
    }

    public ArtifactAvailablility getSignatureExists()
    {
        return (ArtifactAvailablility) getArtifactInfoRecord().get( MAVEN.SIGNATURE_EXISTS ).getValue();
    }

    public void setSignatureExists( ArtifactAvailablility signatureExists )
    {
        getArtifactInfoRecord().addFieldValue(
            new ArtifactAvailablilityFieldValue( MAVEN.SIGNATURE_EXISTS, signatureExists ) );
    }

    public String getClassNames()
    {
        return (String) getArtifactInfoRecord().get( MAVEN.CLASSNAMES ).getValue();
    }

    public void setClassNames( String classNames )
    {
        getArtifactInfoRecord().addFieldValue( new StringFieldValue( MAVEN.CLASSNAMES, classNames ) );
    }

    public String getRemoteUrl()
    {
        return (String) getArtifactInfoRecord().get( MAVEN.REMOTE_URL ).getValue();
    }

    public void setRemoteUrl( String remoteUrl )
    {
        getArtifactInfoRecord().addFieldValue( new StringFieldValue( MAVEN.REMOTE_URL, remoteUrl ) );
    }

    public String getPrefix()
    {
        return (String) getArtifactInfoRecord().get( MAVEN.PLUGIN_PREFIX ).getValue();
    }

    public void setPrefix( String prefix )
    {
        getArtifactInfoRecord().addFieldValue( new StringFieldValue( MAVEN.PLUGIN_PREFIX, prefix ) );
    }

    public List<String> getGoals()
    {
        return ( (StringListFieldValue) getArtifactInfoRecord().get( MAVEN.PLUGIN_GOALS ) ).getValue();
    }

    public void setGoals( List<String> goals )
    {
        getArtifactInfoRecord().addFieldValue( new StringListFieldValue( MAVEN.PLUGIN_GOALS, goals ) );
    }

    // ==

    public static Gav calculateGav( ArtifactInfoRecord ai )
        throws IllegalArtifactCoordinateException
    {
        MavenArtifactInfoRecord mae = ai.adapt( MavenArtifactInfoRecord.class );

        return new Gav( mae.getGroupId(), mae.getArtifactId(), mae.getVersion(), mae.getClassifier(),
            mae.getFileExtension(), null, // snapshotBuildNumber
            null, // snapshotTimeStamp
            mae.getFileName(), // name
            VersionUtils.isSnapshot( mae.getVersion() ), // isSnapshot
            false, // hash
            null, // hashType
            false, // signature
            null ); // signatureType
    }
}
