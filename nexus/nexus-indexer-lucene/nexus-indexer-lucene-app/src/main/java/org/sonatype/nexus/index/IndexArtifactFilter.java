package org.sonatype.nexus.index;

import java.util.Collection;

/**
 * Filters the search result by various conditions.
 * 
 * @author cstamas
 */
public interface IndexArtifactFilter
{
    Collection<ArtifactInfoRecord> filterArtifactInfos( Collection<ArtifactInfoRecord> artifactInfos );

    boolean filterArtifactInfo( ArtifactInfoRecord artifactInfo );
}
