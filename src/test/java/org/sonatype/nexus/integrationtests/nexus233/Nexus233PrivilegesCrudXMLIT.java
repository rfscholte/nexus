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
package org.sonatype.nexus.integrationtests.nexus233;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Response;
import org.sonatype.nexus.integrationtests.AbstractNexusIntegrationTest;
import org.sonatype.nexus.jsecurity.realms.TargetPrivilegeDescriptor;
import org.sonatype.nexus.jsecurity.realms.TargetPrivilegeRepositoryTargetPropertyDescriptor;
import org.sonatype.nexus.test.utils.PrivilegesMessageUtil;
import org.sonatype.nexus.test.utils.SecurityConfigUtil;
import org.sonatype.security.realms.privileges.application.ApplicationPrivilegeMethodPropertyDescriptor;
import org.sonatype.security.rest.model.PrivilegeResource;
import org.sonatype.security.rest.model.PrivilegeStatusResource;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;

/**
 * CRUD tests for XML request/response.
 */
public class Nexus233PrivilegesCrudXMLIT
    extends AbstractNexusIntegrationTest
{

    protected PrivilegesMessageUtil messageUtil;

    public Nexus233PrivilegesCrudXMLIT()
    {
        this.messageUtil =
            new PrivilegesMessageUtil( this.getXMLXStream(), MediaType.APPLICATION_XML );
    }

    @SuppressWarnings( "unchecked" )
    @Test
    public void createReadMethodTest()
        throws IOException
    {
        PrivilegeResource resource = new PrivilegeResource();

        List methods = new ArrayList<String>();
        methods.add( "read" );
        resource.setMethod( methods );
        resource.setName( "createReadMethodTest" );
        resource.setType( TargetPrivilegeDescriptor.TYPE );
        resource.setRepositoryTargetId( "testTarget" );

        // get the Resource object
        List<PrivilegeStatusResource> statusResources = this.messageUtil.createPrivileges( resource );

        AssertJUnit.assertTrue( statusResources.size() == 1 );

        // make sure the id != null
        AssertJUnit.assertNotNull( statusResources.get( 0 ).getId() );

        AssertJUnit.assertEquals( SecurityConfigUtil.getPrivilegeProperty( statusResources.get( 0 ), ApplicationPrivilegeMethodPropertyDescriptor.ID ), "read" );
        AssertJUnit.assertEquals( statusResources.get( 0 ).getName(), "createReadMethodTest - (read)" ); // ' - (read)' is
        // automatically added
        AssertJUnit.assertEquals( statusResources.get( 0 ).getType(), TargetPrivilegeDescriptor.TYPE );
        AssertJUnit.assertEquals( SecurityConfigUtil.getPrivilegeProperty( statusResources.get( 0 ), TargetPrivilegeRepositoryTargetPropertyDescriptor.ID ), "testTarget" );

        SecurityConfigUtil.verifyPrivileges( statusResources );
    }

    @SuppressWarnings( "unchecked" )
    @Test
    public void createCreateMethodTest()
        throws IOException
    {
        PrivilegeResource resource = new PrivilegeResource();

        List methods = new ArrayList<String>();
        methods.add( "create" );
        resource.setMethod( methods );
        resource.setName( "createCreateMethodTest" );
        resource.setType( TargetPrivilegeDescriptor.TYPE );
        resource.setRepositoryTargetId( "testTarget" );

        // get the Resource object
        List<PrivilegeStatusResource> statusResources = this.messageUtil.createPrivileges( resource );

        AssertJUnit.assertTrue( statusResources.size() == 1 );

        // make sure the id != null
        AssertJUnit.assertNotNull( statusResources.get( 0 ).getId() );

        AssertJUnit.assertEquals( SecurityConfigUtil.getPrivilegeProperty( statusResources.get( 0 ), ApplicationPrivilegeMethodPropertyDescriptor.ID ), "create,read" );
        AssertJUnit.assertEquals( statusResources.get( 0 ).getName(), "createCreateMethodTest - (create)" ); // ' - (read)'
        // is
        // automatically added
        AssertJUnit.assertEquals( statusResources.get( 0 ).getType(), TargetPrivilegeDescriptor.TYPE );
        AssertJUnit.assertEquals( SecurityConfigUtil.getPrivilegeProperty( statusResources.get( 0 ), TargetPrivilegeRepositoryTargetPropertyDescriptor.ID ), "testTarget" );

        SecurityConfigUtil.verifyPrivileges( statusResources );
    }

    @SuppressWarnings( "unchecked" )
    @Test
    public void createUpdateMethodTest()
        throws IOException
    {
        PrivilegeResource resource = new PrivilegeResource();

        List methods = new ArrayList<String>();
        methods.add( "update" );
        resource.setMethod( methods );
        resource.setName( "createUpdateMethodTest" );
        resource.setType( TargetPrivilegeDescriptor.TYPE );
        resource.setRepositoryTargetId( "testTarget" );

        // get the Resource object
        List<PrivilegeStatusResource> statusResources = this.messageUtil.createPrivileges( resource );

        AssertJUnit.assertTrue( statusResources.size() == 1 );

        // make sure the id != null
        AssertJUnit.assertNotNull( statusResources.get( 0 ).getId() );

        AssertJUnit.assertEquals( SecurityConfigUtil.getPrivilegeProperty( statusResources.get( 0 ), ApplicationPrivilegeMethodPropertyDescriptor.ID ), "update,read" );
        AssertJUnit.assertEquals( statusResources.get( 0 ).getName(), "createUpdateMethodTest - (update)" ); // ' - (read)'
        // is
        // automatically added
        AssertJUnit.assertEquals( statusResources.get( 0 ).getType(), TargetPrivilegeDescriptor.TYPE );
        AssertJUnit.assertEquals( SecurityConfigUtil.getPrivilegeProperty( statusResources.get( 0 ), TargetPrivilegeRepositoryTargetPropertyDescriptor.ID ), "testTarget" );

        SecurityConfigUtil.verifyPrivileges( statusResources );
    }

    @SuppressWarnings( "unchecked" )
    @Test
    public void createDeleteMethodTest()
        throws IOException
    {
        PrivilegeResource resource = new PrivilegeResource();

        List methods = new ArrayList<String>();
        methods.add( "delete" );
        resource.setMethod( methods );
        resource.setName( "createDeleteMethodTest" );
        resource.setType( TargetPrivilegeDescriptor.TYPE );
        resource.setRepositoryTargetId( "testTarget" );

        // get the Resource object
        List<PrivilegeStatusResource> statusResources = this.messageUtil.createPrivileges( resource );

        AssertJUnit.assertTrue( statusResources.size() == 1 );

        // make sure the id != null
        AssertJUnit.assertNotNull( statusResources.get( 0 ).getId() );

        AssertJUnit.assertEquals( SecurityConfigUtil.getPrivilegeProperty( statusResources.get( 0 ), ApplicationPrivilegeMethodPropertyDescriptor.ID ), "delete,read" );
        AssertJUnit.assertEquals( statusResources.get( 0 ).getName(), "createDeleteMethodTest - (delete)" ); // ' - (read)'
        // is
        // automatically added
        AssertJUnit.assertEquals( statusResources.get( 0 ).getType(), TargetPrivilegeDescriptor.TYPE );
        AssertJUnit.assertEquals( SecurityConfigUtil.getPrivilegeProperty( statusResources.get( 0 ), TargetPrivilegeRepositoryTargetPropertyDescriptor.ID ), "testTarget" );

        SecurityConfigUtil.verifyPrivileges( statusResources );
    }

    @SuppressWarnings( "unchecked" )
    @Test
    public void createAllMethodTest()
        throws IOException
    {
        PrivilegeResource resource = new PrivilegeResource();

        List methods = new ArrayList<String>();
        methods.add( "create" );
        methods.add( "read" );
        methods.add( "update" );
        methods.add( "delete" );
        resource.setMethod( methods );
        resource.setName( "createAllMethodTest" );
        resource.setType( TargetPrivilegeDescriptor.TYPE );
        resource.setRepositoryTargetId( "testTarget" );

        // get the Resource object
        List<PrivilegeStatusResource> statusResources = this.messageUtil.createPrivileges( resource );

        AssertJUnit.assertTrue( statusResources.size() == 4 );

        PrivilegeStatusResource createPriv = this.getPrivilegeByMethod( "create,read", statusResources );
        AssertJUnit.assertNotNull( createPriv.getId() );
        AssertJUnit.assertEquals( SecurityConfigUtil.getPrivilegeProperty( createPriv, ApplicationPrivilegeMethodPropertyDescriptor.ID ), "create,read" );
        AssertJUnit.assertEquals( createPriv.getName(), "createAllMethodTest - (create)" );
        AssertJUnit.assertEquals( createPriv.getType(), TargetPrivilegeDescriptor.TYPE );
        AssertJUnit.assertEquals( SecurityConfigUtil.getPrivilegeProperty( createPriv, TargetPrivilegeRepositoryTargetPropertyDescriptor.ID ), "testTarget" );

        PrivilegeStatusResource readPriv = this.getPrivilegeByMethod( "read", statusResources );
        AssertJUnit.assertNotNull( readPriv.getId() );
        AssertJUnit.assertEquals( SecurityConfigUtil.getPrivilegeProperty( readPriv, ApplicationPrivilegeMethodPropertyDescriptor.ID ), "read" );
        AssertJUnit.assertEquals( readPriv.getName(), "createAllMethodTest - (read)" );
        AssertJUnit.assertEquals( readPriv.getType(), TargetPrivilegeDescriptor.TYPE );
        AssertJUnit.assertEquals( SecurityConfigUtil.getPrivilegeProperty( readPriv, TargetPrivilegeRepositoryTargetPropertyDescriptor.ID ), "testTarget" );

        PrivilegeStatusResource updatePriv = this.getPrivilegeByMethod( "update,read", statusResources );
        AssertJUnit.assertNotNull( updatePriv.getId() );
        AssertJUnit.assertEquals( SecurityConfigUtil.getPrivilegeProperty( updatePriv, ApplicationPrivilegeMethodPropertyDescriptor.ID ), "update,read" );
        AssertJUnit.assertEquals( updatePriv.getName(), "createAllMethodTest - (update)" );
        AssertJUnit.assertEquals( updatePriv.getType(), TargetPrivilegeDescriptor.TYPE );
        AssertJUnit.assertEquals( SecurityConfigUtil.getPrivilegeProperty( updatePriv, TargetPrivilegeRepositoryTargetPropertyDescriptor.ID ), "testTarget" );

        PrivilegeStatusResource deletePriv = this.getPrivilegeByMethod( "delete,read", statusResources );
        AssertJUnit.assertNotNull( deletePriv.getId() );
        AssertJUnit.assertEquals( SecurityConfigUtil.getPrivilegeProperty( deletePriv, ApplicationPrivilegeMethodPropertyDescriptor.ID ), "delete,read" );
        AssertJUnit.assertEquals( deletePriv.getName(), "createAllMethodTest - (delete)" );
        AssertJUnit.assertEquals( deletePriv.getType(), TargetPrivilegeDescriptor.TYPE );
        AssertJUnit.assertEquals( SecurityConfigUtil.getPrivilegeProperty( deletePriv, TargetPrivilegeRepositoryTargetPropertyDescriptor.ID ), "testTarget" );

        SecurityConfigUtil.verifyPrivileges( statusResources );
    }

    private PrivilegeStatusResource getPrivilegeByMethod( String method,
                                                              List<PrivilegeStatusResource> statusResources )
    {
        for ( Iterator<PrivilegeStatusResource> iter = statusResources.iterator(); iter.hasNext(); )
        {
            PrivilegeStatusResource privilegeBaseStatusResource = iter.next();

            if ( SecurityConfigUtil.getPrivilegeProperty( privilegeBaseStatusResource, ApplicationPrivilegeMethodPropertyDescriptor.ID ).equals( method ) )
            {
                return privilegeBaseStatusResource;
            }
        }
        return null;
    }

    @SuppressWarnings( "unchecked" )
    @Test
    public void readTest()
        throws IOException
    {
        PrivilegeResource resource = new PrivilegeResource();

        List methods = new ArrayList<String>();
        methods.add( "read" );
        resource.setMethod( methods );
        resource.setName( "readTest" );
        resource.setType( TargetPrivilegeDescriptor.TYPE );
        resource.setRepositoryTargetId( "testTarget" );

        // get the Resource object
        List<PrivilegeStatusResource> statusResources = this.messageUtil.createPrivileges( resource );

        AssertJUnit.assertTrue( statusResources.size() == 1 );

        // make sure the id != null
        AssertJUnit.assertNotNull( statusResources.get( 0 ).getId() );

        String readPrivId = statusResources.get( 0 ).getId();

        AssertJUnit.assertEquals( SecurityConfigUtil.getPrivilegeProperty( statusResources.get( 0 ), ApplicationPrivilegeMethodPropertyDescriptor.ID ), "read" );
        AssertJUnit.assertEquals( statusResources.get( 0 ).getName(), "readTest - (read)" ); // ' - (read)' is automatically
        // added
        AssertJUnit.assertEquals( statusResources.get( 0 ).getType(), TargetPrivilegeDescriptor.TYPE );
        AssertJUnit.assertEquals( SecurityConfigUtil.getPrivilegeProperty( statusResources.get( 0 ), TargetPrivilegeRepositoryTargetPropertyDescriptor.ID ), "testTarget" );

        Response response = this.messageUtil.sendMessage( Method.POST, resource, readPrivId );

        if ( !response.getStatus().isSuccess() )
        {
            AssertJUnit.fail( "Could not create privilege: " + response.getStatus() );
        }

        statusResources = this.messageUtil.getResourceListFromResponse( response );

        AssertJUnit.assertTrue( statusResources.size() == 1 );

        // make sure the id != null
        AssertJUnit.assertNotNull( statusResources.get( 0 ).getId() );

        AssertJUnit.assertEquals( SecurityConfigUtil.getPrivilegeProperty( statusResources.get( 0 ), ApplicationPrivilegeMethodPropertyDescriptor.ID ), "read" );
        AssertJUnit.assertEquals( statusResources.get( 0 ).getName(), "readTest - (read)" ); // ' - (read)' is automatically
        // added
        AssertJUnit.assertEquals( statusResources.get( 0 ).getType(), TargetPrivilegeDescriptor.TYPE );
        AssertJUnit.assertEquals( SecurityConfigUtil.getPrivilegeProperty( statusResources.get( 0 ), TargetPrivilegeRepositoryTargetPropertyDescriptor.ID ), "testTarget" );

        SecurityConfigUtil.verifyPrivileges( statusResources );

    }

    @SuppressWarnings( "unchecked" )
    @Test
    public void updateTest()
        throws IOException
    {
        PrivilegeResource resource = new PrivilegeResource();

        List methods = new ArrayList<String>();
        methods.add( "read" );
        resource.setMethod( methods );
        resource.setName( "updateTest" );
        resource.setType( TargetPrivilegeDescriptor.TYPE );
        resource.setRepositoryTargetId( "testTarget" );

        // get the Resource object
        List<PrivilegeStatusResource> statusResources = this.messageUtil.createPrivileges( resource );

        AssertJUnit.assertTrue( statusResources.size() == 1 );

        // make sure the id != null
        AssertJUnit.assertNotNull( statusResources.get( 0 ).getId() );

        String readPrivId = statusResources.get( 0 ).getId();

        AssertJUnit.assertEquals( SecurityConfigUtil.getPrivilegeProperty( statusResources.get( 0 ), ApplicationPrivilegeMethodPropertyDescriptor.ID ), "read" );
        AssertJUnit.assertEquals( statusResources.get( 0 ).getName(), "updateTest - (read)" ); // ' - (read)' is
        // automatically
        // added
        AssertJUnit.assertEquals( statusResources.get( 0 ).getType(), TargetPrivilegeDescriptor.TYPE );
        AssertJUnit.assertEquals( SecurityConfigUtil.getPrivilegeProperty( statusResources.get( 0 ), TargetPrivilegeRepositoryTargetPropertyDescriptor.ID ), "testTarget" );

        Response response = this.messageUtil.sendMessage( Method.PUT, resource, readPrivId );

        if ( response.getStatus().getCode() != 405 ) // Method Not Allowed
        {
            AssertJUnit.fail( "Update should have returned a 405: " + response.getStatus() );
        }

    }

    @SuppressWarnings( "unchecked" )
    @Test
    public void deleteTest()
        throws IOException
    {
        PrivilegeResource resource = new PrivilegeResource();

        List methods = new ArrayList<String>();
        methods.add( "read" );
        resource.setMethod( methods );
        resource.setName( "deleteTest" );
        resource.setType( TargetPrivilegeDescriptor.TYPE );
        resource.setRepositoryTargetId( "testTarget" );

        // get the Resource object
        List<PrivilegeStatusResource> statusResources = this.messageUtil.createPrivileges( resource );

        AssertJUnit.assertTrue( statusResources.size() == 1 );

        // make sure the id != null
        AssertJUnit.assertNotNull( statusResources.get( 0 ).getId() );

        String readPrivId = statusResources.get( 0 ).getId();

        AssertJUnit.assertEquals( SecurityConfigUtil.getPrivilegeProperty( statusResources.get( 0 ), ApplicationPrivilegeMethodPropertyDescriptor.ID ), "read" );
        AssertJUnit.assertEquals( statusResources.get( 0 ).getName(), "deleteTest - (read)" ); // ' - (read)' is
        // automatically
        // added
        AssertJUnit.assertEquals( statusResources.get( 0 ).getType(), TargetPrivilegeDescriptor.TYPE );
        AssertJUnit.assertEquals( SecurityConfigUtil.getPrivilegeProperty( statusResources.get( 0 ), TargetPrivilegeRepositoryTargetPropertyDescriptor.ID ), "testTarget" );

        Response response = this.messageUtil.sendMessage( Method.DELETE, resource, readPrivId );

        if ( !response.getStatus().isSuccess() ) // Method Not Allowed
        {
            AssertJUnit.fail( "Delete failed: " + response.getStatus() );
        }

        AssertJUnit.assertNull( SecurityConfigUtil.getCPrivilege( readPrivId ) );

    }

    @SuppressWarnings( "unchecked" )
    @Test
    public void listTest()
        throws IOException
    {
        if ( printKnownErrorButDoNotFail( Nexus233PrivilegesCrudXMLIT.class, "listTest" ) )
        {
            return;
        }        
        
        PrivilegeResource resource = new PrivilegeResource();

        List methods = new ArrayList<String>();
        methods.add( "read" );
        resource.setMethod( methods );
        resource.setName( "listTest" );
        resource.setType( TargetPrivilegeDescriptor.TYPE );
        resource.setRepositoryTargetId( "testTarget" );

        // get the Resource object
        List<PrivilegeStatusResource> statusResources = this.messageUtil.createPrivileges( resource );

        AssertJUnit.assertTrue( statusResources.size() == 1 );

        // make sure the id != null
        AssertJUnit.assertNotNull( statusResources.get( 0 ).getId() );

        AssertJUnit.assertEquals( SecurityConfigUtil.getPrivilegeProperty( statusResources.get( 0 ), ApplicationPrivilegeMethodPropertyDescriptor.ID ), "read" );
        AssertJUnit.assertEquals( statusResources.get( 0 ).getName(), "listTest - (read)" ); // ' - (read)' is
        // automatically added
        AssertJUnit.assertEquals( statusResources.get( 0 ).getType(), TargetPrivilegeDescriptor.TYPE );
        AssertJUnit.assertEquals( SecurityConfigUtil.getPrivilegeProperty( statusResources.get( 0 ), TargetPrivilegeRepositoryTargetPropertyDescriptor.ID ), "testTarget" );

        SecurityConfigUtil.verifyPrivileges( statusResources );

        // now we have something in the repo. now lets get it all...

        Response response = this.messageUtil.sendMessage( Method.GET, resource );

        // get the Resource object
        statusResources = this.messageUtil.getResourceListFromResponse( response );

        SecurityConfigUtil.verifyPrivileges( statusResources );

    }

}
