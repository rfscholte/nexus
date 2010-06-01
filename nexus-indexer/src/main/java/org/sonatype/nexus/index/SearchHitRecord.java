package org.sonatype.nexus.index;

import java.util.List;

public interface SearchHitRecord
    extends ArtifactInfoRecord
{
    List<MatchHighlight> getMatchHighlights();
}
