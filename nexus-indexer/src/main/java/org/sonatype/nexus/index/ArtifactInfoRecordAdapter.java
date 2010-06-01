package org.sonatype.nexus.index;

public interface ArtifactInfoRecordAdapter
    extends ArtifactInfoRecord
{
    /**
     * Returns the wrapped ArtifactInfoRecord.
     * 
     * @return
     */
    ArtifactInfoRecord getArtifactInfoRecord();
}
