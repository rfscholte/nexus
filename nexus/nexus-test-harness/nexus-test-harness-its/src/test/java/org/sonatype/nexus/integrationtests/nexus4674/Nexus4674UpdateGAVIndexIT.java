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
package org.sonatype.nexus.integrationtests.nexus4674;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

import java.util.List;

import org.sonatype.nexus.integrationtests.AbstractNexusIntegrationTest;
import org.sonatype.nexus.rest.model.NexusArtifact;
import org.sonatype.nexus.test.utils.GavUtil;
import org.sonatype.nexus.test.utils.TaskScheduleUtil;
import org.testng.annotations.Test;

/**
 * This test reindex a single version of a GA and check if all versions still present.
 * 
 * @author velo
 */
public class Nexus4674UpdateGAVIndexIT
    extends AbstractNexusIntegrationTest
{

    @Test
    public void searchTest()
        throws Exception
    {
        // auto deploy artifacts need time to be indexed
        TaskScheduleUtil.waitForAllTasksToStop();
        getEventInspectorsUtil().waitForCalmPeriod();

        List<NexusArtifact> r = getSearchMessageUtil().searchFor( "nexus4674" );
        assertThat( r, hasSize( 5 ) );

        // index GAV
        getSearchMessageUtil().reindexGAV( REPO_TEST_HARNESS_REPO, GavUtil.newGav( "nexus4674", "artifact", "1" ) );

        r = getSearchMessageUtil().searchFor( "nexus4674" );
        assertThat( r, hasSize( 5 ) );

        // index subartifact GAV
        getSearchMessageUtil().reindexGAV( REPO_TEST_HARNESS_REPO,
            GavUtil.newGav( "nexus4674.artifact", "subartifact", "1" ) );

        r = getSearchMessageUtil().searchFor( "nexus4674" );
        assertThat( r, hasSize( 5 ) );
    }
}
