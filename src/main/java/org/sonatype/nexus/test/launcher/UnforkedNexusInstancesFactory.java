package org.sonatype.nexus.test.launcher;

import static org.sonatype.nexus.test.utils.PortUtil.getRandomPort;

import java.io.File;

import org.sonatype.nexus.rest.model.StatusResource;
import org.sonatype.nexus.test.utils.NexusIllegalStateException;
import org.sonatype.nexus.test.utils.NexusStatusUtil;
import org.sonatype.nexus.test.utils.PortUtil;
import org.sonatype.nexus.test.utils.TestProperties;

public class UnforkedNexusInstancesFactory
    implements INexusInstancesFactory
{

    private NexusContext context;

    public NexusContext createInstance()
        throws Exception
    {
        Integer nexusPort = getRandomPort();
        File nexusWorkDir = TestProperties.getFile( "nexus.work.dir" );
        return createInstance( nexusPort, nexusWorkDir );
    }

    public NexusContext createInstance( Integer nexusPort, File nexusDir )
        throws Exception
    {
        String nexusWorkDir;
        if ( context == null )
        {
            nexusWorkDir = nexusDir.getAbsolutePath();
            String nexusBaseDir = TestProperties.getPath( "nexus.base.dir" );
            String nexusApp = nexusBaseDir + "/runtime/apps/nexus";

            System.getProperties().setProperty( "plexus.application-port", nexusPort.toString() );
            System.getProperties().setProperty( "plexus.application-host", "0.0.0.0" );
            System.getProperties().setProperty( "plexus.runtime", nexusBaseDir + "/runtime" );
            System.getProperties().setProperty( "plexus.runtime-tmp", nexusWorkDir + "/runtime-tmp" );
            System.getProperties().setProperty( "plexus.apps", nexusBaseDir + "/runtime/apps" );
            System.getProperties().setProperty( "plexus.nexus-work", nexusWorkDir );
            System.getProperties().setProperty( "plexus.nexus-app", nexusApp );
            System.getProperties().setProperty( "plexus.webapp", nexusBaseDir + "/runtime/apps/nexus/webapp" );
            System.getProperties().setProperty( "plexus.webapp-context-path", "/nexus" );
            System.getProperties().setProperty( "plexus.application-conf", nexusWorkDir + "/conf" );
            System.getProperties().setProperty( "plexus.log4j-prop-file",
                                                TestProperties.getPath( "default-configs" ) + "/log4j.properties" );
            System.getProperties().setProperty( "plexus.jetty.xml", nexusBaseDir + "/conf/jetty.xml" );
            System.getProperties().setProperty( "plexus.index.template.file", "templates/index-debug.vm" );
            System.getProperties().setProperty( "plexus.security-xml-file", nexusWorkDir + "/conf/security.xml" );

            final File f = new File( "target/plexus-home" );

            if ( !f.isDirectory() )
            {
                f.mkdirs();
            }

            File bundleRoot = new File( TestProperties.getAll().get( "nexus.base.dir" ) );
            System.setProperty( "basedir", bundleRoot.getAbsolutePath() );

            // System.setProperty( "plexus.appbooter.customizers", "org.sonatype.nexus.NexusBooterCustomizer,"
            // + ITAppBooterCustomizer.class.getName() );

            File classworldsConf = new File( bundleRoot, "conf/classworlds.conf" );

            if ( !classworldsConf.isFile() )
            {
                throw new IllegalStateException( "The bundle classworlds.conf file is not found (\""
                    + classworldsConf.getAbsolutePath() + "\")!" );
            }

            System.setProperty( "classworlds.conf", classworldsConf.getAbsolutePath() );

            // this is non trivial here, since we are running Nexus in _same_ JVM as tests
            // and the PlexusAppBooterJSWListener (actually theused WrapperManager in it) enforces then Nexus may be
            // started only once in same JVM!
            // So, we are _overrriding_ the in-bundle plexus app booter with the simplest one
            // since we dont need all the bells-and-whistles in Service and JSW
            // but we are still _reusing_ the whole bundle environment by tricking Classworlds Launcher

            ThreadedPlexusAppBooterService booter =
                new ThreadedPlexusAppBooterService( classworldsConf, PortUtil.getRandomPort() );
            booter.start();

            context = new NexusContext( booter, nexusPort, nexusDir );
        }
        else
        {
            ThreadedPlexusAppBooterService booter = (ThreadedPlexusAppBooterService) context.getAppBooter();
            booter.start();
        }

        StatusResource status = NexusStatusUtil.getNexusStatus( context.getPort() ).getData();
        if ( !status.getState().equals( "STARTED" ) )
        {
            throw new NexusIllegalStateException( "Failed to start nexus: " + status.getState() );
        }

        return context;
    }

    public void destroyInstance( NexusContext context )
        throws Exception
    {
        if ( context == null )
        {
            return;
        }
        ThreadedPlexusAppBooterService booter = (ThreadedPlexusAppBooterService) context.getAppBooter();
        booter.stop();
    }

    public void shutdown()
        throws Exception
    {
        if ( context != null )
        {
            ThreadedPlexusAppBooterService booter = (ThreadedPlexusAppBooterService) context.getAppBooter();
            booter.shutdown();
            context = null;
        }
    }

}
