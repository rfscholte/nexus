package org.sonatype.nexus.index;

import java.util.Collection;
import org.sonatype.plugin.ExtensionPoint;
import org.sonatype.plugin.Managed;

@Managed
public interface IndexArtifactFilter
{
    Collection<ArtifactInfo> filterArtifactInfos( Collection<ArtifactInfo> artifactInfos );

    boolean filterArtifactInfo( ArtifactInfo artifactInfo );
}