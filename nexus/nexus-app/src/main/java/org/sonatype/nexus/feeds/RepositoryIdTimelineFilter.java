/**
 * Copyright (c) 2008-2011 Sonatype, Inc.
 * All rights reserved. Includes the third-party code listed at http://links.sonatype.com/products/nexus/oss/attributions
 *
 * This program is free software: you can redistribute it and/or modify it only under the terms of the GNU Affero General
 * Public License Version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License Version 3
 * for more details.
 *
 * You should have received a copy of the GNU Affero General Public License Version 3 along with this program.  If not, see
 * http://www.gnu.org/licenses.
 *
 * Sonatype Nexus (TM) Open Source Version is available from Sonatype, Inc. Sonatype and Sonatype Nexus are trademarks of
 * Sonatype, Inc. Apache Maven is a trademark of the Apache Foundation. M2Eclipse is a trademark of the Eclipse Foundation.
 * All other trademarks are the property of their respective owners.
 */
package org.sonatype.nexus.feeds;

import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nullable;

import org.sonatype.nexus.timeline.Entry;
import com.google.common.base.Predicate;

/**
 * Timeline filter that filters by repository IDs.
 *
 * @author: cstamas
 * @since 1.10.0
 */
public class RepositoryIdTimelineFilter
    implements Predicate<Entry>
{
    private final Set<String> repositoryIds;

    public RepositoryIdTimelineFilter( String repositoryId )
    {
        this.repositoryIds = new HashSet<String>();

        this.repositoryIds.add( repositoryId );
    }

    public RepositoryIdTimelineFilter( Set<String> repositoryIds )
    {
        this.repositoryIds = repositoryIds;
    }

    public boolean apply( final Entry hit )
    {
        return ( hit.getData().containsKey( DefaultFeedRecorder.REPOSITORY ) && repositoryIds.contains( hit.getData().get(
            DefaultFeedRecorder.REPOSITORY ) ) );
    }
}
