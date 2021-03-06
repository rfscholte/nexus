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
package org.sonatype.nexus.web;

import java.io.File;

import javax.servlet.http.HttpServlet;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.meterware.httpunit.PostMethodWebRequest;
import com.meterware.httpunit.WebRequest;
import com.meterware.servletunit.InvocationContext;
import com.meterware.servletunit.ServletRunner;
import com.meterware.servletunit.ServletUnitClient;

/**
 * Big fat not: this is semi-finished: maven sets the basedir, hence it was esites to move plexus files to /conf/ folder
 * in root of this module.
 * 
 * @author cstamas
 */
public class PlexusContainerContextListenerTest
{
    protected File webXml;

    protected ServletRunner servletRunner;

    @Before
    public void setUp()
        throws Exception
    {
        webXml = new File( "src/test/resources/httpunit/WEB-INF/web.xml" );

        servletRunner = new ServletRunner( webXml, "/target/httpunit" );
    }

    @Test
    public void testListener()
        throws Exception
    {
        ServletUnitClient client = servletRunner.newClient();

        WebRequest request = new PostMethodWebRequest( "http://localhost/target/httpunit/dummyServlet" );

        InvocationContext context = client.newInvocation( request );

        HttpServlet servlet = (HttpServlet) context.getServlet();

        Assert.assertNotNull( servlet.getServletContext().getAttribute( "plexus" ) );
    }
}
