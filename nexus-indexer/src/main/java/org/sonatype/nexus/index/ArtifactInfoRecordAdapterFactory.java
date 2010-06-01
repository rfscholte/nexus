package org.sonatype.nexus.index;

public interface ArtifactInfoRecordAdapterFactory
{
    ArtifactInfoRecordAdapter createAdapter( ArtifactInfoRecord rec );
}
