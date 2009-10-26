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
package org.sonatype.nexus.integrationtests.nexus1239;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.restlet.data.MediaType;
import org.sonatype.nexus.integrationtests.AbstractNexusIntegrationTest;
import org.sonatype.nexus.test.utils.UserMessageUtil;
import org.sonatype.security.rest.model.PlexusRoleResource;
import org.sonatype.security.rest.model.PlexusUserResource;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;

public class Nexus1239PlexusUserResourceIT
    extends AbstractNexusIntegrationTest
{

    @SuppressWarnings( "unchecked" )
    @Test
    public void getUserTestWithSource()
        throws IOException
    {

        UserMessageUtil userUtil = new UserMessageUtil( this.getJsonXStream(), MediaType.APPLICATION_JSON );
        PlexusUserResource user = userUtil.getPlexusUser( "default", "admin" );
        AssertJUnit.assertEquals( "admin", user.getUserId() );
        AssertJUnit.assertEquals( "changeme@yourcompany.com", user.getEmail() );
        AssertJUnit.assertEquals( "Administrator", user.getName() );
        AssertJUnit.assertEquals( "default", user.getSource() );

        List<PlexusRoleResource> roles = user.getRoles();
        AssertJUnit.assertEquals( 1, roles.size() );

        PlexusRoleResource role = roles.get( 0 );
        AssertJUnit.assertEquals( "Nexus Administrator Role", role.getName() );
        AssertJUnit.assertEquals( "admin", role.getRoleId() );
        AssertJUnit.assertEquals( "default", role.getSource() );
    }

    @SuppressWarnings( "unchecked" )
    @Test
    public void getUserTestWithOutSource()
        throws IOException
    {

        UserMessageUtil userUtil = new UserMessageUtil( this.getJsonXStream(), MediaType.APPLICATION_JSON );
        PlexusUserResource user = userUtil.getPlexusUser( null, "admin" );
        AssertJUnit.assertEquals( "admin", user.getUserId() );
        AssertJUnit.assertEquals( "changeme@yourcompany.com", user.getEmail() );
        AssertJUnit.assertEquals( "Administrator", user.getName() );
        AssertJUnit.assertEquals( "default", user.getSource() );

        List<PlexusRoleResource> roles = user.getRoles();
        AssertJUnit.assertEquals( 1, roles.size() );

        PlexusRoleResource role = roles.get( 0 );
        AssertJUnit.assertEquals( "Nexus Administrator Role", role.getName() );
        AssertJUnit.assertEquals( "admin", role.getRoleId() );
        AssertJUnit.assertEquals( "default", role.getSource() );
    }

    @SuppressWarnings( "unchecked" )
    @Test
    public void getUserTestWithAllSource()
        throws IOException
    {

        UserMessageUtil userUtil = new UserMessageUtil( this.getJsonXStream(), MediaType.APPLICATION_JSON );
        PlexusUserResource user = userUtil.getPlexusUser( "all", "admin" );
        AssertJUnit.assertEquals( "admin", user.getUserId() );
        AssertJUnit.assertEquals( "changeme@yourcompany.com", user.getEmail() );
        AssertJUnit.assertEquals( "Administrator", user.getName() );
        AssertJUnit.assertEquals( "default", user.getSource() );

        List<PlexusRoleResource> roles = user.getRoles();
        AssertJUnit.assertEquals( 1, roles.size() );

        PlexusRoleResource role = roles.get( 0 );
        AssertJUnit.assertEquals( "Nexus Administrator Role", role.getName() );
        AssertJUnit.assertEquals( "admin", role.getRoleId() );
        AssertJUnit.assertEquals( "default", role.getSource() );
    }

    @SuppressWarnings( "unchecked" )
    @Test
    public void getUsersTest()
        throws IOException
    {
        UserMessageUtil userUtil = new UserMessageUtil( this.getJsonXStream(), MediaType.APPLICATION_JSON );
        List<PlexusUserResource> users = userUtil.getPlexusUsers( "default" );

        List<String> userIds = new ArrayList<String>();

        for ( PlexusUserResource plexusUserResource : users )
        {
            userIds.add( plexusUserResource.getUserId() );
        }

        AssertJUnit.assertTrue( userIds.contains( "admin" ) );
        AssertJUnit.assertTrue( userIds.contains( "anonymous" ) );
        AssertJUnit.assertTrue( userIds.contains( "deployment" ) );
        AssertJUnit.assertTrue( userIds.contains( "test-user" ) );
        AssertJUnit.assertEquals( "Users: " + userIds, 4, users.size() );
    }

    @SuppressWarnings( "unchecked" )
    @Test
    public void getUsersTestAllSource()
        throws IOException
    {
        UserMessageUtil userUtil = new UserMessageUtil( this.getJsonXStream(), MediaType.APPLICATION_JSON );
        List<PlexusUserResource> users = userUtil.getPlexusUsers( "all" );

        List<String> userIds = new ArrayList<String>();

        for ( PlexusUserResource plexusUserResource : users )
        {
            userIds.add( plexusUserResource.getUserId() );
        }

        AssertJUnit.assertTrue( userIds.contains( "admin" ) );
        AssertJUnit.assertTrue( userIds.contains( "anonymous" ) );
        AssertJUnit.assertTrue( userIds.contains( "deployment" ) );
        AssertJUnit.assertTrue( userIds.contains( "test-user" ) );
        // AssertJUnit.assertEquals( "Users: "+ userIds, 4, users.size() );

        // NOTE: this needs to be at least the number of users expected in the default realm, the In-Memory realm add
        // another user locator, and there is no way to disable it.
        AssertJUnit.assertTrue( "Users: " + userIds, users.size() >= 4 );
    }

    @SuppressWarnings( "unchecked" )
    @Test
    public void junkSourceTest()
        throws IOException
    {

        UserMessageUtil userUtil = new UserMessageUtil( this.getJsonXStream(), MediaType.APPLICATION_JSON );
        List<PlexusUserResource> users = userUtil.getPlexusUsers( "VOID" );
        AssertJUnit.assertEquals( 0, users.size() );
    }

}
