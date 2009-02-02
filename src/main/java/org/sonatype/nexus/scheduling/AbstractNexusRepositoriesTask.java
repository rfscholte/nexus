/**
 * Sonatype Nexus (TM) Open Source Version.
 * Copyright (c) 2008 Sonatype, Inc. All rights reserved.
 * Includes the third-party code listed at http://nexus.sonatype.org/dev/attributions.html
 * This program is licensed to you under Version 3 only of the GNU General Public License as published by the Free Software Foundation.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License Version 3 for more details.
 * You should have received a copy of the GNU General Public License Version 3 along with this program.
 * If not, see http://www.gnu.org/licenses/.
 * Sonatype Nexus (TM) Professional Version is available from Sonatype, Inc.
 * "Sonatype" and "Sonatype Nexus" are trademarks of Sonatype, Inc.
 */
package org.sonatype.nexus.scheduling;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.codehaus.plexus.util.StringUtils;
import org.sonatype.nexus.proxy.NoSuchRepositoryException;
import org.sonatype.nexus.proxy.NoSuchResourceStoreException;
import org.sonatype.nexus.proxy.repository.GroupRepository;
import org.sonatype.nexus.proxy.repository.Repository;
import org.sonatype.nexus.tasks.descriptors.properties.RepositoryOrGroupPropertyDescriptor;
import org.sonatype.scheduling.DefaultScheduledTask;
import org.sonatype.scheduling.ScheduledTask;
import org.sonatype.scheduling.TaskState;

public abstract class AbstractNexusRepositoriesTask<T>
    extends AbstractNexusTask<T>
{
    private static final String REPO_PREFIX = "repo_";

    private static final String GROUP_PREFIX = "group_";

    public static String getIdFromPrefixedString( String prefix, String prefixedString )
    {
        if ( prefixedString != null && prefixedString.startsWith( prefix ) )
        {
            return prefixedString.substring( prefix.length() );
        }

        return null;
    }

    public String getRepositoryId()
    {
        return getIdFromPrefixedString( REPO_PREFIX, getParameters().get( RepositoryOrGroupPropertyDescriptor.ID ) );
    }

    public void setRepositoryId( String repositoryId )
    {
        if ( !StringUtils.isEmpty( repositoryId ) )
        {
            getParameters().put( RepositoryOrGroupPropertyDescriptor.ID, REPO_PREFIX + repositoryId );
        }
    }

    public String getRepositoryGroupId()
    {
        return getIdFromPrefixedString( GROUP_PREFIX, getParameters().get( RepositoryOrGroupPropertyDescriptor.ID ) );
    }

    public String getRepositoryGroupName()
    {
        try
        {
            return getNexus().readRepositoryGroup( getRepositoryGroupId() ).getName();
        }
        catch ( NoSuchRepositoryException e )
        {
            this.getLogger().warn( "Could not read repository group!", e );

            return getRepositoryGroupId();
        }
    }

    public String getRepositoryName()
    {
        try
        {
            return getNexus().readRepository( getRepositoryId() ).getName();
        }
        catch ( NoSuchRepositoryException e )
        {
            this.getLogger().warn( "Could not read repository!", e );

            return getRepositoryId();
        }
    }
    
    public void setRepositoryGroupId( String repositoryGroupId )
    {
        if ( !StringUtils.isEmpty( repositoryGroupId ) )
        {
            getParameters().put( RepositoryOrGroupPropertyDescriptor.ID, GROUP_PREFIX + repositoryGroupId );
        }
    }

    public boolean allowConcurrentExecution( Map<String, List<ScheduledTask<?>>> activeTasks )
    {
        return !hasIntersectingTasksThatRuns( activeTasks );
    }

    protected boolean hasIntersectingTasksThatRuns( Map<String, List<ScheduledTask<?>>> activeTasks )
    {
        // get all activeTasks that runs and are descendants of AbstractNexusRepositoriesTask
        for ( String taskClz : activeTasks.keySet() )
        {
            try
            {
                Class<?> taskClazz = Class.forName( taskClz );

                if ( AbstractNexusRepositoriesTask.class.isAssignableFrom( taskClazz ) )
                {
                    List<ScheduledTask<?>> tasks = activeTasks.get( taskClz );

                    for ( ScheduledTask<?> task : tasks )
                    {
                        // check against RUNNING intersection
                        if ( TaskState.RUNNING.equals( task.getTaskState() )
                            && DefaultScheduledTask.class.isAssignableFrom( task.getClass() )
                            && repositorySetIntersectionIsNotEmpty( task.getTaskParams().get(
                                RepositoryOrGroupPropertyDescriptor.ID ) ) )
                        {
                            getLogger()
                                .debug(
                                    "Task "
                                        + task.getName()
                                        + " is running and shares same repo or group, so this task will be rescheduled for a later time." );
                            return true;
                        }
                    }
                }
            }
            catch ( ClassNotFoundException e )
            {
                // ignore, cannot happen
            }
        }

        return false;
    }

    protected boolean repositorySetIntersectionIsNotEmpty( String repoOrGroupId )
    {
        String otherRepositoryId = getIdFromPrefixedString( REPO_PREFIX, repoOrGroupId );
        String otherRepositoryGroupId = getIdFromPrefixedString( GROUP_PREFIX, repoOrGroupId );

        // simplest cases, checking for repoId and groupId equality
        if ( getRepositoryId() != null && otherRepositoryId != null
            && StringUtils.equals( getRepositoryId(), otherRepositoryId ) )
        {
            return true;
        }

        if ( getRepositoryGroupId() != null && otherRepositoryGroupId != null
            && StringUtils.equals( getRepositoryGroupId(), otherRepositoryGroupId ) )
        {
            return true;
        }

        // All repo check
        if ( ( getRepositoryId() == null && getRepositoryGroupId() == null )
            || ( otherRepositoryId == null && otherRepositoryGroupId == null ) )
        {
            return true;
        }

        try
        {
            // complex case: repoA may be in both groupA and groupB as member
            // so we actually evaluate all tackled reposes for both task and have intersected those
            List<Repository> thisReposes = new ArrayList<Repository>();

            if ( getRepositoryId() != null )
            {
                thisReposes.add( getNexus().getRepository( getRepositoryId() ) );
            }
            else
            {
                thisReposes.addAll( getNexus()
                    .getRepositoryWithFacet( getRepositoryGroupId(), GroupRepository.class ).getMemberRepositories() );
            }

            List<Repository> otherReposes = new ArrayList<Repository>();

            if ( otherRepositoryId != null )
            {
                otherReposes.add( getNexus().getRepository( otherRepositoryId ) );
            }
            else
            {
                otherReposes.addAll( getNexus()
                    .getRepositoryWithFacet( otherRepositoryGroupId, GroupRepository.class ).getMemberRepositories() );
            }

            HashSet<Repository> testSet = new HashSet<Repository>();
            testSet.addAll( thisReposes );
            testSet.addAll( otherReposes );

            // the set does not intersects
            return thisReposes.size() + otherReposes.size() != testSet.size();
        }
        catch ( NoSuchResourceStoreException e )
        {
            // in this case, one of the tasks will die anyway, let's say false
            return false;
        }
    }
}
