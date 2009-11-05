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
package org.sonatype.nexus.integrationtests.upgrades.nexus652;

import java.io.IOException;

import org.sonatype.nexus.configuration.model.Configuration;
import org.sonatype.nexus.integrationtests.AbstractNexusIntegrationTest;
import org.sonatype.nexus.test.utils.NexusConfigUtil;
import org.sonatype.nexus.test.utils.SecurityConfigUtil;
import org.sonatype.security.configuration.model.SecurityConfiguration;
import org.sonatype.security.configuration.source.SecurityConfigurationSource;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;

/**
 * Test nexus.xml after and upgrade from 1.0.0-beta-5 to 1.0.0.
 */
public class Nexus652Beta5To10UpgradeIT
    extends AbstractNexusIntegrationTest
{

    @Override
    public boolean isSecureTest()
    {
        return true;
    }

    public Nexus652Beta5To10UpgradeIT()
    {
        this.setVerifyNexusConfigBeforeStart( false );
    }

    @Test
    public void checkNexusConfig()
        throws Exception
    {
        // if we made it this far the upgrade worked...

        SecurityConfigurationSource securitySource = container.lookup( SecurityConfigurationSource.class, "file" );
        SecurityConfiguration securityConfig = securitySource.loadConfiguration();

        Configuration nexusConfig = NexusConfigUtil.getNexusConfig();

        AssertJUnit.assertEquals( "Smtp host:", "foo.org", nexusConfig.getSmtpConfiguration().getHostname() );
        AssertJUnit.assertEquals( "Smtp password:", "now", nexusConfig.getSmtpConfiguration().getPassword() );
        AssertJUnit.assertEquals( "Smtp username:", "void", nexusConfig.getSmtpConfiguration().getUsername() );
        AssertJUnit.assertEquals( "Smtp port:", 465, nexusConfig.getSmtpConfiguration().getPort() );

        AssertJUnit.assertEquals( "Security anon username:", "User3", securityConfig.getAnonymousUsername() );
        AssertJUnit.assertEquals( "Security anon password:", "y6i0t9q1e3", securityConfig.getAnonymousPassword() );
        AssertJUnit.assertEquals( "Security anon access:", true, securityConfig.isAnonymousAccessEnabled() );
        AssertJUnit.assertEquals( "Security enabled:", true, securityConfig.isEnabled() );
        AssertJUnit.assertEquals( "Security realm size:", 2, securityConfig.getRealms().size() );
        AssertJUnit.assertEquals( "Security realm:", "XmlAuthenticatingRealm", securityConfig.getRealms().get( 0 ) );
        AssertJUnit.assertEquals( "Security realm:", "XmlAuthorizingRealm", securityConfig.getRealms().get( 1 ) );

        AssertJUnit.assertEquals( "http proxy:", true, nexusConfig.getHttpProxy().isEnabled() );

        AssertJUnit.assertEquals( "Base url:", baseNexusUrl, nexusConfig.getRestApi().getBaseUrl() );

        // we will glance over the repos, because the unit tests cover this.
        AssertJUnit.assertEquals( "Repository Count:", 9, nexusConfig.getRepositories().size() );

        AssertJUnit.assertNotNull( "repo: central", NexusConfigUtil.getRepo( "central" ) );
        AssertJUnit.assertNotNull( "repo: apache-snapshots", NexusConfigUtil.getRepo( "apache-snapshots" ) );
        AssertJUnit.assertNotNull( "repo: codehaus-snapshots", NexusConfigUtil.getRepo( "codehaus-snapshots" ) );
        AssertJUnit.assertNotNull( "repo: releases", NexusConfigUtil.getRepo( "releases" ) );
        AssertJUnit.assertNotNull( "repo: snapshots", NexusConfigUtil.getRepo( "snapshots" ) );
        AssertJUnit.assertNotNull( "repo: thirdparty", NexusConfigUtil.getRepo( "thirdparty" ) );

        // everything else including everything above should be covered by unit tests.

    }

    @Test
    public void checkSecurityConfig()
        throws IOException
    {
        org.sonatype.security.model.Configuration secConfig = SecurityConfigUtil.getSecurityConfig();

        AssertJUnit.assertEquals( "User Count:", 7, secConfig.getUsers().size() );
        AssertJUnit.assertEquals( "Roles Count:", 22, secConfig.getRoles().size() );

        // again, everything should have been upgraded.
    }

}
