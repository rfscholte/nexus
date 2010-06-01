package org.sonatype.nexus.index;

public class MavenArtifactInfoRecordAdapterFactory
    implements ArtifactInfoRecordAdapterFactory
{
    static
    {
        DefaultArtifactInfoRecord.registerArtifactInfoRecordAdapterFactory( MavenArtifactInfoRecord.class,
            new MavenArtifactInfoRecordAdapterFactory() );
    }

    public ArtifactInfoRecordAdapter createAdapter( ArtifactInfoRecord rec )
    {
        if ( rec != null )
        {
            return new MavenArtifactInfoRecordAdapter( rec );
        }
        else
        {
            return null;
        }
    }
}
