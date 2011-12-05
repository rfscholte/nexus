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
package org.sonatype.nexus.scheduling.events;

import java.util.Date;

import org.sonatype.nexus.scheduling.NexusTask;

/**
 * Event fired when a task failed with some error.
 * 
 * @author cstamas
 * @since 1.10.0
 */
public class NexusTaskEventStoppedFailed<T>
    extends NexusTaskEventStopped<T>
{
    /**
     * Failure cause.
     */
    private final Throwable throwable;

    public NexusTaskEventStoppedFailed( final NexusTask<T> task, final Date started, final Throwable throwable )
    {
        super( task, started );
        this.throwable = throwable;
    }

    /**
     * Returns the failure cause.
     * 
     * @return
     */
    public Throwable getFailureCause()
    {
        return throwable;
    }
}
