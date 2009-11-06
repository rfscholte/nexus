package org.sonatype.nexus.test.utils;

import java.io.File;
import java.util.Collections;
import java.util.Properties;

import org.sonatype.jettytestsuite.AuthenticationInfo;
import org.sonatype.jettytestsuite.ControlledServer;
import org.sonatype.jettytestsuite.ProxyServer;
import org.sonatype.jettytestsuite.ServletInfo;
import org.sonatype.jettytestsuite.ServletServer;
import org.sonatype.jettytestsuite.WebappContext;

public class JettyInstaceFactory
{

    public static ServletServer getDefaultFileServer( int port )
        throws Exception
    {
        return getFileServer( port, TestProperties.getFile( "proxy-repo-target-dir" ) );
    }

    public static ServletServer getDefaultSecureFileServer( int port )
        throws Exception
    {
        return getSecureFileServer( port, TestProperties.getFile( "proxy-repo-target-dir" ) );
    }

    public static ServletServer getFileServer( int port, File location )
        throws Exception
    {
        ServletServer server = new ServletServer();
        server.setPort( port );

        WebappContext context = new WebappContext();
        server.setWebappContexts( Collections.singletonList( context ) );
        context.setName( "remote" );

        ServletInfo servletInfo = new ServletInfo();
        context.setServletInfos( Collections.singletonList( servletInfo ) );
        servletInfo.setMapping( "/*" );
        servletInfo.setServletClass( "org.mortbay.jetty.servlet.DefaultServlet" );
        Properties parameters = new Properties();
        parameters.setProperty( "resourceBase", location.getAbsolutePath() );
        parameters.setProperty( "dirAllowed", Boolean.TRUE.toString() );
        servletInfo.setParameters( parameters );
        server.initialize();

        return server;
    }

    public static ServletServer getSecureFileServer( int port, File location )
        throws Exception
    {
        ServletServer server = getFileServer( port, location );
        WebappContext context = server.getWebappContexts().get( 0 );
        AuthenticationInfo auth = new AuthenticationInfo();
        auth.setAuthMethod( "BASIC" );
        auth.setCredentialsFilePath( TestProperties.getPath( "test.resources.folder",
                                                             "default-configs/realm.properties" ) );
        context.setAuthenticationInfo( auth );
        server.initialize();

        return server;
    }

    public static ProxyServer getHttpProxyServer( int port )
        throws Exception
    {
        ProxyServer server = new ProxyServer();
        server.setPort( port );

        server.initialize();

        return server;
    }

    public static ControlledServer getHttpProxyWtihReturnCodeControlServer( int port )
        throws Exception
    {
        ControlledServer server = new ControlledServer();
        server.setPort( port );

        server.initialize();

        return server;
    }
}
