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
package org.sonatype.nexus.security.ldap.realms.testharness.nexus3244;

import java.io.IOException;
import java.util.List;

import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Response;
import org.sonatype.nexus.integrationtests.RequestFacade;
import org.sonatype.nexus.integrationtests.TestContainer;
import org.sonatype.nexus.security.ldap.realms.api.dto.LdapUserAndGroupConfigurationDTO;
import org.sonatype.nexus.security.ldap.realms.testharness.AbstractLdapIntegrationIT;
import org.sonatype.nexus.security.ldap.realms.testharness.LdapUserGroupMessageUtil;
import org.sonatype.plexus.rest.representation.XStreamRepresentation;
import org.sonatype.security.rest.model.PlexusUserListResourceResponse;
import org.sonatype.security.rest.model.PlexusUserResource;
import org.sonatype.security.rest.model.PlexusUserSearchCriteriaResource;
import org.sonatype.security.rest.model.PlexusUserSearchCriteriaResourceRequest;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.thoughtworks.xstream.XStream;

public class Nexus3244ClearConfigWhenChangeIT 
extends AbstractLdapIntegrationIT
{
    
    private XStream xstream;

    private MediaType mediaType;

    public Nexus3244ClearConfigWhenChangeIT()
    {
        super();
    }
    
    @BeforeClass
    public void init()
    {
        this.xstream = this.getJsonXStream();
        this.mediaType = MediaType.APPLICATION_JSON;        
    }
    
    @Test
    public void testConfigIsUpdatedWhenChanged()
        throws Exception
    {   
        // start with good configuration
        Assert.assertEquals( 4, this.doSearch( "", false, "LDAP" ).size() );
        
        // mess up ldap configuration
        LdapUserGroupMessageUtil userGroupUtil = new LdapUserGroupMessageUtil( xstream, mediaType );
        LdapUserAndGroupConfigurationDTO userGroupConfig = userGroupUtil.getUserGroupConfig();
        String originalIdAttribute = userGroupConfig.getUserIdAttribute();
        userGroupConfig.setUserIdAttribute( "JUNKINVALIDJUNK" );
        userGroupUtil.updateUserGroupConfig( userGroupConfig );

        Assert.assertEquals( 0, this.doSearch( "", false, "LDAP" ).size() );
        
        // change config back to correct state
        userGroupConfig.setUserIdAttribute( originalIdAttribute );
        userGroupUtil.updateUserGroupConfig( userGroupConfig );
        Assert.assertEquals( 4, this.doSearch( "", false, "LDAP" ).size() );
    }

    
    private List<PlexusUserResource> doSearch( String userId, boolean effective, String source )
        throws IOException
    {
        PlexusUserSearchCriteriaResourceRequest resourceRequest = new PlexusUserSearchCriteriaResourceRequest();
        PlexusUserSearchCriteriaResource criteria = new PlexusUserSearchCriteriaResource();
        criteria.setUserId( userId );
        criteria.setEffectiveUsers( effective );
        resourceRequest.setData( criteria );

        XStreamRepresentation representation = new XStreamRepresentation( xstream, "", mediaType );

        String serviceURI = RequestFacade.SERVICE_LOCAL + "user_search/" + source;

        // now set the payload
        representation.setPayload( resourceRequest );

        log.debug( "sendMessage: " + representation.getText() );

        Response response = RequestFacade.sendMessage( serviceURI, Method.PUT, representation );

        Assert.assertTrue( response.getStatus().isSuccess(), "Status: " + response.getStatus() );

        PlexusUserListResourceResponse userList = (PlexusUserListResourceResponse) this.parseResponse(
            response,
            new PlexusUserListResourceResponse() );

        return userList.getData();
    }
    
    private Object parseResponse( Response response, Object expectedObject )
    throws IOException
    {
    
        String responseString = response.getEntity().getText();
        log.debug( " getResourceFromResponse: " + responseString );
    
        XStreamRepresentation representation = new XStreamRepresentation( xstream, responseString, mediaType );
        return representation.getPayload( expectedObject );
    }
    
}
