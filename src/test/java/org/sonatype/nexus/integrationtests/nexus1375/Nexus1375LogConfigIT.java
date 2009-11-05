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
package org.sonatype.nexus.integrationtests.nexus1375;

import java.io.File;

import org.restlet.data.MediaType;
import org.sonatype.nexus.integrationtests.AbstractNexusIntegrationTest;
import org.sonatype.nexus.rest.model.LogConfigResource;
import org.sonatype.nexus.test.utils.LogConfigMessageUtil;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;

/**
 * @author juven
 */
public class Nexus1375LogConfigIT
    extends AbstractNexusIntegrationTest
{

    protected LogConfigMessageUtil messageUtil;

    @Override
    public boolean isSecureTest()
    {
        return true;
    }

    public Nexus1375LogConfigIT()
    {
        messageUtil = new LogConfigMessageUtil( this.getXMLXStream(), MediaType.APPLICATION_XML );
    }

    @Test( enabled = false )
    // TODO log4j is disabled
    public void getLogConfig()
        throws Exception
    {
        LogConfigResource resource = messageUtil.getLogConfig();

        AssertJUnit.assertEquals( "DEBUG", resource.getRootLoggerLevel() );

        AssertJUnit.assertEquals( "console", resource.getRootLoggerAppenders() );

        AssertJUnit.assertEquals( "%4d{yyyy-MM-dd HH:mm:ss} %-5p [%-15.15t] - %c - %m%n",
                                  resource.getFileAppenderPattern() );

        File expectedLoggerLocation = new File( "target/logs/nexus.log" ).getCanonicalFile();

        File actualLoggerLocation = new File( resource.getFileAppenderLocation() ).getCanonicalFile();

        AssertJUnit.assertEquals( expectedLoggerLocation.getAbsolutePath(), actualLoggerLocation.getAbsolutePath() );
    }

    @Test( enabled = false )
    // TODO log4j is disabled
    public void updateLogConfig()
        throws Exception
    {
        LogConfigResource resource = messageUtil.getLogConfig();

        AssertJUnit.assertEquals( "DEBUG", resource.getRootLoggerLevel() );
        AssertJUnit.assertEquals( "console", resource.getRootLoggerAppenders() );

        resource.setRootLoggerLevel( "ERROR" );

        messageUtil.updateLogConfig( resource );

        AssertJUnit.assertEquals( "ERROR", resource.getRootLoggerLevel() );

        resource.setRootLoggerLevel( "DEBUG" );

        messageUtil.updateLogConfig( resource );

        AssertJUnit.assertEquals( "DEBUG", resource.getRootLoggerLevel() );
    }
}
